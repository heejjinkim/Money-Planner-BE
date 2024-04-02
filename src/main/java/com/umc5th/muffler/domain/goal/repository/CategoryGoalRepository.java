package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.CategoryGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategoryGoalRepository extends JpaRepository<CategoryGoal, Long> {
    Optional<CategoryGoal> findByGoalIdAndCategoryId(Long goalId, Long categoryId);

    @Query("SELECT cg.id FROM CategoryGoal cg WHERE cg.goal.id = :goalId")
    List<Long> findByGoalId(Long goalId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CategoryGoal cg WHERE cg.id in :ids")
    void deleteByIds(List<Long> ids);
}
