package com.example.letsplay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class ProductDTO {
    private String name;
    private String description;
    private double price;
    private String owner;
}
