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



public class IndiceSearcher {

private String indexPath; 				//ubicacion del indice
private  String facetPath; 				//ubicacion de las facetas
private  IndexSearcher searcher;         //busador indices
private  QueryParser parser;             //Parsear Queries
private  IndexReader reader;             //Lector de índices
private  TaxonomyReader taxoReader;      //Lectura de facetas
private  FacetsCollector fc;        //Colector de facetas.
private  FacetsConfig fconfig;           //Configuración de facetas


IndiceSearcher(){
	indexPath = "./indice";
    facetPath = "./facet";

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

	} catch (IOException e){
		System.err.println("Error al obtener indice");
        System.exit(-1);
	}
}

public static void main(String[] args) { 
	IndiceSearcher indice = new IndiceSearcher();
	Analyzer analyzer = new StandardAnalyzer();
    Similarity similarity = new ClassicSimilarity();
	indice.indexSearch(analyzer, similarity); 
}


public  void indexSearch(Analyzer analyzer, Similarity similarity){
    Directory dir = null;
    FSDirectory taxoDir = null;
    
    try{
       //Directorio donde se encuentra el índice
        dir = FSDirectory.open(Paths.get(indexPath));
        //Directorio donde estan las facetas
        taxoDir = FSDirectory.open(Paths.get(facetPath));
        //Creamos el objeto IndexSearcher y abrimos para lectura
        reader = DirectoryReader.open(dir);
        fconfig = new FacetsConfig();
        searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
        taxoReader = new DirectoryTaxonomyReader(taxoDir);

        //opciones
        String mostrar = new String("no");
        String salir = new String("si");
        TopDocs documentos = null;
        fc = new FacetsCollector();

        while(salir.equals("si") ){
        	System.out.println("\n");
        	System.out.println("**********************************************************\n");
            System.out.println("Introduzca el campo sobre el que realizar la busqueda:\n");
            System.out.println("(0) Sobre todos los campos\n(1) Busqueda por un solo campo\n(2) Busqueda por varios campos\n(3) Salir\n");
            Scanner sc = new Scanner(System.in);
            int valor = sc.nextInt();

            switch(valor){
            	//--------------------------------------------------------------------------------------------------
            	//SOBRE TODOS LOS CAMPOS
                case 0:
                	System.out.println("**********************************************************\n");
                	System.out.println("Introduzca el tipo de consulta que desea realizar:\n");
                	System.out.println("(0) Consulta Generica\n(1) Boolean Query\n(2) Por frases\n");

                    sc = new Scanner(System.in);
                    valor = sc.nextInt();

                    switch(valor){
                        case 0:
                        	documentos = ConsultaGenerica("TODO", analyzer);
                            break;
                        case 1:
                            documentos = ConsultaBooleana("TODO", analyzer);
                            break;
                        case 2:
                        	documentos = ConsultaFrase("TODO", analyzer);
                        	break;
                        default:
                            System.out.println("Error");
                            break;
                    }

                    break;
 				
 				//--------------------------------------------------------------------------------------------------
                //INTRODUCIR CONSULTA POR UN SOLO CAMPO MANUALMENTE    
                case 1:
                	System.out.println("**********************************************************\n");
                	System.out.println("Introduzca el campo por el que desea buscar (ejemplo: genre/year/title): ");
                	String line = new String();
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
				    line = in.readLine();

				    //hacemos consulta generica solo porque solo podemos buscar por un campo
				    documentos = ConsultaGenerica(line, analyzer);
            		break;

            	//--------------------------------------------------------------------------------------------------	
            	//VARIOS CAMPOS
				case 2:
                    System.out.println("**********************************************************\n");
                    System.out.println("Introduzca los campos por los que desea buscar (ejemplo: genre year title): ");
                    BufferedReader in2 = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
                    String line2 = in2.readLine();
                    String[] campos = line2.split(" ");
                    for(int i=0; i<campos.length; i++)  System.out.println(campos[i]);
                        documentos = ConsultaMultiple(analyzer, campos);
                    
                    break;

                //--------------------------------------------------------------------------------------------------    
                //SALIR
                case 3:
                    System.out.println("Busqueda cerrada.\n");
                    return;

				//--------------------------------------------------------------------------------------------------	
				//CUALQUIER OTRA OPCION QUE NO SEA DE LAS QUE DA EL MENU
                default:
                    System.out.println("Error: Opcion no valida");
                    break;
        	}

        		//VEMOS DOCUMENTOS RELEVANTES
                ScoreDoc[] hits = documentos.scoreDocs;

                if( hits.length >0){
                    System.out.println("¿Desea mostrar los documentos relacionados? ");
                    mostrar = (new Scanner(System.in)).nextLine().toLowerCase();
                    System.out.println("\n"); 

                    //si es si, mostramos documentos
                    if(mostrar.equals("si")){
                        mostrarDocumentos(documentos);
                    }

                }else{
                    System.out.println("No se han encontrado resultados...");
                }

                System.out.println("\n\n");    
                System.out.println("---------------------------------------------------------------\n");
				System.out.println("\n");
                System.out.println("¿Desea realizar otra consulta?\n");
                System.out.println("Para continuar pulse 'Si'.\nPara salir pulse cualquier letra.");
                salir = (new Scanner(System.in)).nextLine().toLowerCase();

            }

            reader.close();

        }catch(IOException e){
            try{
                reader.close();
            } catch(IOException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
	}

	public TopDocs ConsultaGenerica(String campo, Analyzer analyzer) throws IOException {
		//creamos entrada y se redijire a buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
        //parseo consulta
        QueryParser parser = new QueryParser(campo, analyzer);

        String line;
        do{
        	System.out.println("***********************************************\n");
        	System.out.println("Introduzca consulta: \n");
        	//leo la linea
        	line = in.readLine(); 
        }while(line == null || line.length() <= 0); //hasta que line sea null o 0
    	
    	//quitamos espacios en blancos al principio y al final
        line = line.trim();

        TopDocs docs = null; //este para mostrar los relevantes
        TopDocs tf = null; //este para mostrar facetas
        Query query = null;

        try{
            query = parser.parse(line);
            docs = searcher.search(query, 10);
            //para facetas
            tf = FacetsCollector.search(searcher, query, 10, fc);
        }catch (org.apache.lucene.queryparser.classic.ParseException e){
            System.out.println("¡¡¡Error en la consulta generica!!!");
        }

        //mostramos documentos encontrados
        long totalHits = docs.totalHits.value;
        System.out.println("\n");
        System.out.println("Hay: " + totalHits + " documentos encontrados");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");

        System.out.print("¿Desea mostrar las facetas? si/no: ");
        String res = in.readLine();

        if(res.equals("si")){
          MostrarFacetas(query);
        }

        return docs;
    }

    public TopDocs ConsultaBooleana(String campo, Analyzer analyzer) throws IOException{
    	//creamos entrada y se redijire a buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
        //parseo consulta
        QueryParser parser = new QueryParser(campo, analyzer);

       	System.out.println("***********************************************\n");
       	System.out.println("Introduzca consulta: \n");

       	//obtengo la consulta y la separo por los espacios en blanco para poder ver los campos
        Scanner sc1 = new Scanner(System.in);
       	String frase = sc1.nextLine();
       	String[] todas = frase.split(" ");

        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();

        Query q1 = null;

        for(int i=0; i<todas.length; i++){
            try{
                q1 = parser.parse(todas[i]);
            }catch(org.apache.lucene.queryparser.classic.ParseException e){
            	System.out.println("¡¡¡Error en la consulta booleana!!!");
            }

            BooleanClause bc1 = new BooleanClause(q1, BooleanClause.Occur.MUST);
            bqbuilder.add(bc1);
        }

        BooleanQuery bq = bqbuilder.build();
      
        //mostramos documentos
        TopDocs tdocs = searcher.search(bq, 10);
        long totalHits = tdocs.totalHits.value;
        System.out.println("\n");
        System.out.println("Hay: " + totalHits + " documentos encontrados");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");

        //facetas
        TopDocs ft = null;
        ft = FacetsCollector.search(searcher, bq, 10, fc);

        System.out.print(" ¿Desea mostrar las facetas? si/no: ");
        String res = in.readLine();

        if(res.equals("si")){
          MostrarFacetas(q1);
        }
        
        return tdocs;
    }

    public TopDocs ConsultaFrase(String campo, Analyzer analyzer) throws IOException{
        PhraseQuery.Builder pqbuilder = new PhraseQuery.Builder();

        System.out.println("Introduzca la frase que quiere buscar (se mostraran las peliculas/series que tengan la frase escrita en ese mismo orden): ");
        Scanner sc = new Scanner(System.in);
        String consulta = sc.nextLine();
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

        long totalHits = tdocs.totalHits.value;
        System.out.println("\n");
        System.out.println("Hay: " + totalHits + " documentos encontrados");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");

        //facetas
        TopDocs ft = null;
        ft = FacetsCollector.search(searcher, pq, 10, fc);
        System.out.print(" ¿Desea mostrar las facetas? si/no: ");
        
        String res = sc.nextLine();
        if(res.equals("si")){
          MostrarFacetas(pq);
        }

        return tdocs;
    }

    public TopDocs ConsultaMultiple(Analyzer analyzer, String[] campos) throws IOException{
        int total=campos.length;
        QueryParser[] parser=new QueryParser[total];
        String[] values=new String[total];

        for(int i=0; i<total; i++){
            parser[i]=new QueryParser(campos[i], analyzer);
            System.out.println("Introduzca el " + campos[i] + ": ");
            Scanner sc1 = new Scanner(System.in);
            values[i] = sc1.nextLine();
        }

        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();

        Query[] q=new Query[total];
        try{
            for(int i=0; i<total; i++){
                q[i]=parser[i].parse(values[i]);
            }

        }catch (org.apache.lucene.queryparser.classic.ParseException e){
            System.out.println("Error en cadena consulta. ");
        }

        BooleanClause[] bc=new BooleanClause[total];
        for(int i=0; i<total; i++){
            bc[i]=new BooleanClause(q[i], BooleanClause.Occur.MUST);
            bqbuilder.add(bc[i]);
        }

        BooleanQuery bq = bqbuilder.build();
        TopDocs tdocs = searcher.search(bq, 10);

        System.out.println("\n");
        System.out.println("Hay: " + tdocs.totalHits + " documentos encontrados");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");

        return tdocs;

    }

    public void mostrarDocumentos(TopDocs docs) throws IOException{
    	ScoreDoc[] hits = docs.scoreDocs;

        for(int j=0; j<hits.length; j++){

        	//buscamos en el documento
        	Document doc = searcher.doc(hits[j].doc);

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


            System.out.println("****************************************************************");
            if(title != null && title != ""){
            	System.out.println("* Title: " + title);
            }
           	
           	if(year != null && year != ""){
	           	System.out.println("* Year: " + year);
	        }

	        if(certificate != null && certificate != ""){ //preguntar profe porque no se me quitan los " " de esto
	            System.out.println("* Certificate: " + certificate);
	        }

            if(duration != null && duration != ""){
            	System.out.println("* Duration: " + duration);
            }

            if(genre != null && genre != ""){
            	System.out.println("* Genre: " + genre);
            }

            if(rating != null && rating != ""){
            	System.out.println("* Rating: " + rating);
            }

            //para que no muestre toda la descripcion 
			if(description.length()>100){
	            description = description.substring(0, 100);
	            description += "...";
	        }

            if(description != null && description != ""){
            	System.out.println("* Description: " + description);
            }

            if(stars != null && !stars.equals("[]")){
            	System.out.println("* Stars: " + stars);
            }

            if(director != null && !director.equals("[]")){
                System.out.println("* Director: " + director);
            }

            if(votes != null && votes != ""){
            	System.out.println("* Votes: " + votes); 
            }
        }

    }


     public void MostrarFacetas(Query query){
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
      String [] vector_facetas =  new String[4*5];

      int i=0;

      try{
          FacetsCollector fc1 = new FacetsCollector();
          TopDocs tdc = FacetsCollector.search(searcher, query, 10, fc1);

          Facets fcCount = new FastTaxonomyFacetCounts(taxoReader, fconfig, fc1);
          List<FacetResult> allDims = fcCount.getAllDims(100);

          System.out.println("\nCategorias totales " + allDims.size()+ " \nMostrando las " + 5 + " (máx) más relevantes de cada una...");

          //Para cada categoria mostramos el valor de la etiqueta y num ocurrencias
          for( FacetResult fr : allDims ){
              System.out.println("\nCategoria: " + fr.dim);
              int cont=0;
              //Almacenamos cada etiqueta en un vector
              for(LabelAndValue lv : fr.labelValues){
                  if(cont < 5){
                      vector_facetas[i]=new String(fr.dim+ ", (#n)-> "+ lv.label + "");
                      System.out.println(lv.label + ", (#n)-> "+ lv.value);
                  }else
                      break;
                  cont++;
                  i++;
              }
          }

        System.out.print("\n¿Quieres filtrar por facetas? si/no: ");
          String res =  in.readLine();

          if(res.equals("si")){
              tdc = FiltrarPorFacetas(query, tdc, vector_facetas);
              mostrarDocumentos(tdc);
          }

      }catch(IOException e){
        System.out.println("Error al mostrar facetas. ");
      }


  }

  public TopDocs FiltrarPorFacetas(Query query, TopDocs td2, String [] vector_facetas){
      //inicializamos el DrillDownQuery con la consulta realizada
      DrillDownQuery ddq = new DrillDownQuery(fconfig, query);
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));


      try{

          System.out.println("\n\nFiltramos query( " + ddq.toString()+ " ) a la que aplicaremos DrillDownQuery");
          System.out.println("Total hits = "+ td2.totalHits);
          System.out.println("\nFiltrar por: ");

          for(int i=0 ; i < 5*4; i++){
            if(vector_facetas[i]!= null)
              System.out.println("\n(" + i +")" + " " + vector_facetas[i]);
          }

          System.out.print("\nIntroduzca los filtros: ");
          String entrada_teclado =  in.readLine();
          String[] filtros = entrada_teclado.split(" ");
          int[] faceta_n = new int[filtros.length];

          for(int i=0; i < faceta_n.length; i++){
              faceta_n[i]= Integer.parseInt(filtros[i]);
              //Busca la primera ocurrencia de ">"
              int ultpos = vector_facetas[faceta_n[i]].indexOf(">");
              //busco la primera de ,
              int istart = vector_facetas[faceta_n[i]].indexOf(",");
              String faceta = vector_facetas[faceta_n[i]].substring(ultpos+1, vector_facetas[faceta_n[i]].length());
              faceta = faceta.trim();
              String categoria = vector_facetas[faceta_n[i]].substring(0,istart);
              categoria = categoria.trim();
              //Realizamos operación AND entre cada dimensión
              ddq.add(categoria, faceta);
          }

          System.out.println("\n\nNueva búsqueda  ( " + ddq.toString() + " )");

          //volvemos a hacer el search con el nuevo ddq que contiene las facetas.
           FacetsCollector fc1 = new FacetsCollector();
           td2 = FacetsCollector.search(searcher, ddq, 10, fc1);

           System.out.println("\nCoincidencias totales = " + td2.totalHits);

       }catch(IOException e){
          System.out.println("Error al filtrar facetas. ");
       }

       return td2;
   }
     
}
