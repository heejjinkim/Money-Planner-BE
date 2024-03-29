package com.umc5th.muffler.domain.goal.controller;

import com.umc5th.muffler.domain.goal.dto.DailyBudgetsRequest;
import com.umc5th.muffler.domain.goal.dto.GoalConverter;
import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import com.umc5th.muffler.domain.goal.dto.GoalGetResponse;
import com.umc5th.muffler.domain.goal.dto.GoalInfo;
import com.umc5th.muffler.domain.goal.dto.GoalListResponse;
import com.umc5th.muffler.domain.goal.dto.GoalPreviewResponse;
import com.umc5th.muffler.domain.goal.dto.GoalPreviousResponse;
import com.umc5th.muffler.domain.goal.dto.GoalReportResponse;
import com.umc5th.muffler.domain.goal.dto.GoalUpdateRequest;
import com.umc5th.muffler.domain.goal.service.GoalCreateService;
import com.umc5th.muffler.domain.goal.service.GoalService;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.global.response.Response;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goal")
public class GoalController {

    private final GoalService goalService;
    private final GoalCreateService goalCreateService;

    @PostMapping
    public Response<Void> create(@RequestBody @Valid GoalCreateRequest request, Authentication authentication) {
        goalCreateService.create(request, authentication.getName());
        return Response.success();
    }

    @GetMapping("/previous")
    public Response<GoalPreviousResponse> getPrevious(Authentication authentication) {
        List<Goal> goals = goalService.getGoals(authentication.getName());
        return Response.success(GoalConverter.getGoalPreviousResponse(goals));
    }

    @GetMapping("/restore")
    public ResponseEntity<Void> checkRestore(Authentication authentication,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        boolean isExists = goalService.checkRestore(authentication.getName(), startDate, endDate);
        if (isExists) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/restore")
    public Response<Void> restore(Authentication authentication,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                        @RequestParam boolean restore) {
        goalService.restore(authentication.getName(), startDate, endDate, restore);
        return Response.success();
    }

    @PatchMapping("/{goalId}")
    public Response<Void> updateTitleAndIcon(@PathVariable Long goalId, @RequestBody @Valid GoalUpdateRequest request, Authentication authentication) {
        goalService.updateTitleAndIcon(goalId, request.getTitle(), request.getIcon(), authentication.getName());
        return Response.success();
    }

    @PatchMapping("/{goalId}/daily-budgets")
    public Response<Void> updateDailyBudgets(@PathVariable Long goalId, @RequestBody @Valid DailyBudgetsRequest request, Authentication authentication) {
        goalCreateService.updateDailyBudgets(authentication.getName(), goalId, request.getDailyBudgets());
        return Response.success();
    }

    @DeleteMapping("/{goalId}")
    public Response<Void> delete(@PathVariable Long goalId, Authentication authentication) {
        goalService.delete(goalId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/report/{goalId}")
    public Response<GoalReportResponse> getReport(@PathVariable Long goalId, Authentication authentication){
        GoalReportResponse response = goalService.getReport(goalId, authentication.getName());
        return Response.success(response);
    }

    @GetMapping("/{goalId}")
    public Response<GoalGetResponse> getGoal(@PathVariable Long goalId, Authentication authentication){
        GoalGetResponse response = goalService.getGoalWithTotalCost(goalId, authentication.getName());
        return Response.success(response);
    }

    @GetMapping("/now")
    public Response<GoalInfo> getNowGoal(Authentication authentication) {
        GoalInfo response = goalService.getGoalNow(authentication.getName());
        return Response.success(response);
    }

    @GetMapping("/not-now")
    public Response<GoalPreviewResponse> getGoalPreview(Authentication authentication,
            @RequestParam (name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @PageableDefault(size = 10) Pageable pageable) {
        GoalPreviewResponse response = goalService.getGoalPreview(authentication.getName(), pageable, endDate);
        return Response.success(response);
    }

    @GetMapping("/list")
    public Response<GoalListResponse> getGoalList(Authentication authentication) {
        GoalListResponse response = goalService.getGoalList(authentication.getName());
        return Response.success(response);
    }
}
