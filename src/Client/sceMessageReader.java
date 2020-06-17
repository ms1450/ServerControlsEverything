package Client;

//Imports for this class
import java.io.BufferedReader;
import java.io.IOException;

//Helper class that prints out all the messages the server is sending to the clients display
public class sceMessageReader extends Thread {
    BufferedReader reader;

    //constructor for sceMessageReader
    public sceMessageReader(BufferedReader reader){
        this.reader = reader;
    }

    //Runs for the entire duration of the connection, printing out server messages
    public void run(){
        while(sceClient.running){
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Checks if server has sent a QUIT
            if (line != null && line.equals("QUIT")) {
                System.err.println("Server Disconnected.");
                sceClient.running = false;
            }
            if(line != null) System.out.println("> "+ line);
        }
    }
}
