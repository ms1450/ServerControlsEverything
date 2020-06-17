package Server;

import java.io.BufferedReader;
import java.io.IOException;

public class sceDataParser extends Thread{
    BufferedReader reader;
    String filename;
    int usrcode;
    sceDatabaseHandler database;

    public sceDataParser(BufferedReader reader,
                         String filename,
                         int usrcode,
                         sceDatabaseHandler database){
        this.reader = reader;
        this.filename = filename;
        this.usrcode = usrcode;
        this.database = database;
    }

    public void run() {
        boolean running = true;
        while(running){
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line != null && line.equals("QUIT")) {
                sceConnectionThread.running = false;
                running = false;
            }
            else if(line != null) {
                database.addToConversation(filename,usrcode,line);
            }
        }
    }
}
