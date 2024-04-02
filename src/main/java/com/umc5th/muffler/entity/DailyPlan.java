package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@Entity
@Getter
public class DailyPlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long budget;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isZeroDay = false;

    @Column(nullable = false)
    @Builder.Default
    private Long totalCost = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Rate rate;

    @Column(length = 1024)
    private String rateMemo;

    public static DailyPlan of(LocalDate date, Long budget, Goal goal) {
        return DailyPlan.builder()
                .date(date)
                .budget(budget)
                .goal(goal)
                .build();
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public void updateRate(String rateMemo, Rate rate){
        this.rateMemo = rateMemo;
        this.rate = rate;
    }
    public void toggleZeroDay() {
        isZeroDay = !isZeroDay;
    }
    public void updateTotalCost(Long difference) {
        this.totalCost += difference;
    }
    public Boolean isPossibleToAlarm(Long addition) {
        return totalCost <= budget && budget < totalCost + addition;
    }
}
