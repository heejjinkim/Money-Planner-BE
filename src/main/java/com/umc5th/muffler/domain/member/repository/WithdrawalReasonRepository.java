package com.umc5th.muffler.domain.member.repository;

import com.umc5th.muffler.entity.WithdrawalReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalReasonRepository extends JpaRepository<WithdrawalReason, Long> {
}
