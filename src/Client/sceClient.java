package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class sceClient {
    final static String IPADDRESS = "192.168.29.45";
    final static int PORTNUM = 5439;
    static boolean running = false;

    public static void main(String [] args) throws IOException{
        Socket socket = new Socket(IPADDRESS,PORTNUM);
        System.err.println("Connection Established with Server on IP:"+IPADDRESS+" Port:" +PORTNUM);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Scanner userinp = new Scanner(System.in);

        System.err.println("Enter User Code: ");
        int usrcode = Integer.parseInt(userinp.nextLine());

        writer.println("USRCODE "+usrcode);
        writer.flush();

        String line = "";
        try {
            line = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(line.equals("CNFD")){
            System.err.println("User Code Confirmed");
            System.err.println("Enter Recipient User Code: ");
            int recpusrcode = Integer.parseInt(userinp.nextLine());

            writer.println("RECP_USRCODE "+recpusrcode);
            writer.flush();

            initiateConversation(writer, input, userinp);
            socket.close();
        }
        else if(line.equals("DENY")){
            System.err.println("Another Connection with the Same User Code Active");
            System.err.println("Disconnected");
        }
    }

    static void initiateConversation(PrintWriter output, BufferedReader input, Scanner userinp) throws IOException {
        Thread reader = new sceMessageReader(input);
        reader.start();

        running = true;
        while(running){
            String line = userinp.nextLine();
            output.println(line);
            output.flush();
            if(line.equals("QUIT")){
                running = false;
                System.err.println("Closing Connection to Server.");
            }
        }
        input.close();
        output.close();
        System.err.println("Connection with IP:" + IPADDRESS + " closed Successfully.");
    }
}
