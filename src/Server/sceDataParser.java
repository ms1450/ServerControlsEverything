package Server;

//imports for this method
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

//this method constantly reads a clients message and appends it to the database
public class sceDataParser extends Thread{
    //information about the client
    BufferedReader reader;
    PrintWriter writer;
    String filename;
    int usrcode;
    sceDatabaseHandler database;

    public sceDataParser(BufferedReader reader,
                         PrintWriter writer,
                         String filename,
                         int usrcode,
                         sceDatabaseHandler database){
        this.reader = reader;
        this.writer = writer;
        this.filename = filename;
        this.usrcode = usrcode;
        this.database = database;
    }

    public void run() {
        boolean running = true;
        while(running){
            String line = null;
            //read the data sent over by the client
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //client sends 'QUIT'
            if (line != null && line.equals("QUIT")) {
                sceConnectionThread.running = false;
                running = false;
            }
            //client sends 'REFRESH'
            else if(line != null && line.equals("REFRESH")){
                try {
                    database.sendOutMessages(filename,writer);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            //client sends a regular message
            else if(line != null) {
                database.addToConversation(filename,usrcode,line);
            }
        }
    }
}
