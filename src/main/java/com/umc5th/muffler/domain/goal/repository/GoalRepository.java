package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalRepositoryCustom {
    @Query("SELECT goal from Goal goal where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetween(@Param("date")LocalDate date, @Param("memberId")String memberId);

    @Query("SELECT distinct g FROM Goal g LEFT JOIN FETCH g.categoryGoals cg LEFT JOIN FETCH cg.category WHERE g.id = :id AND g.member.id = :memberId")
    Optional<Goal> findByIdWithCategoryGoals(@Param("id") Long id, @Param("memberId") String memberId);

    @Query("SELECT goal from Goal goal join fetch goal.dailyPlans where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetweenAndDailyPlans(LocalDate date, String memberId);

    @Query("SELECT g FROM Goal g JOIN FETCH g.dailyPlans WHERE g.id = :goalId")
    Optional<Goal> findByIdAndFetchDailyPlans(Long goalId);

    @Query("SELECT g FROM Goal g WHERE g.id = :goalId AND g.member.id = :memberId")
    Optional<Goal> findByIdAndMemberId(Long goalId, String memberId);

    @Modifying
    @Query("DELETE FROM Goal g WHERE g.id = :goalId AND g.member.id = :memberId")
    void deleteByIdAndMemberId(Long goalId, String memberId);
}
