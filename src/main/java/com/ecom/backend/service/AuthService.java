package com.ecom.backend.service;


import com.ecom.backend.model.User;
import com.ecom.backend.payload.LoginRequest;
import com.ecom.backend.payload.LoginResponse;
import com.ecom.backend.payload.SignupDTO;

public interface AuthService {
    User createUser(SignupDTO signupDTO);
    LoginResponse signUser(LoginRequest loginRequest);

}
