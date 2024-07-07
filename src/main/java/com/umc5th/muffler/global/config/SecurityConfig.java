package com.umc5th.muffler.global.config;

import com.umc5th.muffler.global.security.OAuthLoginSuccessHandler;
import com.umc5th.muffler.global.security.OAuthService;
import com.umc5th.muffler.global.security.jwt.JwtTokenFilter;
import com.umc5th.muffler.global.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile({"local", "prod"})
    public class SecurityConfig {

    private final OAuthService oAuthService;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final JwtTokenUtils jwtTokenUtils;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers(
                        "/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/health-check",
                        "/swagger-ui.html", "/swagger-ui/**", "/*/api-docs/**", "/swagger-resources/**", "/webjars/**",
                        "/api/member/refresh-token", "/api/member/login");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(auth -> auth
                        .antMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                ).oauth2Login(oAuth -> oAuth
                        .successHandler(oAuthLoginSuccessHandler)
                        .userInfoEndpoint().userService(oAuthService)
                ).addFilterBefore(new JwtTokenFilter(jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
