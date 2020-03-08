package net.adrianh.jchat.shared;

import java.io.Serializable;
import java.util.*;

public class Chat implements Serializable {
    private String name;
    private Set<User> members; // Maybe not a good idea?
    private List<Message> log;

    public Chat(String name, Set<User> members) {
        this.name = name;
        this.members = members;
        this.log = new LinkedList<>();
    }

    public String getName() { return this.name;}
    public Set<User> getMembers() { return this.members;}
    public List<Message> getLog() { return Collections.unmodifiableList(this.log);}


    public void addMember(User user) {
        this.members.add(user);
    }
    public void removeMember(User user) {
        this.members.remove(user);
    } // (Not implemented)

    public void addMessage(Message msg) {
        this.log.add(msg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Chat other = (Chat)o;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
