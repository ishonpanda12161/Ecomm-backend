package com.ecom.backend.payload;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Size(min = 3,message = "Must contain at least 3 characters.")
    @Column(name = "username")
    private String username;

    @NotBlank
    @Size(min = 3,message = "Must contain at least 3 characters.")
    private String password;
}
