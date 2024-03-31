package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.Routine;
import com.umc5th.muffler.entity.base.WeeklyRepeatDay;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@DiscriminatorValue("WEEKLY")
public class WeeklyRoutine extends Routine {
    @Builder.Default
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<WeeklyRepeatDay> weeklyRepeatDays = new ArrayList<>();

    @Column
    private Integer weeklyTerm;
}
