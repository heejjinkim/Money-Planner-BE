package com.umc5th.muffler.domain.member.dto;

import com.umc5th.muffler.entity.constant.SocialType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WithdrawRequest {
    @NotNull
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    @NotNull
    private String reason;

    // 애플 탈퇴 시 필요
    private String authenticationCode;
}
