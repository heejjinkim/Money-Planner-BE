package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@DynamicInsert
public class MemberAlarm extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column(nullable = false)
    @ColumnDefault("true")
    @Setter
    private Boolean isDailyPlanRemindAgree;

    @Column(nullable = false)
    @ColumnDefault("true")
    @Setter
    private Boolean isTodayEnrollRemindAgree;

    @Column(nullable = false)
    @ColumnDefault("true")
    @Setter
    private Boolean isYesterdayEnrollRemindAgree;

    @Column(nullable = false)
    @ColumnDefault("true")
    @Setter
    private Boolean isGoalEndReportRemindAgree;

    @OneToOne(mappedBy = "memberAlarm")
    private Member member;
    public void enrollToken(String token) {
        if (token != null) {
            this.token = token;
        }
    }
    public void deleteToken() {
        this.token = null;
    }
}
