package com.umc5th.muffler.domain.member.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WithdrawRequest {
    @NotNull
    private String reason;
}
