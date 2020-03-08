package net.adrianh.jchat.shared;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contains information that makes it possible to join specific groups and
 * pass join requests to the server
 * @author Adrian Håkansson, adrhak@student.chalmers.se
 * @version 2020/03/08
 */
public class JoinRequest implements Serializable {
    private User user;
    private String chatRequest;

    /**
     * @param user the requesting user
     * @param chatRequest the name of the chat that is requested
     */
    public JoinRequest(User user, String chatRequest) {
        this.user = user;
        this.chatRequest = chatRequest;
    }

    /**
     * Gets the name of the chat requested
     * @return the name of the chat
     */
    public String getChatRequest() {
        return this.chatRequest;
    }

    /**
     * Gets the requesting user
     * @return the user
     */
    public User getUser() {
        return this.user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        JoinRequest other = (JoinRequest) o;
        return user.equals(other.getUser()) && chatRequest.equals(other.getChatRequest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.user,this.chatRequest);
    }
}
