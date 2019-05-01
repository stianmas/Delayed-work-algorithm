import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Stian Masserud
 * @version 1.0
 * 
 * Class to handle reading from file and prepare the needle and haystack.
 */
class Monitor {
	
	private String needleName, haystackName;
	private ArrayList<char[]> needle, haystack;
	private Horspool hp;

	/**
	 * Constructor
	 * Reads needle and haystack from txt-files and call on Horspool.java to find patterns
	 * @param filename for needle, haystack
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Monitor(String needleName, String haystackName) {
		this.needleName = needleName;
		this.haystackName = haystackName;
		needle = readFromFile(needleName);
		haystack = readFromFile(haystackName);
		if(needle.isEmpty() || haystack.isEmpty()) {
			System.out.println("Needle or haystack is null");
			System.exit(1);
		}
		hp = new Horspool();
		System.out.println("Haystack:\n" + new String(haystack.get(0))+"\n");
		for(int i = 0; i < needle.size(); i++) {
			System.out.println("\nNeedle:" + new String(needle.get(i)) + "\n-------------");
			if(needle.get(i) != null)
				hp. boyerMooreHorspool(needle.get(i), haystack.get(0));
		}
	}

	/** 
	 * Reads needle and haystack from file
	 * @param filename
	 * @return array of chars containing needle/haystack
	 */
	@SuppressWarnings("unchecked")
	private ArrayList readFromFile(String s) {
		File f = new File(s);
		try {
			Scanner sc = new Scanner(f);
			ArrayList<char[]> tmp = new ArrayList<char[]>();
			while(sc.hasNextLine()) {
				String read = sc.nextLine();
				if(!read.equals("")) {
					tmp.add(read.toCharArray());
				}
			}
			return tmp;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(2);
		}
		System.out.println("Something went wrong with reading from file.");
		return null;
	}
}
