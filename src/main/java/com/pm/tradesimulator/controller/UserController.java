package com.pm.tradesimulator.controller;

import com.pm.tradesimulator.model.user.User;
import com.pm.tradesimulator.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //  GET /api/users — list all users
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<Map<String, String>> users = userService.getAllUsers().stream()
                .map(u -> Map.of(
                        "id",   u.getId(),
                        "name", u.getName()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    //  GET /api/users/active — get active user
    @GetMapping("/users/active")
    public ResponseEntity<?> getActiveUser() {
        User user = userService.getActiveUser();
        return ResponseEntity.ok(Map.of(
                "id",   user.getId(),
                "name", user.getName()
        ));
    }

    //  POST /api/users/active — switch active user
    @PostMapping("/users/active")
    public ResponseEntity<?> setActiveUser(@RequestBody Map<String, String> body) {
        try {
            String userId = body.get("userId");
            userService.setActiveUser(userId);
            User user = userService.getActiveUser();
            return ResponseEntity.ok(Map.of(
                    "id",      user.getId(),
                    "name",    user.getName(),
                    "message", "Switched to " + user.getName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}