package com.ecom.backend.service;


import com.ecom.backend.model.User;
import com.ecom.backend.payload.LoginRequest;
import com.ecom.backend.payload.LoginResponse;
import com.ecom.backend.payload.LoginResult;
import com.ecom.backend.payload.SignupDTO;
import org.springframework.http.ResponseCookie;

import java.util.Map;

public interface AuthService {
    User createUser(SignupDTO signupDTO);
    LoginResult signUser(LoginRequest loginRequest);
    ResponseCookie signoutUser();
}
