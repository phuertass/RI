
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.lang.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    public void indexarCapitulosUnidos() throws CsvValidationException, IOException {

        Reader reader = null;
        try {
            reader = new FileReader(csvUnidos[0]);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        CSVReader csvReader= new CSVReader(reader);
        String[] firstLine;
        firstLine=csvReader.readNext();
        // Creamos un array de campos con la primera linea del csv
        String[] campos = new String [firstLine.length];

        for(int i = 0; i < firstLine.length; i++) {
            campos[i] = firstLine[i];
        }

        System.out.println("\nCampos: ");
        System.out.println("Size: " + campos.length);
        for (String campo : campos) {
            System.out.println(campo);
        }

        //indexar los capitulos unidos
        for (File csv : csvUnidos) {
            Document doc = new Document();
            try {
                reader = new FileReader(csv);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            csvReader= new CSVReader(reader);

            String[] nextRecord;

            csvReader.readNext();
            while ((nextRecord = csvReader.readNext()) != null){
                if(!nextRecord[1].isEmpty()){
                    // episode_id INT
                    doc.add(new org.apache.lucene.document.IntPoint(campos[1], Integer.parseInt(nextRecord[1])));
                    doc.add(new org.apache.lucene.document.StoredField(campos[1], Integer.parseInt(nextRecord[1])));
                }
                if(!nextRecord[2].isEmpty()){
                    // spoken words TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[2], nextRecord[2], org.apache.lucene.document.Field.Store.NO));
                }
                if(!nextRecord[3].isEmpty()){
                    // raw_text TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[3], nextRecord[3], org.apache.lucene.document.Field.Store.YES));
                }
                if(!nextRecord[4].isEmpty()){
                    // imdb rating DOUBLE
                    doc.add(new org.apache.lucene.document.DoublePoint(campos[4], Double.parseDouble(nextRecord[4])));
                    doc.add(new org.apache.lucene.document.StoredField(campos[4], Double.parseDouble(nextRecord[4])));
                }
                if(!nextRecord[5].isEmpty()){
                    // imdb votes INT
                    double imdbVotes = Double.parseDouble(nextRecord[5]);
                    int imdbVotesInt = (int) imdbVotes;
                    doc.add(new org.apache.lucene.document.IntPoint(campos[5], imdbVotesInt));
                    //doc.add(new org.apache.lucene.document.StoredField(campos[5], imdbVotesInt));
                }
                if(!nextRecord[6].isEmpty()){
                    // numer in season INT
                    doc.add(new org.apache.lucene.document.IntPoint(campos[6], Integer.parseInt(nextRecord[6])));
                    //doc.add(new org.apache.lucene.document.StoredField(campos[6], Integer.parseInt(nextRecord[6])));
                }
                if(!nextRecord[7].isEmpty()){
                    // original air date
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(nextRecord[7]);
                        doc.add(new org.apache.lucene.document.LongPoint(campos[7], date.getTime()));
                        doc.add(new org.apache.lucene.document.StoredField(campos[7], date.getTime()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(!nextRecord[8].isEmpty()){
                    // original air year INT
                    doc.add(new org.apache.lucene.document.IntPoint(campos[8], Integer.parseInt(nextRecord[8])));
                    //doc.add(new org.apache.lucene.document.StoredField(campos[8], Integer.parseInt(nextRecord[8])));
                }
                if(!nextRecord[9].isEmpty()){
                    // season INT
                    doc.add(new org.apache.lucene.document.IntPoint(campos[9], Integer.parseInt(nextRecord[9])));
                    doc.add(new org.apache.lucene.document.StoredField(campos[9], Integer.parseInt(nextRecord[9])));
                }
                if(!nextRecord[10].isEmpty()){
                    // title TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[10], nextRecord[10], org.apache.lucene.document.Field.Store.YES));
                }
                if(!nextRecord[11].isEmpty()){
                    // us viewsers in millions DOUBLE
                    doc.add(new org.apache.lucene.document.DoublePoint(campos[11], Double.parseDouble(nextRecord[11])));
                    doc.add(new org.apache.lucene.document.StoredField(campos[11], Double.parseDouble(nextRecord[11])));
                }
                if(!nextRecord[12].isEmpty()){
                    // views INT
                    double views = Double.parseDouble(nextRecord[12]);
                    int viewsInt = (int) views;
                    doc.add(new org.apache.lucene.document.IntPoint(campos[12], viewsInt));
                    //doc.add(new org.apache.lucene.document.StoredField(campos[12], viewsInt));
                }
            }
            writer.addDocument(doc);


        }
    }

    public void indexarCapitulos() throws CsvValidationException, IOException {
        Reader reader = null;
        try {
            reader = new FileReader(csvCaps[0]);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        CSVReader csvReader = new CSVReader(reader);
        String[] firstLine;
        firstLine = csvReader.readNext();
        // Creamos un array de campos con la primera linea del csv
        String[] campos = new String[firstLine.length];

        for (int i = 0; i < firstLine.length; i++) {
            campos[i] = firstLine[i];
        }

        System.out.println("\nCampos: ");
        System.out.println("Size: " + campos.length);
        for (String campo : campos) {
            System.out.println(campo);
        }

        // indexar los capitulos
        for (File csv : csvCaps) {
            Document doc = new Document();
            try {
                reader = new FileReader(csv);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            csvReader = new CSVReader(reader);

            String[] nextRecord;
            csvReader.readNext();

            while ((nextRecord = csvReader.readNext()) != null) {
                if (!nextRecord[1].isEmpty()) {
                    // episode_id INT
                    doc.add(new org.apache.lucene.document.IntPoint(campos[1], Integer.parseInt(nextRecord[1])));
                    //doc.add(new org.apache.lucene.document.StoredField(campos[1], Integer.parseInt(nextRecord[1])));
                }
                if (!nextRecord[2].isEmpty()) {
                    // number INT
                    doc.add(new org.apache.lucene.document.IntPoint(campos[2], Integer.parseInt(nextRecord[2])));
                    //doc.add(new org.apache.lucene.document.StoredField(campos[2], Integer.parseInt(nextRecord[2])));
                }
                if (!nextRecord[3].isEmpty()) {
                    // timestamp in ms LONG
                    doc.add(new org.apache.lucene.document.LongPoint(campos[3], Long.parseLong(nextRecord[3])));
                    //doc.add(new org.apache.lucene.document.StoredField(campos[3], Long.parseLong(nextRecord[3])));
                }
                if (!nextRecord[4].isEmpty()) {
                    // raw_text TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[4], nextRecord[4], org.apache.lucene.document.Field.Store.YES));
                }
                if (!nextRecord[5].isEmpty()) {
                    // raw location TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[5], nextRecord[5], org.apache.lucene.document.Field.Store.YES));
                }
                if (!nextRecord[6].isEmpty()) {
                    // spoken words TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[6], nextRecord[6], org.apache.lucene.document.Field.Store.NO));
                }
            }
            writer.addDocument(doc);

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


        /*try {
            mi_indice.indexarCapitulosUnidos();
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }*/

        try {
            mi_indice.indexarCapitulos();
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }

        mi_indice.close();


    }
}