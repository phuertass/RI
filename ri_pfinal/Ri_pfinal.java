/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import java.lang.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
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


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Ri_pfinal {

    private String indexPath;
    private IndexSearcher searcher;
    private QueryParser parser;
    private IndexReader reader;

    private JTextField txtConsulta;
    private JButton botonConsulta;
    private List<String> campos;
    private JComboBox<String> opcionesIndice;
    
    public Ri_pfinal(String indexP, JTextField txtConsulta, JButton botonConsulta, JComboBox<String> opcionesIndice) {
        indexPath = indexP;
        this.txtConsulta = txtConsulta;
        this.botonConsulta = botonConsulta;
        this.opcionesIndice= opcionesIndice;
        //this.campos= campos;

        try {
            //Obtenemos directorios
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            //Abrimos directorios
            reader = DirectoryReader.open(dir);

            //buscadores
            searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            System.err.println("Error al obtener indice");
            System.exit(-1);
        }
        botonConsulta.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                ConsultaGenerica();
                
            }
        });
    }

    public static void main(String[] args) {
        if (args.length < 1) {
        System.out.println("Se requiere indexPath");
        return;
    }

    String indexPath = args[0];

    


    Analyzer analyzer = new StandardAnalyzer();
    Similarity similarity = new ClassicSimilarity();

    
    indice.indexSearch(analyzer, similarity); 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MostrarInterfaz();
            }
        });
    }
 private static void MostrarInterfaz() {
        JFrame frame = new JFrame("Interfaz de Búsqueda");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField txtConsulta = new JTextField(20);
        JButton botonConsulta = new JButton("Buscar");
        JComboBox<String> opcionesIndice = new JComboBox<>(new String[]{"Guiones", "Capítulos"});

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
       
        frame.add(txtConsulta);
        
        frame.add(opcionesIndice);
        frame.add(botonConsulta);

        frame.pack();
        frame.setVisible(true);

        // Crear instancia de Ri_pfinal con la interfaz gráfica
        Ri_pfinal indice = new Ri_pfinal("ruta/del/indice", txtConsulta, botonConsulta, opcionesIndice);
    }
    public void indexSearch(Analyzer analyzer, Similarity similarity) {
        Directory dir = null;

        try {
            //Directorio donde se encuentra el índice
            dir = FSDirectory.open(Paths.get(indexPath));
            System.out.println(indexPath);
            //Creamos el objeto IndexSearcher y abrimos para lectura
            reader = DirectoryReader.open(dir);

            searcher = new IndexSearcher(reader);
            //System.out.println("Número total de documentos en el índice: " + reader.numDocs());
            searcher.setSimilarity(similarity);

            //opciones
            String mostrar = new String("no");
            String salir = new String("si");
            TopDocs documentos = null;

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
                    
                    return;

				//--------------------------------------------------------------------------------------------------	
				//CUALQUIER OTRA OPCION 
                default:
                    System.out.println("Error: Opcion no valida");
                    break;
        	}

                // DOCUMENTOS RELEVANTES
                ScoreDoc[] hits = documentos.scoreDocs;

                if (hits.length > 0) {
                    System.out.println("¿Desea mostrar los 10 documentos más relevantes ? ");
                    mostrar = (new Scanner(System.in)).nextLine().toLowerCase();
                    //System.out.println(mostrar); 
                    System.out.println("\n");

                    if (mostrar.equals("si")) {
                        mostrarDocumentos(documentos);
                    }

                } else {
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

        } catch (IOException e) {
            try {
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public TopDocs ConsultaGenerica(String campo, Analyzer analyzer) throws IOException {
        //creamos entrada y se redijire a buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        String line;
        do {
            System.out.println("***********************************************\n");
            System.out.println("Introduzca consulta: \n");
            //leo la linea
            line = in.readLine();

        } while (line == null || line.length() <= 0); //hasta que line sea null o 0

        //quitamos espacios en blancos al principio y al final
        line = line.trim();

        TopDocs docs = null; //este para mostrar los relevantes
        Query query = null;

        //
        try {

            QueryParser parser = new QueryParser(campo, analyzer);
            query = parser.parse(line);

            docs = searcher.search(query, 10);

        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            System.out.println("---Error en la consulta---");
        }
        //}
        //mostramos documentos encontrados
        long totalHits = docs.totalHits.value;
        System.out.println("\n");
        System.out.println("Hay: " + totalHits + " documentos ");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");
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

        

        return tdocs;
    }

    public TopDocs ConsultaMultiple(Analyzer analyzer, String[] campos) throws IOException {
        int total = campos.length;
        QueryParser[] parser = new QueryParser[total];
        String[] values = new String[total];

        for (int i = 0; i < total; i++) {
            parser[i] = new QueryParser(campos[i], analyzer);
            System.out.println("Introduzca el " + campos[i] + ": ");
            Scanner sc1 = new Scanner(System.in);
            values[i] = sc1.nextLine();
        }

        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();

        Query[] q = new Query[total];
        try {
            for (int i = 0; i < total; i++) {
                q[i] = parser[i].parse(values[i]);
            }

        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            System.out.println("---Error en la consulta---");
        }

        BooleanClause[] bc = new BooleanClause[total];
        for (int i = 0; i < total; i++) {
            bc[i] = new BooleanClause(q[i], BooleanClause.Occur.MUST); //AND LÓGICO
            bqbuilder.add(bc[i]);
        }

        BooleanQuery bq = bqbuilder.build();
        TopDocs tdocs = searcher.search(bq, 10);

        System.out.println("\n");
        System.out.println("Hay: " + tdocs.totalHits + " documentos ");
        System.out.println("***********************************************\n");
        System.out.println("\n\n");

        return tdocs;

    }

    public void mostrarDocumentos(TopDocs docs) throws IOException {
        ScoreDoc[] hits = docs.scoreDocs;

        for (int j = 0; j < hits.length; j++) {
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

}
