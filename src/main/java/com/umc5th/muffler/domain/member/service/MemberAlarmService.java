package com.umc5th.muffler.domain.member.service;

import com.umc5th.muffler.domain.member.dto.AlarmAgreeUpdateRequest;
import com.umc5th.muffler.domain.member.dto.AlarmAgreementResponse;
import com.umc5th.muffler.domain.member.dto.MemberConverter;
import com.umc5th.muffler.domain.member.dto.TokenEnrollRequest;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.MemberAlarm;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAlarmService {
    private final MemberRepository memberRepository;

    @Transactional
    public void fetchAlarmAgree(String memberId, AlarmAgreeUpdateRequest request) {
        Member member = memberRepository.findMemberFetchAlarm(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        member = MemberConverter.toEntity(member, request);
        memberRepository.save(member);
    }

    @Transactional
    public void enrollAlarmToken(String memberId, TokenEnrollRequest request) {
        Member member = memberRepository.findMemberFetchAlarm(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        member.enrollToken(request.getToken());
    }

    @Transactional
    public void deleteAlarmToken(String memberId) {
        Member member = memberRepository.findMemberFetchAlarm(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        member.deleteToken();
    }

    @Transactional(readOnly = true)
    public AlarmAgreementResponse getAlarmAgreement(String memberId) {
        Member member = memberRepository.findMemberFetchAlarm(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        MemberAlarm memberAlarm = member.getMemberAlarm();

        return AlarmAgreementResponse.builder()
                .isTodayEnrollRemindAgree(memberAlarm.getIsTodayEnrollRemindAgree())
                .isDailyPlanRemindAgree(memberAlarm.getIsDailyPlanRemindAgree())
                .isGoalEndReportRemindAgree(memberAlarm.getIsGoalEndReportRemindAgree())
                .isYesterdayEnrollRemindAgree(memberAlarm.getIsYesterdayEnrollRemindAgree())
                .build();
    }
}
