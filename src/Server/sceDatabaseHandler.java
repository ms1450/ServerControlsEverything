package Server;

import java.io.*;
import java.util.ArrayList;

public class sceDatabaseHandler {
    final String userslistPATH = "D:\\SCE\\userslist.txt";
    final String preConvPATH = "D:\\SCE\\Conversations\\";
    final String afterConvPATH = ".txt";
    final String endString = "-------------------------------END-------------------------------";

    public void sendOutMessages(String filename, PrintWriter output) throws IOException {
        File file = new File(preConvPATH + filename + afterConvPATH);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            output.println(st);
            output.flush();
        }
    }

    public ArrayList<String> getUserCodes() throws IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader(userslistPATH));
        ArrayList<String> users = new ArrayList<>();
        String line = bufReader.readLine();
        while (line != null) {
            users.add(line);
            line = bufReader.readLine();
        }
        bufReader.close();
        return users;
    }

    public void addUser(String usercode) {
        try{
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(userslistPATH, true)));
            writer.println(usercode);
            writer.flush();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    public void createConversation(String filename){
        try{
            FileWriter fileWriter = new FileWriter(preConvPATH + filename + afterConvPATH, true);
            fileWriter.write("-----------------------------" + filename + "-----------------------------\r\n");
            fileWriter.write(endString);
            fileWriter.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public boolean findConversation(String filename){
        File file = new File(preConvPATH + filename + afterConvPATH);
        return file.exists();
    }

    public void addToConversation(String filename, int usercode, String text){
        try{
            removeLastLine(preConvPATH + filename + afterConvPATH);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(preConvPATH + filename + afterConvPATH, true)));
            writer.println(usercode + " : " + text);
            writer.println(endString);
            writer.flush();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    void removeLastLine(String filename) throws IOException {
        RandomAccessFile f = new RandomAccessFile(filename, "rw");
        long length = f.length() - 1;
        byte b;
        do {
            length -= 1;
            f.seek(length);
            b = f.readByte();
        } while(b != 10 && length > 0);
        if (length == 0) {
            f.setLength(length);
        } else {
            f.setLength(length + 1);
        }
    }
}
