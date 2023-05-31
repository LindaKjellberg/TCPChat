package TCPChatt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    //Listens for incoming connections and clients, creates socket object to communication between them
    private ServerSocket serverSocket;

    //Insert constructor to set up server socket

    public Server (ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    //Method startServer, responsible for keep Server running
    public void startServer(){
        //Input error handling
        try {
            //While loop to keep Server running indefinitely
            while (!serverSocket.isClosed()){   //Waiting for client to connect while serverSocket isn't closed
                Socket socket = serverSocket.accept();   //Blocking method: program is halted here until client connects.
                // When a client does connect, a socket object is returned which can be used to communicate with the client.
                System.out.println("A new client has connected to server!");
                ClientHandler clientHandler = new ClientHandler(socket);

                //Each object of this class (with the constructor from our socket method) will be responsible for communicating with the client, and it will implement the interface Runnable.
                //Runnable is implemented on a Class whose instances will each be executed by a separate Thread, this is vital to the function of the application.
                // If we didn't spawn a new Thread to handle the connection to each new client, our application would only be able to handle one client at a time.
                //When a class implements the Runnable interface the instances of the class will be executed by a separate Thread, specifically whatever is in the overwritten Run()-method of the class that implements Runnable is what is executed on a separate Thread.
                //To spawn a new Thread, we first need to create a Thread object, and then pass in our object that is an instance of a class that implements Runnable.

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        }catch (IOException e){

        }

    }
    //Method to avoid using nested try-blocks in our startServer, and if an error occurs we just want to shut down our server socket
    public void closeServerSocket(){
        try{    //Make sure serverSocket is not null, if it is then we get a null-pointer exception if we call close on it
            if (serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //Main method to instantiate object and run
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234); //Pass in port number
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
