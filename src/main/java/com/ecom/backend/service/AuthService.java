package com.ecom.backend.service;


import com.ecom.backend.model.User;
import com.ecom.backend.payload.*;
import org.springframework.http.ResponseCookie;

import java.util.Map;

public interface AuthService {
    UserDTO createUser(SignupDTO signupDTO);
    LoginResult signUser(LoginRequest loginRequest);
    ResponseCookie signoutUser();
}
