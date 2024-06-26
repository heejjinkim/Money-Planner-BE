package com.umc5th.muffler.domain.member.repository;

import com.umc5th.muffler.entity.MemberAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberAlarmRepository extends JpaRepository<MemberAlarm, Long> {
    @Query("select ma from MemberAlarm ma where ma.member.id = :memberId")
    Optional<MemberAlarm> findByMemberId(@Param("memberId") String memberId);
}

