package com.example.letsplay.repository;

import com.example.letsplay.model.Product;
import com.example.letsplay.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository
        extends MongoRepository<Product, String> {

    @Query("{'products': ?0}")
    Optional<Product> findByProduct(String id);
}
