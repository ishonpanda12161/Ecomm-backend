package com.ecom.backend.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
public class SignupDTO {
    @NotBlank
    @Size(min = 2,message = "Name contain at least 2 characters.")
    private String name;

    @NotBlank
    @Size(min = 3,message = "Must contain at least 3 characters.")
    private String username;

    @NotBlank
    @Size(min = 3,message = "Must contain at least 3 characters.")
    private String password;

    @Email
    @NotBlank
    @Size(min = 11,message = "Must contain at least 3 characters.")
    private String email;

    private Set<String> roles = new HashSet<>();

}
