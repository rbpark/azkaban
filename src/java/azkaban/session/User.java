package azkaban.session;

import java.util.Set;

public class User {
    private final int id;
    private final String username;
    private Set<String> usertype;
    private Set<String> groups;
    
    public User(int id, String name) {
        this.id = id;
        this.username = name;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}