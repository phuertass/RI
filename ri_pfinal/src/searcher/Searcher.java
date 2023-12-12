/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package searcher;


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

/**
 *
 * @author anne
 */
public class Searcher {
    //UBICACIONES DE LOS INDICES
    String capunidosPath;
    String capsPath;
    String indexPath;
    //UBICACION FACETAS
    String facetasPath;
    
    IndexSearcher searcher;
    QueryParser parser;
    IndexReader reader;
    TaxonomyReader taxoReader;
    FacetsCollector colectorFacetas;
    FacetsConfig configFacetas;
    TopDocs td2;
String[] vector_facetas;
String[] matriz;
DrillDownQuery ddq;
    
    
    Searcher(String dir, String fdir){
    
        
        try{
        
        
        Directory directorio = FSDirectory.open(Paths.get(dir));
        FSDirectory fdirectorio = FSDirectory.open(Paths.get(fdir));
        reader = DirectoryReader.open(directorio);
        taxoReader = new DirectoryTaxonomyReader(fdirectorio);
        //buscadores
        searcher = new IndexSearcher(reader);
        //facetas
        configFacetas = new FacetsConfig();
        //facets collector 
        colectorFacetas = new FacetsCollector();
        

    }catch (IOException e){
        System.err.println("Error al obtener indice");
        System.exit(-1);
    }
  
    
  }
    
/*public static void main(String[] args) { 
    
    Analyzer analyzer = new StandardAnalyzer();
    Similarity similarity = new ClassicSimilarity();
    
    
}*/


public String indexSearch(String consulta, String type,ArrayList<String> campos,Directory dir,FSDirectory fdir){
    Analyzer analyzer = new StandardAnalyzer();
    Similarity similarity = new ClassicSimilarity();
    
    try{
       //DIRECTORIO DE LOS INDICES
        
        Directory indexPath= dir;
        
        //DIRECTORIO DE FACETAS
        FSDirectory taxoDir = fdir;
       
             
      
        IndexReader reader = DirectoryReader.open(indexPath);
        configFacetas = new FacetsConfig();
        searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
        taxoReader = new DirectoryTaxonomyReader(taxoDir);

        TopDocs documentos = null;
        colectorFacetas = new FacetsCollector();
        //Aquí se recoge el tipo de consulta por interfaz
        
        switch(type){
            case "Consulta Generica":
                documentos = ConsultaGenerica(campos.get(0), analyzer, consulta);
                break;
            case "Consulta Booleana":
                documentos = ConsultaBooleana(campos.get(0), analyzer, consulta);
                break;
            case "Consulta Frases":
                documentos = ConsultaFrase(campos.get(0), analyzer, consulta);
                break;
            case "Consulta Multiple":
                documentos = ConsultaMultiple(analyzer,campos, consulta);
                break;
            default:
               
        }

        String resultado = mostrarDocumentos(documentos);
        reader.close();

        if(!resultado.isEmpty()){
            return resultado;
        }else{
            return("No hay ningún resultado");
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
        
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
        
    QueryParser parser = new QueryParser(campo, analyzer);

    TopDocs docs = null; 
    TopDocs tfacet = null; 
    Query query = null;
    System.out.println(consulta);

    try{
        query = parser.parse(consulta);
       System.out.println(query);
        docs = searcher.search(query, 10);
           
            tfacet = FacetsCollector.search(searcher, query, 10, colectorFacetas);
        }catch (org.apache.lucene.queryparser.classic.ParseException e){
            System.out.println("¡¡¡ERROR CONSULTA GENÉRICA!!!");
        }

        MostrarFacetas(docs, query);

        return docs;
    }

    public  TopDocs ConsultaBooleana(String campo, Analyzer analyzer, String consulta) throws IOException{
       
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
        
        QueryParser parser = new QueryParser(campo, analyzer);

        
        String[] todas = consulta.split(" ");

        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();
        Query q1 = null;

        for (String toda : todas) {
            try {
                q1 = parser.parse(toda);
            }catch(org.apache.lucene.queryparser.classic.ParseException e){
                System.out.println("¡¡¡ERROR CONSULTA BOOLEANA!!!");
            }
            BooleanClause bc1 = new BooleanClause(q1, BooleanClause.Occur.MUST);
            bqbuilder.add(bc1);
        }

        BooleanQuery bq = bqbuilder.build();
      
        //mostramos documentos
        TopDocs tdocs = searcher.search(bq, 10);
        //facetas
        TopDocs tfacet = FacetsCollector.search(searcher, bq, 10, colectorFacetas);
        
        MostrarFacetas(tdocs, q1);
        
        return tdocs;
    }

    public  TopDocs ConsultaFrase(String campo, Analyzer analyzer, String consulta) throws IOException{
        PhraseQuery.Builder pqbuilder = new PhraseQuery.Builder();
       
        consulta=consulta.toLowerCase();
       
        String[] frase = consulta.split(" ");
        System.out.println(frase);
       
        for(int i=0; i<frase.length; i++){ 
            pqbuilder.add(new Term(campo, frase[i]), i); 
        }

        PhraseQuery pq = pqbuilder.build();
        //mostramos documentos
        TopDocs tdocs = searcher.search(pq,10);

        //facetas
        TopDocs tfacet = FacetsCollector.search(searcher, pq, 10, colectorFacetas);
        MostrarFacetas(tdocs, pq);

        return tdocs;
    }

    public String mostrarDocumentos(TopDocs docs) throws IOException{
        ScoreDoc[] hits = docs.scoreDocs;
        String texto = new String();
   
        texto += "\nHay " + docs.totalHits +  " documentos encontrados.\n";
        texto += "\n";

     
        for (int i=0;i<hits.length;i++) {
            int docId=hits[i].doc;
            
            Document doc = searcher.doc(hits[i].doc);
            
            int documentonum=i+1;
            String id_episode = doc.get("episode_id");
            String title = doc.get("title");
           float puntuacion= hits[i].score;
           String spoken_words=doc.get("spoken_words");
            texto += "****************************************************************\n";
            
            if(documentonum != 0){
                texto += "* Documento #: " + id_episode + "\n";
            }
            if(id_episode != null && id_episode != ""){
                texto += "* ID: " + id_episode + "\n";
            }
            if(title != null && title != ""){
                texto += "* Título: " + title + "\n";
            }
            
            if(puntuacion != 0){
                texto += "* Puntuación: " + puntuacion + "\n";
            }
            
            /*if(spoken_words.length()>100){
                spoken_words = spoken_words.substring(0, 100);
                spoken_words += "...";
            }*/
            
        }
        
        return texto;

    }
    public TopDocs ConsultaMultiple(Analyzer analyzer, ArrayList<String> campos, String consulta) throws IOException{
        int total=campos.size();
        QueryParser[] parser=new QueryParser[total];
        String[] values=new String[total];

        for(int i=0; i<total; i++){
            parser[i]=new QueryParser(campos.get(i), analyzer);
            values[i] = parser[i].toString();
        }

        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();

        Query[] q=new Query[total];
        try{
            for(int i=0; i<total; i++){
                q[i]=parser[i].parse(values[i]);
                System.out.println(q[i]);
            }

        }catch (org.apache.lucene.queryparser.classic.ParseException e){
            System.out.println("---Error en la consulta---");
        }

        BooleanClause[] bc=new BooleanClause[total];
        for(int i=0; i<total; i++){
            bc[i]=new BooleanClause(q[i], BooleanClause.Occur.MUST); //AND LÓGICO
            bqbuilder.add(bc[i]);
        }

        BooleanQuery bq = bqbuilder.build();
        TopDocs tdocs = searcher.search(bq, 10);

         MostrarFacetas(tdocs,bq);
        

        


        return tdocs;

    }
    public String[] getMatriz(){
	return matriz;	
    }
    
    public void MostrarFacetas(TopDocs tdc,  Query q1){
        ddq = new DrillDownQuery(configFacetas,q1);
        vector_facetas = new String[4*3];
        matriz = new String[4*3];
        int j=0;

        try{
            FacetsCollector fc1 = new FacetsCollector();
            td2 = FacetsCollector.search(searcher,q1,10,fc1);
            Facets fcCount2 = new FastTaxonomyFacetCounts(taxoReader,configFacetas,fc1);
            List<FacetResult> allDims = fcCount2.getAllDims(100);
            
            
            for( FacetResult fr : allDims){
                int cont=0;
                
                for(LabelAndValue lv : fr.labelValues){
                    if(cont < 5){ 
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

            
            Directory dirCapsUnidos = FSDirectory.open(Paths.get(capunidosPath));
            Directory dirCaps = FSDirectory.open(Paths.get(capsPath));
            
            FSDirectory taxoDir = FSDirectory.open(Paths.get(facetasPath));
            
            IndexReader reader = DirectoryReader.open(dirCapsUnidos);
            configFacetas = new FacetsConfig();
            //Creamos el objeto IndexSearcher usando el IndexReader anterior
            searcher = new IndexSearcher(reader);
            taxoReader = new DirectoryTaxonomyReader(taxoDir);
            searcher.setSimilarity(similarity);
            //TopDocs documentos = null;
            colectorFacetas = new FacetsCollector();

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
            Facets facets2 = new FastTaxonomyFacetCounts(taxoReader,configFacetas,fc1);
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




