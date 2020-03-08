package net.adrianh.jchat.server;

import net.adrianh.jchat.shared.Chat;
import net.adrianh.jchat.shared.JoinRequest;
import net.adrianh.jchat.shared.Message;
import net.adrianh.jchat.shared.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * The server-side application. Handles messages and keeps track of chat rooms
 * @author Adrian Håkansson, adrhak@student.chalmers.se
 * @version 2020/03/08
 */
public class ChatServer {
    private static final String FILE_PATH = System.getProperty("user.home")+"/jChat-server";
    private static final int PORT = 64206;
    private HashMap<ClientHandler,User> currentClients;
    private Set<Chat> chatSet;
    private Chat defaultChat;
    public ChatServer() {
        currentClients = new HashMap<>();
        chatSet = new HashSet<>();
        defaultChat = new Chat("default",new HashSet<>());
        chatSet.add(defaultChat);
        readSavedLog(defaultChat);
    }

    /**
     * Starts the server
     */
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

    /**
     * Gets the currently connected clients
     * @return the map of current clients
     */
    public HashMap<ClientHandler,User> getCurrentClients() {
        return this.currentClients;
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }

    /**
     * Sends a message to every active client belonging to the group of the message
     * @param msg the message to be sent
     */
    public void broadcastMessage(Message msg) {
        Chat chat = this.defaultChat;
        // Find the chat (if it's not the default)
        if (!msg.getChat().equals(this.defaultChat.getName())) {
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

    /**
     * Removes a client from the list of currently active clients
     * @param c the client to be removed
     */
    public void removeClient(ClientHandler c) {
        currentClients.remove(c);
    }

    /**
     * Gets the set of all chats created
     * @return the set of all chats
     */
    public Set<Chat> getChatSet() { return this.chatSet;}

    /**
     * Reads a saved chat object from a location on disk
     * @param c the chat to be read
     */
    public void readSavedLog(Chat c) {
        try {
            File file = new File(FILE_PATH+"/logs/"+c.getName()+".txt");
            if (file.exists()) {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    String name = str.split(">")[0].substring(1);
                    String message = str.split(">")[1].substring(1);
                    User tempUser = new User(name);
                    c.addMessage(new Message(tempUser, message, c.getName()));
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }



    // A new ClientHandler object is created for every new session
    static class ClientHandler implements Runnable {

        private ChatServer server;
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        /**
         * @param server the parent server object
         * @param socket the socket for the connection to the client
         * @param ois the object input stream
         * @param oos the object output stream
         */
        public ClientHandler(ChatServer server, Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
            this.server = server;
            this.socket = socket;
            this.ois = ois;
            this.oos = oos;
        }

        /**
         * Waits for client to identify with a username
         */
        public void initialConnection() {

            // Wait for client to identify itself and add the client to currentClients
            try {
                User connectingUser = (User)ois.readObject();
                server.getCurrentClients().put(this,connectingUser);

            } catch(ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Adds the client to the requested chat or creates the chat if it does not exist
         * @param request the request to be processed
         */
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
            server.readSavedLog(newChat);
            try {
                oos.writeObject(newChat);
            } catch(IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * Gets the object output stream
         * @return the object output stream
         */
        public ObjectOutputStream getOutputStream() { return this.oos;}

        /**
         * Handles both incoming messages and join requests
         */
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
                                c.addMessage(incomingMsg);
                                File chatFile = new File(FILE_PATH+"/logs/"+c.getName()+".txt");
                                if (!chatFile.exists()) {
                                    chatFile.getParentFile().mkdirs();
                                    chatFile.createNewFile();
                                }
                                try (FileWriter fw = new FileWriter(chatFile,true);
                                    BufferedWriter bw = new BufferedWriter(fw);
                                    PrintWriter out = new PrintWriter(bw))
                                {
                                    out.print(incomingMsg);
                                }

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