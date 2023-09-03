package com.example.letsplay.service;

import com.example.letsplay.model.User;
import com.example.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.letsplay.config.UserInfoUserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserInfoService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userInfo = userRepo.findByEmail(username);
         return userInfo.map(UserInfoUserDetails::new)
                 .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
