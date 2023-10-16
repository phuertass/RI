import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.ArrayList;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.FilteringTokenFilter;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;


public class Ejercicio3 {

    static class NumbersFilter extends FilteringTokenFilter {
        private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);

        public NumbersFilter(TokenStream input) {
            super(input);
        }

        @Override
        protected boolean accept() throws IOException {
            String token = new String(termAttribute.buffer(), 0, termAttribute.length());
            if (token.matches("[0-9,.]+")) {
                return false;
            }
            return true;
        }

    }

    public static void main(String[] args) throws IOException {
        // Creamos un objeto de Tika y otro de Metadata para parsear los archivos
        Tika tika = new Tika();
        Metadata metadata = new Metadata();

        // Obtenemos el directorio y los archivos que cuelgan de él
        File directory = new File(args[0]);
        String[] files = directory.list();

        for (String fileName : files) {
            // Si no existe, creamos el directorio
            File outputDir = new File("./AnalizadorPersonalizado");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            File file = new File("./" + args[0] + "/" + fileName);
            String text = new String();

            // Parseamos y analizamos el archivo
            try {
                text = tika.parseToString(file);
                System.out.println("Parseando archivo...");
                System.out.println("Archivo parseado: " + fileName + "\n");
            } catch (Exception e) {
                System.out.println("No se puede parsear...\n\n");
            }

            // Parseamos el fichero de texto plano
            tika.parse(file, metadata);

            // Usamos CustomAnalyzer para poder usar los distintos analizadores
            // Convertimos a minúsculas todas las palabras
            // Invertimos los tokens que tienen una longitud mayor que 10
            // Ponemos en mayúsculas los tokens que tienen una longitud menor o igual a 3
            Analyzer analyzer = CustomAnalyzer.builder()
                    .withTokenizer("standard")
                    .addTokenFilter("lowercase")
                    .whenTerm(token -> token.length() > 10)
                    .addTokenFilter("reversestring")
                    .endwhen()
                    .whenTerm(token -> token.length() <= 3)
                    .addTokenFilter("uppercase")
                    .endwhen()
                    .build();

            TokenStream stream = analyzer.tokenStream(null, text);
            // Eliminamos los números
            stream = new NumbersFilter(stream);

            // Creamos el PrintWriter para escribir en cada fichero
            PrintWriter writer = new PrintWriter("./AnalizadorPersonalizado/" + fileName + ".txt");

            // Se llama antes de usar incrementToken()
            stream.reset();
            // Obtenemos los tokens
            while (stream.incrementToken())
                writer.print(" | " + stream.getAttribute(CharTermAttribute.class).toString() + " | " + "\n");

            System.out.println();

            stream.end(); // Se llama cuando se termina de iterar
            stream.close(); // Liberamos los recursos asociados al stream
        }
    }
}