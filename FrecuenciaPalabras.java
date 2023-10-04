import java.util.*;


// Clase que define las ocurrencias de una palabra
// Se usa con la opcion -t
public class FrecuenciaPalabras{ 
	public String palabra;
	public int ocurrencias;
	
	public FrecuenciaPalabras(String p, int o){
		palabra=p;
		ocurrencias=o;
	}
}

// Para ordenar las palabras en orden descendente
class SortbyOcurrencias implements Comparator<FrecuenciaPalabras>{ 
	public int compare(FrecuenciaPalabras a, FrecuenciaPalabras b) {
		return b.ocurrencias - a.ocurrencias; 
	} 
} 