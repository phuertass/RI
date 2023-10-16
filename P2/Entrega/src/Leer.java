import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;


/**
 *
 * @author Patricia Villalba Crucelaegui
 */

public class Leer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {     
		Reader reader = new FileReader("./n_movies.csv");

		if(!reader.ready()){
			System.out.println("Error en el fichero de datos");
		}

		CSVReader csvReader = new CSVReader(reader);

		String[] nextRecord;
		String[] firstLine;
		int i = 1;

		//Leo documento
		firstLine = csvReader.readNext();

		while((nextRecord = csvReader.readNext()) != null){
			int p; 
			System.out.println(i + "---------------");
			for (p = 0; p < firstLine.length; p++) {
				System.out.println(firstLine[p] + "−> " + nextRecord[p]);
			}
			i++;
		}
  	}

}