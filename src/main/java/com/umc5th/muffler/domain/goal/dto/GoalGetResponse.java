package com.umc5th.muffler.domain.goal.dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoalGetResponse {
    private String title;
    private String icon;
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalBudget;
    private long totalCost;
    private List<CategoryGoalResponse> categoryGoals;
    private List<Long> dailyBudgets;
}
