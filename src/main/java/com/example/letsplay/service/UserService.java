package com.example.letsplay.service;

import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.User;
import com.example.letsplay.model.UserDTO;
import com.example.letsplay.model.UserIdDTO;

import javax.validation.ConstraintViolationException;
import java.util.List;

public interface UserService {
    public void createUser(User user) throws ConstraintViolationException, UserException;
    public List<UserDTO> getAllUser() throws ConstraintViolationException, UserException;
    public List<UserIdDTO> getAllUsersByAdmin() throws ConstraintViolationException, UserException;
    public UserDTO getSingleUser(String id) throws ConstraintViolationException, UserException;
    public void updateUser(String id, User user) throws ConstraintViolationException, UserException;
    public void deleteUser(String id) throws ConstraintViolationException, UserException;
}
