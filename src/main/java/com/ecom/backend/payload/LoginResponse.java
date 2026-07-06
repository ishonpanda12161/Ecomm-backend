package com.ecom.backend.payload;

import com.ecom.backend.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class LoginResponse {

    private Long id;
    private String token;
    private String username;
    private Set<String> roles;
    private String email;

    public LoginResponse(String token, String username, Collection<? extends GrantedAuthority> authorities,Long id,String email) {
        this.token = token;
        this.username = username;
        this.id = id;
        this.email = email;
        this.roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}
