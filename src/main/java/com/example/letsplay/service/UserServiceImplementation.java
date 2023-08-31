package com.example.letsplay.service;

import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.User;
import com.example.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService{
    @Autowired
    private UserRepository userRepo;

    //if the email exits, throws e
    @Override
    public void createUser(User user) throws ConstraintViolationException, UserException {
        Optional<User> userOptional = userRepo.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new UserException(UserException.userAlreadyExists());
        } else {
            userRepo.save(user);
        }
    }

    @Override
    public List<User> getAllUser() throws ConstraintViolationException, UserException {
        List<User> allUsers = userRepo.findAll();
        if (!allUsers.isEmpty()) {
            return allUsers;
        } else {
            return new ArrayList<User>();
        }
    }

    @Override
    public User getSingleUser(String id) throws ConstraintViolationException, UserException {
        Optional<User> singleUser = userRepo.findById(id);
        if (!singleUser.isPresent()) {
            throw new UserException(UserException.NotFoundException(id));
        } else {
            return singleUser.get();
        }
    }

    @Override
    public void updateUser(String id, User user) throws UserException {
        Optional<User> userWithId = userRepo.findById(id);
        Optional<User> userWithSameEmail = userRepo.findByEmail(user.getEmail());
        System.out.println("email" + user.getEmail());
        if (userWithId.isPresent()){
            if(userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new UserException(UserException.userAlreadyExists());
            }

            User userToUpdate = userWithId.get();
            userToUpdate.setName(user.getName());
            System.out.println("getName" + user.getName());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(user.getPassword());
            userToUpdate.setRole(user.getRole());
            userRepo.save(userToUpdate);
        } else {
            throw new UserException(UserException.NotFoundException(id));
        }
    }

    @Override
    public void deleteUser(String id) throws ConstraintViolationException, UserException {
        Optional<User> userOptional = userRepo.findById(id);
        if (!userOptional.isPresent()){
            throw new UserException(UserException.NotFoundException(id));
        } else{
            userRepo.deleteById(id);
        }
    }
}
