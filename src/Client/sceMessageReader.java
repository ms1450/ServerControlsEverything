package Client;


import java.io.BufferedReader;
import java.io.IOException;

public class sceMessageReader extends Thread {
    BufferedReader reader;
    public sceMessageReader(BufferedReader reader){
        this.reader = reader;
    }

    public void run(){

        while(sceClient.running){
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line != null && line.equals("QUIT")) {
                System.err.println("Host Disconnected.");
                sceClient.running = false;
            }
            if(line != null) System.out.println("> "+ line);
        }
    }
}
