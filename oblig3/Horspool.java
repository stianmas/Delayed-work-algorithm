import java.util.HashMap;

/**
 * @author Stian Masserud
 * @version 1.0
 * 
 * Class containing Boyer-Moore Horspool algorithm
 */
class Horspool {

	/**
	 * Method for finding matches in a txt-file with the horspool algorithm
	 * @param A pattern, a text to look for the pattern
	 * @return An array of matches
	 */
	public void boyerMooreHorspool(char[] needle, char[] haystack){
		int[] result = new int[haystack.length];

		if (needle.length > haystack.length || needle == haystack){ 
			System.out.println("Needle is longer or the same as haystack");
			return; 
		}

		HashMap<Character, Integer> badshift = new HashMap<Character, Integer>();
	
		int offset = 0, scan = 0;
		int last = needle.length -1;
		int maxoffset = haystack.length - needle.length;
		for(int i = 0; i < needle.length; i++){
			if(last - i == 0) {
				badshift.put(needle[i], 1);	
			} else {
				badshift.put(needle[i], last - i);
			}
		}
		String match = "";
		while(offset <= maxoffset){
			for(scan = last; needle[scan] == haystack[scan+offset] || needle[scan] == '_'; scan--){ 
				if(scan == 0){ // match found!
					match = "";
					for(int i = 0; i < needle.length; i++){
						match += haystack[offset + i];
					}
					System.out.println("Match: " + match);
						break;
				}
			}

			// Calculate offset
			if(needle[scan] == '_') {
				offset += 1;
			}else if(badshift.containsKey(haystack[offset + last])) {
				offset += badshift.get(haystack[offset + last]);
			} else {
				offset += needle.length;
			}
		}
	    if(match.equals("")) {
			System.out.println("No match found");
	    }
	}
}
