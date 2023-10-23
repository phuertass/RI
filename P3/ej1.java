import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.Reader;
import java.io.CSVReader;
import java.io.FileReader;
import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;


public class ej1{

    public static void main(String[] args){
        String carpetaCU = "/home/anne/Escritorio/RI/RI/P3/CapitulosUnidos";
        String carpetaC = "/home/anne/Escritorio/RI/RI/P3/Capitulos";
        
        double valoraciones=0;
        double votos=0;
        int personajesTotal=0, totalCU=0, totalC=0;

        File carpetaCapUnid  = new File(carpetaCU);
        File carpetaCaps  = new File(carpetaC);

        File[] csvUnidos= carpetaCapUnid.listFiles((dir,nombre)->nombre.endsWith(".csv"));
        File[] csvCaps= carpetaCaps.listFiles((dir,nombre)->nombre.endsWith(".csv"));


        if(csvUnidos!=null){
            for(int i=0; i< csvUnidos.length;i++){
                File archivo= csvUnidos[i];
                Reader reader = new FileReader(archivo);
                CSVReader csvReader= new CSVReader(reader);
                String[] nextRecord;
                String[] firstLine;

                firstLine=csvReader.readNext();

                while ((nextRecord = csvReader.readNext()) != null){
                    if(nextRecord.length>=4){
                        double valoracion = Double.parseDouble(nextRecord[3]);
                        int votacion=  Integer.parseInt(nextRecord[4]);

                        valoraciones+=valoracion;
                        votos+=votacion;
                    }
                }
                totalCU++;

                
            }
        }
        if(csvCaps!=null){
            for(int i=0; i< csvCaps.length;i++){
                File archivo= csvCaps[i];
                Reader reader = new FileReader(archivo);
                CSVReader csvReader= new CSVReader(reader);
                String[] nextRecord;
                String[] firstLine;

                firstLine=csvReader.readNext();

                while ((nextRecord = csvReader.readNext()) != null){
                    if(nextRecord.length>=4){
                        int personajes=Integer.parseInt(nextRecord[3]);
                        personajesTotal+=personajes;
                    }
                }
                totalC++;

                
            }
        }

        if(totalCU>0){
            double valoracionMedia= valoraciones/totalCU;
            double mediaVotos= votos/totalCU;

            System.out.println("Valoración media de todos los capítulos --> "+ valoracionMedia);
            System.out.println("Media del número de votaciones --> "+ mediaVotos);
            
        }
        if(totalC>0){
            double mediaPersonajes = personajesTotal/totalC;
            System.out.println("Media del número de personajes por capítulo --> "+ mediaPersonajes);
            
        }

        

    }
   

}

