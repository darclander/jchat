import java.io.Serializable;
import java.util.Objects;

public final class User implements Serializable {

    private String name;
    //private User[] friends;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null | getClass() != o.getClass()) { return false; }
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
