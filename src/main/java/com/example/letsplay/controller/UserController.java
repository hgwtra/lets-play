package com.example.letsplay.controller;

import java.util.List;
import java.util.Optional;

import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.User;
import com.example.letsplay.model.UserAuthentication;
import com.example.letsplay.repository.UserRepository;
import com.example.letsplay.service.JwtService;
import com.example.letsplay.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepo;


    //Get all users - admin only
    @GetMapping("/users/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() throws UserException {
        List<User> userList = userService.getAllUser();
        return new ResponseEntity<>(userList, !userList.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    //Create a new user - everyone
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

    //Get single user - admin, user can only get themselves
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getSingleUser(@PathVariable("id") String id, @AuthenticationPrincipal UserDetails userDetails) throws UserException {
        Optional<User> user = userRepo.findById(id);
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")) && user.get().getEmail().equals(userDetails.getUsername())) {
            return new ResponseEntity<>(userService.getSingleUser(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Access denied", HttpStatus.NOT_FOUND);
        }
    }



    //Update a user - admin, user can only update themselves
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateById(
            @PathVariable("id") String id,
            @RequestBody User user,
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<User> existingUser = userRepo.findById(id);

        if (existingUser.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        User sameUser = existingUser.get();

        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")) && user.getEmail().equals(userDetails.getUsername()))) {
            try {
                userService.updateUser(id, user);
                return new ResponseEntity<>("Updated User with id " + id, HttpStatus.OK);

            } catch (ConstraintViolationException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            } catch (UserException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }
    }

    //Delete a user - admin
    @DeleteMapping("/users/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id) throws UserException {
            try{
                userService.deleteUser(id);
                return new ResponseEntity<>("Successfully deleted user with id " + id, HttpStatus.OK);
            } catch (UserException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
    }

    @PostMapping("/users/login")
    public String getToken(@RequestBody UserAuthentication userInfo) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userInfo.getUsername(), userInfo.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userInfo.getUsername());
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }
}

