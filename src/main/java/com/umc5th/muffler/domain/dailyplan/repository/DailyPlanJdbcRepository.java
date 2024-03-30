package com.umc5th.muffler.domain.dailyplan.repository;

import com.umc5th.muffler.entity.DailyPlan;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DailyPlanJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchUpdateBudget(List<DailyPlan> dailyPlans, List<Long> dailyBudgets) {
        String sql = "UPDATE daily_plan SET budget = ? WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DailyPlan dailyPlan = dailyPlans.get(i);
                Long budget = dailyBudgets.get(i);
                ps.setLong(1, budget);
                ps.setLong(2, dailyPlan.getId());
            }

            @Override
            public int getBatchSize() {
                return dailyPlans.size();
            }
        });
    }
}
