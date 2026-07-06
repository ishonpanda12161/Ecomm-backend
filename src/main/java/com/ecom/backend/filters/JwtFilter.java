package com.ecom.backend.filters;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(SecurityContextHolder.getContext().getAuthentication()!=null)
        {
            filterChain.doFilter(request,response);
            return;
        }

        String header = request.getHeader("Authorization");
        if(header==null || !header.startsWith("Bearer "))
        {
            filterChain.doFilter(request,response);
            return;
        }

        String token = header.substring(7);

        if(!jwtUtils.valid(token))
        {
            filterChain.doFilter(request,response);
            return;
        }

        String username = jwtUtils.extractUsername(token);
        UserDetailsImpl user = (UserDetailsImpl) securityUserService.loadUserByUsername(username);

        if(user==null)
        {
            filterChain.doFilter(request,response);
            return;
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request,response);
    }
}
