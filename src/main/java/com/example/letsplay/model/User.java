package com.example.letsplay.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;

@Data
@Getter
@Setter
@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor


public class User  {
    @Id
    private String id;
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "email cannot be null")
    @Email
    private String email;
    @NotNull(message = "password cannot be null")
    private String password;
    @NotNull(message = "role cannot be null")
    private String role;

}
