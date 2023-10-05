import java.util.*;

public class FrecuenciaPalabras{
	public int ocurrencias;
	public String palabra;

	public FrecuenciaPalabras(String p, int o){
		palabra=p;
		ocurrencias=o;
	}
}

class OrdenarPorOcurrencias implements Comparator<FrecuenciaPalabras>{
	public int compare(FrecuenciaPalabras a, FrecuenciaPalabras b) {
		return b.ocurrencias - a.ocurrencias; 
	} 
} 