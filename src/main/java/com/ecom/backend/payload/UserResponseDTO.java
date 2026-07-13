package com.ecom.backend.payload;

import com.ecom.backend.model.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.Set;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;

    public UserResponseDTO(Long id, String username, String email, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;

    }
}
