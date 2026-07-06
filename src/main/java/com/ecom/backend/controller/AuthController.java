package com.ecom.backend.controller;

import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.exceptions.ResourceAlreadyExistsException;
import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.mapper.UserMapper;
import com.ecom.backend.model.AppRoles;
import com.ecom.backend.model.Role;
import com.ecom.backend.model.User;
import com.ecom.backend.payload.LoginRequest;
import com.ecom.backend.payload.LoginResponse;
import com.ecom.backend.payload.SignupDTO;
import com.ecom.backend.repository.RoleRepository;
import com.ecom.backend.repository.UserRepository;
import com.ecom.backend.security.Payload.UserDetailsImpl;
import com.ecom.backend.security.jwt.JwtUtils;
import com.ecom.backend.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/open")
@RequiredArgsConstructor
public class AuthController {


    private final AuthServiceImpl authService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(
            @Valid @RequestBody SignupDTO signupDTO
            )
    {
        return ResponseEntity.ok(authService.createUser(signupDTO));
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signin(
            @Valid @RequestBody LoginRequest loginRequest
            )
    {

        return ResponseEntity.ok().body(authService.signUser(loginRequest));

    }


}
