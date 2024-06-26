package com.umc5th.muffler.domain.member.dto;

import com.umc5th.muffler.entity.Inquiry;
import com.umc5th.muffler.entity.Member;

public class MailConverter {

    public static Inquiry toEntity(InquiryRequest request, Member member){
        return Inquiry.builder()
                .content(request.getContent())
                .memberId(member.getId())
                .build();
    }
}
