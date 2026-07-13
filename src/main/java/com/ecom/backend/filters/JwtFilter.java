package com.ecom.backend.filters;

import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.exceptions.JwtException;
import com.ecom.backend.security.Payload.UserDetailsImpl;
import com.ecom.backend.security.jwt.JwtUtils;
import com.ecom.backend.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl securityUserService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {

        if(SecurityContextHolder.getContext().getAuthentication()!=null)
        {
            filterChain.doFilter(request,response);
            return;
        }

        String token = parseToken(request);
        if(token==null)
        {
            throw new JwtException("Null token.","Validation");
        }
        try{
            jwtUtils.valid(token);
        }
        catch (JwtException e)
        {
            throw new JwtException("Invalid token.","Validation");
        }

        String username = jwtUtils.extractUsername(token);
        UserDetailsImpl user = (UserDetailsImpl) securityUserService.loadUserByUsername(username);

        if(user==null)
        {
            throw new JwtException("Cannot find user.","Database");
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request,response);
    }

    private String parseToken(HttpServletRequest request)
    {
        return jwtUtils.getTokenFromCookie(request);
    }
}

