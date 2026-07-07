package com.ecom.backend.controller;

import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.model.User;
import com.ecom.backend.payload.*;
import com.ecom.backend.security.Payload.UserDetailsImpl;
import com.ecom.backend.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        LoginResult loginResult = authService.signUser(loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,loginResult.responseCookie().toString()).body(loginResult.loginResponse());
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout()
    {
        ResponseCookie cookie = authService.signoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(new HashMap<>(Map.of("message","You have been signed out.")));
    }

    @GetMapping("/username")
    public String getUsername(Authentication authentication)
    {
        if(authentication==null) throw new GenericAPIException("NULL");
        return authentication.getName();
    }

    @GetMapping("/user")
    public UserDetails getUserDetails(Authentication authentication)
    {
        if(authentication==null)
        {
            throw new GenericAPIException("NULL");
        }
        return (UserDetailsImpl) authentication.getPrincipal();
    }

}
