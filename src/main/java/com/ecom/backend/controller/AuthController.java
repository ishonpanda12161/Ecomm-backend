package com.ecom.backend.controller;

import com.ecom.backend.model.User;
import com.ecom.backend.payload.LoginRequest;
import com.ecom.backend.payload.LoginResponse;
import com.ecom.backend.payload.SignupDTO;
import com.ecom.backend.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
