package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * This is a runnable class in charge of master service server socket - serversocket
 */
public class serversocketthread implements Runnable {

    private static int Port;
    private static Map<String, String> idBook;

    /**
     * This constructs a server socket with admin credential file
     *
     * @param idBook admin credential file
     * @param Port serversocket listening port
     */
    public serversocketthread(Map<String, String> idBook, int Port) {
        this.idBook = idBook;
        this.Port = Port;
    }

    /**
     * This method is the major running server socket thread.
     */
    @Override
    public void run() {
        try (
                ServerSocket ss = new ServerSocket(Port);
        ) {
            InetAddress lh = InetAddress.getLocalHost();
            System.out.printf("Current program running on socket %s : %d%n", lh.toString(), ss.getLocalPort());

            while (true) {
                Socket client = ss.accept();
                Thread cl = new Thread(new cliagent(client, idBook));
                cl.start();
            }

        } catch (UnknownHostException uhe) {
            uhe.getSuppressed();
            System.out.println("37 " + uhe.toString());
        } catch (IOException ioe) {
            ioe.getSuppressed();
            System.out.println("40 " + ioe.toString());
        }
    }


}

/**
 * Client agent for per request.
 */
class cliagent implements Runnable {
    private Socket client;
    private JSONParser jp = new JSONParser();
    private static Map<String, String> idBook;

    /**
     * Construct a new thread with parameters
     *
     * @param client Client socket
     * @param id     Administrator ID book
     */
    public cliagent(Socket client, Map<String, String> id) {
        this.client = client;
        this.idBook = id;
    }

    enum CMD {
        QUERY,
        ADMINLOGIN,
        ADD,
        DELETE,
        CHECK
    }

    /**
     * This is the main method where the client socket would receive any command or data input from client per request,
     * and when the client is closed, this socket would terminate itself, client would timeout at 1 second.
     */
    public void run() {
        try {
            InputStream cliIn = client.getInputStream();
            DataInputStream din = new DataInputStream(cliIn);
            OutputStream cliOu = client.getOutputStream();
            DataOutputStream dos = new DataOutputStream(cliOu);


            JSONObject request = (JSONObject) jp.parse(din.readUTF());
            CMD command = CMD.valueOf(request.get("%CMD").toString());
            switch (command) {
                case QUERY:
                    String target = (String) request.get("%QUERY");
                    JSONObject answer = findMean(target);
                    dos.writeUTF(answer.toString());
                    break;
                case ADMINLOGIN:
                    JSONObject id = (JSONObject) request.get("%ADMINLOGIN");
                    dos.writeBoolean(checkID(id));
                    break;
                case ADD:
                    JSONObject addWord = (JSONObject) request.get("%WORD");
                    boolean addDone = addWord(addWord);
                    dos.writeBoolean(addDone);
                    break;
                case DELETE:
                    String delWord = (String) request.get("%WORD");
                    boolean delDone = delWord(delWord);
                    dos.writeBoolean(delDone);
                    break;
                case CHECK:
                    String ifexist = (String) request.get("%WORD");
                    dos.writeBoolean(exist(ifexist));
                    break;
                default:
                    break;
            }
            if (!client.isConnected()) {
                client.setSoTimeout(1000);
                client.close();
            }

        } catch (IOException ioe) {
            ioe.getSuppressed();
            //System.out.println("108 " + ioe.toString());
        } catch (ParseException pe) {
            System.out.println("110 " + pe.toString());
        }
    }

    /**
     * This method would add word as requested by admin client, it would double check the existence upon addition.
     *
     * @param word the word to be added as requested by admin client.
     * @return Addition successful or any interruption happens within the addition process.
     */
    private boolean addWord(JSONObject word) {
        boolean result = false;
        try {
            char init = word.get("%%WORD").toString().charAt(0);
            String filename = String.format("Dictionaries/%c.json", init);
            File df = new File(filename);
            Reader dict = new FileReader(df);
            JSONObject dictObj = (JSONObject) jp.parse(dict);
            String wObjH = word.get("%%WORD").toString();
            JSONObject wObjB = (JSONObject) word.get("%%BODY");
            FileWriter todict = new FileWriter(df);

            if (!dictObj.containsKey(wObjH)) {
                dictObj.put(wObjH, wObjB);
                todict.write(dictObj.toString());
                todict.flush();
                result = true;
            }

        } catch (FileNotFoundException fnfe) {
            result = false;
            fnfe.getSuppressed();
            System.out.println("129 " + fnfe.toString());
        } catch (ParseException pe) {
            result = false;
            pe.getSuppressed();
            System.out.println("132 " + pe.toString());
        } catch (IOException ioe) {
            result = false;
            ioe.printStackTrace();
            System.out.println("135 " + ioe.toString());
        }
        return result;
    }

    /**
     * This method would delete the word as requested by admin client, it would double check if the word exist in the
     * dictionary upon deletion.
     *
     * @param targetWord The word to be deleted as requested by admin client.
     * @return Delete successful or any interruption happens within the deletion process.
     */
    private boolean delWord(String targetWord) {
        boolean result = false;
        try {
            char init = targetWord.charAt(0);
            String filename = String.format("Dictionaries/%c.json", init);
            File df = new File(filename);
            Reader dict = new FileReader(df);
            JSONObject dictObj = (JSONObject) jp.parse(dict);
            dict.close();

            if (dictObj.containsKey(targetWord)) {
                dictObj.remove(targetWord);
                FileWriter todict = new FileWriter(df);
                todict.write(dictObj.toString());
                todict.flush();
                return true;
            }

        } catch (FileNotFoundException fnfe) {
            result = false;
            fnfe.getSuppressed();
            //System.out.println("159 " + fnfe.toString());
        } catch (ParseException pe) {
            result = false;
            pe.getSuppressed();
            //System.out.println("162 " + pe.toString());
        } catch (IOException ioe) {
            result = false;
            ioe.printStackTrace();
            //System.out.println("165 " + ioe.toString());
        }
        return result;
    }

    /**
     * This method is supporting method which checks whether the word admin want to add or delete in the dicitonary
     *
     * @param targetWord The word admin want to add or delete.
     * @return the word exist or not.
     */
    private boolean exist(String targetWord) {
        boolean result = false;
        try {
            char init = targetWord.charAt(0);
            String filename = String.format("Dictionaries/%c.json", init);
            File df = new File(filename);
            Reader dict = new FileReader(df);
            JSONObject dictObj = (JSONObject) jp.parse(dict);
            dict.close();

            if (dictObj.containsKey(targetWord)) {
                return true;
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.getSuppressed();
            System.out.println("186 " + fnfe.toString());
        } catch (ParseException pe) {
            pe.getSuppressed();
            System.out.println("189 " + pe.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("192 " + ioe.toString());
        }
        return result;
    }

    /**
     * This method would find the word queried by the client.
     *
     * @param targetWord The word queried by the client
     * @return The meaning object.
     */
    private JSONObject findMean(String targetWord) {
        JSONObject nosuchword = new JSONObject();
        nosuchword.put("No such word", targetWord);
        //This part would find the file match the initial
        try {
            char init = targetWord.charAt(0);
            String filename = String.format("Dictionaries/%c.json", init);
            File df = new File(filename);
            Reader dict = new FileReader(df);
            JSONObject dictObj = (JSONObject) jp.parse(dict);
            dict.close();

            if (dictObj.containsKey(targetWord)) {
                return (JSONObject) dictObj.get(targetWord);
            } else {

            }
        } catch (IOException ioe) {
            ioe.getSuppressed();
            System.out.println("216 " + ioe.toString());
        } catch (ParseException pe) {
            pe.getSuppressed();
            System.out.println("219 " + pe.toString());
        }

        return nosuchword;
    }

    /**
     * This method checks the admin credential against credential file.
     *
     * @param id The credential sent by the client.
     * @return if the credential is valid for allowing client enter modification mode.
     */
    private boolean checkID(JSONObject id) {
        String name = id.get("%NAME").toString();
        String pass = id.get("%PASS").toString();
        if (idBook.containsKey(name) && idBook.get(name).equals(pass)) {
            return true;
        }
        return false;
    }
}
