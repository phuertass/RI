import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;

import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.index.DirectoryReader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;


//ANNE SERRANO ANDRADES
//PABLO HUERTAS ARROYO

public class IndiceSearcher {

private final String indexPath;
private  IndexSearcher searcher;         
private  QueryParser parser;             
private  IndexReader reader;

private TaxonomyReader taxoReader;
private FacetsCollector fc;
private FacetsConfig fconfig;
private final String facetPath;


IndiceSearcher(String indexP, String facetP){
	indexPath = indexP;
    facetPath = facetP;

	try{
		//Obtenemos directorios
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Directory taxoDir = FSDirectory.open(Paths.get(facetPath));
        //Abrimos directorios
        reader = DirectoryReader.open(dir);
        taxoReader = new DirectoryTaxonomyReader(taxoDir);
        //buscadores
        searcher = new IndexSearcher(reader);
        //facets
        fconfig = new FacetsConfig();
        // facets collector
        fc = new FacetsCollector();

	} catch (IOException e){
		System.err.println("Error al obtener indice");
        System.exit(-1);
	}
}

public static void main(String[] args) { 
	
    if (args.length < 1) {
        System.out.println("Se requiere indexPath");
        return;
    }

    String indexPath = args[0];
    String facetPath = "indexCS";


    if(indexPath.contains("indexCU")){
        facetPath = "indexCU";
    }

    IndiceSearcher indice = new IndiceSearcher(indexPath, facetPath);

//    Analyzer analyzer = new StandardAnalyzer();
//    Similarity similarity = new ClassicSimilarity();

    Analyzer defaultAnalyzer = new WhitespaceAnalyzer();

    Map<String, Analyzer> analyzerPerField = new HashMap<>();
    analyzerPerField.put("spoken_words", new EnglishAnalyzer());
    analyzerPerField.put("title", new EnglishAnalyzer());
    analyzerPerField.put("raw_character_text", new EnglishAnalyzer());
    analyzerPerField.put("raw_location_text", new EnglishAnalyzer());
    analyzerPerField.put("raw_text", new EnglishAnalyzer());

    PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, analyzerPerField);

    Similarity similarity = new ClassicSimilarity();

    
    indice.indexSearch(analyzer, similarity); 
}


public  void indexSearch(Analyzer analyzer, Similarity similarity){
    Directory dir = null;
    
    try{
       //Directorio donde se encuentra el índice
        dir = FSDirectory.open(Paths.get(indexPath));
        //Directorio donde estan las facetas
        FSDirectory taxoDir = FSDirectory.open(Paths.get(facetPath));

        System.out.println("Directorio del índice: " + indexPath);
        System.out.println("Directorio de las facetas: " + facetPath);

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

        while(salir.equals("si") ){
        	System.out.println("\n");
            System.out.println("**********************************************************\n");
            System.out.println("Introduzca el campo sobre el que realizar la busqueda:\n");
            System.out.println("(0) Sobre todos los campos\n(1) Busqueda por un solo campo\n(2) Busqueda por varios campos\n(3) Consulta doble(para interfaz)\n");
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
                // UN SOLO CAMPO 
                case 1:
                	System.out.println("**********************************************************\n");
                	System.out.println("Introduzca el campo por el que desea buscar");
                	String line = new String();
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
				    line = in.readLine();

				    
				    documentos = ConsultaGenerica(line, analyzer);
            		break;

            	//--------------------------------------------------------------------------------------------------	
            	//VARIOS CAMPOS
				case 2:
                    System.out.println("**********************************************************\n");
                    System.out.println("Introduzca los campos por los que desea buscar");
                    BufferedReader in2 = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
                    String line2 = in2.readLine();
                    String[] campos = line2.split(" ");
                    for(int i=0; i<campos.length; i++)  System.out.println(campos[i]);
                        documentos = ConsultaMultiple(analyzer, campos);
                    
                    break;

                //--------------------------------------------------------------------------------------------------    
                //SALIR
                case 3:
                    in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

                    String campoConsulta1, valorConsulta1, campoConsulta2, valorConsulta2;

                    System.out.print("Ingrese el campo para la primera consulta: (CAPITULOS UNIDOS) ");
                    campoConsulta1 = in.readLine();

                    System.out.print("Ingrese el valor para la primera consulta: ");
                    valorConsulta1 = in.readLine();

                    System.out.print("Ingrese el campo para la segunda consulta: (CAPITULOS SIMPLES)");
                    campoConsulta2 = in.readLine();

                    System.out.print("Ingrese el valor para la segunda consulta: ");
                    valorConsulta2 = in.readLine();

                    // Realizar las consultas
                    consultarIndice("indexCU", "facetsCU", campoConsulta1, valorConsulta1, analyzer, similarity);
                    consultarIndice("indexCS", "facetsCS", campoConsulta2, valorConsulta2, analyzer, similarity);
                    return;

				//--------------------------------------------------------------------------------------------------	
				//CUALQUIER OTRA OPCION 
                default:
                    System.out.println("Error: Opcion no valida");
                    break;
        	}

        		// DOCUMENTOS RELEVANTES
                ScoreDoc[] hits = documentos.scoreDocs;

                if( hits.length >0){
                    System.out.println("¿Desea mostrar los 10 documentos más relevantes ? ");
                    mostrar = (new Scanner(System.in)).nextLine().toLowerCase();
                     //System.out.println(mostrar); 
                    System.out.println("\n"); 

                    
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
        TopDocs ft  = FacetsCollector.search(searcher, bq, 10, fc);
        System.out.print(" ¿Desea mostrar las facetas? si/no: ");
        String res = in.readLine();
        if(res.equals("si")){
            MostrarFacetas(q1);
        }
        
        return tdocs;
    }
	public TopDocs ConsultaGenerica(String campo, Analyzer analyzer) throws IOException {
		//creamos entrada y se redijire a buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
      
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
            QueryParser parser = new QueryParser(campo, analyzer);
            query = parser.parse(line);
            docs = searcher.search(query, 10);
            //para facetas
            tf = FacetsCollector.search(searcher, query, 10, fc);

        }catch (org.apache.lucene.queryparser.classic.ParseException e){
            System.out.println("---Error en la consulta---");
        }

        //mostramos documentos encontrados
        long totalHits = docs.totalHits.value;
        System.out.println("\n");
        System.out.println("Hay: " + totalHits + " documentos ");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");

        System.out.print("¿Desea mostrar las facetas? si/no: ");
        String res = in.readLine();

        if(res.equals("si")){
            MostrarFacetas(query);
        }

        return docs;
    }

    public TopDocs ConsultaFrase(String campo, Analyzer analyzer) throws IOException{
        PhraseQuery.Builder pqbuilder = new PhraseQuery.Builder();

        System.out.println("Introduzca la frase que quiere buscar (se mostraran las peliculas/series que tengan la frase escrita en ese mismo orden): ");
        Scanner sc = new Scanner(System.in);
        String consulta = sc.nextLine();
        //convertimos a minuscula
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
        TopDocs ft = FacetsCollector.search(searcher, pq, 10, fc);
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
            System.out.println("---Error en la consulta---");
        }

        BooleanClause[] bc=new BooleanClause[total];
        for(int i=0; i<total; i++){
            bc[i]=new BooleanClause(q[i], BooleanClause.Occur.MUST); //AND LÓGICO
            bqbuilder.add(bc[i]);
        }

        BooleanQuery bq = bqbuilder.build();
        TopDocs tdocs = searcher.search(bq, 10);

        System.out.println("\n");
        System.out.println("Hay: " + tdocs.totalHits + " documentos ");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");

        //facetas
        TopDocs ft = FacetsCollector.search(searcher, bq, 10, fc);
        System.out.print(" ¿Desea mostrar las facetas? si/no: ");
        Scanner sc = new Scanner(System.in);
        String res = sc.nextLine();
        if(res.equals("si")){
            MostrarFacetas(bq);
        }


        return tdocs;

    }

    public void mostrarDocumentos(TopDocs docs) throws IOException{
    	ScoreDoc[] hits = docs.scoreDocs;

        for(int j=0; j<hits.length; j++){
            int docId = hits[j].doc;
        	//buscamos en el documento
        	Document doc = searcher.doc(hits[j].doc);

            System.out.println("Documento #" + (j + 1));
        System.out.println("ID: " + doc.get("episode_id"));
        System.out.println("Título: " + doc.get("title")); 
        System.out.println("Puntuación: " + hits[j].score);
        
        //System.out.println("Contenido: " + doc.toString()); // Arreglar
        System.out.println("\n---------------------------\n");
        }

    }


    public void MostrarFacetas(Query query){
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
        String [] vector_facetas =  new String[4*5];
        DrillDownQuery ddq = new DrillDownQuery(fconfig, query); //
        //contador
        int i=0;

        System.out.println("\n\nFiltramos query( " + ddq.toString()+ " )"); //

        try{
            FacetsCollector fc = new FacetsCollector();
            TopDocs tdc = FacetsCollector.search(searcher, query, 10, fc);
            System.out.println("Total hits = "+ tdc.totalHits);
            Facets fcCount2 = new FastTaxonomyFacetCounts(taxoReader, fconfig, fc); //num ocurrencias de cada una
            List<FacetResult> todasDims = fcCount2.getAllDims(100);

            System.out.println("\nCategorias totales " + todasDims.size()+ " \n");

            //Para cada categoria mostramos el valor de la etiqueta y num ocurrencias
            for( FacetResult fr : todasDims ){
                System.out.println("\nDimension: " + fr.dim);
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
            System.out.println(e.getMessage());
        }
    }

    public TopDocs FiltrarPorFacetas(Query query, TopDocs td2, String [] vector_facetas){
        //inicializamos el DrillDownQuery con la consulta realizada
        DrillDownQuery ddq = new DrillDownQuery(fconfig, query);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));


        try{

            System.out.println("\n\nFiltramos query( " + ddq.toString()+ " )");
            FacetsCollector fc1 = new FacetsCollector();
            System.out.println("Total hits = "+ td2.totalHits);

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

            //volvemos a hacer el search con el nuevo ddq que contiene las facetas.
            td2 = FacetsCollector.search(searcher, ddq, 10, fc1);
            Facets fcCount2 = new FastTaxonomyFacetCounts(taxoReader,fconfig,fc1);
            List<FacetResult> allDims = fcCount2.getAllDims(100);

            System.out.println("\nCoincidencias totales = " + td2.totalHits);

        }catch(IOException e){
            System.out.println("Error al filtrar facetas. ");
        }

        return td2;
    }



    public TopDocs consultarIndice(String indexPath, String facetPath, String campo, String valor, Analyzer analyzer, Similarity similarity) throws IOException {
        Directory dir = null;
        DirectoryTaxonomyReader taxoReader = null;
        DirectoryReader reader = null;
        IndexSearcher searcher = null;
        FacetsConfig fconfig = null;

        try {
            // Directorio donde se encuentra el índice
            dir = FSDirectory.open(Paths.get(indexPath));
            // Directorio donde están las facetas
            FSDirectory taxoDir = FSDirectory.open(Paths.get(facetPath));

            System.out.println("Directorio del índice: " + indexPath);
            System.out.println("Directorio de las facetas: " + facetPath);

            // Creamos el objeto IndexSearcher y abrimos para lectura
            reader = DirectoryReader.open(dir);
            fconfig = new FacetsConfig();
            searcher = new IndexSearcher(reader);
            searcher.setSimilarity(similarity);
            taxoReader = new DirectoryTaxonomyReader(taxoDir);

            // Realizar la consulta
            return ConsultaGenericaValor(campo, valor, analyzer, searcher, fconfig);
        } finally {
            // Cerrar los recursos abiertos
            if (reader != null) {
                reader.close();
            }
            if (dir != null) {
                dir.close();
            }
            if (taxoReader != null) {
                taxoReader.close();
            }
        }
    }
    public TopDocs ConsultaGenericaValor(String campo, String valor, Analyzer analyzer, IndexSearcher searcher, FacetsConfig fconfig) throws IOException {
        String line = valor.trim(); // Usamos el valor directamente

        TopDocs docs = null; // Para mostrar los relevantes

        Query query = null;

        try {
            QueryParser parser = new QueryParser(campo, analyzer);
            query = parser.parse(line);
            docs = searcher.search(query, 10);
            // No se hace la búsqueda de facetas aquí según la lógica proporcionada en el código original
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            System.out.println("---Error en la consulta---");
        }

        // Mostrar documentos encontrados
        long totalHits = docs.totalHits.value;
        System.out.println("\n");
        System.out.println("Hay: " + totalHits + " documentos ");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");
        return docs;
    }


}
