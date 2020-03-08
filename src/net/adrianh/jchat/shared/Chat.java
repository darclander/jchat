package net.adrianh.jchat.shared;

import java.io.Serializable;
import java.util.*;

/**
 * Contains information about a chat's name, message log and its members
 * @author Adrian Håkansson, adrhak@student.chalmers.se
 * @version 2020/03/08
 */
public class Chat implements Serializable {
    private String name;
    private Set<User> members; // Maybe not a good idea?
    private List<Message> log;

    /**
     * @param name the name of the chat
     * @param members the initial set of chat members
     */
    public Chat(String name, Set<User> members) {
        this.name = name;
        this.members = members;
        this.log = new LinkedList<>();
    }

    /**
     * Gets the name of the chat
     * @return the chat name
     */
    public String getName() { return this.name;}

    /**
     * Gets the members of the chat
     * @return a set of the chat members
     */
    public Set<User> getMembers() { return this.members;}

    /**
     * Gets the full chat log
     * @return the chat log
     */
    public List<Message> getLog() { return Collections.unmodifiableList(this.log);}

    /**
     * Adds a member to the chat
     * @param user the user to be added
     */
    public void addMember(User user) {
        this.members.add(user);
    }

    /**
     * Adds a message to the chat log
     * @param msg the message to be added
     */
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
