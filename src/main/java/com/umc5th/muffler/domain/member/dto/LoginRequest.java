package com.umc5th.muffler.domain.member.dto;

import com.umc5th.muffler.entity.constant.SocialType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    @NotNull
    private String token;
}
