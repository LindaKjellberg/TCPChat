package TCPChatt;

/*The ClientHandler Class, doesn't have a main method, instead it is being run from the Thread in the Server Class when we call start on the Thread.
In this Class we will ____________________________ with blocking operations.
 We implement the Runnable interface to broadcast the messages from clients to each other through the Server Class */

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    //Static array list of every ClientHandler-object we've instantiated, every instance of this class
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); //Keeps track of all our clients and allows us to broadcast to multiple clients
    private Socket socket;
    private BufferedReader bufferedReader; //Used to read messages sent from clients
    private BufferedWriter bufferedWriter; //Used to send messages to clients from other clients, broadcasted from the ArrayList
    private String clientUserName;

    //Constructor
    public ClientHandler(Socket socket) {
        //Set up class properties inside try-block
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //This Stream to send things from. Wrapping our OutputStream which is a byte-type, in a OutputStreamWriter which is a character-type, in a BufferedWriter.
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));   //This Stream to read things from. Repeating previous step for InputStreamReader
            this.clientUserName = bufferedReader.readLine(); //What will be sent over from the client is a line of the typed in text, and when they hit space we get a new line and the buffer starts over. We read from a bufferedReader, along with that a new line will be sent over.
            clientHandlers.add(this);   //Adds client to chat
            broadcastMessage("SERVER: " + clientUserName + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);    //Shuts down everything (entire communication)
        }
    }

    //The run method is listening for messages, and it is another blocking operation, meaning the program will be stuck unless a message is received.
    //If we weren't multiple Threads our program would be stuck waiting for a message from the client.
    @Override
    public void run() { //Everything in the run method is what is run on a separate Thread. We want to listen to separate messages.
        String messageFromClient;   //Holds message from client
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();  //The program will halt here until we get a message from the client
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;  //When the client disconnects, we break out of the while loop
            }
        }
    }

    //We will have another method handling the rest of our operations, otherwise our program will be stuck waiting for messages to come in.
    //We also want to be able to send messages, we don't want to have to wait for a message before we can actually send one.
    public void broadcastMessage(String messageToSend) {
        //Loop through ArrayList of clientHandlers to send the message to each client connected
        for (ClientHandler clientHandler : clientHandlers) { //For each clientHandlers in our ArrayList the clientHandler object will represent clientHandlers for each time/in each iteration of the loop
            try {
                if (!clientHandler.clientUserName.equals(clientUserName)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine(); //Sends over a new line when the user pressen enter, which tells the program we're done writing
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    //This method removes client from chat when a user disconnects
    public void removeClientHandler(){
        clientHandlers.remove(this);    //Removes client from chat
        broadcastMessage("SERVER: " + clientUserName + " has left the chat!");
    }

    //This method will close down the connection and streams when a user disconnects
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();  //Here is where we call the method and the client is removed. We only need to close the outer wrapper
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close(); //Closing the socket will also close its input- and output-stream
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
