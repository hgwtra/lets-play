package com.example.letsplay.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.example.letsplay.model.User;
import com.example.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class UserController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        List<User> userList = userRepo.findAll();

        if (!userList.isEmpty()){
            return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No users available", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUsers(@RequestBody User user){
        try {
            userRepo.save(user);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getSingleUser(@PathVariable("id") String id){
        Optional<User> userOptional =userRepo.findById(id);
        if (userOptional.isPresent()) {
            return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found with id" + id, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateById(@PathVariable("id") String id, @RequestBody User user){
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            User userToSave = userOptional.get();
            userToSave.setName(user.getName() != null ? user.getName() : userToSave.getName());
            userToSave.setEmail(user.getEmail() != null ? user.getEmail() : userToSave.getEmail());
            userToSave.setPassword(user.getPassword() != null ? user.getPassword() : userToSave.getPassword());
            userToSave.setRole(user.getRole() != null ? user.getRole() : userToSave.getRole());
            userRepo.save(userToSave);
            return new ResponseEntity<>(userToSave, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found with id" + id, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id) {
        try {
            userRepo.deleteById(id);
            return new ResponseEntity<>("Successfully deleted id" + id, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
