package com.ecom.backend.security.service;

import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.model.User;
import com.ecom.backend.repository.UserRepository;
import com.ecom.backend.security.Payload.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public @Nullable UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user==null)
        {
            throw new ResourceNotFoundException("User","username",username);
        }
        return new UserDetailsImpl(user);
    }
}

