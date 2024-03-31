package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.Routine;
import com.umc5th.muffler.entity.constant.MonthlyRepeatType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("MONTHLY")
public class MonthlyRoutine extends Routine {
    // Monthly Column
    @Column
    @Enumerated(EnumType.STRING)
    private MonthlyRepeatType monthlyRepeatType;

    @Column
    private Integer specificDay; // SPECIFIC_DAY_OF_MONTH에 해당하는 일
}
