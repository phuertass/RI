import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.classic.ClassicFilter;
import org.apache.lucene.analysis.classic.ClassicTokenizer;
import org.apache.lucene.analysis.classic.ClassicTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilterFactory;
import org.apache.lucene.analysis.pattern.SimplePatternSplitTokenizerFactory;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.SynonymFilter;

public class Ejercicio2 {

    public static void muestraToken(TokenStream stream, String texto) throws IOException {
    	//mostramos el nombre del token que estamos usando
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + texto + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("\n");

        //Inicializamos el stream  antes de llamar a incrementToken
        stream.reset();
        //obtenemos los tokens
        while (stream.incrementToken()) {
            System.out.print(" "+stream.getAttribute(CharTermAttribute.class).toString());
        }

        stream.end();
        stream.close();
        System.out.println("\n\n");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //Insertamos un texto que sera el que probaremos con los distintos tokens
        String cadena = "En un lugar de la Mancha, de cuyo nombre no quiero acordarme, no ha mucho tiempo que vivía un hidalgo de los de lanza en astillero, adarga antigua, rocín flaco y galgo corredor.";
        System.out.println("\n\n");
        System.out.println("Texto original: " + cadena);
        System.out.println("\n\n");

        //Creamos el diccionario de sinonimos necesario para SynonymFilter
        SynonymMap.Builder builder = new SynonymMap.Builder(true);
        builder.add(new CharsRef("nombre"), new CharsRef("apodo"), true);
        builder.add(new CharsRef("acordarme"), new CharsRef("hacer memoria"), true);
        builder.add(new CharsRef("hidalgo"), new CharsRef("noble"), true);
        builder.add(new CharsRef("astillero"), new CharsRef("varadero"), true);
        builder.add(new CharsRef("rocin"), new CharsRef("caballo"), true);
        builder.add(new CharsRef("galgo"), new CharsRef("lebrel"), true);
        SynonymMap sinonimos = builder.build();

		//Muestra el texto por defecto
		muestraToken(new StandardAnalyzer().tokenStream(null, cadena), "StandardAnalyzer");

		//Convierte los tokens a minúsculas
		muestraToken(new LowerCaseFilter(new WhitespaceAnalyzer().tokenStream(null, cadena)), "LowerCaseFilter");

		//Elimina los tokens de la cadena que se encuentren en el conjunto de palabras vacías
		CharArraySet stopSet = SpanishAnalyzer.getDefaultStopSet();
		Iterator iter = stopSet.iterator();
		muestraToken(new StopFilter(new StandardAnalyzer().tokenStream(null, cadena), stopSet), "StopFilter");
		
		//Reduce cada token a su raíz
		muestraToken(new SnowballFilter(new StandardAnalyzer().tokenStream(null, cadena), "Spanish"), "SnowballFilter");
		
		//Realiza combinaciones de tokens
		muestraToken(new ShingleFilter(new StandardAnalyzer().tokenStream(null, cadena)), "ShingleFilter");
		
		//Reduce los tokens a una longitud dada y si es de menor longitud los elimina
		muestraToken(new EdgeNGramTokenFilter(new StandardAnalyzer().tokenStream(null, cadena), 4), "EdgeNGramTokenFilter");
		
		//Igual que el anterior, pero hace combinaciones
		muestraToken(new NGramTokenFilter(new StandardAnalyzer().tokenStream(null, cadena), 4), "NGramTokenFilter");
		
		//Añadimos algunas common words necesarias para el funcionamiento del CommonGramsFilter
		String[] words = new String[] {"en", "de", "un", "la"};
		CharArraySet commonWords = new CharArraySet(words.length, false);
		commonWords.addAll(Arrays.asList(words));

		//Realiza combinaciones con los commonWords junto a las que aparecen próximas
		muestraToken(new CommonGramsFilter(new StandardAnalyzer().tokenStream(null, cadena), commonWords), "CommonGramsFilter");
		
		//Crea tokens con los sinónimos que le hemos dado
		muestraToken(new SynonymFilter(new StandardAnalyzer().tokenStream(null, cadena), sinonimos, true), "SynonymFilter");
		

    }

}
