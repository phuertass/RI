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


public class PracticaTika{
    public static String identifyLanguage(String texto){
        LanguageDetector identifier = new OptimaizeLangDetector().loadModels();
        LanguageResult idioma = identifier.detect(texto);

        return idioma.getLanguage();
    }
    public static void main (String[] args)throws Exception {
        

        if(args.length < 2){
            System.out.println("Número de parámetros incorrectos");
            System.out.println("USO -> <[-d/-l/-t]> <directorio>");
            return;
        }

        

        //DIRECTORIO
        File directorio = new File(args[1]);
        String[] ficheros = directorio.list();
        System.out.println("Ficheros en el directorio actual: " + ficheros.length);
        System.out.println("Directorio actual: " + directorio.getAbsolutePath());
        System.out.println("Nombre de los ficheros del directorio actual:" + Arrays.toString(ficheros));
        System.out.println("args[0] = " + args[0]);

        Tika tika = new Tika();

        //Para poder leer más caracteres
        tika.setMaxStringLength(1000000000);
        Metadata metadata = new Metadata();
        
        //OPCIONES
        String opcion=args[0];
        if("-d".equals(opcion)){
            crearTabla(ficheros, tika, metadata, args[1]);
        }else if("-l".equals(opcion)){
            extraerEnlaces(ficheros, tika,metadata, args[1]);
        }else if ("-t".equals(opcion)){
            //crearCSV(ficheros, tika);
        }else{
            System.out.println("OPCION NO VÁLIDA, USO -> <[-d/-l/-t]> ");
        }

    }

    //OPCIÓN -D CREAR TABLA
    public static void crearTabla(String[] ficheros, Tika tika, Metadata metadata, String args) throws Exception{
        if (ficheros== null || ficheros.length==0){
            System.out.println("El directorio está vaciío");
        } 

            //Encabezados 
            System.out.println("Nombre\tTipo\tCodificación\tIdioma");
            for(String nombre : ficheros){

                String nameFile = args+ "/"+ nombre;
                System.out.println(nameFile);
                File archivo = new File(nameFile);

                if (!archivo.exists() || !archivo.isFile()) {

                    System.out.println("El archivo " + nombre + " no existe o no es un archivo válido.");
                    continue; // Skip to the next file
                }
                
                    tika.parse(archivo,metadata);

                    //TIPO
                    String tipo = tika.detect(archivo);

                    //IDIOMA
                    String contenido = tika.parseToString(archivo);
                    String idioma= identifyLanguage(contenido);

                    //CODIFICACIÓN
                    
                    String codificacion = metadata.get("Content-Encoding");

                    //IMPRIMIR LOS DATOS

                    System.out.println(nombre + "\t"+tipo + "\t"+ codificacion+"\t"+idioma);

                

            
        }
        
    }
    //OPCION -L OBTENER ENLACES

    public static void extraerEnlaces(String[] ficheros, Tika tika, Metadata metadata, String args)throws Exception{

        for(String nombre : ficheros){
            String nameFile = args+ "/"+ nombre;
                
                System.out.println(nameFile);
                File archivo = new File(nameFile);
            
            
                LinkContentHandler linkHandler = new LinkContentHandler();

                ParseContext parseContext = new ParseContext();
                AutoDetectParser parser = new AutoDetectParser();

                InputStream input = new FileInputStream(archivo);
                parser.parse(input,linkHandler,metadata,parseContext);

                List<Link> enlaces = linkHandler.getLinks();
                if(enlaces.isEmpty()){
                    System.out.println("No hay links en : " +archivo.getName()+".");
                }else {
                    System.out.println("Links encontrados en " +archivo.getName()+":");
                    for(Link enlace : enlaces){
                    
                        System.out.println(enlace.getUri());
                }
                
                }
                


            
        }
    }
}
