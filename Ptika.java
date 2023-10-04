import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.io.IOException;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.langdetect.optimaize.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

import java.io.InputStream;
import java.net.URL;  
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.ContentHandler;
import java.util.ArrayList;
import java.util.*;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.Link;
import org.xml.sax.SAXException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.PrintWriter;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

/**
 *
 * @author Patricia Villalba Crucelaegui
 */

public class Ptika {

// *********************************************************************************************************************************************************************
//                                                          FUNCIONES 
// *********************************************************************************************************************************************************************


  //FUNCION PARA OBTENER EL LENGUAJE DEL FICHERO 
  public static String identifyLanguage(String text) {
    //Identificamos el idioma
    LanguageDetector identifier  = new  OptimaizeLangDetector().loadModels();
    //Devolvemos el idioma
    LanguageResult idioma = identifier.detect(text);

    return idioma.getLanguage();
  }



  //FUNCION PARA OBTENER EL NOMBRE DEL FICHERO 
  public static String getNombre(File f){
    //Obtenemos nombre del fichero
    String nombre;
    nombre = f.getName();

    return nombre;
  }

 
  //FUNCION PARA OBTENER LA CODIFICACION DEL FICHERO 
  public static String getEnconding(String text){
    //Obtenemos los bytes de lectura del texto
    CharsetDetector cd = new CharsetDetector();
    cd.setText(text.getBytes());
    CharsetMatch cm = cd.detect();
    
    return cm.getName();
  }


// *********************************************************************************************************************************************************************
//                                                          MAIN 
// *********************************************************************************************************************************************************************

  public static void main(String[] args) throws Exception{

  if(args.length < 2){
      System.out.println("Parametros incorrectos.");
      System.out.println("Pruebe con: <[ -d | -l | -t ]> <directorio>.");
      System.exit(0);
    }
    
    //Leemos el directorio y los archivos que tiene
    File directory = new File(args[1]);
    String[] ficheros = directory.list();

    //Creamos una instancia de Tika
    Tika tika = new Tika();
    
    //Para que el string no tenga memoria limitada
    tika.setMaxStringLength(1000000000);
    Metadata metadata = new Metadata();
    
    System.out.println("\n");

    if(args.length>1){

     

      if(args[0].toString().equals("-d")){
        //Creamos la tabla
        System.out.format("|        Nombre                                          Tipo de fichero                                    Codificacion                                            Idioma                   |%n");
        System.out.format("|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|%n");

        //Se parsean los ficheros pasados como argumento y se extrae el contenido
        for(String f : ficheros){
          File file = new File("./"+args[1]+"/"+f);
          
          //Parseamos
          tika.parse(file, metadata);
          String contenido = tika.parseToString(file);            

          //Detectamos el MIME tipo del fichero
          String type = tika.detect(file);

          //Extraemos el idioma
          String idioma = identifyLanguage(contenido);

          //Extraemos la codificación
          String cod = getEnconding(contenido);

          //Extraemos nombre
          String nam = getNombre(file);

          //Le damos formato a la tabla
          String formato = "| %-50s         %-50s       %-25s     %-25s %n";
          System.out.format(formato, nam, type, cod, idioma);
          System.out.format("|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|%n");
        }
        
        System.out.format("|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|%n");
        
     

 
      

        } else if(args[0].toString().equals("-l")){
          for(String f : ficheros){
            File file = new File("./"+args[1]+"/"+f);

            //Definimos la estructura de almacenamiento de los links
            LinkContentHandler linkH = new LinkContentHandler();

            //Vemos el contexto en el que trabajamos
            ParseContext parseContext = new ParseContext();
            //Detectamos tipo documento y parseamos
            AutoDetectParser parser = new AutoDetectParser();

            //Devolvemos el contenido en el objeto handler y los metadatos
            InputStream input = new FileInputStream(file);
            parser.parse(input,linkH,metadata,parseContext);

            //Guardamos los links que encontramos en una lista
            List<Link> links=linkH.getLinks();
            System.out.println("Links encontrados en el archivo " +file.getName()+ ":");

            //Si no hay links, pues que devuelva que no encuentra
            if(links.isEmpty()){
              System.out.println("No hay links en este archivo.");
            }else for (Link link:links) { //si hay links, pues los imprime
              System.out.println(link.getUri());
            }

            System.out.println("\n");
          }

      
    


        } else if(args[0].toString().equals("-t")){
          //Primero vemos si tenemos creado el directorio CSV y si no, pues lo creamos
          File csv = new File("CSV");
          if(!csv.exists()){
            csv.mkdir();
          }

          for(String f: ficheros){
            File file = new File("./"+args[1]+"/"+f);

            //Parseamos fichero
            tika.parse(file, metadata);
            String contenido = tika.parseToString(file).toLowerCase();

            //Separamos las palabras
            String[] palabras = contenido.split("\\s+|[\\.|\\,|\\;|\\:|\\¿|\\?|\\¡|\\!|\\(|\\)|\\{|\\}|\\*|\\$|\\^|\\=|\\'|\\'|\\-|\\_|\\–|\\<|\\>|\\#|\\`|\\~|\\€]");

            //Pasamos a ver la ocurrencia de cada palabra. Para ello creamos un arraylist de la clase 
            //frecuenciapalabras donde almacenaremos el num de veces que aparece una palabra
            ArrayList<FrecuenciaPalabras> numfrecuencia = new ArrayList<FrecuenciaPalabras>();

            //Recorremos el array en busca de la palabra y su frecuencia
            for(String palabra : palabras){
              int contador = 0; //inicializamos un contador a 0
              Boolean esta = false;

              while(contador < numfrecuencia.size() && !esta){ //mientras que el contador no sea mayor que el num de palabras y no se encuentre
                if(numfrecuencia.get(contador).palabra.compareTo(palabra)==0){ //si la encontramos por primera vez
                  esta = true;
                }else{ //si no, pues incrementamos el num de veces que se encuentra
                  contador++;
                }
              }

              if(esta){
                numfrecuencia.get(contador).ocurrencias++; //aumentamos el num de veces que aparece
              }else{
                numfrecuencia.add(new FrecuenciaPalabras(palabra, 1)); //anadimos 1 para que no desaparezca
              }
            }

            //Ordenamos en orden decreciente
            Collections.sort(numfrecuencia, new SortbyOcurrencias());

            //quitamos las que no sirven
            for(int i=0; i<numfrecuencia.size(); i++){
              if(numfrecuencia.get(i).palabra.compareTo("")==0){
                numfrecuencia.remove(i);
              }
            }

            //Generamos los CSV para cada documento
            String filename = "./CSV/" + file.getName() + ".csv";
            System.out.println("Directorio CSV creado para el documento " +file.getName());

            // Añadimos la información al fichero CSV
            String d_csv="";
            
            for(int i=0; i<numfrecuencia.size(); i++){
              d_csv+=numfrecuencia.get(i).palabra+";"+numfrecuencia.get(i).ocurrencias+"\n";
            }
            
            //Usamos PrintWriter ya que nos permite imprimir representaciones formateadas de una salida de stream de texto
            PrintWriter writer = new PrintWriter(filename);
            writer.print(d_csv);
            writer.close();
            }
        }
      }
    }

    
}

/**************************************************
javac -cp tika-app-2.4.1.jar Prueba.java
java -cp tika-app-2.4.1.jar:.  Prueba -d documentos
***************************************************/

