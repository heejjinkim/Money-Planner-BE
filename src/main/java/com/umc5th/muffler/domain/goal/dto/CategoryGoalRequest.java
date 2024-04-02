package com.umc5th.muffler.domain.goal.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryGoalRequest {
    @NotNull
    private Long categoryGoalId;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long categoryBudget;
}
