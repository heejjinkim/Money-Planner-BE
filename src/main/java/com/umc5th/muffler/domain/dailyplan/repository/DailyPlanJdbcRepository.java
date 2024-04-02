package com.umc5th.muffler.domain.dailyplan.repository;

import com.umc5th.muffler.entity.DailyPlan;
import java.sql.Date;
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

    public void batchInsert(List<DailyPlan> dailyPlans) {
        String sql = "INSERT INTO daily_plan (date, budget, is_zero_day, total_cost, goal_id) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DailyPlan dailyPlan = dailyPlans.get(i);
                ps.setDate(1, Date.valueOf(dailyPlan.getDate()));
                ps.setLong(2, dailyPlan.getBudget());
                ps.setBoolean(3, dailyPlan.getIsZeroDay());
                ps.setLong(4, dailyPlan.getTotalCost());
                ps.setLong(5, dailyPlan.getGoal().getId());
            }

            @Override
            public int getBatchSize() {
                return dailyPlans.size();
            }
        });
    }

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
