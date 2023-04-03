package com.example.demo.controller;

import com.example.demo.dto.CreateUserDTO;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/")
    public List<User> listUser() {
        return userService.listUsers();
    }

    @PostMapping("/")
    public ResponseEntity createUser(@RequestBody CreateUserDTO usr) {
        User u = new User();
        BeanUtils.copyProperties(usr, u);
        // TODO: Better error handling this is horrible.
        try {
            User createdUser = userService.createUser(u);
            return new ResponseEntity<User>(createdUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Email already used.", HttpStatus.BAD_REQUEST);
        }
    }

    // Update operation
    @PutMapping("/{id}")
    public User updateUser(@RequestBody User usr,
                           @PathVariable("id") String userId) {
        return userService.updateUser(
                usr, userId);
    }

    @DeleteMapping("/{id}")
    public String deleteDepartmentById(@PathVariable("id")
                                       String userId) {
        try {
            userService.deleteUser(userId);
            return "User deleted Successfully";
        } catch (RuntimeException e) {
            return "User not found";

        }
    }

}
