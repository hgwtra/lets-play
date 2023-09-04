package com.example.letsplay.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthentication {
    @Field
    @NotBlank(message = "please enter your email")
    private String username;
    @Field
    @NotBlank(message = "password cannot be empty")
    private String password;
}
