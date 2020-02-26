package net.adrianh.jchat.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Queue;

public class Chat implements Serializable {
    private String name;
    private List<User> members;
    private Queue<Message> log;

    public Chat(String name, List<User> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() { return this.name;}
    public List<User> getMembers() { return this.members;}
    public Queue<Message> getLog() { return this.log;} // TODO: Defensive copying

    public void addMember(User user) {
        this.members.add(user);
    }
    public void removeMember(User user) {
        this.members.remove(user);
    }

    public void addMessage(Message msg) {
        this.log.add(msg);
    }

}
