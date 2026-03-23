package com.pm.tradesimulator.service.user;

import com.pm.tradesimulator.model.user.User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, User> users = new LinkedHashMap<>();
    private String activeUserId = "alice";

    public UserService() {
        // create 3 users at startup
        users.put("alice",   new User("alice",   "Alice"));
        users.put("bob",     new User("bob",     "Bob"));
        users.put("charlie", new User("charlie", "Charlie"));
    }

    public User getActiveUser() {
        return users.get(activeUserId);
    }

    public void setActiveUser(String userId) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("Unknown user: " + userId);
        }
        activeUserId = userId;
    }

    public User getUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Unknown user: " + userId);
        }
        return user;
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public String getActiveUserId() {
        return activeUserId;
    }
}