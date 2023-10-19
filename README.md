## Introduction

This is a simple client-server chat application that allows multiple clients to connect to a central server and exchange messages in a group chat using TCP communication protocol. It demonstrates the use of sockets, threads, and basic network communication in Java. The project is divided into three main classes: `Client`, `ClientHandler`, and `Server`.


## Project Structure

- **Client**: The `Client` class represents the client-side of the application. It connects to the server, sends messages, and listens for incoming messages from other clients. It utilizes threads to handle these operations.

- **ClientHandler**: The `ClientHandler` class is responsible for managing individual client connections on the server-side. Each connected client is handled by a separate `ClientHandler` thread. The `ClientHandler` class listens for incoming messages from clients and broadcasts them to other clients.

- **Server**: The `Server` class sets up a server socket, listens for incoming client connections, and spawns a new `ClientHandler` thread for each connected client. It is responsible for starting and managing the server.


## Usage

### Running the Server
To start the server, run the `Server` class by executing its `main` method. The server will listen for incoming connections on a specified port (in this case, port 1234).

```java
Server server = new Server(new ServerSocket(1234));
server.startServer();
```


### Running the Clients
To start a client, run the `Client` class by executing its `main` method. You will be prompted to enter a username for the group chat. Once a username is provided, the client will connect to the server and begin sending and receiving messages.

```java
Client client = new Client(new Socket("localhost", 1234), userName);
client.listenForMessage();
client.sendMessage();
```

### Group Chat
Clients can exchange messages in a group chat. Each message sent by a client is broadcasted to all other connected clients. The server acts as an intermediary to manage the communication between clients.


### Exiting the Application
To exit a client or the server, simply close the console or terminate the program. The application handles socket closure and cleanup.


## Important Notes
- The project uses multi-threading to allow multiple clients to connect and chat simultaneously.

- The server can handle multiple client connections at once, making it suitable for small-scale group chat applications.

- Error handling is in place to ensure the graceful closure of sockets and streams in case of errors or client disconnections.

- It is essential to have the server running before clients attempt to connect.

## Conclusion
This Java client-server chat application provides a foundation for building more advanced chat systems with enhanced features and security. It serves as a valuable example of network communication in a client-server architecture.
