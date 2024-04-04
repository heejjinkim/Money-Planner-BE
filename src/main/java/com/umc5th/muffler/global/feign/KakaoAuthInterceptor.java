package com.umc5th.muffler.global.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

public class KakaoAuthInterceptor implements RequestInterceptor {
    @Value("${kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

    @Override
    public void apply(RequestTemplate template) {
        template.header("Content-Type", "application/x-www-form-urlencoded");
        template.header("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY);
    }
}
