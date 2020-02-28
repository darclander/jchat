package net.adrianh.jchat.server;

import java.io.*;
import java.net.*;
import java.util.*;
import net.adrianh.jchat.shared.*;

public class ChatServer {
    private static int PORT = 64206;
    private HashMap<User,ClientHandler> currentClients;
    private List<Message> chatLog;

    public ChatServer() {
        currentClients = new HashMap<>();
        chatLog = new LinkedList<>();

    }

    private void start() {
        try (ServerSocket servSocket = new ServerSocket(PORT)) {
            while (true) {
                // Wait for connection
                Socket clientSocket = servSocket.accept();
                // Create object streams for socket
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                System.out.println("Client " +clientSocket.getInetAddress()+" connected");
                // Assign a new thread for the session
                ClientHandler client = new ClientHandler(this, clientSocket, ois, oos);
                Thread t = new Thread(client);
                t.start();
                System.out.println("Assigned new thread for client");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<User,ClientHandler> getCurrentClients() {
        return this.currentClients;
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }

    // Send the message to every client in currentClients
    public void broadcastMessage(Message msg) {
        for(Map.Entry<User,ClientHandler> entry: currentClients.entrySet()) {
            try {
                // Check if the current client is a member of the chat
                if (msg.getChat().getMembers().contains(entry.getKey())) {
                    entry.getValue().getOutputStream().writeObject(msg);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClient(ClientHandler c) {
        currentClients.remove(c);
    }

    // Ersätt med defensiv kopia (defensive copying.)
    // Kolla noga på hjälpklassen java.util.Collections
    public List<Message> getChatLog() { return this.chatLog;}

    // A new ClientHandler object is created for every new session
    static class ClientHandler implements Runnable {

        private ChatServer server;
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        public ClientHandler(ChatServer server, Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
            this.server = server;
            this.socket = socket;
            this.ois = ois;
            this.oos = oos;
        }

        public void initialConnection() {

            // Send all messages stored in chatLog to the new client
            for (Message m: server.getChatLog()) {
                try {
                    oos.writeObject(m);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            // Wait for client to identify itself and add the client to currentClients
            try {
                User connectingUser = (User)ois.readObject();
                server.getCurrentClients().put(connectingUser,this);

            } catch(ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }

        public ObjectOutputStream getOutputStream() { return this.oos;}

        @Override
        public void run() {
            initialConnection();
            try {
                while (!socket.isClosed()) {
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
}