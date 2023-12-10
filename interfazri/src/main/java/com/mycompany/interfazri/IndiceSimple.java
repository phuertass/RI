package com.mycompany.interfazri;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import com.opencsv.CSVReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import java.io.FileReader;
import org.apache.lucene.search.IndexSearcher;
import java.util.ArrayList;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.analysis.FilteringTokenFilter;
import java.lang.*;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.search.IndexSearcher;


//PATRICIA VILLALBA CRUCELAEGUI

public class IndiceSimple {
	String indexPath = "src/main/java/indice";
	String facetPath = "src/main/java/facet";
	private FacetsConfig fconfig;
	private IndexWriter writer;
	private Analyzer analyzer;
	private Similarity similarity;
	private DirectoryTaxonomyWriter taxoWriter;
	private static final String SAMPLE_CSV_FILE_PATH = "src/main/java/n_movies.csv";


	//constructor por defecto
	IndiceSimple(){
		analyzer = new StandardAnalyzer();
		similarity = new ClassicSimilarity();
	}

	//constructor de copia
	IndiceSimple(Analyzer ana, Similarity simi){
		analyzer=ana;
		similarity=simi;
	}

	public void configurarIndice() throws IOException {
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setSimilarity(similarity);
		// Crear un nuevo indice cada vez que se ejecute
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

		// Localizacion del indice
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		//localizacion faceta
		FSDirectory taxoDir = FSDirectory.open(Paths.get(facetPath));
		//creamos faceta
		fconfig = new FacetsConfig();

		// Creamos el indice y faceta
		writer = new IndexWriter(dir, iwc);
		taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
	}


	public String getVotes(String cadena){
		//quito la coma
		if(!cadena.isEmpty()){
			cadena = cadena.replace(",", "").trim();
		}

	    return cadena.trim();
	}

	public String getGenre(String cadena){
		if(!cadena.isEmpty()){
			cadena = cadena.replace(",", "").trim();
		}

		return cadena.trim();
	}

	public String getDuration(String cadena){
		//quito min y lo dejo como un entero
		if(!cadena.isEmpty()){
			cadena = cadena.replaceAll("[^0-9]", "").trim();
		}

		return cadena.trim();
	}

	public String getStars(String cadena){
		if(!cadena.isEmpty() && !cadena.equals("[]")){
			//cadena = cadena.replaceAll("[^a-zA-Z]", " ").trim();
                        cadena = cadena.replaceAll("['']", "").trim();
			cadena = cadena.replaceAll("[,]", "").trim();
			cadena = cadena.replaceAll("[|]", "").trim();
			cadena = cadena.replaceAll("[\"]", "").trim();
			cadena = cadena.replace("[", "").trim();
			cadena = cadena.replace("]", "").trim();
		}
                
		return cadena.trim();
	}

	public String getCertificate(String cadena){
		if(!cadena.isEmpty()){
			cadena = cadena.replace("-", " ").trim();
		}

		return cadena.trim();
	}

	public String getRating(String cadena){
		return cadena.trim();
	}

	public String getDescription(String cadena){
		return cadena.trim();
	}

	public String getYear(String cadena){
		if(!cadena.isEmpty()){
			cadena = cadena.replaceAll("[^a-zA-Z-0-9–]", " ").trim(); //quito parentesis y lo que no sean numeros

			String parts[] = cadena.split("–"); 
			String part1 = parts[0].trim(); 
			String part2 = parts[0].trim();
			String c = "";

			if (parts.length > 1){
				part2 = parts[1];
			}

			int inicio=0, fin=0, aux=0;

			//convierto a int para poder operar con los datos
			try{
				inicio = Integer.parseInt(part1);
				fin = Integer.parseInt(part2);
			}catch (NumberFormatException ex){
				System.out.println("");
			}

			//genero anios
			if(cadena.contains("–")){
				for (aux=inicio; aux<=fin; aux++) {
					c += " " + String.valueOf(aux) ; //2005 2006 2007...
				}
				//cadena = cadena + " " + String.valueOf(aux);
				cadena = cadena.replaceAll("[–]", " ").trim();
				cadena = c;
			}else{
				return part1;
			}
		}

		return cadena.trim();
	}


	public static long convertToLong(String cadena) {
	    long valor;
	    try {
	        valor = Long.parseLong(cadena + "");
	    } catch (NumberFormatException | NullPointerException nfe) {
	        return 0; //Valor default en caso de no poder convertir  a Long
	    }
	    return valor;
	}


	public void indexarDocumentos() throws IOException{
		//leemos el csv y parseamos
		Reader reader = new FileReader(SAMPLE_CSV_FILE_PATH);			
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

		//recorremos el csv
		Iterator<CSVRecord> csv_it = csvParser.iterator();
		CSVRecord first=csv_it.next();

		//creamos un array con los campos del csv
		String[] campos = new String[first.size()];

		for(int i=0; i<first.size(); i++){
			campos[i]=first.get(i);
		}

		for(CSVRecord csvRecord : csvParser){
			Document doc = new Document();

			//CAMPOS INDEXACION 
			String todo = new String();
			todo = todo.trim();
			for(int i=0; i<9; i++){
				todo+=csvRecord.get(i) + " ";
			}

			//CONFIGURAMOS FACETAS
			fconfig.setMultiValued("year", true);
			fconfig.setMultiValued("genre", true);
			fconfig.setMultiValued("stars", true);
			fconfig.setMultiValued("director", true);

			if(!csvRecord.get(0).isEmpty()){
				doc.add(new TextField(campos[0], csvRecord.get(0), Field.Store.YES)); //titulo
			}

			if(!csvRecord.get(1).isEmpty()){
				doc.add(new StringField(campos[1], getYear(csvRecord.get(1)), Field.Store.YES)); //anio
				doc.add(new NumericDocValuesField(campos[1], convertToLong(getYear(csvRecord.get(1)))));
				doc.add(new FacetField(campos[1], getYear(csvRecord.get(1)))); //YEAR
			}

			if(!csvRecord.get(2).isEmpty()){
				doc.add(new StringField(campos[2], getCertificate(csvRecord.get(2)), Field.Store.YES)); //certificate
			}

			if(!csvRecord.get(3).isEmpty()){
				doc.add(new StringField(campos[3], getDuration(csvRecord.get(3)), Field.Store.YES)); //duration
				doc.add(new NumericDocValuesField(campos[3], convertToLong(getDuration(csvRecord.get(3)))));
			}

			if(!csvRecord.get(4).isEmpty()){
				doc.add(new TextField(campos[4], getGenre(csvRecord.get(4)), Field.Store.YES)); //genre
				doc.add(new FacetField(campos[4], getGenre(csvRecord.get(4)))); 
			}
			
			if(!csvRecord.get(5).isEmpty()){
				doc.add(new StringField(campos[5], getRating(csvRecord.get(5)), Field.Store.YES)); // rating 
				doc.add(new NumericDocValuesField(campos[5], convertToLong(csvRecord.get(5))));
			}
			
			if(!csvRecord.get(6).isEmpty()){
				doc.add(new TextField(campos[6], getDescription(csvRecord.get(6)), Field.Store.YES)); //description 
			}

			if(!csvRecord.get(7).isEmpty() && !csvRecord.get(7).equals("[]")){
				String cadena = getStars(csvRecord.get(7)); //separo los nombres

				String[] parts = cadena.split("Stars"); //divido
				String director = parts[0].trim(); 
				String stars = parts[0].trim();

				if (parts.length > 1){
					stars = parts[1].trim();
					stars = stars.replace(":", "").trim();
					stars = stars.replaceAll("\\s{2,}", " ").trim(); //reemplaza 2 o mas espacios en blanco por 1

					if(!stars.equals("[]")){
						doc.add(new TextField(campos[7], stars.trim(), Field.Store.YES)); //stars
					}

					if(!director.equals("[]")){
						doc.add(new TextField("director", director.trim(), Field.Store.YES)); //director
					}
					
					
				}else{
					doc.add(new TextField(campos[7], stars.trim(), Field.Store.YES));
				}
				
				if(!director.equals("[]")){
					doc.add(new FacetField(campos[7], stars));
					doc.add(new FacetField("director", director)); 
				}
				
				//doc.add(new TextField("TODO", todo, Field.Store.YES));
			}
			
			if(!csvRecord.get(8).isEmpty()){
				doc.add(new StringField(campos[8], getVotes(csvRecord.get(8)), Field.Store.YES)); //votes
				doc.add(new NumericDocValuesField(campos[8], Long.valueOf(getVotes(csvRecord.get(8))))); //votes
			}

			doc.add(new TextField("TODO", todo, Field.Store.YES));
			//writer.addDocument(doc); //lo escribimos en el documento
			writer.addDocument(fconfig.build(taxoWriter, doc)); //incluimos las facetas en el doc	
		}
		
	}

	public void close(){
		try{
			writer.commit();
			writer.close();
			taxoWriter.close();
		} catch (IOException e) {
			System.out.println("Error closing the index.");
		}
	}

	public static void main (String[] args) {
		System.out.println("Creando índice...\n");
		// Analizador a utilizar
		Analyzer analyzer = new StandardAnalyzer();
		// Medida de Similitud (modelo de recuperacion) por defecto BM25
		Similarity similarity = new ClassicSimilarity();
		// Llamados al constructor con los parametros
		IndiceSimple baseline = new IndiceSimple(analyzer, similarity);

		// Creamos el indice
		try{
			baseline.configurarIndice();
		}catch(IOException ex){
			System.out.println("Error configuring the index");
		}

		// Insertamos los documentos
		try{
			baseline.indexarDocumentos();
		}catch(IOException ex){
			System.out.println("Error indexing the index");
		}

		// Cerramos el indice
		baseline.close();
		System.out.println("Indice creado.\n\n");
	}

}




