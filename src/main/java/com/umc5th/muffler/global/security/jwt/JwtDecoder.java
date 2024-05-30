package com.umc5th.muffler.global.security.jwt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CommonException;
import java.util.Base64;
import java.util.Base64.Decoder;

public class JwtDecoder {
    private JwtDecoder() {}

    public static <T> T decodePayload(String token, Class<T> targetClass) {
        String[] tokenParts = token.split("\\.");
        String payloadJWT = tokenParts[1];
        Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(payload, targetClass);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.TOKEN_ERROR, "JWT 토큰 페이로드 decode 중 에러 발생");
        }
    }
}
