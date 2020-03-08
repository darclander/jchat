package net.adrianh.jchat.shared;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contains information about the user
 * @author Adrian Håkansson, adrhak@student.chalmers.se
 * @version 2020/03/08
 */
public final class User implements Serializable {

    private String name;
    //private User[] friends;

    /**
     * @param name the name of the user to be created
     */
    public User(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the user
     * @return the name of the user
     */
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        User other = (User)o;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() { return this.name; }

}