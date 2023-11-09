
import com.opencsv.CSVReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.lang.*;
import java.nio.file.Paths;


public class Indice {
    private Analyzer analyzer;
    private Similarity similarity;
    private FSDirectory dir;
    private IndexWriter writer;
    String indexPath = "index";

    File[] csvUnidos;
    File[] csvCaps;

    //constructor de copia
    Indice(Analyzer ana, Similarity simi){
        analyzer=ana;
        similarity=simi;
    }

    public void initialize(){
        String carpetaCU = "./CapitulosUnidos";
        String carpetaC = "./Capitulos";

        File carpetaCapitulosUnidos = new File(carpetaCU);
        File carpetaCapitulos = new File(carpetaC);

        csvUnidos = carpetaCapitulosUnidos.listFiles((dir,nombre)->nombre.endsWith(".csv"));
        csvCaps = carpetaCapitulos.listFiles((dir,nombre)->nombre.endsWith(".csv"));

        if(csvUnidos.length == 0 || csvCaps.length == 0){
            System.out.println("No hay archivos csv en las carpetas");
            System.exit(0);
        }else{
            System.out.println("\nArchivos csv encontrados");
        }
    }

    public void configurarIndice(){
        //configurar el indice
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // configurar el modelo de recuperacion
        config.setSimilarity(similarity);
        //configurar el modo de apertura del indice
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try {
            dir = FSDirectory.open(Paths.get(indexPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            writer = new IndexWriter(dir, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("\nIndice configurado correctamente");

    }

    public void indexarCapitulosUnidos(){
        //indexar los capitulos unidos
        for (File csv : csvUnidos) {
            try {
                Reader reader = new FileReader(csv);
                CSVReader csvReader = new CSVReader(reader);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close(){
        try {
            writer.commit();
            writer.close();
            System.out.println("\nIndice close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main (String[] args) {
        System.out.println("Creando índice...\n");

	    // Analizador a utilizar
		Analyzer analyzer = new StandardAnalyzer();
		// Medida de Similitud (modelo de recuperacion) por defecto BM25
		Similarity similarity = new ClassicSimilarity();
        // Llamar al constructor de la clase Indice
        Indice mi_indice = new Indice(analyzer, similarity);

        System.out.println("Analizador: " + mi_indice.analyzer.getClass().getName());
        System.out.println("Modelo de recuperación: " + mi_indice.similarity.getClass().getName());

        mi_indice.initialize();
        mi_indice.configurarIndice();

        mi_indice.close();


    }
}