package com.umc5th.muffler.global.config.mock;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MockFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String mockToken = request.getHeader("Authorization");
        System.out.println(mockToken);
        if (StringUtils.hasText(mockToken) && mockToken.startsWith("Bearer")) {
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
            UserDetails principal = new User(mockToken.substring(7), "", authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            throw new AuthenticationServiceException("[mock filter] no id in header");
        }
    }
}
