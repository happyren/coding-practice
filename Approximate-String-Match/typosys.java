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
        typosys dis = new typosys();
        try{
            double recall = 0.0;
            double accuracy = 0.0;
            for(int i = 0; i<misspell.size(); i++) {
                Integer maxval = Integer.MIN_VALUE;
                String correct = "";
                Map<String, Integer> possible = new HashMap<>();
                for(String word : dic) {
                    int distance = dis.globaldistance(misspell.get(i), word);
                    if(distance!=0){
                        correct = distance > maxval ? word : correct;
                        maxval = distance > maxval ? distance : maxval;
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
        typosys dis = new typosys();
        try{
            double recall = 0.0;
            double accuracy = 0.0;
            for(int i = 0; i<misspell.size(); i++){
                Integer maxval = Integer.MIN_VALUE;
                String correct = "";
                Map<String, Integer> possible = new HashMap<>();
                for(String word : dic.get(misspell.get(i).charAt(0))){
                    int distance = dis.globaldistance(misspell.get(i), word);
                    if(distance != 0){
                        correct = distance > maxval ? word : correct;
                        maxval = distance > maxval ? distance : maxval;
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
 * This is the main class for typo correcting system
 * @param insertion insertion penalty weight
 * @param deletion deletion penalty weight
 * @param replace replace penalty weight
 * @param match match reward weight
 */
public class typosys{
    private static int insertion = -1, deletion = -1, replace = -2, match = 0;

    /**
     * The main method of the typo processing
     *      
     * @param args Weight used in GED, in the form of [insertion deletion replace match] all must be integer
     */
    public static void main(String[] args) {
        try{
            insertion = Integer.parseInt(args[0]);
            deletion = Integer.parseInt(args[1]);
            replace = Integer.parseInt(args[2]);
            match = Integer.parseInt(args[3]);
        } catch (Exception e) {
            //do nothing
        }
        

        System.out.printf("Start running on parameter [%d %d %d %d]%n",insertion,deletion,replace,match);

        typosys it = new typosys();
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
    }

    /**
     * This is a method to calculate the GED between two word, weight can be set as CML argument
     * 
     * @param word1 one of the word to be compared
     * @param word2 one of the other word to be compared
     * @return The GED to tranform from word1 to word2
     */
    public int globaldistance(String word1, String word2) {
        int[] current = new int[word1.length()+1];
        for(int i=0; i<word1.length()+1; i++){
            current[i] = -i;
        }
        int[] above = new int[word1.length()+1];
        for(char ch : word2.toCharArray()) {
            above = current.clone();
            for(int i = 0; i < current.length; i++) {
                current[i] = current[i] + insertion;
            }
            for(int i = 0; i<word1.length(); i++){
                current[i+1] = max(
                    current[i+1],
                    current[i] + deletion,
                    above[i] + (word1.charAt(i) == ch ? match : replace));
            }
        }
        return current[word1.length()];
    }

    /**
     * This is a method to find the maximum of three integer
     * 
     * @param a integer to be compared a
     * @param b integer to be compared b
     * @param c integer to be compared c
     * @return maximum of these three integer
     */
    public int max(int a, int b, int c) {
        int temp = Math.max(a, b);
        int max = Math.max(temp, c);
        return max;
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
}