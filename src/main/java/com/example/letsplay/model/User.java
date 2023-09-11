package com.example.letsplay.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Getter
@Setter
@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor


public class User  {
    @Id
    private String id;
    @Field
    @NotBlank(message = "name cannot be empty")
    private String name;
    @Field
    @NotBlank(message = "email cannot be empty")
    @Email(message = "invalid email format")
    private String email;
    @Field
    @NotBlank(message = "password cannot be empty")
    @Size(min = 4, max = 1000000000, message = "password must be between 4 and 50 characters")
    private String password;
    @Field
    @NotNull(message = "role cannot be empty")
    private String role;

}
