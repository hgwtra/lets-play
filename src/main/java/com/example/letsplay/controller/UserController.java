package com.example.letsplay.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.User;
import com.example.letsplay.repository.UserRepository;
import com.example.letsplay.service.UserService;
import jakarta.validation.ConstraintViolationException;
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
    @Autowired
    private UserService userService;

    //Get all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() throws UserException {
        List<User> userList = userService.getAllUser();
        return new ResponseEntity<>(userList, !userList.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    //Create a new user
    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@RequestBody User user){
        try {
            userService.createUser(user);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //Get single user
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getSingleUser(@PathVariable("id") String id){
        try {
            return new ResponseEntity<>(userService.getSingleUser(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //Update a user
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateById(@PathVariable("id") String id, @RequestBody User user) {
        try {
            userService.updateUser(id, user);
            return new ResponseEntity<>("Updated User with id" + id, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //Delete a user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id) throws UserException {
            try{
                userService.deleteUser(id);
                return new ResponseEntity<>("Successfully deleted user with id " + id, HttpStatus.OK);
            } catch (UserException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
}

