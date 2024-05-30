package com.umc5th.muffler.global.feign;

import com.umc5th.muffler.domain.member.dto.AppleToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "apple-client",
        url = "https://appleid.apple.com"
)
public interface AppleClient {
    @PostMapping("/auth/token")
    AppleToken getIdToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType,
            @RequestParam("code") String code
    );
}
