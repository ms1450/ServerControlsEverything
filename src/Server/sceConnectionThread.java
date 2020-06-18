package Server;

//imports for this class
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

//this class handles the connection with a single client
//it deals with the initial messages that get sent to the
//client as well as creating files and modifying them
public class sceConnectionThread extends Thread{
    //Information about the client connection
    Socket socket;
    int usrcode;
    int reccode;
    String filename;
    BufferedReader input;
    PrintWriter output;
    sceDatabaseHandler database;

    //the state of the connection with client
    static volatile boolean running;

    sceConnectionThread(Socket client, int usrcode){
        this.socket = client;
        this.usrcode = usrcode;
        this.database = new sceDatabaseHandler();
    }

    public void run(){
        try{
            //Gets the input and output streams
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            //Checks if the User Code is already in the database or a new user
            //and sends out a text accordingly
            ArrayList<String> usercodes = database.getUserCodes();
            if(usercodes.contains(Integer.toString(usrcode))){
                output.println("<SERVER> Welcome Back User #"+usrcode);
            }
            else{
                database.addUser(Integer.toString(usrcode));
                output.println("<SERVER> Welcome to SCE User #"+usrcode);
            }
            output.flush();

            //Receives the recipient code from the client
            reccode = Integer.parseInt(input.readLine().split(" ")[1]);
            filename = filenameCreate(usrcode,reccode);

            //Searches the Conversation folder for a previous chat log
            //if absent, it creates a new one, if found, it sends out
            //the chat log to client
            if(database.findConversation(filename)){
                database.sendOutMessages(filename, output);
            }
            else{
                database.createConversation(filename);
            }

            output.println("<SERVER> You can now start adding messages to the database");
            output.println("<SERVER> Type 'EXIT' to end connection, 'REFRESH' to update messages");
            output.flush();

            //Starts the storeData thread
            Thread storeData = new sceDataParser(input, output, filename, usrcode, database);
            storeData.start();
            while(true){
                //Constantly checks if the thread is running and as soon as the thread ends
                //it closes all the streams and the connections
                if(!storeData.isAlive()){
                    System.err.println("Closing Connection #" + usrcode + " with IP:"+ socket.getInetAddress().getHostAddress());
                    sceConnectionCreator.usrcodes.remove(Integer.valueOf(usrcode));
                    input.close();
                    output.close();
                    socket.close();
                    break;
                }
            }
            System.err.println("Disconnected from : " + usrcode);

        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    //Compares the two user codes and creates a string that either of the two user
    //codes would have in common
    public String filenameCreate(int usrcode, int reccode){
        if(usrcode > reccode){
            return reccode + "AND" + usrcode;
        }
        else if(reccode > usrcode){
            return usrcode + "AND" + reccode;
        }
        else{
            return "ONLY" + usrcode;
        }
    }
}
