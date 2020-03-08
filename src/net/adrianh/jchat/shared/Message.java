package net.adrianh.jchat.shared;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contains information about a message such as the text, the sender and the chat
 * it belongs to
 * @author Adrian Håkansson, adrhak@student.chalmers.se
 * @version 2020/03/08
 */
public final class Message implements Serializable {

    private String msg;
    private User sender;
    private String chat;

    /**
     * @param sender the user sending the message
     * @param msg the text of the message
     * @param chat the chat the message is sent in
     */
    public Message(User sender, String msg, String chat) {
        this.sender = sender;
        this.msg = msg;
        this.chat = chat;
    }

    /**
     * Gets the sending user
     * @return the sending user
     */
    public User getSender() {
        return this.sender;
    }

    /**
     * Gets the chat object
     * @return the chat
     */
    public String getChat() {
        return this.chat;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Message other = (Message)o;
        return msg.equals(other.msg) && sender.equals(other.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg,sender);
    }


    @Override
    public String toString() {
        return (String.format("<%s>:%s\n",this.sender.getName(),this.msg));
    }

}