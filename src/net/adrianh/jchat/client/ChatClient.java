package net.adrianh.jchat.client;

import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.beans.PropertyChangeSupport;
import net.adrianh.jchat.shared.*;

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
                     BufferedWriter bw = new BufferedWriter(fw))
                {
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

    public void setUser(String name) {
        this.user = new User(name);
        notifyObservers("usernameChosen",user);
    }
    public User getUser() {return this.user;}

    public Set<Chat> getChatsJoined() { return this.chatsJoined; }

    public void sendText(String text) {
        try {
            Message newMsg = new Message(this.user,text, this.currentChat.getName());
            socketOut.writeObject(newMsg);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Chat getCurrentChat() {
        return this.currentChat;
    }

    public void setCurrentChat(Chat chat) {
        this.currentChat = chat;
        notifyObservers("chatChange",this.currentChat);
    }

    public void connectAndListen() {
        Listener serverListener = new Listener();
        Thread listenerThread= new Thread(serverListener);
        listenerThread.start();
    }

    public void sendJoinRequest(String chat) {
        try {
            JoinRequest request = new JoinRequest(this.user,chat);
            socketOut.writeObject(request);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }


    public void addObserver(final PropertyChangeListener obs) {
        this.obs.addPropertyChangeListener(obs);
    }

    public void removeObserver(final PropertyChangeListener obs) {
        this.obs.removePropertyChangeListener(obs);
    }

    public void notifyObservers(String pName, Object o) {
        obs.firePropertyChange(pName, null, o);
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
    }

    class Listener implements Runnable {

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