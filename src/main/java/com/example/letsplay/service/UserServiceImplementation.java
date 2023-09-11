package com.example.letsplay.service;

import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.Product;
import com.example.letsplay.model.User;
import com.example.letsplay.model.UserDTO;
import com.example.letsplay.model.UserIdDTO;
import com.example.letsplay.repository.ProductRepository;
import com.example.letsplay.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.validation.ConstraintViolationException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService{
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private ModelMapper modelMapper;

    //Covert to DTO
    public UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
    public List<UserDTO> convertToDtos(List<User> users) {
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    //Covert to DTO to get UserId
    public UserIdDTO convertToIdDto(User user) {
        return modelMapper.map(user, UserIdDTO.class);
    }
    public List<UserIdDTO> convertToIdDtos(List<User> users) {
        return users.stream()
                .map(this::convertToIdDto)
                .collect(Collectors.toList());
    }

    private boolean isValidRole(String role) {
        return role.equalsIgnoreCase("ROLE_ADMIN") || role.equalsIgnoreCase("ROLE_USER");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 4 && password.length() <= 50 && !password.contains(" ");
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String removeExtraSpaces(String value) {
        return value.replaceAll("\\s+", " ").trim();
    }

    private boolean isValid(User user) {
        String trimmedName = user.getName().trim();
        return isNotBlank(user.getName()) && isNotBlank(user.getEmail()) && isValidRole(user.getRole())
                && isValidPassword(user.getPassword());
    }

    @Override
    public void createUser(User user) throws ConstraintViolationException, UserException {
        Optional<User> userOptional = userRepo.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new UserException(UserException.userAlreadyExists());
        } else {
            if (isValid(user)) {
                user.setRole(user.getRole().toUpperCase());
                user.setName(removeExtraSpaces(user.getName()));
                String userId = "";
                do {
                    userId = UUID.randomUUID().toString().split("-")[0];
                } while (userRepo.existsById(userId));
                user.setId(userId);
                String salt = user.getId();
                String saltedPassword = user.getPassword() + salt;
                //System.out.println("salted Password when create " + saltedPassword);
                String hashedPassword = passwordEncoder.encode(saltedPassword);
                user.setPassword(hashedPassword);
                userRepo.save(user);
            } else {
                throw new ConstraintViolationException("Fields can't be all spaces\nRole must either be ROLE_ADMIN or ROLE_USER\nPassword must be at least 4 characters, maximum 50 characters, no spaces allowed", null);
            }
        }
    }



    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<UserDTO> getAllUser() throws ConstraintViolationException, UserException {
        List<User> allUsersList = userRepo.findAll();
        List<UserDTO> allUsers = convertToDtos(allUsersList);
        if (!allUsers.isEmpty()) {
            return allUsers;
        } else {
            return new ArrayList<UserDTO>();
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserIdDTO> getAllUsersByAdmin() throws ConstraintViolationException, UserException {
        List<User> allUsersList = userRepo.findAll();
        List<UserIdDTO> allUsers = convertToIdDtos(allUsersList);
        if (!allUsers.isEmpty()) {
            return allUsers;
        } else {
            return new ArrayList<UserIdDTO>();
        }
    }

    @Override
    public UserDTO getSingleUser(String id) throws ConstraintViolationException, UserException {
        Optional<User> singleUser = userRepo.findById(id);
        if (!singleUser.isPresent()) {
            throw new UserException(UserException.NotFoundException(id));
        } else {
            UserDTO singleUserDto = convertToDto(singleUser.get());
            return singleUserDto;
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateUser(String id, User user) throws UserException {
        Optional<User> userWithId = userRepo.findById(id);
        Optional<User> userWithSameEmail = userRepo.findByEmail(user.getEmail());

        if (userWithId.isPresent()){
            User userToUpdate = userWithId.get();
            userToUpdate.setName(removeExtraSpaces(user.getName()));
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(removeExtraSpaces(user.getPassword()));
            userToUpdate.setRole(user.getRole().toUpperCase());

            if (!isValid(userToUpdate)) {
                throw new UserException(UserException.invalidUpdates());
            }
            //add salt
            userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(userToUpdate);
        } else {
            throw new UserException(UserException.NotFoundException(id));
        }
    }


    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteUser(String id) throws ConstraintViolationException, UserException {
        Optional<User> userOptional = userRepo.findById(id);
        if (!userOptional.isPresent()){
            throw new UserException(UserException.NotFoundException(id));
        } else{
            // Delete associated products with the same userId
            User userToDelete = userOptional.get();

            List<Product> allProducts = productRepo.findAll();
            List<Product> productsToDelete = new ArrayList<>();

            for (Product product : allProducts) {
                if (userToDelete.getId().equals(product.getUserId())) {
                    productsToDelete.add(product);
                }
            }

            productRepo.deleteAll(productsToDelete);
            userRepo.deleteById(id);
        }
    }


}
