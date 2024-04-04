package com.umc5th.muffler.global.feign;

import com.umc5th.muffler.domain.member.dto.KakaoUnlinkResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakao-client", url = "https://kapi.kakao.com", configuration = {KakaoAuthInterceptor.class})
public interface KakaoClient {

    @PostMapping("/v1/user/unlink?target_id_type=user_id")
    KakaoUnlinkResponse unlinkMember(@RequestParam(value = "target_id") Long memberId);
}
