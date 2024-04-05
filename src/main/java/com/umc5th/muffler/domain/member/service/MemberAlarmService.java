package com.umc5th.muffler.domain.member.service;

import com.umc5th.muffler.domain.member.dto.AlarmAgreeUpdateRequest;
import com.umc5th.muffler.domain.member.dto.AlarmAgreementResponse;
import com.umc5th.muffler.domain.member.dto.TokenEnrollRequest;
import com.umc5th.muffler.domain.member.repository.MemberAlarmRepository;
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
    private final MemberAlarmRepository memberAlarmRepository;

    @Transactional
    public void fetchAlarmAgree(String memberId, AlarmAgreeUpdateRequest request) {
        MemberAlarm memberAlarm = memberAlarmRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        memberAlarm.setIsDailyPlanRemindAgree(request.getDailyPlanRemindAgree());
        memberAlarm.setIsTodayEnrollRemindAgree(request.getTodayEnrollRemindAgree());
        memberAlarm.setIsGoalEndReportRemindAgree(request.getGoalEndRemindAgree());
        memberAlarm.setIsYesterdayEnrollRemindAgree(request.getYesterdayEnrollRemindAgree());
    }

    @Transactional
    public void enrollAlarmToken(String memberId, TokenEnrollRequest request) {
        MemberAlarm memberAlarm = memberAlarmRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        memberAlarm.enrollToken(request.getToken());
    }

    @Transactional
    public void deleteAlarmToken(String memberId) {
        MemberAlarm memberAlarm = memberAlarmRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        memberAlarm.deleteToken();
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
