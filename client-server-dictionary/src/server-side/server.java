package server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This is a runnable class in charge of creating a server administration thread.
 */
class admin implements Runnable {
    private static Map<String, String> idBook;
    private static boolean exit = false;

    /**
     * Construct a administration thread.
     * @param idBook administrator login credential file.
     */
    public admin(Map<String, String> idBook) {
        this.idBook = idBook;
    }

    /**
     * This execute the server administration operation menus.
     */
    @Override
    public void run() {
        server it = new server();
        Scanner kb = new Scanner(System.in);
        while (!exit) {
            System.out.println("What would you like to do? : 1. Add Admin 2. Remove Admin 3. terminate Server");
            String in = kb.nextLine();
            int ops = 0;
            try {
                ops = Integer.parseInt(in);
            } catch (NumberFormatException nfe) {
                System.out.print("The operation you put in is ");
            }
            if (ops == 1) {
                it.addAdmins(kb);
            } else if (ops == 3) {
                exit = true;
            } else if (ops == 2) {
                it.removeAdmins(kb);
            } else {
                System.out.println("Invalid, you need to choose operation as number [1-3]");
            }
        }
        it.writeID();
        System.exit(0);
    }
}

/**
 * This is the main class of server side.
 */
public class server {
    private static Map<String, String> idBook;
    private static boolean exit = false;

    /**
     * Main method takes no argument, server socket port number is reserved as 50000.
     * @param args
     */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Wrong number of argument!\nUsage jar -jar server.jar <Port>");
            System.exit(0);
        }
        try {
            int Port = Integer.parseInt(args[0]);
            if(Port > 1024 && Port < 65535) {
                server it = new server();
                idBook = new HashMap<>();
                it.readID();
                Thread admin = new Thread(new admin(idBook));
                Thread serversocket = new Thread(new serversocketthread(idBook, Port));
                serversocket.setPriority(10);
                admin.setPriority(1);
                serversocket.start();
                admin.start();
            } else {
                System.out.println("Not a valid port number!!!");
            }
        } catch (NumberFormatException nfe) {
            System.out.println("This value could not be a port!!!");
        }
    }

    /**
     * This method in charge of add administrator to credential file
     * @param kb user CML input method
     */
    void addAdmins(Scanner kb) {
        System.out.print("Please input server.admin name: ");
        String name = kb.nextLine();
        System.out.print("Please input server.admin password: ");
        String pass = kb.nextLine();
        if (idBook.containsKey(name)) {
            System.out.println("Admin exists!");
        } else {
            idBook.put(name, pass);
        }
    }

    /**
     * This method in charge of remove administrator from credential file
     * @param kb user CML input method
     */
    void removeAdmins(Scanner kb) {
        System.out.print("Please input server.admin name: ");
        String name = kb.nextLine();
        if (idBook.containsKey(name)) {
            idBook.remove(name);
        } else {
            System.out.println("No such administrator.");
        }
    }

    /**
     * This method in charge of read admin credential file from binary file on file system
     */
    void readID() {
        try {
            File id = new File("Dictionaries/id.bat");
            FileInputStream fileIn = new FileInputStream(id);
            ObjectInputStream idIn = new ObjectInputStream(fileIn);
            idBook = (HashMap) idIn.readObject();
            idIn.close();
            fileIn.close();
        } catch (FileNotFoundException fne) {
            fne.getSuppressed();
            System.out.println("main 73 " + fne.toString());
        } catch (IOException ioe) {
            ioe.getSuppressed();
            System.out.println("main 76 " + ioe.toString());
        } catch (ClassNotFoundException cnfe) {
            cnfe.getStackTrace();
            System.out.println("main 79 " + cnfe.toString());
        }
    }

    /**
     * This method in charge of writing admin credential file to binary file on file system
     */
    void writeID() {
        try {
            File id = new File("Dictionaries/id.bat");
            FileOutputStream fileOut = new FileOutputStream(id);
            ObjectOutputStream idOut = new ObjectOutputStream(fileOut);
            idOut.writeObject(idBook);
            idOut.close();
            fileOut.close();
        } catch (FileNotFoundException fne) {
            fne.getSuppressed();
            System.out.println("main 93 " + fne.toString());
        } catch (IOException ioe) {
            ioe.getSuppressed();
            System.out.println("main 96 " + ioe.toString());
        }
    }
}
