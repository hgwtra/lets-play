package com.example.letsplay.service;

import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.Product;
import com.example.letsplay.model.User;
import com.example.letsplay.repository.ProductRepository;
import com.example.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.validation.ConstraintViolationException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService{
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepo;

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
                //String salt = generateRandomSalt(); // Generate a random salt
                //String saltedPassword = salt + user.getPassword();
                //String hashedPassword = passwordEncoder.encode(saltedPassword);
                //user.setPassword(hashedPassword);
                //userRepo.save(user);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepo.save(user);
            } else {
                throw new ConstraintViolationException("Fields can't be all spaces\nRole must either be ROLE_ADMIN or ROLE_USER\nPassword must be at least 4 characters, maximum 50 characters, no spaces allowed", null);
            }
        }
    }

    private String generateRandomSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[12];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
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

        if (userWithId.isPresent()){
            if(userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new UserException(UserException.userAlreadyExists());
            } else {
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
            }
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
