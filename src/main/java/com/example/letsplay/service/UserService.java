package com.example.letsplay.service;

import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

public interface UserService {
    public void createUser(User user) throws ConstraintViolationException, UserException;
    public List<User> getAllUser() throws ConstraintViolationException, UserException;
    public User getSingleUser(String id) throws ConstraintViolationException, UserException;
    public void updateUser(String id, User user) throws ConstraintViolationException, UserException;
    public void deleteUser(String id) throws ConstraintViolationException, UserException;
}
