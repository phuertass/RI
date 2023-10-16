import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.*;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream;


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
import org.apache.lucene.analysis.core.StopAnalyzer;


public class Ejercicio1 {


    public static void analizadores(Analyzer analyzer, String name, String contenido, String file) throws IOException{
      //usamos TokenStream para enumerar la secuencia de tokens
      TokenStream stream = analyzer.tokenStream(null, contenido);
      CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
      //Inicializamos el stream  antes de llamar a incrementToken
      stream.reset();
      
      Map<String,Integer> ocurrencias = new HashMap<String, Integer>();
      
      //Añadimos todos los tokens del TokenStream al map
      while(stream.incrementToken()){
        String palabra=attr.toString();
        Integer value=ocurrencias.get(palabra);
        
        if(value==null){
          ocurrencias.put(palabra,1);
        }else{
          ocurrencias.put(palabra,value+1);
        }    
      }

      //ordenamos las palabras
      ocurrencias=sortByValue(ocurrencias);   

      //Creamos archivos .txt con el recuento de las palabras de cada documento
      PrintWriter writer = new PrintWriter("./Estadisticas/" + file + "-" + name +".txt");

      writer.print("--------------------------------------------------------\n");
      writer.print("                  "  + name +"                 " + "\n");
      writer.print("     Numero de tokens en el archivo: " + ocurrencias.size() + " " + "\n");
      writer.print("--------------------------------------------------------\n");
  

      //imprimimos la palabra y su num de frecuencia
      for (Map.Entry<String, Integer> pair : ocurrencias.entrySet()) {
        writer.print(pair.getKey() + ";" + pair.getValue() + "\n");
      }
     
      stream.end();
      stream.close();

    }

public static Map<String, Integer> sortByValue(final Map<String, Integer> wordCounts) {
  /*Introduce las entradas del Map en un Stream, lo ordena con los valores que tiene el Map
  y crea otro map con las entradas ya ordenadas y con los valores del antiguo Map.*/
    return wordCounts.entrySet()
            .stream()
            .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }


public static void main(String[] args) throws IOException{
  //Comprobamos los argumentos de entrada
  if(args.length < 1){
    System.out.println("ERROR: parámetros incorrectos.");
    System.out.println("Por favor, introduzca el directorio.");
    System.exit(0);
  }

    //Obtenemos el directorio que se le pasa como argumento y los ficheros que contiene
    File directorio = new File(args[0]);
    String[] archivos = directorio.list();

    //Creamos un objeto Tika y le establecemos el num de caracteres para los strings (visto en la p1)
    Tika tika = new Tika();
    tika.setMaxStringLength(1000000000);
    //Creamos un objeto de metadata para parsear los ficheros
    Metadata metadata = new Metadata();

    System.out.println("\n");

    //Vemos si existe el directorio 'Estadisticas', si no es asi, lo creamos
    File est = new File("./Estadisticas");
    if(!est.exists()){
      est.mkdir();
    }

    //Recorremos todos los archivos
    for(String f : archivos){
      File file = new File("./"+args[0]+"/"+f);

      //Parseamos
      tika.parse(file, metadata);
      String contenido = new String();

      try{
        contenido = tika.parseToString(file);
        System.out.println("Parseando archivo...");
        System.out.println("Archivo parseado: " + file + "\n");
      }catch (Exception e){ 
        System.out.println("No se puede parsear...\n\n");
      }

      CharArraySet stopSet = SpanishAnalyzer.getDefaultStopSet();
      
      //usamos funcion
      analizadores(new WhitespaceAnalyzer(), "WhitespaceAnalyzer", contenido, f);
      analizadores(new StandardAnalyzer(), "StandardAnalyzer", contenido, f);
      analizadores(new SimpleAnalyzer(), "SimpleAnalyzer", contenido, f);
      analizadores(new StopAnalyzer(stopSet), "StopAnalyzer EMPTY_WORDS_SET", contenido, f);
      analizadores(new StopAnalyzer(stopSet), "StopAnalyzer SPANISH_STOP_WORDS_SET", contenido, f);
      analizadores(new SpanishAnalyzer(), "SpanishAnalyzer", contenido, f);
        
    }
  }
}



