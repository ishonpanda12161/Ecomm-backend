package com.ecom.backend.payload;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> roles;

    public UserResponseDTO(Long id, String username, String email, Collection<? extends GrantedAuthority> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;

    }
}
