package net.adrianh.jchat.client;

import net.adrianh.jchat.shared.Chat;
import net.adrianh.jchat.shared.JoinRequest;
import net.adrianh.jchat.shared.Message;
import net.adrianh.jchat.shared.User;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * The main class and model for the client application.
 * It stores data for the client such username, chats joined and the currently active chat.
 * @author Adrian Håkansson, adrhak@student.chalmers.se
 * @version 2020/03/08
 */
public class ChatClient {

    private static String SERVER_ADDRESS = "localhost";
    private static int PORT = 64206;
    private final PropertyChangeSupport obs = new PropertyChangeSupport(this);
    private User user;
    private Chat currentChat;
    private Set<Chat> chatsJoined;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private Sound msgSound;
    private Controller controller;

    public ChatClient() {
        loadSettings();
        GUI view = new GUI();
        controller = new Controller(this,view);
        this.addObserver(view);
        chatsJoined = new HashSet<>();
        msgSound = new Sound("message");


    }

    /**
     * Loads the config file from disk
     */
    private void loadSettings() {
        try {
            File settingsFile = new File(System.getProperty("user.home")+"/jChat/jChat.conf");
            settingsFile.getParentFile().mkdirs();
            if (settingsFile.exists()) {
                Scanner sc = new Scanner(settingsFile);
                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    if (str.split("=")[0].equals("SERVER_ADDRESS")) {
                        SERVER_ADDRESS = str.split("=")[1];
                    }
                    if (str.split("=")[0].equals("PORT")) {
                        PORT = Integer.parseInt(str.split("=")[1]);
                    }
                }
            } else { // Create settings file with default values
                settingsFile.createNewFile();
                try (FileWriter fw = new FileWriter(settingsFile,true);
                     BufferedWriter bw = new BufferedWriter(fw))  {
                        bw.write("SERVER_ADDRESS="+SERVER_ADDRESS);
                        bw.write("\nPORT="+PORT);
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch(NumberFormatException e) {
            notifyObservers("wrongConfig",e);
        }
    }

    /**
     * Gets the chats this user has joined
     * @return a set of the chats this user has joined
     */
    public Set<Chat> getChatsJoined() { return this.chatsJoined; }

    /**
     * Changes the username for this client
     * @param name The new username for this client
     */
    public void setUser(String name) {
        this.user = new User(name);
        notifyObservers("usernameChosen",user);
    }

    /**
     * Changes the currently active chat and notifies the view
     * @param chat The chat to be set as the currently active chat
     */
    public void setCurrentChat(Chat chat) {
        this.currentChat = chat;
        notifyObservers("chatChange",this.currentChat);
    }

    /**
     * Assigns a new thread for a new Listener object that tries to connect to the server
     */
    public void connectAndListen() {
        Listener serverListener = new Listener();
        Thread listenerThread= new Thread(serverListener);
        listenerThread.start();
    }

    /**
     * Creates a Message object and sends it to the server
     * @param text The message to be sent
     */
    public void sendText(String text) {
        try {
            Message newMsg = new Message(this.user,text, this.currentChat.getName());
            socketOut.writeObject(newMsg);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Contacts the server with a request to join a chat room
     * @param chat The name of the chat the client tries to join
     */
    public void sendJoinRequest(String chat) {
        try {
            JoinRequest request = new JoinRequest(this.user,chat);
            socketOut.writeObject(request);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds an observer for the model
     * @param obs The listener object
     */
    public void addObserver(final PropertyChangeListener obs) {
        this.obs.addPropertyChangeListener(obs);
    }

    /**
     * Notify the observers of a change
     * @param pName The name of the change event
     * @param o The new version of the changed object
     */
    public void notifyObservers(String pName, Object o) {
        obs.firePropertyChange(pName, null, o);
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
    }

    class Listener implements Runnable {

        /**
         * Connects to the server, identifies itself and waits for incoming messages
         */
        @Override
        public void run() {
            try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
                socketOut = new ObjectOutputStream(socket.getOutputStream());
                socketIn = new ObjectInputStream(socket.getInputStream());
                // Send username to server
                socketOut.writeObject(user);
                controller.joinChat(); // Hacky way to make 'default' chat appear without user interaction
                while (true) {
                    try {
                        Object o = socketIn.readObject();
                        // Handle incoming messages
                        if (o instanceof Message) {
                            Message incomingMsg = (Message) o;
                            for (Chat c : chatsJoined) {
                                if (incomingMsg.getChat().equals(c.getName())) {
                                    c.addMessage(incomingMsg);
                                    if (c.equals(currentChat)) {
                                        notifyObservers("newMsg", incomingMsg);
                                        if (!incomingMsg.getSender().equals(user)) {
                                            msgSound.play();
                                        }
                                    }
                                }
                            }
                        }
                        // Handle chat joins
                        if (o instanceof Chat) {
                            currentChat = (Chat) o;
                            chatsJoined.add(currentChat);
                            notifyObservers("chatChange", currentChat);

                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch(NoRouteToHostException | UnknownHostException | ConnectException e) {
              notifyObservers("noConnection",e);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}