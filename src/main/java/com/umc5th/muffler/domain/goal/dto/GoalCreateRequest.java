package com.umc5th.muffler.domain.goal.dto;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalCreateRequest {
    @NotBlank
    private String icon;
    @NotBlank
    private String title;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private Long totalBudget;
    @NotNull
    private Boolean restore;

    @NotNull
    private List<CategoryGoalRequest> categoryGoals;

    @NotNull
    private List<Long> dailyBudgets;
}
