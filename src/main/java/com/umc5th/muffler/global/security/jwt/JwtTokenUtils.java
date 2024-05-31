package com.umc5th.muffler.global.security.jwt;

import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_TOKEN;

import com.umc5th.muffler.entity.constant.Role;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.util.DateTimeProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenUtils {
    private static final int ACCESS_TOKEN_DURATION = 1;
    private static final int REFRESH_TOKEN_DURATION = 30;

    private final DateTimeProvider dateTimeProvider;
    private final Key key;

    public JwtTokenUtils(DateTimeProvider dateTimeProvider, @Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = secretKey.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.dateTimeProvider = dateTimeProvider;
    }

    public TokenInfo generateToken(Authentication authentication) {
        String authority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = generateAccessToken(authentication, authority);
        String refreshToken = generateRefreshToken();

        return new TokenInfo("Bearer", accessToken, refreshToken);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        } catch (JwtException e) {
            log.info("JWT Token error: " + e.getMessage());
        }
        return false;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new CommonException(INVALID_TOKEN, "권한 정보가 없는 토큰입니다.");
        }
        List<SimpleGrantedAuthority> authority = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        validateAuthorityValue(authority);

        UserDetails principal = new User(claims.getSubject(), "", authority);
        return new UsernamePasswordAuthenticationToken(principal, "", authority);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private void validateAuthorityValue(List<SimpleGrantedAuthority> authority) {
        if (authority.size() != 1
                || !Role.isValidAuthority(authority.get(0))
        ) {
            throw new CommonException(INVALID_TOKEN, "유효하지 않은 권한 값을 가진 토큰입니다.");
        }
    }

    private String generateAccessToken(Authentication authentication, String authority) {
        Date accessTokenExpiresIn = dateTimeProvider.getDateAfterDays(ACCESS_TOKEN_DURATION);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authority)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken() {
        Date refreshTokenExpiresIn = dateTimeProvider.getDateAfterDays(REFRESH_TOKEN_DURATION);
        return Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
