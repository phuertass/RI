package com.mycompany.interfazri;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer; 
import org.apache.lucene.document.*;
import org.apache.lucene.queryparser.classic.QueryParser; 
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;

import java.io.*;
import java.util.*;
import java.util.List;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Iterator;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import java.util.Scanner;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.document.Field;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.index.DirectoryReader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;

import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.TaxonomyFacets;
import org.apache.lucene.facet.taxonomy.IntTaxonomyFacets;



public class IndiceSearcherInterfaz {

String indexPath;                //ubicacion del indice
String facetPath;                //ubicacion de las facetas
IndexSearcher searcher;         //busador indices
QueryParser parser;             //Parsear Queries
IndexReader reader;             //Lector de índices
TaxonomyReader taxoReader;      //Lectura de facetas
FacetsCollector fc;        //Colector de facetas.
FacetsConfig fconfig;           //Configuración de facetas
TopDocs td2;
String[] vector_facetas;
String[] matriz;
DrillDownQuery ddq;

IndiceSearcherInterfaz(){
    indexPath = "src/main/java/indice";
    facetPath = "src/main/java/facet";

    try{
        //Obtenemos directorios
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Directory taxoDir = FSDirectory.open(Paths.get(facetPath));
        //Abrimos directorios
        reader = DirectoryReader.open(dir);
        taxoReader = new DirectoryTaxonomyReader(taxoDir);
        //buscadores
        searcher = new IndexSearcher(reader);
        //facetas
        fconfig = new FacetsConfig();
        //facets collector 
        fc = new FacetsCollector();
        td2 = null;
        ddq = new DrillDownQuery(fconfig);

    }catch (IOException e){
        System.err.println("Error al obtener indice");
        System.exit(-1);
    }
}

public static void main(String[] args) { 
    IndiceSearcherInterfaz indice = new IndiceSearcherInterfaz();
    Analyzer analyzer = new StandardAnalyzer();
    Similarity similarity = new ClassicSimilarity();
    String result = indice.indexSearch("2011", "Consulta Booleana"); 
    System.out.println(result); 
 

    String prueba ="0 3";
    String result2 = indice.FiltrarPorFacetas(prueba);
    System.out.println(result2);
}


public String indexSearch(String consulta, String type){
    Analyzer analyzer = new StandardAnalyzer();
    Similarity similarity = new ClassicSimilarity();
    
    try{
       //Directorio donde se encuentra el índice
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        //Directorio donde estan las facetas
        FSDirectory taxoDir = FSDirectory.open(Paths.get(facetPath));
        //Creamos el objeto IndexSearcher y abrimos para lectura
        IndexReader reader = DirectoryReader.open(dir);
        fconfig = new FacetsConfig();
        searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
        taxoReader = new DirectoryTaxonomyReader(taxoDir);

        TopDocs documentos = null;
        fc = new FacetsCollector();

        switch(type){
            case "Consulta Generica":
                documentos = ConsultaGenerica("TODO", analyzer, consulta);
                break;
            case "Consulta Booleana":
                documentos = ConsultaBooleana("TODO", analyzer, consulta);
                break;
            case "Consulta Frases":
                documentos = ConsultaFrase("TODO", analyzer, consulta);
                break;
            default:
                return("Error");
        }

        String resultado = mostrarDocumentos(documentos);
        reader.close();

        if(!resultado.isEmpty()){
            return resultado;
        }else{
            return("No se han encontrado resultados para la consulta");
        }

        }catch(IOException e){
            try{
                reader.close();
            }catch(IOException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return ("indice");
    }
    

    public  TopDocs ConsultaGenerica(String campo, Analyzer analyzer, String consulta) throws IOException {
        //creamos entrada y se redijire a buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
        //parseo consulta
        QueryParser parser = new QueryParser(campo, analyzer);

        TopDocs docs = null; //este para mostrar los relevantes
        TopDocs tf = null; //este para mostrar facetas
        Query query = null;

        try{
            query = parser.parse(consulta);
            docs = searcher.search(query, 10);
            //para facetas
            tf = FacetsCollector.search(searcher, query, 10, fc);
        }catch (org.apache.lucene.queryparser.classic.ParseException e){
            System.out.println("¡¡¡Error en la consulta generica!!!");
        }

        MostrarFacetas(docs, query);

        return docs;
    }

    public  TopDocs ConsultaBooleana(String campo, Analyzer analyzer, String consulta) throws IOException{
        //creamos entrada y se redijire a buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
        //parseo consulta
        QueryParser parser = new QueryParser(campo, analyzer);

        //obtengo la consulta y la separo por los espacios en blanco para poder ver los campos
        String[] todas = consulta.split(" ");

        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();
        Query q1 = null;

        for (String toda : todas) {
            try {
                q1 = parser.parse(toda);
            }catch(org.apache.lucene.queryparser.classic.ParseException e){
                System.out.println("¡¡¡Error en la consulta booleana!!!");
            }
            BooleanClause bc1 = new BooleanClause(q1, BooleanClause.Occur.MUST);
            bqbuilder.add(bc1);
        }

        BooleanQuery bq = bqbuilder.build();
      
        //mostramos documentos
        TopDocs tdocs = searcher.search(bq, 10);
        //facetas
        TopDocs ft = FacetsCollector.search(searcher, bq, 10, fc);
        
        MostrarFacetas(tdocs, q1);
        
        return tdocs;
    }

    public  TopDocs ConsultaFrase(String campo, Analyzer analyzer, String consulta) throws IOException{
        PhraseQuery.Builder pqbuilder = new PhraseQuery.Builder();
        //convertimos todo a minuscula
        consulta=consulta.toLowerCase();
        //separamos
        String[] frase = consulta.split(" ");

        //vemos cuales son sus campos
        for(int i=0; i<frase.length; i++){ //recorro la frase
            pqbuilder.add(new Term(campo, frase[i]), i); //busco por campos
        }

        PhraseQuery pq = pqbuilder.build();
        //mostramos documentos
        TopDocs tdocs = searcher.search(pq,10);

        //facetas
        TopDocs ft = FacetsCollector.search(searcher, pq, 10, fc);
        MostrarFacetas(tdocs, pq);

        return tdocs;
    }

    public String mostrarDocumentos(TopDocs docs) throws IOException{
        ScoreDoc[] hits = docs.scoreDocs;
        String texto = new String();
   
        texto += "\nHay " + docs.totalHits +  " documentos encontrados.\n";
        texto += "\n";

        for (ScoreDoc hit : hits) {
            //buscamos en el documento
            Document doc = searcher.doc(hit.doc);
            String title = doc.get("title");
            String year = doc.get("year");
            String certificate = doc.get("certificate");
            String duration = doc.get("duration");
            String genre = doc.get("genre");
            String rating = doc.get("rating");
            String description = doc.get("description");
            String stars = doc.get("stars");
            String director = doc.get("director");
            String votes = doc.get("votes");
            texto += "****************************************************************\n";
            
            if(title != null && title != ""){
                texto += "* Title: " + title + "\n";
            }
            if(year != null && year != ""){
                texto += "* Year: " + year + "\n";
            }
            if(certificate != null && certificate != ""){ //preguntar profe porque no se me quitan los " " de esto
                texto += "* Certificate: " + certificate + "\n";
            }
            if(duration != null && duration != ""){
                texto += "* Duration: " + duration + "\n";
            }
            if(genre != null && genre != ""){
                texto += "* Genre: " + genre + "\n";
            }
            if(rating != null && rating != ""){
                texto += "* Rating: " + rating + "\n";
            }
            //para que no muestre toda la descripcion
            if(description.length()>100){
                description = description.substring(0, 100);
                description += "...";
            }
            if(description != null && description != ""){
                texto += "* Description: " + description + "\n";
            }
            if(stars != null && !stars.equals("[]")){
                texto += "* Stars: " + stars + "\n";
            }
            if(director != null && !director.equals("[]")){
                texto += "* Director: " + director + "\n";
            }
            if(votes != null && votes != ""){
                texto += "* Votes: " + votes+ "\n";
            }
        }
        
        return texto;

    }
    
    public String[] getMatriz(){
	return matriz;	
    }
    
    public void MostrarFacetas(TopDocs tdc,  Query q1){
        ddq = new DrillDownQuery(fconfig,q1);
        vector_facetas = new String[4*3];
        matriz = new String[4*3];
        int j=0;

        try{
            FacetsCollector fc1 = new FacetsCollector();
            td2 = FacetsCollector.search(searcher,ddq,10,fc1);
            Facets fcCount2 = new FastTaxonomyFacetCounts(taxoReader,fconfig,fc1);
            List<FacetResult> allDims = fcCount2.getAllDims(100);
            
            //Para cada faceta Director/Genero/Origen mostramos el valor de la etiqueta y su numero de ocurrencias
            for( FacetResult fr : allDims){
                int cont=0;
                //Almacenamos cada etiqueta en un vector
                for(LabelAndValue lv : fr.labelValues){
                    if(cont < 3){ //MOSTRAMOS SOLO 3 CATEGORIAS
                        vector_facetas[j] = fr.dim+ "::#->"+ lv.label + "";
                        matriz[j] = lv.label + " ("+ lv.value+")";
                    }else{
                        break;
                    }
                    cont++;
                    j++;
                }
            }
        
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public String FiltrarPorFacetas(String filtros){
        //Analyzer analyzer = new StandardAnalyzer();
        Similarity similarity = new ClassicSimilarity();
        DrillDownQuery ddq2 = ddq.clone();
        FacetsCollector fc1 = new FacetsCollector();

        String[] separadas = filtros.split(" ");
        int[] filtradas = new int[separadas.length];
        
        for(int i=0; i<separadas.length; i++){
            filtradas[i]=Integer.parseInt(separadas[i]);
        }

        try{

            //Directorio donde se encuentra el índice
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            FSDirectory taxoDir = FSDirectory.open(Paths.get(facetPath));
            //lo abrimos para lectura
            IndexReader reader = DirectoryReader.open(dir);
            fconfig = new FacetsConfig();
            //Creamos el objeto IndexSearcher usando el IndexReader anterior
            searcher = new IndexSearcher(reader);
            taxoReader = new DirectoryTaxonomyReader(taxoDir);
            searcher.setSimilarity(similarity);
            //TopDocs documentos = null;
            fc = new FacetsCollector();

            for(int i=0;i<filtradas.length;i++){
                //encontramos primera ocurrencia de >
                int iend = vector_facetas[filtradas[i]].indexOf(">");
                //encontramos primera ocurrencia de :
                int istart = vector_facetas[filtradas[i]].indexOf(":");
                String faceta = vector_facetas[filtradas[i]].substring(iend+1,vector_facetas[filtradas[i]].length());
                String categoria = vector_facetas[filtradas[i]].substring(0,istart);
                ddq2.add(categoria,faceta);
            }
            
            td2 = FacetsCollector.search(searcher,ddq2,10,fc1);
            Facets facets2 = new FastTaxonomyFacetCounts(taxoReader,fconfig,fc1);
            List<FacetResult> allDims = facets2.getAllDims(100);
            String resultado = mostrarDocumentos(td2);

            if(!resultado.isEmpty()){
                return resultado;
            }else{
                return "No hay resultados para esta consulta...";
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }

        return "Cerrado filtrar facetas";
    }

}



