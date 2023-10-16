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

    public static void analyzeText(Analyzer analyzer, String name, String content, String fileName) throws IOException {
        TokenStream stream = analyzer.tokenStream(null, content);
        CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
        stream.reset();

        Map<String, Integer> wordCounts = new HashMap<>();

        while (stream.incrementToken()) {
            String word = attr.toString();
            Integer count = wordCounts.get(word);

            if (count == null) {
                wordCounts.put(word, 1);
            } else {
                wordCounts.put(word, count + 1);
            }
        }

        wordCounts = sortByValue(wordCounts);

        PrintWriter writer = new PrintWriter("./Estadisticas/" + fileName + "-" + name + ".txt");

        writer.print("--------------------------------------------------------\n");
        writer.print("                  " + name + "                 " + "\n");
        writer.print("     Number of tokens in the document: " + wordCounts.size() + " " + "\n");
        writer.print("--------------------------------------------------------\n");

        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            writer.print(entry.getKey() + ";" + entry.getValue() + "\n");
        }

        stream.end();
        stream.close();
    }

    public static Map<String, Integer> sortByValue(final Map<String, Integer> wordCounts) {
        return wordCounts.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("ERROR: Incorrect parameters.");
            System.out.println("Please provide the directory path.");
            System.exit(0);
        }

        File directory = new File(args[0]);
        String[] files = directory.list();

        Tika tika = new Tika();
        tika.setMaxStringLength(1000000000);
        Metadata metadata = new Metadata();

        System.out.println("\n");

        File statisticsDirectory = new File("./Estadisticas");
        if (!statisticsDirectory.exists()) {
            statisticsDirectory.mkdir();
        }

        for (String fileName : files) {
            File file = new File("./" + args[0] + "/" + fileName);
            tika.parse(file, metadata);
            String content = new String();

            try {
              content = tika.parseToString(file);
              System.out.println("Parseando archivo...");
              System.out.println("Archivo parseado: " + fileName + "\n");
          } catch (Exception e) {
              System.out.println("No se puede parsear...\n\n");
          }
            CharArraySet stopSet = SpanishAnalyzer.getDefaultStopSet();

            analyzeText(new WhitespaceAnalyzer(), "WhitespaceAnalyzer", content, fileName);
            analyzeText(new StandardAnalyzer(), "StandardAnalyzer", content, fileName);
            analyzeText(new SimpleAnalyzer(), "SimpleAnalyzer", content, fileName);
            analyzeText(new StopAnalyzer(stopSet), "StopAnalyzer EMPTY_WORDS_SET", content, fileName);
            analyzeText(new StopAnalyzer(SpanishAnalyzer.getDefaultStopSet()), "StopAnalyzer SPANISH_STOP_WORDS_SET", content, fileName);
            analyzeText(new SpanishAnalyzer(), "SpanishAnalyzer", content, fileName);
        }
    }
}