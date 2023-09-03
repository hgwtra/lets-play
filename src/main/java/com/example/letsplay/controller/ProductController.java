package com.example.letsplay.controller;

import java.util.List;
import java.util.Optional;

import com.example.letsplay.exception.ProductException;
import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.Product;
import com.example.letsplay.model.User;
import com.example.letsplay.repository.ProductRepository;
import com.example.letsplay.service.ProductService;
import com.example.letsplay.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController

public class ProductController {
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private ProductService productService;

    //Get all products - everyone
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() throws ProductException {
        List<Product> productList = productService.getAllProduct();
        return new ResponseEntity<>(productList, !productList.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    //Create a new product - everyone
    @PostMapping("/products/create")
    public ResponseEntity<?> createProducts(@RequestBody Product product){
        try {
            productService.createProduct(product);
            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ProductException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    //Get single product - everyone
    @GetMapping("/products/get/{id}")
    public ResponseEntity<?> getSingleProduct(@PathVariable("id") String id){
        try {
            return new ResponseEntity<>(productService.getSingleProduct(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    //Update a product - everyone
    @PutMapping("/products/update/{id}")
    public ResponseEntity<?> updateProductById(@PathVariable("id") String id, @RequestBody Product product) throws ProductException{
        try {
            productService.updateProduct(id, product);
            return new ResponseEntity<>("Updated Product with id" + id, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ProductException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/products/delete/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable("id") String id) throws ProductException {
        try{
            productService.deleteProduct(id);
            return new ResponseEntity<>("Successfully deleted user with id " + id, HttpStatus.OK);
        } catch (ProductException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
