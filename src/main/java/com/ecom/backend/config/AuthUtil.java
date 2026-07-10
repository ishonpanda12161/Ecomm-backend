package com.ecom.backend.config;

import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.model.User;
import com.ecom.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;


    public String loggedInEmail() {
        User user = loggedInUser();
        return user.getEmail();
    }

    public Long loggedInId()
    {
        User user = loggedInUser();
        return user.getId();
    }

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null)
        {
            throw new GenericAPIException("Cannot find user.");
        }
        User user = userRepository.findByUsername(authentication.getName());
        if(user==null)
        {
            throw new GenericAPIException("Cannot find user.");
        }

        return user;
    }
}
