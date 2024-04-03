package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.goal.dto.GoalTerm;
import com.umc5th.muffler.entity.Goal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface GoalRepositoryCustom {
    List<Goal> findGoalsByYearMonth(String memberId, YearMonth yearMonth);
    Slice<Goal> findByMemberIdAndDailyPlans(String memberId, Pageable pageable, LocalDate today, LocalDate startDate);
    List<FinishedGoal> findFinishedGoals(LocalDate date);
    List<GoalTerm> findGoalsWithinDateRange(LocalDate startDate, LocalDate endDate);
}
