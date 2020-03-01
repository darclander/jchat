package net.adrianh.jchat.server;

import java.io.*;
import java.net.*;
import java.util.*;
import net.adrianh.jchat.shared.*;

public class ChatServer {
    private static final int PORT = 64206;
    private HashMap<ClientHandler,User> currentClients;
    private Set<Chat> chatSet;
    private Chat defaultChat;
    public ChatServer() {
        currentClients = new HashMap<>();
        chatSet = new HashSet<>();
        defaultChat = new Chat("default",new HashSet<>());
        chatSet.add(defaultChat);

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

    public HashMap<ClientHandler,User> getCurrentClients() {
        return this.currentClients;
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }

    // Send the message to every client in currentClients
    public void broadcastMessage(Message msg) {
        Chat chat = this.defaultChat;
        // Find the chat (if it's not the default)
        if (!msg.getChat().equals("default")) {
            for (Chat c: this.chatSet) {
                if (c.getName().equals(msg.getChat())) {
                    chat = c;
                    break;
                }
            }
        }
        // Find all active clients that are members of the specific chat
        for(Map.Entry<ClientHandler,User> entry: currentClients.entrySet()) {
            try {
                // Check if the current client is a member of the chat
                if (chat.getMembers().contains(entry.getValue())) {
                    // Send the message
                    entry.getKey().getOutputStream().writeObject(msg);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClient(ClientHandler c) {
        currentClients.remove(c);
    }

    public Set<Chat> getChatSet() { return this.chatSet;}

    public Chat getDefaultChat() { return this.defaultChat;}



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

            // Wait for client to identify itself and add the client to currentClients
            try {
                User connectingUser = (User)ois.readObject();
                server.getCurrentClients().put(this,connectingUser);
                System.out.println(connectingUser);
                // Connect user to default chat
                server.getDefaultChat().addMember(connectingUser);
                oos.writeObject(server.getDefaultChat());

            } catch(ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }

        // Handles join requests
        private void joinChat(JoinRequest request) {
            for (Chat c: server.getChatSet()) {
                if (request.getChatRequest().equals(c.getName())) {
                    c.addMember(request.getUser());
                    System.out.println(request.getUser().getName() + " joined "+ request.getChatRequest());
                    try {
                        oos.writeObject(c); // Send response to client with the full chat object
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            // Create chat if it does not exist
            Set<User> members = new HashSet<>();
            members.add(request.getUser());
            Chat newChat = new Chat(request.getChatRequest(),members);
            server.getChatSet().add(newChat);
            try {
                oos.writeObject(newChat);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        public ObjectOutputStream getOutputStream() { return this.oos;}

        @Override
        public void run() {
            initialConnection();
            try {
                while (!socket.isClosed()) {
                    // Received object can either be a message or a join request
                    Object o = ois.readObject();

                    if (o instanceof Message) {
                        Message incomingMsg = (Message) o;
                        // Find the chat object referenced by getChat() string (maybe turn into separate method?)
                        for(Chat c: server.getChatSet()) {
                            if (incomingMsg.getChat().equals(c.getName())) {
                                c.getLog().add(incomingMsg);
                            }
                        }
                        System.out.println(incomingMsg);
                        server.broadcastMessage(incomingMsg);
                    }

                    if (o instanceof JoinRequest) {
                        JoinRequest request = (JoinRequest) o;
                        joinChat(request);
                    }

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