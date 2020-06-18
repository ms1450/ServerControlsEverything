package Server;

//imports for this class
import java.io.*;
import java.util.ArrayList;

//Handles the databse side of the program, it is used
//to create files and modify files
public class sceDatabaseHandler {
    //Fixed path for the userlist database and the conversations folder
    final String userslistPATH = "D:\\SCE\\userslist.txt";
    final String preConvPATH = "D:\\SCE\\Conversations\\";
    final String afterConvPATH = ".txt";
    final String endString = "-------------------------------END-------------------------------";

    //this method sends out messages from a txt file to a PrintWriter output stream
    public void sendOutMessages(String filename, PrintWriter output) throws IOException {
        File file = new File(preConvPATH + filename + afterConvPATH);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            output.println(st);
            output.flush();
        }
    }

    //this method gets a list of user codes that have ever connected to the sever
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

    //adds a new user to the userslist.txt along with the date and time they connected first
    public void addUser(String usercode) {
        try{
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(userslistPATH, true)));
            writer.println(usercode);
            writer.flush();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    //this method creates a txt file in the conversations folder based on the filename provided
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

    //this method looks for a txt file in the conversations folder based on the filename provided
    public boolean findConversation(String filename){
        File file = new File(preConvPATH + filename + afterConvPATH);
        return file.exists();
    }

    //this method adds a the string provided to a filename in the conversations folder
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

    //this helper function removes the last "---END---" line
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
