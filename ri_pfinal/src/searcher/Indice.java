package searcher;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.facet.FacetsConfig;

import java.io.*;
import java.lang.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Indice {

    private final Analyzer analyzer;
    private final Similarity similarity;
    private IndexWriter writer;

    private final String indexPath;
    private final String facetPath;

    private FacetsConfig fconfig;
    private DirectoryTaxonomyWriter taxoWriter;

    File[] csvUnidos;
    File[] csvCaps;

    //constructor de copia
    Indice(Analyzer ana, Similarity simi, String indexP, String faceP) {
        analyzer = ana;
        similarity = simi;
        indexPath = indexP;
        facetPath = faceP;
    }

    public void initialize() {
        String carpetaCU = "./CapitulosUnidos";
        String carpetaC = "./Capitulos";

        File carpetaCapitulosUnidos = new File(carpetaCU);
        File carpetaCapitulos = new File(carpetaC);

        csvUnidos = carpetaCapitulosUnidos.listFiles((dir, nombre) -> nombre.endsWith(".csv"));
        csvCaps = carpetaCapitulos.listFiles((dir, nombre) -> nombre.endsWith(".csv"));

        if (csvUnidos.length == 0 || (csvCaps != null ? csvCaps.length : 0) == 0) {
            System.out.println("No hay archivos csv en las carpetas");
            System.exit(0);
        } else {
            System.out.println("\nArchivos csv encontrados");
        }
    }

    public void configurarIndice() {
        //configurar el indice
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // configurar el modelo de recuperacion
        config.setSimilarity(similarity);
        //configurar el modo de apertura del indice
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        FSDirectory dir;
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

        //localizacion faceta
        try {
            FSDirectory taxoDir = FSDirectory.open(Paths.get(facetPath));
            //creamos faceta
            fconfig = new FacetsConfig();
            taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\nIndice configurado correctamente");

    }

    public static long convertToLong(String cadena) {
        long valor;
        try {
            valor = Long.parseLong(cadena);
        } catch (NumberFormatException | NullPointerException nfe) {
            return 0; //Valor default en caso de no poder convertir  a Long
        }
        return valor;
    }

    public void indexarCapitulosUnidos() throws CsvValidationException, IOException {

        Reader reader = null;
        try {
            reader = new FileReader(csvUnidos[0]);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        CSVReader csvReader = new CSVReader(reader);
        String[] firstLine;
        firstLine = csvReader.readNext();
        // Creamos un array de campos con la primera linea del csv
        String[] campos = new String[firstLine.length];

        System.arraycopy(firstLine, 0, campos, 0, firstLine.length);

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
            csvReader = new CSVReader(reader);

            String[] nextRecord;

            StringBuilder todo = new StringBuilder();
            todo = new StringBuilder(todo.toString().trim());

            csvReader.readNext();
            while ((nextRecord = csvReader.readNext()) != null) {

                //CONFIGURAMOS FACETAS
                fconfig.setMultiValued("imdb_rating", true);
                fconfig.setMultiValued("numer_in_season", true);

                if (!nextRecord[1].isEmpty()) {
                    // episode_id INT
                    todo.append(nextRecord[1].trim()).append(" ");
                    doc.add(new org.apache.lucene.document.StringField(campos[1], nextRecord[1].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.document.NumericDocValuesField(campos[1], Long.valueOf(nextRecord[1].trim())));
                }
                if (!nextRecord[2].isEmpty()) {
                    // spoken words TEXT
                    todo.append(nextRecord[2]).append(" ");
                    doc.add(new org.apache.lucene.document.TextField(campos[2], nextRecord[2], org.apache.lucene.document.Field.Store.NO));
                }
                if (!nextRecord[3].isEmpty()) {
                    // raw_text TEXT
                    todo.append(nextRecord[3]).append(" ");
                    doc.add(new org.apache.lucene.document.TextField(campos[3], nextRecord[3], org.apache.lucene.document.Field.Store.YES));
                }
                if (!nextRecord[4].isEmpty()) {
                    // imdb rating DOUBLE
                    todo.append(nextRecord[4].trim()).append(" ");
                    doc.add(new org.apache.lucene.document.StringField(campos[4], nextRecord[4].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.document.StoredField(campos[4], Double.parseDouble(nextRecord[4])));
                    doc.add(new org.apache.lucene.facet.FacetField(campos[4], nextRecord[4].trim()));
                }

                if (!nextRecord[5].isEmpty()) {

                    // imdb votes INT
                    double imdbVotes = Double.parseDouble(nextRecord[5]);
                    int imdbVotesInt = (int) imdbVotes;
                    todo.append(imdbVotesInt).append(" ");

                    doc.add(new org.apache.lucene.document.StringField(campos[5], nextRecord[5].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.document.NumericDocValuesField(campos[5], imdbVotesInt));

                }
                if (!nextRecord[6].isEmpty()) {
                    // numer in season INT
                    todo.append(nextRecord[6].trim()).append(" ");
                    doc.add(new org.apache.lucene.document.StringField(campos[6], nextRecord[6].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.document.NumericDocValuesField(campos[6], Long.valueOf(nextRecord[6].trim())));
                    doc.add(new org.apache.lucene.facet.FacetField(campos[6], nextRecord[6].trim()));

                }
                if (!nextRecord[7].isEmpty()) {
                    todo.append(nextRecord[7]).append(" ");
                    // original air date
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(nextRecord[7]);
                        todo.append(date.toString());

                        doc.add(new org.apache.lucene.document.LongPoint(campos[7], date.getTime()));
                        doc.add(new org.apache.lucene.document.StoredField(campos[7], date.getTime()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (!nextRecord[8].isEmpty()) {
                    // original air year INT

                    todo.append(nextRecord[8].trim()).append(" ");
                    doc.add(new org.apache.lucene.document.StringField(campos[8], nextRecord[8].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.document.NumericDocValuesField(campos[8], Long.valueOf(nextRecord[8].trim())));
                }
                if (!nextRecord[9].isEmpty()) {
                    // season INT

                    todo.append(nextRecord[9].trim()).append(" ");
                    doc.add(new org.apache.lucene.document.StringField(campos[9], nextRecord[9].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.document.NumericDocValuesField(campos[9], Long.valueOf(nextRecord[9].trim())));

                }
                if (!nextRecord[10].isEmpty()) {
                    todo.append(nextRecord[10].trim()).append(" ");
                    // title TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[10], nextRecord[10], org.apache.lucene.document.Field.Store.YES));
                }
                if (!nextRecord[11].isEmpty()) {

                    // us viewsers in millions DOUBLE
                    todo.append(nextRecord[11].trim()).append(" ");
                    doc.add(new org.apache.lucene.document.StringField(campos[11], nextRecord[11].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.document.StoredField(campos[11], Double.parseDouble(nextRecord[11])));
                }
                if (!nextRecord[12].isEmpty()) {

                    // views INT
                    double views = Double.parseDouble(nextRecord[12]);
                    int viewsInt = (int) views;
                    todo.append(viewsInt).append(" ");

                    doc.add(new org.apache.lucene.document.StringField(campos[12], nextRecord[12].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.document.NumericDocValuesField(campos[12], viewsInt));
                }
            }

            doc.add(new org.apache.lucene.document.TextField("TODO", todo.toString(), org.apache.lucene.document.TextField.Store.YES));
            //writer.addDocument(doc);
            writer.addDocument(fconfig.build(taxoWriter, doc)); //incluimos las facetas en el doc

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

        System.arraycopy(firstLine, 0, campos, 0, firstLine.length);

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

            StringBuilder todo = new StringBuilder();
            todo = new StringBuilder(todo.toString().trim());

            while ((nextRecord = csvReader.readNext()) != null) {

                //CONFIGURAMOS FACETAS
                fconfig.setMultiValued("number", true);
                fconfig.setMultiValued("timestamp_in_ms", true);

                if (!nextRecord[1].isEmpty()) {
                    todo.append(nextRecord[1]);
                    // episode_id INT
                    doc.add(new org.apache.lucene.document.StringField(campos[1], nextRecord[1].trim(), org.apache.lucene.document.Field.Store.YES));
                }
                if (!nextRecord[2].isEmpty()) {
                    todo.append(nextRecord[2]);
                    // number INT
                    doc.add(new org.apache.lucene.document.StringField(campos[2], nextRecord[2].trim(), org.apache.lucene.document.Field.Store.YES));
                    doc.add(new org.apache.lucene.facet.FacetField(campos[2], nextRecord[2].trim()));
                }
                if (!nextRecord[3].isEmpty()) {
                    todo.append(nextRecord[3]);
                    // timestamp in ms LONG
                    doc.add(new org.apache.lucene.document.LongPoint(campos[3], Long.parseLong(nextRecord[3])));
                    doc.add(new org.apache.lucene.document.StoredField(campos[3], Long.parseLong(nextRecord[3])));
                    doc.add(new org.apache.lucene.facet.FacetField(campos[3], nextRecord[3].trim()));
                }
                if (!nextRecord[4].isEmpty()) {
                    todo.append(nextRecord[4]);
                    // raw_text TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[4], nextRecord[4], org.apache.lucene.document.Field.Store.YES));
                }
                if (!nextRecord[5].isEmpty()) {
                    todo.append(nextRecord[5]);
                    // raw location TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[5], nextRecord[5], org.apache.lucene.document.Field.Store.YES));
                }
                if (!nextRecord[6].isEmpty()) {
                    todo.append(nextRecord[6]);
                    // spoken words TEXT
                    doc.add(new org.apache.lucene.document.TextField(campos[6], nextRecord[6], org.apache.lucene.document.Field.Store.YES));
                }
            }

            doc.add(new org.apache.lucene.document.TextField("TODO", todo.toString(), org.apache.lucene.document.TextField.Store.YES));
//            writer.addDocument(doc);
            writer.addDocument(fconfig.build(taxoWriter, doc)); //incluimos las facetas en el doc

        }
    }

    public void close() {
        try {
            writer.commit();
            writer.close();
            taxoWriter.close();
            System.out.println("\nIndice close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Se requieren al menos dos argumentos: indexPath y opción (1 o 2)");
            return;
        }

        String indexPath = args[0];
        int opcion = Integer.parseInt(args[1]);
        System.out.println("Creando índice en: " + indexPath + "\n");

        Analyzer defaultAnalyzer = new WhitespaceAnalyzer();

        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("spoken_words", new EnglishAnalyzer());
        analyzerPerField.put("title", new EnglishAnalyzer());
        analyzerPerField.put("raw_character_text", new EnglishAnalyzer());
        analyzerPerField.put("raw_location_text", new EnglishAnalyzer());
        analyzerPerField.put("raw_text", new EnglishAnalyzer());

        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, analyzerPerField);

        Similarity similarity = new ClassicSimilarity();

        Indice mi_indice = new Indice(analyzer, similarity, indexPath, "facets");

        System.out.println("Analizador: " + mi_indice.analyzer.getClass().getName());
        System.out.println("Modelo de recuperación: " + mi_indice.similarity.getClass().getName());

        mi_indice.initialize();
        mi_indice.configurarIndice();

        try {
            if (opcion == 1) {
                mi_indice.indexarCapitulosUnidos();
            } else if (opcion == 2) {
                mi_indice.indexarCapitulos();
            } else {
                System.out.println("Opción no válida. Debe ser 1 o 2.");
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }

        mi_indice.close();
    }

}
