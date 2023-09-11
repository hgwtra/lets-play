package com.example.letsplay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserIdDTO {
    private String id;
    private String name;
    private String email;
    private String role;
}

