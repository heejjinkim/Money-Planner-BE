package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.CategoryGoal;
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
public class GoalJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertCategoryGoals(List<CategoryGoal> categoryGoals) {
        String sql = "INSERT INTO category_goal (budget, category_id, goal_id) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CategoryGoal categoryGoal = categoryGoals.get(i);
                ps.setLong(1, categoryGoal.getBudget());
                ps.setLong(2, categoryGoal.getCategory().getId());
                ps.setLong(3, categoryGoal.getGoal().getId());
            }

            @Override
            public int getBatchSize() {
                return categoryGoals.size();
            }
        });
    }

    public void batchInsertDailyPlans(List<DailyPlan> dailyPlans) {
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

}
