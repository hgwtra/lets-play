package com.example.letsplay.service;

import com.example.letsplay.model.Product;
import com.example.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    //connect userid to product
    public Product createProductWithUser(Product product, String userID) {
        product.setUserId(userID);        // Set the userId on the product
        return productRepo.save(product); // Save the product with the associated userId
    }
}
