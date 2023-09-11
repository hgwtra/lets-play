package com.example.letsplay.service;

import com.example.letsplay.exception.ProductException;
import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.Product;
import com.example.letsplay.model.ProductDTO;
import com.example.letsplay.model.User;
import com.example.letsplay.model.UserDTO;
import com.example.letsplay.repository.ProductRepository;
import com.example.letsplay.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplementation implements ProductService{

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ProductDTO convertToDto(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        Optional<User> user = userRepository.findById(product.getUserId());
        if (user.isPresent()) {
            productDTO.setOwner(user.get().getName());
        }
        return productDTO;
    }

    public List<ProductDTO> convertToDtos(List<Product> products) {
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
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
    public List<ProductDTO> getAllProduct() throws ConstraintViolationException, ProductException {
        List<Product> allProducts = productRepo.findAll();
        List<ProductDTO> allProductsDTO = convertToDtos(allProducts);
        if (!allProductsDTO.isEmpty()) {
            return allProductsDTO;
        } else {
            return new ArrayList<ProductDTO>();
        }
    }

    @Override
    public ProductDTO getSingleProduct(String id) throws ConstraintViolationException, ProductException {
        Optional<Product> singleProduct = productRepo.findById(id);
        if (!singleProduct.isPresent()) {
            throw new ProductException(ProductException.NotFoundException(id));
        } else {
            ProductDTO singleProductDTO = convertToDto(singleProduct.get());
            return singleProductDTO;
        }
    }

    //what if a user has 2 identical products
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteProduct(String id) throws ConstraintViolationException, ProductException {
        Optional<Product> productOptional = productRepo.findById(id);
        if (!productOptional.isPresent()){
            throw new ProductException(ProductException.NotFoundException(id));
        } else{
            productRepo.deleteById(id);
        }
    }
}
