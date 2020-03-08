package net.adrianh.jchat.shared;

import java.io.Serializable;
import java.util.Objects;

public final class Message implements Serializable {

    private String msg;
    private User sender;
    private String chat;

    public Message(User sender, String msg, String chat) {
        this.sender = sender;
        this.msg = msg;
        this.chat = chat;
    }


    public String getMsg() {
        return this.msg;
    }

    public User getSender() {
        return this.sender;
    }

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