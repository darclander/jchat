import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.beans.PropertyChangeSupport;

public class ChatClient {

    // TODO: Load settings from file on disk
    private static String SERVER_ADDRESS = "178.62.204.237";
    private static int PORT = 64206;
    private final PropertyChangeSupport obs = new PropertyChangeSupport(this);
    private GUI view;
    private User user;
    private Controller controller;
    private Queue<Message> chatLog;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;

    public ChatClient() {
        chatLog = new LinkedList<>(); // TODO: Save log to disk
        view = new GUI(this);
        controller = new Controller(this,view);
    }

    public void setUser(String name) {
        this.user = new User(name);
        notifyObservers("usernameChosen",user);
    }
    public User getUser() {return this.user;}

    public void sendText(User sender, String text) {
        try {
            Message newMsg = new Message(sender,text);
            socketOut.writeObject(newMsg);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Queue<Message> getChatLog() {
        return chatLog;
    }

    private void connectAndListen() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS,PORT);
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
            //socketOut.write
            while (true) {
                try {
                    Message incomingMsg = (Message)socketIn.readObject();
                    chatLog.add(incomingMsg);
                    notifyObservers("newMsg",incomingMsg);
                    System.out.println(incomingMsg);
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
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
        client.connectAndListen();
    }
}
