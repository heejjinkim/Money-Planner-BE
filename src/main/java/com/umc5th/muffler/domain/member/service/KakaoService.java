package com.umc5th.muffler.domain.member.service;

import com.umc5th.muffler.global.security.jwt.JwtOICDProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final String ISS = "https://kauth.kakao.com";
    @Value("${jwt.aud}")
    private String AUD;

    private final JwtOICDProvider jwtOICDProvider;

    public String login(String idToken) {
        Jwt<Header, Claims> jwt = jwtOICDProvider.getUnsignedTokenClaims(idToken, ISS, AUD);
        return jwt.getBody().getSubject();
    }
}
