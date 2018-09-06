import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * This is a runnable class to approximate match a word in a unindexed dictionary
 * @param dic unindexed dictionary 
 * @param misspell misspelled word list
 * @param corlist correction word list
 * @param message misspelled word list name
 */
class nonIndexedDict implements Runnable {
    List<String> dic = new ArrayList<>();
    List<String> misspell = new ArrayList<>();
    List<String> corlist = new ArrayList<>();
    String message = "";

    /**
     * This constructs the runnable object
     * 
     * @param dic nonindexed dictionary hashmap
     * @param misspell misspell word list
     * @param corlist corrected word list
     * @param message misspell dataset name
     */
    public nonIndexedDict(List<String> dic, List<String> misspell, List<String> corlist, String message) {
        this.dic = dic;
        this.misspell = misspell;
        this.corlist = corlist;
        this.message = message;
    }

    /**
     * Command to be executed within the runnable object
     */
    public void run() {
        transpos dis = new transpos();
        try{
            double recall = 0.0;
            double accuracy = 0.0;
            for(int i = 0; i<misspell.size(); i++) {
                Integer maxval = Integer.MAX_VALUE;
                String correct = "";
                Map<String, Integer> possible = new HashMap<>();
                for(String word : dic) {
                    int distance = dis.damerauLDistance(misspell.get(i), word);
                    if(distance != 0) {
                        correct = distance < maxval ? word : correct;
                        maxval = distance < maxval ? distance : maxval;
                        possible.put(correct, maxval);
                    }
                }
                if(possible.containsKey(corlist.get(i))) {
                    recall += 1;
                }
                if(corlist.get(i).equals(correct)) {
                    accuracy += 1;
                }
            }
            System.out.println(message+" dataset against nonindexed dictionary has recall "+ recall / misspell.size());
            System.out.println(message+" dataset against nonindexed dictionary has accuracy "+ accuracy/misspell.size());
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}

/**
 * This is a runnable class to approximate match a word in an indexed dictionary
 * @param dic indexed dictionary
 * @param misspell misspelled word list
 * @param corlist correction word list
 * @param message misspelled word list name
 */
class indexedDict implements Runnable {
	Map<Character, List<String>> dic = new HashMap<>();
	List<String> misspell = new ArrayList<>();
	List<String> corlist = new ArrayList<>();
	String message = "";

    /**
     * This constructs the runnable object
     * 
     * @param dic indexed dictionary hashmap
     * @param misspell misspell word list
     * @param corlist corrected word list
     * @param message misspell dataset name
     */
    public indexedDict(Map<Character, List<String>> dic, List<String> misspell, List<String> corlist, String message) {
    	this.dic = dic;
    	this.misspell = misspell;
    	this.corlist = corlist;
    	this.message = message;
    }

    /**
     * Command to be executed within the runnable object
     */
    public void run(){
    	transpos dis = new transpos();
    	try{
    		double recall = 0.0;
    		double accuracy = 0.0;
    		for(int i = 0; i<misspell.size(); i++){
    			Integer maxval = Integer.MAX_VALUE;
    			String correct = "";
    			Map<String, Integer> possible = new HashMap<>();
    			for(String word : dic.get(misspell.get(i).charAt(0))){
    				int distance = dis.damerauLDistance(misspell.get(i), word);
                    if(distance != 0){
    			        correct = distance < maxval ? word : correct;
   				        maxval = distance < maxval ? distance : maxval;
   					    possible.put(correct, maxval);
                    }
    			}
    			if(possible.containsKey(corlist.get(i))) {
    				recall += 1;
    			}
    			if(corlist.get(i).equals(correct)) {
    				accuracy += 1;
    			}
    		}
    		System.out.println(message+" dataset against indexed dictionary has recall "+recall / misspell.size());
    		System.out.println(message+" dataset against indexed dictionary has accuracy "+accuracy / misspell.size());
    	} catch (Exception e){
            // Throwing an exception
    		System.out.println (e);
    	}

    }
}

/**
 * This is the main class for damerau levenshtein distance calculation
 *
 * @param insertion penalty for insertion
 * @param deletion penalty for deletion
 * @param replace penalty for replace
 * @param match reward for match
 * @param transpos reduced penalty for transposition (0 as default as 1 in the original algorithm)
 */
public class transpos {
	private static int insertion = 1, deletion = 1, replace = 2, match = 0, transpos = 0;

	/**
     * The main method of the typo processing
     *      
     * @param args Weight used in GED, in the form of [insertion deletion replace match transpos] all must be integer
     */
	public static void main(String[] args) {
		try{
			insertion = Integer.parseInt(args[0]);
        	deletion = Integer.parseInt(args[1]);
        	replace = Integer.parseInt(args[2]);
        	match = Integer.parseInt(args[3]);
        	transpos = Integer.parseInt(args[4]);
    	} catch (Exception e) {
    		// do nothing
    	}

		System.out.printf("Start running on parameter [%d %d %d %d %d]%n", insertion, deletion, replace, match, transpos);

		transpos it = new transpos();
		List<String> dict = new ArrayList<>();
		List<String> wiki_mis = new ArrayList<>();
		List<String> wiki_cor = new ArrayList<>();
		try{
			Scanner fr = new Scanner(new File("./Data/dict.txt"));
			while(fr.hasNextLine()){
				String word = fr.nextLine();
				dict.add(word);
			}
			fr.close();
			fr = new Scanner(new File("./Data/wiki_misspell.txt"));
			while(fr.hasNextLine()){
				String word = fr.nextLine();
				wiki_mis.add(word);
			}
			fr.close();
			fr = new Scanner(new File("./Data/wiki_correct.txt"));
			while(fr.hasNextLine()){
				String word = fr.nextLine();
				wiki_cor.add(word);
			}
			fr.close();
		} catch(FileNotFoundException e){
			System.out.println(e);
		}

		Map<Character, List<String>> dic = it.byInitial(dict);

        //Processing wikipedia dataset with indexed dictionary
        //*
		Thread wikiIndex = new Thread(new indexedDict(dic, wiki_mis, wiki_cor, "wikipedia"));
		wikiIndex.start();
        //*/

		//Uncomment to process wikipedia dataset with nonindexed dictionary
		/*
       	Thread wikiNonIndex = new Thread(new nonIndexedDict(dict, wiki_mis, wiki_cor, "wikipedia"));
        wikiNonIndex.start();
        //*/

		/*Uncomment this block to test the correctiveness of damerau-levenshtein algorithm [using parameters 1 1 1 0 0]
        System.out.println(it.damerauLDistance("file", "test")==4);
        System.out.println(it.damerauLDistance("test", "test")==0);
        System.out.println(it.damerauLDistance("test", "testy")==1);
        System.out.println(it.damerauLDistance("test", "tets")==1);
        System.out.println(it.damerauLDistance("test", "testing")==3);
        System.out.println(it.damerauLDistance("test", "tsety")==2);
        System.out.println(it.damerauLDistance("feild", "field")==1);
        System.out.println(it.damerauLDistance("fieldwork", "feildwrok")==2);
        //*/

	}

	/**
     * This is a method to index word list by words initial
     * 
     * @param a list of words
     * @return word hashmap indexed by the words initial
     */
	public Map<Character, List<String>> byInitial(List<String> a) {
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		Map<Character, List<String>> result = new HashMap<>();
		for(char ch : alphabet){
			List<String> temp = new ArrayList<>();
			for(String word : a) {
				if(word.charAt(0) == ch) {
					temp.add(word);
				}
			}
			result.put(ch, temp);
		}
		return result;
	}

    /**
     * This is a method to calculate the damerau-levenshtein distance between two word.
     * 
     * @param word1 one of the word to be compared
     * @param word2 one of the other word to be compared
     * @return The damerau-levenshtein distance to tranform from word1 to word2
     */
    public int damerauLDistance(String word1, String word2) {
    	if(word1.length() == 1) {
    		int count = 0;
    		for( int i=0; i<word2.length(); i++ ) {
   	 			if( word2.charAt(i) == word1.charAt(0) ) {
        			count++;
    			} 
			}
    		return word2.length() - count ;
    	}else if(word2.length() == 1) {
    		int count = 0;
    		for( int i=0; i<word1.length(); i++ ) {
   	 			if( word1.charAt(i) == word2.charAt(0) ) {
        			count++;
    			} 
			}
    		return word1.length()-count;
    	}else {
    		int[] current = new int[word1.length()+1];
    		for(int i=0; i<word1.length()+1; i++){
            	current[i] = i;
        	}
        	int[] prev = new int[word1.length()+1];
        	int[] above = new int[word1.length()+1];
        	char w2 = ' ', pre = ' ';
        	for(int j=0; j<word2.length(); j++){
        		prev = current.clone();
        		for(int i = 0; i < current.length; i++) {
                	current[i] = current[i] + insertion;
            	}
        		w2 = word2.charAt(j);
        		if(j > 1){
        			for(int i = 0; i < word1.length(); i++){
        				if(i<1){
        					current[i+1] = min(
                   	 		current[i+1],
                    		current[i] + deletion,
                    		prev[i] + (word1.charAt(i) == w2 ? match : replace));
        				} else {
        					current[i+1] = min4(
                   	 		current[i+1],
                    		current[i] + deletion,
                    		prev[i] + (word1.charAt(i) == w2 ? match : replace),
                    		prev[i-1] + ((word1.charAt(i) == pre && word1.charAt(i-1) == w2) ? transpos : 2 * replace));
        				}
            		}
        		}else{
        			for(int i = 0; i < word1.length(); i++){
                		current[i+1] = min(
                   	 	current[i+1],
                    	current[i] + deletion,
                    	prev[i] + (word1.charAt(i) == w2 ? match : replace));
            		}
        		}
        		pre = w2;
        		above = prev.clone();
        	}
        	return current[word1.length()];
    	}
    }

 	/**
     * This is a method to find the minimum of three integer
     * 
     * @param a integer to be compared a
     * @param b integer to be compared b
     * @param c integer to be compared c
     * @return minimum of these three integer
     */
 	public int min(int i1, int i2, int i3) {
 		int temp = Math.min(i1, i2);
 		int minimum = Math.min(temp, i3);
 		return minimum;
 	}

 	/**
     * This is a method to find the minimum of three integer
     * 
     * @param a integer to be compared a
     * @param b integer to be compared b
     * @param c integer to be compared c
     * @return minimum of these three integer
     */
 	public int min4(int i1, int i2, int i3, int i4) {
 		int minimum = Math.min(i1, i2);
 		minimum = Math.min(minimum, i3);
 		minimum = Math.min(minimum, i4);
 		return minimum;
 	}

 }