package com.umc5th.muffler.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlarmAgreementResponse {
    private Boolean isDailyPlanRemindAgree;
    private Boolean isTodayEnrollRemindAgree;
    private Boolean isYesterdayEnrollRemindAgree;
    private Boolean isGoalEndReportRemindAgree;
}
