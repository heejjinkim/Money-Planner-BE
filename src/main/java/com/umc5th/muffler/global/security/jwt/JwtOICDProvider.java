package com.umc5th.muffler.global.security.jwt;

import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_TOKEN;

import com.umc5th.muffler.global.response.exception.CommonException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtOICDProvider {
    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");
        if (splitToken.length != 3) {
            throw new CommonException(INVALID_TOKEN, "Invalid KakaoIdToken");
        }
        return splitToken[0] + "." + splitToken[1] + ".";
    }

    public Jwt<Header, Claims> getUnsignedTokenClaims(String token, String iss, String aud) {
        try {
            return Jwts.parserBuilder()
                    .requireAudience(aud)
                    .requireIssuer(iss)
                    .build()
                    .parseClaimsJwt(getUnsignedToken(token));
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token", e);
            throw new CommonException(INVALID_TOKEN, "Expired JWT Token");
        } catch (Exception e) {
            log.error(e.toString());
            throw new CommonException(INVALID_TOKEN, "Invalid KakaoIdToken");
        }
    }
}
