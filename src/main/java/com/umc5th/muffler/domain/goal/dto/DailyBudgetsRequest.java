package com.umc5th.muffler.domain.goal.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyBudgetsRequest {
    @NotNull
    private List<Long> dailyBudgets;
}
