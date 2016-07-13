/**
 * @author Stian Masserud
 * @version 1.0
 *
 * Assignment: http://www.uio.no/studier/emner/matnat/ifi/INF2220/h15/obligatoriske-oppgaver/oblig-2/oblig2.pdf
 *
 * Class containing Main
 */
class Oblig2 {
	
	/**
	 * Main method. Takes an argument which is supposed to be a txt file.
	 * @param A txt-file.
	 * @return
	 */
	public static void main(String[] args) {
		if(args.length != 2) {
			// Manpower is a dummy.
			System.out.println("ERROR! USAGE: <java Main your_projectfile.txt manpower>");
			System.exit(1);
		}
		Graph graph = new Graph(args[0]);		
	}
}