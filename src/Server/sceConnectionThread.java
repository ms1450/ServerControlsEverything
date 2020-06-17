package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class sceConnectionThread extends Thread{
    Socket socket;
    int usrcode;
    int reccode;
    String filename;
    static volatile boolean running;

    BufferedReader input;
    PrintWriter output;
    sceDatabaseHandler database;

    sceConnectionThread(Socket client, int usrcode){
        this.socket = client;
        this.usrcode = usrcode;
        this.database = new sceDatabaseHandler();
    }

    public void run(){
        try{
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            ArrayList<String> usercodes = database.getUserCodes();
            if(usercodes.contains(Integer.toString(usrcode))){
                output.println("Welcome Back "+usrcode);
            }
            else{
                database.addUser(Integer.toString(usrcode));
                output.println("Added User to Database "+usrcode);
            }
            output.flush();

            reccode = Integer.parseInt(input.readLine().split(" ")[1]);
            filename = filenameCreate(usrcode,reccode);


            if(database.findConversation(filename)){
                database.sendOutMessages(filename, output);
            }
            else{
                database.createConversation(filename);
            }

            output.println("New Messages will now be Stored in the Database, Enter QUIT to Exit");
            output.flush();

            Thread storeData = new sceDataParser(input, filename, usrcode, database);
            storeData.start();
            while(true){
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
