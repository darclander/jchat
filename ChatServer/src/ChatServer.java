import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static int PORT = 64206;
    private List<ClientHandler> currentClients;
    private Queue<Message> chatLog;

    public ChatServer() {
        currentClients = new ArrayList<>();
        chatLog= new LinkedList<>();
        try {
            ServerSocket servSocket = new ServerSocket(PORT);
            while (true) {
                // Wait for connection
                Socket clientSocket = servSocket.accept();
                // Create object streams for socket
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                System.out.println("Client " +clientSocket.getInetAddress()+" connected");
                // Assign a new thread for the session
                ClientHandler client = new ClientHandler(this, clientSocket, ois, oos);
                // Effectively keep track of this session to make "broadcasts" possible
                currentClients.add(client);
                Thread t = new Thread(client);
                t.start();
                System.out.println("Assigned new thread for client");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        new ChatServer();
    }

    // Send the message to every client in currentClients
    public void broadcastMessage(Message msg) {
        for (ClientHandler c: currentClients) {
            try {
                c.getOutputStream().writeObject(msg);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClient(ClientHandler c) {
        currentClients.remove(c);
    }

    public Queue<Message> getChatLog() { return this.chatLog;}
}

// A new ClientHandler object is created for every new session
class ClientHandler implements Runnable {

    private ChatServer server;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public ClientHandler(ChatServer server, Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        this.server = server;
        this.socket = socket;
        this.ois = ois;
        this.oos = oos;
        for (Message m: server.getChatLog()) {
            try {
                oos.writeObject(m);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        server.broadcastMessage(new Message(new User("server"),"A new client has connected"));
    }

    public ObjectOutputStream getOutputStream() { return this.oos;};

    @Override
    public void run() {
        try {
            while (true) {
                Message incomingMsg = (Message)ois.readObject();
                server.getChatLog().add(incomingMsg);
                System.out.println(incomingMsg);
                server.broadcastMessage(incomingMsg);
            }

        } catch(IOException e) { // Triggers when user closes the window
            try {
                ois.close();
                oos.close();
                socket.close();
                server.removeClient(this);
                System.out.println("Client " + socket.getInetAddress() + " disconnected");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        catch(ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }
}
