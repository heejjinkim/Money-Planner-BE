package com.umc5th.muffler.global.feign;

import org.springframework.context.annotation.Bean;

public class FeignCustomConfig {
    @Bean
    public KakaoAuthInterceptor kakaoAuthInterceptor() {
        return new KakaoAuthInterceptor();
    }
}
