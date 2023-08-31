package com.example.letsplay.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@Document(collection = "products")
@AllArgsConstructor
@NoArgsConstructor


public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    @DBRef
    private String userId;
}
