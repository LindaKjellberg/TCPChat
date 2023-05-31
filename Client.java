package TCPChatt;

/*The Client Class is very similar to the ClientHandler Class,
except in this class we will use threads to deal with blocking operations a little differently.
 We won't implement the Runnable interface*/

import java.util.Scanner;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    //Method to represent the Client with a Socket and a userName as arguments
    public Client(Socket socket, String userName){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //This Stream to send things from. Wrapping our OutputStream which is a byte-type, in a OutputStreamWriter which is a character-type, in a BufferedWriter.
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));   //This Stream to read things from. Repeating previous step for InputStreamReader
            this.userName = userName;
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    //Method sendMessage is the connection the Server has spawned to handle a Client
    public void sendMessage(){
        try{
            //Send the userName over to the ClientHandler (which is waiting for a userName to be entered), so the clientHandler can identify it
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush(); //Because we probably won't fill the buffer with the userName and newLine

            Scanner scanner = new Scanner(System.in); //Will continue to scan for messages while there's still a connection to the Server
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();  //Using the Scanner we get what the user typed into the console and send it over
                bufferedWriter.write(userName + ": " + messageToSend); //Use the bufferedWriter to attach the userName to the message
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    //Method to listen for messages which haven't been broadcasted yet, this is where we're going to use a new Thread again
    //As we will be listening for new messages, this will be a blocking operation - so we don't have to wait until we receive a message before we can send one.
    //Instead of implementing the Runnable interface like we did in the ClientHandler, we are just going to create a new Thread and pass the Runnable-object as a parameter.
    public void listenForMessage(){
        new Thread(new Runnable(){

            @Override
            public void run() { //What is placed within this overridden Run method is what will be executed on a separate Thread.
                String msgFromGroupChat;

                while (socket.isConnected()){   //While connection is made:
                    try{
                        msgFromGroupChat = bufferedReader.readLine();   //Read from GroupChat
                        System.out.print(msgFromGroupChat); //Print out what every other user said (sent from the Server)
                    } catch (IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }

            }
        }).start(); //Call start method on Thread object which is waiting for the broadcastMessage from the ClientHandler Class
    }

    //closeEverything method
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Main method to run everything
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);   //Scanner to take input from the keyboard for:
        System.out.println("Enter your username for the group chat: ");
        String userName = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, userName);

        //These two are blocking methods (because they have infinite while loops, while the socket is still connected)
        //But because they are on different Threads they can both run at the same time without halting each other
        client.listenForMessage();
        client.sendMessage();
    }
}
