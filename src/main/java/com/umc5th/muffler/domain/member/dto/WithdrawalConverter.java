package com.umc5th.muffler.domain.member.dto;

import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.WithdrawalReason;

public class WithdrawalConverter {
    public static WithdrawalReason toEntity(Member member, WithdrawRequest request) {
        return WithdrawalReason.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .reason(request.getReason())
                .build();
    }
}
