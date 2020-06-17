package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class sceConnectionCreator {
    final static int PORTNUMBER = 5439;
    static ArrayList<Integer> usrcodes = new ArrayList<>();

    public static void main(String[] args) {
        ArrayList<Thread> clients = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(PORTNUMBER);
            while(true) {
                System.err.println("Listening on " + PORTNUMBER);
                Socket client = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter writer = new PrintWriter(client.getOutputStream());

                int usrcode = 0;
                try {
                    usrcode = Integer.parseInt(input.readLine().split(" ")[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (usrcodes.contains(usrcode)) {
                    writer.println("DENY");
                    writer.flush();
                    break;
                } else {
                    System.err.println("Connection #" + usrcode + " established with IP:" + client.getInetAddress().getHostAddress());
                    writer.println("CNFD");
                    writer.flush();

                    Thread thread = new sceConnectionThread(client, usrcode);
                    thread.start();

                    clients.add(thread);
                    usrcodes.add(usrcode);
                }
            }
        }catch (IOException ie){
            ie.printStackTrace();
        }
    }
}
