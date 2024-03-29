package com.umc5th.muffler.domain.member.repository;

import com.umc5th.muffler.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String>, MemberRepositoryCustom {
    Optional<Member> findByRefreshToken(String refreshToken);
    @Query("select m from Member m join fetch m.memberAlarm where m.id = :memberId")
    Optional<Member> findMemberFetchAlarm(@Param("memberId") String memberId);
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.goals WHERE m.id = :memberId")
    Optional<Member> findByIdAndFetchGoals(String memberId);
}
