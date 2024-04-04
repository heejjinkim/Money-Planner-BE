package com.umc5th.muffler.domain.member.repository;

import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import java.time.LocalDate;
import java.util.List;

public interface MemberRepositoryCustom {
    List<NotEnrolledMember> findTodayNotEnrolledMember(LocalDate today);
    List<NotEnrolledMember> findYesterdayNotEnrolledMember(LocalDate yesterday);
    void deleteMemberAndRelatedEntities(String memberId);
}
