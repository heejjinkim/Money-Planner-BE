package com.umc5th.muffler.domain.dailyplan.repository;

import com.umc5th.muffler.entity.DailyPlan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long>, DailyPlanRepositoryCustom {
    List<DailyPlan> findByGoalIdAndDateBetween(Long goalId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT dp FROM DailyPlan dp WHERE dp.goal.member.id = :memberId AND dp.date = :date")
    Optional<DailyPlan> findByMemberIdAndDate(String memberId, LocalDate date);

    @Query("SELECT dp FROM DailyPlan dp WHERE dp.goal.member.id = :memberId AND dp.date BETWEEN :startDate AND :endDate")
    List<DailyPlan> findByMemberIdAndDateBetween(String memberId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT dailyPlan FROM DailyPlan dailyPlan "
            + "JOIN FETCH dailyPlan.goal "
            + "WHERE dailyPlan.date = :date "
            + "AND dailyPlan.goal.member.id = :memberId")
    Optional<DailyPlan> findDailyPlanWithGoalByDateAndMember(String memberId, LocalDate date);

    @Query("SELECT dp.id FROM DailyPlan dp WHERE dp.goal.id = :goalId")
    List<Long> findByGoalId(Long goalId);

    @Transactional
    @Modifying
    @Query("DELETE FROM DailyPlan dp WHERE dp.id in :ids")
    void deleteByIds(List<Long> ids);
}
