package com.umc5th.muffler.domain.member.service;

import com.umc5th.muffler.global.feign.KakaoClient;
import com.umc5th.muffler.global.response.exception.FeignException;
import com.umc5th.muffler.global.security.jwt.JwtOICDProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private static final String ISS = "https://kauth.kakao.com";
    private static final String KAKAO_LEAVE_EXCEPTION = "NotRegisteredUserException";
    @Value("${jwt.aud}")
    private String AUD;

    private final KakaoClient kakaoClient;
    private final JwtOICDProvider jwtOICDProvider;

    public String login(String idToken) {
        Jwt<Header, Claims> jwt = jwtOICDProvider.getUnsignedTokenClaims(idToken, ISS, AUD);
        return jwt.getBody().getSubject();
    }

    public void leave(String memberId) {
        try {
            kakaoClient.unlinkMember(Long.valueOf(memberId));
            log.info("아이디 {}의 카카오 계정 연결 끊기가 완료되었습니다.", memberId);
        } catch (FeignException e) {
            String errorResult = e.getErrorResult();
            if (!errorResult.contains(KAKAO_LEAVE_EXCEPTION)) {
                throw e;
            }
            log.error("아이디 {}의 계정은 이미 카카오 계정 연결 끊기가 완료되었습니다.: {}", memberId, KAKAO_LEAVE_EXCEPTION);
        }
    }
}
