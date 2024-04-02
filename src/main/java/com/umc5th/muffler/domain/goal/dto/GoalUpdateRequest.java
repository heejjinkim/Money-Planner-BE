package com.umc5th.muffler.domain.goal.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GoalUpdateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String icon;
}
