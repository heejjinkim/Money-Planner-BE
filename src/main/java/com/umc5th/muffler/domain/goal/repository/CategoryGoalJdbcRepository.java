package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.CategoryGoal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryGoalJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<CategoryGoal> categoryGoals) {
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

    public void batchUpdateBudget(List<CategoryGoal> categoryGoals) {
        String sql = "UPDATE category_goal SET budget = ? WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CategoryGoal categoryGoal = categoryGoals.get(i);
                Long budget = categoryGoal.getBudget();
                ps.setLong(1, budget);
                ps.setLong(2, categoryGoal.getId());
            }

            @Override
            public int getBatchSize() {
                return categoryGoals.size();
            }
        });
    }
}
