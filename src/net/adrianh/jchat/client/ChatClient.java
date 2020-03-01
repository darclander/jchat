package net.adrianh.jchat.client;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.beans.PropertyChangeSupport;
import net.adrianh.jchat.shared.*;

public class ChatClient {

    // TODO: Load settings from file on disk
    private static final String SERVER_ADDRESS = "localhost"; // TODO: Store on disk
    private static final int PORT = 64206;
    private final PropertyChangeSupport obs = new PropertyChangeSupport(this);
    private User user;
    private Chat currentChat;
    private Set<Chat> chatsJoined;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;

    public ChatClient() {
        GUI view = new GUI();
        Controller controller = new Controller(this,view);
        this.addObserver(view);
        chatsJoined = new HashSet<>();
    }

    public void setUser(String name) {
        this.user = new User(name);
        notifyObservers("usernameChosen",user);
    }
    public User getUser() {return this.user;}

    public void sendText(User sender, String text) {
        try {
            Message newMsg = new Message(sender,text, this.currentChat.getName());
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
            try( Socket socket = new Socket(SERVER_ADDRESS,PORT) ) {
                socketOut = new ObjectOutputStream(socket.getOutputStream());
                socketIn = new ObjectInputStream(socket.getInputStream());
                // Send username to server
                socketOut.writeObject(user);
                while (true) {
                    try {
                        Object o = socketIn.readObject();
                        // Handle incoming messages
                        if (o instanceof Message) {
                            Message incomingMsg = (Message) o;
                            for (Chat c: chatsJoined) {
                                if (incomingMsg.getChat().equals(c.getName())) {
                                    c.getLog().add(incomingMsg);
                                    if (c.equals(currentChat)) {
                                        notifyObservers("newMsg",incomingMsg);
                                    }
                                }
                            }
                            System.out.println(incomingMsg);
                        }
                        // Handle chat joins
                        if (o instanceof Chat) {
                            currentChat = (Chat) o;
                            chatsJoined.add(currentChat);
                            notifyObservers("chatChange",currentChat);
                        }

                    } catch(ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}