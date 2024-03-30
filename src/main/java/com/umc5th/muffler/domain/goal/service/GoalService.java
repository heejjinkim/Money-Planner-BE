package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.GOAL_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;

import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.dto.GoalConverter;
import com.umc5th.muffler.domain.goal.dto.GoalGetResponse;
import com.umc5th.muffler.domain.goal.dto.GoalInfo;
import com.umc5th.muffler.domain.goal.dto.GoalListResponse;
import com.umc5th.muffler.domain.goal.dto.GoalPreviewResponse;
import com.umc5th.muffler.domain.goal.dto.GoalReportResponse;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.util.CalcUtils;
import com.umc5th.muffler.global.util.DateTimeProvider;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final DateTimeProvider dateTimeProvider;
    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final CategoryGoalRepository categoryGoalRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public List<Goal> getGoals(String memberId) {
        Member member = memberRepository.findByIdAndFetchGoals(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        return member.getGoals();
    }

    @Transactional(readOnly = true)
    public boolean checkRestore(String memberId, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        return expenseRepository.existsExpense(memberId, startDate, endDate);
    }

    public void updateTitleAndIcon(Long goalId, String title, String icon, String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByIdAndMemberId(goalId, memberId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));
        goal.updateTitle(title);
        goal.updateIcon(icon);
    }

    @Transactional
    public void delete(Long goalId, String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByIdAndMemberId(goalId, memberId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));

        List<Long> categoryGoalIds = categoryGoalRepository.findByGoalId(goalId);
        categoryGoalRepository.deleteByIds(categoryGoalIds);

        List<Long> dailyPlanIds = dailyPlanRepository.findByGoalId(goalId);
        dailyPlanRepository.deleteByIds(dailyPlanIds);

        goalRepository.deleteByIdAndMemberId(goalId, memberId);
    }

    public GoalReportResponse getReport(Long goalId, String memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByIdWithCategoryGoals(goalId, memberId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));

        List<DailyPlan> dailyPlans = goal.getDailyPlans();
        List<CategoryGoal> categoryGoals = goal.getCategoryGoals();
        List<Expense> expenses = expenseRepository.findAllByMemberAndDateBetween(member, goal.getStartDate(), goal.getEndDate());

        return GoalConverter.getGoalReportResponse(categoryGoals, dailyPlans, expenses);
    }

    public GoalGetResponse getGoalWithTotalCost(Long goalId, String memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));
        List<DailyPlan> dailyPlans = goal.getDailyPlans();

        return GoalConverter.getGoalWithTotalCostResponse(goal, dailyPlans);
    }

    @Transactional(readOnly = true)
    public GoalInfo getGoalNow(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Goal goal = goalRepository.findByDateBetweenAndDailyPlans(dateTimeProvider.nowDate(), memberId).orElse(null);
        if (goal == null) {
            return new GoalInfo();
        }

        Long totalCost = CalcUtils.sumDailyPlanTotalCost(goal.getDailyPlans());
        return GoalConverter.getNowGoalResponse(goal, totalCost);
    }

    @Transactional(readOnly = true)
    public GoalPreviewResponse getGoalPreview(String memberId, Pageable pageable, LocalDate endDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        LocalDate today = dateTimeProvider.nowDate();

        Slice<Goal> goalList = goalRepository.findByMemberIdAndDailyPlans(member.getId(), pageable, today, endDate);
        if (goalList.isEmpty()) {
            return new GoalPreviewResponse();
        }

        Map<Goal, Long> goalAndTotalCost = new LinkedHashMap<>();

        goalList.stream()
                .filter(goal -> goal.getEndDate().isBefore(today))
                .forEach(goal -> calculateGoalCost(goalAndTotalCost, goal));
        List<Goal> futureGoals = goalList.stream()
                .filter(goal -> goal.getEndDate().isAfter(today))
                .collect(Collectors.toList());

        return GoalConverter.getGoalPreviewResponse(goalAndTotalCost, futureGoals, goalList.hasNext());
    }

    private void calculateGoalCost(Map<Goal, Long> goalAndTotalCost, Goal goal) {
        Long totalCost = goal.getDailyPlans().stream().mapToLong(DailyPlan::getTotalCost).sum();
        goalAndTotalCost.put(goal, totalCost);
    }

    @Transactional(readOnly = true)
    public GoalListResponse getGoalList(String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        List<Goal> goalList = member.getGoals();

        if (goalList.isEmpty()) {
            return new GoalListResponse();
        }
        goalList.sort(Comparator.comparing(Goal::getEndDate).reversed());

        return GoalConverter.getGoalListResponse(goalList);
    }
}
