package com.example.letsplay.service;

import com.example.letsplay.exception.ProductException;
import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.Product;
import com.example.letsplay.model.User;
import com.example.letsplay.repository.ProductRepository;
import com.example.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImplementation implements ProductService{

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private UserRepository userRepository;

    private boolean isValidUserID(String userID, UserRepository userRepository) {
        Optional<User> userOptional = userRepository.findById(userID);
        return userOptional.isPresent();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String removeExtraSpaces(String value) {
        return value.replaceAll("\\s+", " ").trim();
    }

    private boolean isValid(Product product, UserRepository userRepository) {
        return isNotBlank(product.getName()) && isNotBlank(product.getDescription())
                && isValidUserID(product.getUserId().trim(), userRepository);
    }

    @Override
    public void createProduct(Product product) throws ConstraintViolationException, ProductException {
        Optional<Product> productOptional = productRepo.findByProduct(product);
        if (productOptional.isPresent()) {
            throw new ProductException(ProductException.productAlreadyExists());
        } else {
            if (isValid(product, userRepository)) {
                product.setDescription(removeExtraSpaces(product.getDescription()));
                product.setName(removeExtraSpaces(product.getName()));
                product.setUserId(product.getUserId().trim());
                productRepo.save(product);
            } else {
                throw new ConstraintViolationException("Fields can't be all spaces\nUser ID must exist in the database", null);
            }
        }
    }

    @Override
    public List<Product> getAllProduct() throws ConstraintViolationException, ProductException {
        List<Product> allProducts = productRepo.findAll();
        if (!allProducts.isEmpty()) {
            return allProducts;
        } else {
            return new ArrayList<Product>();
        }
    }

    @Override
    public Product getSingleProduct(String id) throws ConstraintViolationException, ProductException {
        Optional<Product> singleProduct = productRepo.findById(id);
        if (!singleProduct.isPresent()) {
            throw new ProductException(ProductException.NotFoundException(id));
        } else {
            return singleProduct.get();
        }
    }

    //what if a user has 2 identical products
    @Override
    public void updateProduct(String id, Product product) throws ConstraintViolationException, ProductException {
        Optional<Product> productWithId = productRepo.findById(id);
        //Optional<Product> productWithSame? = productRepo.findBy?(product.get?());
        if (productWithId.isPresent()){
            Product productToUpdate = productWithId.get();
            productToUpdate.setName(product.getName());
            productToUpdate.setDescription(product.getDescription());
            productToUpdate.setPrice(product.getPrice());
            productToUpdate.setUserId(product.getUserId());
            if (!isValid(productToUpdate, userRepository)){

            }
            productRepo.save(productToUpdate);
        } else {
            throw new ProductException(ProductException.NotFoundException(id));
        }
    }

    @Override
    public void deleteProduct(String id) throws ConstraintViolationException, ProductException {
        Optional<Product> productOptional = productRepo.findById(id);
        if (!productOptional.isPresent()){
            throw new ProductException(ProductException.NotFoundException(id));
        } else{
            productRepo.deleteById(id);
        }
    }
}
