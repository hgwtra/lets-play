package com.example.letsplay.service;

import com.example.letsplay.exception.ProductException;
import com.example.letsplay.exception.UserException;
import com.example.letsplay.model.Product;
import com.example.letsplay.model.ProductDTO;
import com.example.letsplay.model.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

public interface ProductService {
    public void createProduct(Product product) throws ConstraintViolationException, ProductException;
    public List<ProductDTO> getAllProduct() throws ConstraintViolationException, ProductException;
    public ProductDTO getSingleProduct(String id) throws ConstraintViolationException, ProductException;
    public void updateProduct(String id, Product product) throws ConstraintViolationException, ProductException;
    public void deleteProduct(String id) throws ConstraintViolationException, ProductException;
}




