package com.ecom.backend.service;

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
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public User createUser(SignupDTO signupDTO) {
        if(userRepository.existsByUsername(signupDTO.getUsername()))
        {
            throw new ResourceAlreadyExistsException("User","username",signupDTO.getUsername());
        }

        signupDTO.setPassword(passwordEncoder.encode(signupDTO.getPassword()));

        if(signupDTO.getRoles()==null || signupDTO.getRoles().isEmpty())
        {
            signupDTO.setRoles(new HashSet<>(Set.of("ROLE_USER")));
        }
        Set<AppRoles> appRoles;
        try{
            appRoles = signupDTO.getRoles().stream().map(AppRoles::valueOf).collect(Collectors.toSet());
        }
        catch (Exception e)
        {
            throw new GenericAPIException("One or more roles are invalid.");
        }
        Set<Role> roles = roleRepository.findByRoleNameIn(appRoles);
        User user = userMapper.signupToUser(signupDTO);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public LoginResponse signUser(LoginRequest loginRequest) {
        try{
            Authentication auth = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword());
            auth = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(auth);

            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            String token = jwtUtils.generateToken(userDetails.getUsername());
            return new LoginResponse(token,userDetails.getUsername(),userDetails.getAuthorities(),userDetails.getId(),userDetails.getEmail());
        }
        catch (AuthenticationException e)
        {
            throw new ResourceNotFoundException("User","username",loginRequest.getUsername());
        }
    }
}
