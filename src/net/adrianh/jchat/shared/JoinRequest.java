package net.adrianh.jchat.shared;

import java.io.Serializable;

public class JoinRequest implements Serializable {
    private User user;
    private String chatRequest;

    public JoinRequest(User user, String chatRequest) {
        this.user = user;
        this.chatRequest = chatRequest;
    }

    public String getChatRequest() {
        return this.chatRequest;
    }

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
}
