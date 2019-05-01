/**
 * @author Stian Masserud
 * @version 1.0
 * 
 * Assignment: http://www.uio.no/studier/emner/matnat/ifi/INF2220/h15/obligatoriske-oppgaver/oblig-3/oblig3.pdf
 *
 * class containing main method
 */
class Oblig3 {
	
	public static void main(String[] args) {
		if(args.length == 2) {
			new Monitor(args[0], args[1]);
		} else {
			System.out.println("Usage: java Oblig3 <needle.txt> <haystack.txt>");
		}
	}
}
