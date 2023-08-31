package com.example.letsplay.service;

import com.example.letsplay.exception.ProductException;
import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.Product;
import com.example.letsplay.model.User;
import com.example.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImplementation implements ProductService{

    @Autowired
    private ProductRepository productRepo;

    //connect userid to product
    public Product createProductWithUser(Product product, String userID) {
        product.setUserId(userID);        // Set the userId on the product
        return productRepo.save(product); // Save the product with the associated userId
    }

    @Override
    public void createProduct(Product product) throws ConstraintViolationException, ProductException, ProductException {
        Optional<Product> productOptional = productRepo.findByProduct(product);
        if(productOptional.isPresent()){
            throw new ProductException(ProductException.productAlreadyExists());
        } else{
            productRepo.save(product);
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
