package com.umc5th.muffler.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryGoalResponse {
    private Long categoryGoalId;
    private Long categoryId;
    private String icon;
    private Long categoryBudget;
}
