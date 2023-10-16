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


public class Ejercicio3{

	//funcion para eliminar numeros
	static class NumerosFilter extends FilteringTokenFilter { 
		private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
		
		public NumerosFilter(TokenStream in) { 
			super(in);
		}

		@Override
		protected boolean accept() throws IOException{
			String token = new String(termAtt.buffer(), 0, termAtt.length());
			if(token.matches("[0-9,.]+")){
				return false;
			}
			return true;
		}

	}	

	public static void main(String[] args) throws IOException{
		//Creamos un objeto de tika y otro de metadata para parsear los ficheros
		Tika tika = new Tika();
        Metadata metadata = new Metadata();

        //Obtenemos el directorio y los archivos que cuelgan de el
    	File directorio = new File(args[0]);
    	String[] archivos = directorio.list();

    	for (String file : archivos ) {
    		//Si no existe, creamos el directorio 
			File dir = new File("./MiAnalizador");
			if (!dir.exists()){
				dir.mkdir();
			}

    		File f = new File("./" + args[0] + "/" + file);
    		String text = new String();

    		//Parseamos y analizamos el archivo
			try{
				text = tika.parseToString(f);
				System.out.println("Parseando archivo...");
            	System.out.println("Archivo parseado: " + file + "\n");
			}catch (Exception e){ 
				System.out.println("No se puede parsear...\n\n");
			}

			//Parseamos el fichero de texto plano
	        tika.parse(f,metadata);

	        //Usamos CustomAnalyzer para poder usar los distintos analizadores
	        //Lo que hago es: pasar a minusculas todas las palabras 
	        //Los tokens que tienen una longitud mayor que 10 los invierto
	        //Los tokens que tienen una longitud menor o igual a 3 los pongo en mayusculas
			Analyzer analyzer = CustomAnalyzer.builder()
	   			.withTokenizer("standard")
	   			.addTokenFilter("lowercase")
	   			.whenTerm(t -> t.length() > 10)
      				.addTokenFilter("reversestring")
    			.endwhen()
    			.whenTerm(t -> t.length() <= 3)
      				.addTokenFilter("uppercase")
    			.endwhen()
	   			.build();


			TokenStream stream = analyzer.tokenStream(null, text);
			//eliminamos numeros
			stream = new NumerosFilter(stream);

			//Creamos el printwriter para escribir en cada fichero
			PrintWriter writer = new PrintWriter("./MiAnalizador/" + file + ".txt");

			//Se le llama antes de usar incrementToken()
			stream.reset();
			//Obtenemos los tokens
			while(stream.incrementToken())
			    writer.print(" | "+stream.getAttribute(CharTermAttribute.class).toString() + " | " + "\n");

			System.out.println();
				
			stream.end(); //Se le llama cuando se termina de iterar
			stream.close(); //Liberas los recursos asociados al stream
	    		
	    	}
	}
}