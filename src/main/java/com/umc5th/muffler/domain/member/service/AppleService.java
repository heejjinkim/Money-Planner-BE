package com.umc5th.muffler.domain.member.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.INTERNAL_SERVER_ERROR;

import com.umc5th.muffler.domain.member.dto.AppleIdToken;
import com.umc5th.muffler.domain.member.dto.AppleToken;
import com.umc5th.muffler.global.feign.AppleClient;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.security.jwt.JwtDecoder;
import com.umc5th.muffler.global.util.DateTimeProvider;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleService {
    private final AppleClient appleClient;
    private final AppleProperties appleProperties;
    private final DateTimeProvider dateTimeProvider;

    public String login(String authenticationCode) {
        String idToken = getAppleToken(authenticationCode).getIdToken();
        return JwtDecoder.decodePayload(idToken, AppleIdToken.class).getSub();
    }

    public void leave(String authenticationCode) {
        String accessToken = getAppleToken(authenticationCode).getAccessToken();

        appleClient.leave(
                appleProperties.getClientId(),
                generateClientSecret(),
                accessToken
        );
    }

    private AppleToken getAppleToken(String authenticationCode) {
        return appleClient.getAuthToken(
                appleProperties.getClientId(),
                generateClientSecret(),
                appleProperties.getGrantType(),
                authenticationCode
        );
    }

    private String generateClientSecret() {
        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, appleProperties.getKeyId())
                .setIssuer(appleProperties.getTeamId())
                .setAudience(appleProperties.getAudience())
                .setSubject(appleProperties.getClientId())
                .setExpiration(dateTimeProvider.getDateAfterMinutes(5))
                .setIssuedAt(dateTimeProvider.getIssuedDate())
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        Security.addProvider(new BouncyCastleProvider());
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(appleProperties.getPrivateKey());
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
            return converter.getPrivateKey(privateKeyInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MemberException(INTERNAL_SERVER_ERROR, "String 타입 ApplePrivateKey convert 중 에러 발생");
        }
    }
}
