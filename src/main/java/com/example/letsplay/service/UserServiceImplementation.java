package com.example.letsplay.service;

import com.example.letsplay.model.User;
import com.example.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService{
    @Autowired
    private UserRepository userRepo;
    @Override
    public void createUser(User user) {
        userRepo.findByUser(user.getEmail());
    }
}
