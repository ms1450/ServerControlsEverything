package Server;

//imports for this class
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//this class initializes the server side, forms the connections and starts the
//connection threads
public class sceConnectionCreator {
    //port number on which the server is hosted
    final static int PORTNUMBER = 5439;
    //list of usrcodes currently connected with the server
    static ArrayList<Integer> usrcodes = new ArrayList<>();

    public static void main(String[] args) {
        //Array list containing the threads that are handling clients
        ArrayList<Thread> clients = new ArrayList<>();
        try {
            //Hosting a server on the assigned port number
            ServerSocket serverSocket = new ServerSocket(PORTNUMBER);
            while(true) {
                System.err.println("Listening on " + PORTNUMBER);
                //Connected with a client
                Socket client = serverSocket.accept();
                //Getting the Input and Output streams
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter writer = new PrintWriter(client.getOutputStream());

                //Gets the user code from the client
                int usrcode = 0;
                try {
                    usrcode = Integer.parseInt(input.readLine().split(" ")[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Checks if the array list usrcodes contains the usercode or not
                //Used to prevent two client with same usercode from connecting at once
                if (usrcodes.contains(usrcode)) {
                    writer.println("DENY");
                    writer.flush();
                    break;
                } else {
                    //Send out a CNFD to client
                    System.err.println("Connection #" + usrcode + " established with IP:" + client.getInetAddress().getHostAddress());
                    writer.println("CNFD");
                    writer.flush();

                    //Start a thread to handle the client connection
                    Thread thread = new sceConnectionThread(client, usrcode);
                    thread.start();

                    //Add the connected client to clients list and the usercode to usrcodes list
                    clients.add(thread);
                    usrcodes.add(usrcode);
                }
            }
        }catch (IOException ie){
            ie.printStackTrace();
        }
    }
}
