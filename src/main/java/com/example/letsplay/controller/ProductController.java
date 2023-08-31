package com.example.letsplay.controller;

import java.util.List;
import java.util.Optional;

import com.example.letsplay.model.Product;
import com.example.letsplay.model.User;
import com.example.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class ProductController {
    @Autowired
    private ProductRepository productRepo;

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        List<Product> allProducts = productRepo.findAll();
        if(!allProducts.isEmpty()) {
            return new ResponseEntity<List<Product>>(allProducts, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No products available", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProducts(@RequestBody Product product){
        try {
            productRepo.save(product);
            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getSingleProduct(@PathVariable("id") String id){
        Optional<Product> productOptional =productRepo.findById(id);
        if (productOptional.isPresent()) {
            return new ResponseEntity<>(productOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found with id" + id, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProductById(@PathVariable("id") String id, @RequestBody Product product){
        Optional<Product> productOptional =productRepo.findById(id);
        if (productOptional.isPresent()) {
            Product productToSave = productOptional.get();
            productToSave.setName(product.getName() != null ? product.getName() : productToSave.getName());
            productToSave.setDescription(product.getDescription() != null ? product.getDescription() : productToSave.getDescription());
            productToSave.setPrice(!Double.isNaN(product.getPrice()) ? product.getPrice() : productToSave.getPrice());
            productRepo.save(productToSave);
            return new ResponseEntity<>(productToSave, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found with id" + id, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable("id") String id) {
        try {
            productRepo.deleteById(id);
            return new ResponseEntity<>("Successfully deleted id" + id, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
