package com.umc5th.muffler.domain.goal.dto;

import com.umc5th.muffler.domain.dailyplan.repository.dao.DailyPlanWithCostAndBudget;
import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;

import java.util.*;
import java.util.stream.Collectors;

public class GoalConverter {
    public static GoalPreviousResponse getGoalPreviousResponse(List<Goal> goals) {
        return new GoalPreviousResponse(
                goals.stream()
                        .sorted(Comparator.comparing(Goal::getStartDate).reversed())
                        .map(goal -> new GoalTerm(goal.getStartDate(), goal.getEndDate()))
                        .collect(Collectors.toList())
        );
    }

    public static GoalGetResponse getGoalWithTotalCostResponse(Goal goal, List<DailyPlanWithCostAndBudget> dailyPlans){
        return GoalGetResponse.builder()
                .totalBudget(goal.getTotalBudget())
                .title(goal.getTitle())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .icon(goal.getIcon())
                .totalCost(getTotalCost(dailyPlans))
                .categoryGoals(getCategoryGoals(goal.getCategoryGoals()))
                .dailyBudgets(getDailyBudgets(dailyPlans))
                .build();
    }

    private static long getTotalCost(List<DailyPlanWithCostAndBudget> dailyPlans) {
        return dailyPlans.stream().mapToLong(dp -> dp.getTotalCost()).sum();
    }

    private static List<CategoryGoalRequest> getCategoryGoals(List<CategoryGoal> categoryGoals) {
        return categoryGoals.stream()
                .map(cg -> new CategoryGoalRequest(cg.getId(), cg.getCategory().getId(), cg.getBudget()))
                .collect(Collectors.toList());
    }

    private static List<Long> getDailyBudgets(List<DailyPlanWithCostAndBudget> dailyPlans) {
        return dailyPlans.stream()
                .map(dp -> dp.getBudget())
                .collect(Collectors.toList());
    }

    public static GoalReportResponse getGoalReportResponse(List<CategoryGoal> categoryGoals, List<DailyPlan> dailyPlans, List<Expense> expenses) {
        long zeroDayCount = dailyPlans.stream().filter(DailyPlan::getIsZeroDay).count();

        Map<Long, CategoryGoalReportDto> categoryReportsMap = initCategoryReportsMap(categoryGoals);
        Map<String, Long> categoryTotalCostsMap = new HashMap<>();

        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().getName();
            Long categoryId = expense.getCategory().getId();

            // 카테고리별 총 비용 업데이트
            categoryTotalCostsMap.put(categoryName, categoryTotalCostsMap.getOrDefault(categoryName, 0L) + expense.getCost());

            // 해당 카테고리에 대한 리포트가 존재하는 경우(목표를 세운 카테고리의 경우) 리포트 업데이트
            CategoryGoalReportDto report = categoryReportsMap.get(categoryId);
            if (report != null) {
                report.addExpense(expense.getCost());
            }
        }

        List<CategoryTotalCostDto> categoryTotalCosts = categoryTotalCostsMap.entrySet().stream()
                .map(entry -> new CategoryTotalCostDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(CategoryTotalCostDto::getTotalCost).reversed())
                .collect(Collectors.toList());

        List<CategoryGoalReportDto> categoryGoalReports = new ArrayList<>(categoryReportsMap.values());
        categoryGoalReports.sort(Comparator.comparingLong(CategoryGoalReportDto::getTotalCost).reversed());
        for (CategoryGoalReportDto categoryGoalReport : categoryGoalReports) {
            categoryGoalReport.calculateAvgCost();
        }

        return GoalReportResponse.builder()
                .categoryTotalCosts(categoryTotalCosts)
                .categoryGoalReports(categoryGoalReports)
                .zeroDayCount(zeroDayCount)
                .build();
    }

    private static Map<Long, CategoryGoalReportDto> initCategoryReportsMap(List<CategoryGoal> categoryGoals) {
        return categoryGoals.stream()
                .collect(Collectors.toMap(
                        categoryGoal -> categoryGoal.getCategory().getId(),
                        categoryGoal -> CategoryGoalReportDto.builder()
                                .categoryName(categoryGoal.getCategory().getName())
                                .categoryIcon(categoryGoal.getCategory().getIcon())
                                .categoryBudget(categoryGoal.getBudget())
                                .build()
                ));
    }

    public static GoalInfo getNowGoalResponse(Goal goal, List<DailyPlanWithCostAndBudget> dailyPlans) {
        return GoalInfo.builder()
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .icon(goal.getIcon())
                .totalBudget(goal.getTotalBudget())
                .totalCost(getTotalCost(dailyPlans))
                .endDate(goal.getEndDate())
                .build();
    }

    public static GoalPreviewResponse getGoalPreviewResponse(Map<Goal, Long> pastInfos, List<Goal> futureGoals, Boolean hasNext) {

        List<GoalInfo> past = pastInfos.entrySet().stream()
                .map(entry -> GoalInfo.builder()
                        .goalId(entry.getKey().getId())
                        .goalTitle(entry.getKey().getTitle())
                        .icon(entry.getKey().getIcon())
                        .totalBudget(entry.getKey().getTotalBudget())
                        .totalCost(entry.getValue())
                        .endDate(entry.getKey().getEndDate())
                        .build())
                .collect(Collectors.toList());

        List<GoalInfo> future = futureGoals.stream()
                .map(goal -> GoalInfo.builder()
                        .goalId(goal.getId())
                        .goalTitle(goal.getTitle())
                        .icon(goal.getIcon())
                        .totalBudget(goal.getTotalBudget())
                        .endDate(goal.getEndDate())
                        .build())
                .collect(Collectors.toList());

        return GoalPreviewResponse.builder()
                .futureGoal(future)
                .endedGoal(past)
                .hasNext(hasNext)
                .build();
    }

    public static GoalListResponse getGoalListResponse(List<Goal> goalList) {

        List<GoalListInfo> info = goalList.stream()
                .map(goal -> GoalListInfo.builder()
                        .goalId(goal.getId())
                        .goalTitle(goal.getTitle())
                        .icon(goal.getIcon())
                        .build())
                .collect(Collectors.toList());

        return GoalListResponse.builder()
                .goalList(info)
                .build();
    }
}
