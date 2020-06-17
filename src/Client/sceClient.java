package Client;

// Imports for this Class
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class sceClient {
    //The Server IP Address
    final static String IPADDRESS = "127.0.0.1";
    //Port Number to establish TCP Connection on
    final static int PORTNUM = 5439;
    //Running state of the Thread
    static boolean running = false;

    public static void main(String [] args) throws IOException{
        //Establish a TCP connection with the server
        Socket socket = new Socket(IPADDRESS,PORTNUM);
        System.out.println("Connection Established with Server on IP:"+IPADDRESS+" Port:" +PORTNUM);

        //Get the OutputStream and InputStream as well as User Input Stream
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Scanner userinp = new Scanner(System.in);

        //Get the User Code
        System.err.print("Enter User Code: ");
        int usrcode = Integer.parseInt(userinp.nextLine());

        //Send out the User Code to the Server
        writer.println("USRCODE "+usrcode);
        writer.flush();

        //Read if the User Code gets Accepted or Denied
        String line = "";
        try {
            line = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Server sends CNFD
        if(line.equals("CNFD")){
            System.out.println("User Code Confirmed");

            //Get the Recipient User Code
            System.err.println("Enter Recipient User Code: ");
            int recpusrcode = Integer.parseInt(userinp.nextLine());

            //Send out Recipient User Code to the Server
            writer.println("RECP_USRCODE "+recpusrcode);
            writer.flush();

            //Start the Conversation Method
            initiateConversation(writer, input, userinp);
        }
        else if(line.equals("DENY")){
            //Server sends DENY
            System.err.println("Another Connection with the Same User Code Active");
        }
        closeConnection(socket,writer,input,userinp);
    }

    //Closes the Socket and all the Data Streams
    public static void closeConnection(Socket socket, PrintWriter output, BufferedReader input, Scanner userinput) throws IOException {
        input.close();
        output.close();
        userinput.close();
        socket.close();
        System.out.println("Connection with IP:" + IPADDRESS + " closed Successfully.");
    }

    //Starts the reader thread as well as takes in user input and sends it to the server
    static void initiateConversation(PrintWriter output, BufferedReader input, Scanner userinp) throws IOException {
        //Starts the sceMessageReader Thread
        Thread reader = new sceMessageReader(input);
        reader.start();

        //Runs till User sends a QUIT, or the Server sends out a QUIT
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
    }
}
