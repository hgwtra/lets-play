package com.example.letsplay.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Getter
@Setter
@Builder
@Document(collection = "products")
@AllArgsConstructor
@NoArgsConstructor


public class Product {
    @Id
    private String id;
    @Field
    @NotBlank(message = "name cannot be empty")
    private String name;
    @Field
    @NotBlank(message = "description cannot be empty")
    private String description;
    @Field
    @DecimalMin(value = "0.0", message = "Product price must be greater than or equal to 0")
    @NotNull(message = "price cannot be empty")
    private double price;
    @Field
    @NotNull(message = "userID cannot be empty")
    private String userId;
}
