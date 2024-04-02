package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.CATEGORY_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.GOAL_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_GOAL_INPUT;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanJdbcRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.dto.CategoryGoalRequest;
import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalJdbcRepository;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.exception.CategoryException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalCreateService {

    private final GoalRepository goalRepository;
    private final CategoryGoalRepository categoryGoalRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryGoalJdbcRepository categoryGoalJdbcRepository;
    private final DailyPlanJdbcRepository dailyPlanJdbcRepository;

    public void create(GoalCreateRequest request, String memberId) {
        Member member = memberRepository.findByIdAndFetchGoals(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        memberRepository.findByIdAndFetchCategories(memberId);
        validateGoalInput(request, member);

        Goal goal = Goal.of(request.getStartDate(), request.getEndDate(), request.getTitle(), request.getIcon(), request.getTotalBudget(), member);
        Goal savedGoal = goalRepository.save(goal);

        List<CategoryGoal> categoryGoals = createCategoryGoals(member.getCategories(), savedGoal, request.getCategoryGoals());
        List<DailyPlan> dailyPlans = createDailyPlans(savedGoal, request.getStartDate(), request.getDailyBudgets());

        if (request.getCanRestore()) {
            handleRestore(request, memberId, dailyPlans);
        }

        categoryGoalJdbcRepository.batchInsert(categoryGoals);
        dailyPlanJdbcRepository.batchInsert(dailyPlans);
    }

    }

    public void updateDailyBudgets(String memberId, Long goalId, List<Long> dailyBudgets) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByIdAndFetchDailyPlans(goalId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));
        validateDailyPlans(goal.getStartDate(), goal.getEndDate(), dailyBudgets, goal.getTotalBudget());

        List<DailyPlan> dailyPlans = goal.getDailyPlans();
        dailyPlanJdbcRepository.batchUpdateBudget(dailyPlans, dailyBudgets);
    }

    private void handleRestore(GoalCreateRequest request, String memberId, List<DailyPlan> dailyPlans) {
        if (request.getRestore()) {
            Map<LocalDate, Long> costMap = expenseRepository
                    .findTotalCostDate(memberId, request.getStartDate(), request.getEndDate());

            dailyPlans.forEach(dailyPlan -> {
                if (costMap.containsKey(dailyPlan.getDate())) {
                    dailyPlan.updateTotalCost(costMap.get(dailyPlan.getDate()));
                }});
            return;
        }

        List<Long> expenseIds = expenseRepository
                .findByMemberIdAndDateRange(memberId, request.getStartDate(), request.getEndDate());
        expenseRepository.deleteByIds(expenseIds);
    }

    private void validateGoalInput(GoalCreateRequest request, Member member) {
        validateGoalPeriod(member.getGoals(), request.getStartDate(), request.getEndDate());
        validateCategoryGoals(request.getCategoryGoals(), request.getTotalBudget());
        validateDailyPlans(request.getStartDate(), request.getEndDate(), request.getDailyBudgets(), request.getTotalBudget());
    }

    private List<CategoryGoal> createCategoryGoals(List<Category> categories, Goal goal, List<CategoryGoalRequest> categoryGoals) {
        List<CategoryGoal> result = new ArrayList<>();
        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        for (CategoryGoalRequest categoryGoal : categoryGoals) {
            Category category = Optional.ofNullable(categoryMap.get(categoryGoal.getCategoryId()))
                    .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));

            result.add(CategoryGoal.of(categoryGoal.getCategoryBudget(), category, goal));
        }
        return result;
    }

    private List<DailyPlan> createDailyPlans(Goal goal, LocalDate startDate, List<Long> dailyBudgets) {
        return IntStream.range(0, dailyBudgets.size())
                .mapToObj(i -> DailyPlan.of(startDate.plusDays(i), dailyBudgets.get(i), goal))
                .collect(Collectors.toList());
    }

    private void validateGoalPeriod(List<Goal> goals, LocalDate startDate, LocalDate endDate) {
        if (!startDate.isBefore(endDate)) {
            throw new GoalException(INVALID_GOAL_INPUT, "시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        for (Goal goal : goals) {
            if (!goal.getStartDate().isAfter(endDate) && !goal.getEndDate().isBefore(startDate)) {
                throw new GoalException(INVALID_GOAL_INPUT, "기존 목표 기간과 겹칠 수 없습니다.");
            }
        }
    }

    private void validateCategoryGoals(List<CategoryGoalRequest> categoryGoals, Long totalBudget) {
        HashSet<Long> uniqueIdSet = new HashSet<>();
        for (CategoryGoalRequest category : categoryGoals) {
            if (!uniqueIdSet.add(category.getCategoryId())) {
                throw new GoalException(INVALID_GOAL_INPUT, "똑같은 카테고리의 목표를 만들 수 없습니다.");
            }
        }

        long categoryBudgetSum = categoryGoals.stream()
                .mapToLong(CategoryGoalRequest::getCategoryBudget)
                .sum();
        if (categoryBudgetSum > totalBudget) {
            throw new GoalException(INVALID_GOAL_INPUT, "카테고리 목표 금액 총 합은 전체 목표 금액을 초과할 수 없습니다.");
        }
    }

    private void validateDailyPlans(LocalDate startDate, LocalDate endDate, List<Long> dailyBudgets, Long totalBudget) {
        if (dailyBudgets.size() != (ChronoUnit.DAYS.between(startDate, endDate) + 1)) {
            throw new GoalException(INVALID_GOAL_INPUT, "전체 목표 기간 내 각각의 일일 계획이 존재해야 합니다.");
        }

        long dailyBudgetSum = dailyBudgets.stream()
                .mapToLong(Long::longValue)
                .sum();
        if (dailyBudgetSum != totalBudget) {
            throw new GoalException(INVALID_GOAL_INPUT, "일일 계획 금액의 총 합이 목표 전체 금액과 같아야 합니다.");
        }
    }
}
