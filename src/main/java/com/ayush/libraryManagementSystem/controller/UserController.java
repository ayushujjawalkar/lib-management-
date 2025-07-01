package com.ayush.libraryManagementSystem.controller;

import com.ayush.libraryManagementSystem.service.UserService;
import com.ayush.libraryManagementSystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*") // Allow all for testing
@RestController
@RequestMapping("/api/users")
public class UserController
{
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers()
    {

        return userService.findAll();
    }

    @PostMapping
    public User addUser(@RequestBody User user )
    {

        return userService.save(user);
    }

}
