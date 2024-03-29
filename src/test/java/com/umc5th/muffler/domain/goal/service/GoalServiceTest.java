package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.GOAL_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.goal.dto.GoalGetResponse;
import com.umc5th.muffler.domain.goal.dto.GoalInfo;
import com.umc5th.muffler.domain.goal.dto.GoalPreviewResponse;
import com.umc5th.muffler.domain.goal.dto.GoalReportResponse;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryGoalFixture;
import com.umc5th.muffler.fixture.DailyPlanFixture;
import com.umc5th.muffler.fixture.GoalFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.util.DateTimeProvider;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@SpringBootTest
class GoalServiceTest {

    @Autowired
    private GoalService goalService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private GoalRepository goalRepository;
    @MockBean
    private CategoryGoalRepository categoryGoalRepository;
    @MockBean
    private DailyPlanRepository dailyPlanRepository;
    @MockBean
    private DateTimeProvider dateTimeProvider;

    @Test
    void 전체_목표조회가_성공한경우() {
        String memberId = "1";
        when(memberRepository.findByIdAndFetchGoals(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatCode(() -> goalService.getGoals(memberId)).doesNotThrowAnyException();

        verify(memberRepository).findByIdAndFetchGoals(memberId);
    }

    @Test
    void 전체목표조회시_요청한유저가_존재하지않는경우() {
        String memberId = "1";
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoals(memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 목표삭제에_성공할경우() {
        String memberId = "1";
        Long goalId = 1L;

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(memberId);

        Goal mockGoal = mock(Goal.class);
        when(mockGoal.getMember()).thenReturn(mockMember);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByIdAndMemberId(goalId, memberId)).thenReturn(Optional.of(mockGoal));

        goalService.delete(goalId, memberId);

        verify(goalRepository).deleteByIdAndMemberId(goalId, memberId);
    }

    @Test
    void 목표삭제시_요청한유저가_존재하지않는경우() {
        String memberId = "1";
        Long goalId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.delete(goalId, memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 목표삭제시_해당목표가_존재하지않는경우() {
        String memberId = "1";
        Long goalId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.delete(goalId, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", GOAL_NOT_FOUND);
    }

    @Test
    void 목표삭제시_등록한유저가_아닌경우() {
        String memberId = "1";
        Long goalId = 1L;

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(memberId);

        Goal mockGoal = mock(Goal.class);
        when(mockGoal.getMember()).thenReturn(mock(Member.class));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(mockGoal));

        assertThatThrownBy(() -> goalService.delete(goalId, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", GOAL_NOT_FOUND);
    }

    @Test
    void 목표탭_진행중_조회_성공() {
        String memberId = "1";
        Member mockMember = MemberFixture.MEMBER_ONE;
        Goal mockGoal = GoalFixture.create();

        when(dateTimeProvider.nowDate()).thenReturn(mockGoal.getStartDate());
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenAndDailyPlans(any(), any())).thenReturn(Optional.of(mockGoal));

        GoalInfo response = goalService.getGoalNow(memberId);

        assertNotNull(response);
        assertEquals(mockGoal.getId(), response.getGoalId());

        verify(goalRepository).findByDateBetweenAndDailyPlans(any(), any());
    }

    @Test
    void 목표탭_진행중_사용자_존재X() {
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoalNow(memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 목표탭_전체조회_성공() {
        String memberId = "1";
        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize);

        Member mockMember = MemberFixture.MEMBER_ONE;
        Goal mockEndedGoal = GoalFixture.create();
        Goal mockFutureGoal = GoalFixture.create(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2));
        List<Goal> goalList = List.of(mockFutureGoal, mockEndedGoal);
        Slice<Goal> goalSlice = new SliceImpl<>(goalList, pageable, false);

        when(dateTimeProvider.nowDate()).thenReturn(LocalDate.of(2024, 1, 5));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByMemberIdAndDailyPlans(eq(memberId), eq(pageable), any(), any())).thenReturn(goalSlice);

        GoalPreviewResponse response = goalService.getGoalPreview(memberId, pageable, null);

        assertNotNull(response);
        assertEquals(mockEndedGoal.getId(), response.getEndedGoal().get(0).getGoalId());
        assertEquals(mockFutureGoal.getId(), response.getFutureGoal().get(0).getGoalId());
        assertEquals(goalSlice.hasNext(), response.getHasNext());

        verify(goalRepository).findByMemberIdAndDailyPlans(eq(memberId), eq(pageable), any(), any());
    }

    @Test
    void 목표탭_전체조회_사용자_존재X() {
        String memberId = "1";
        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoalPreview(memberId, pageable, null))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 목표_리스트_조회_성공() {
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatCode(() -> goalService.getGoalList(memberId)).doesNotThrowAnyException();

        verify(memberRepository).findById(memberId);
    }

    @Test
    void 목표_리스트_사용자_존재X() {
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoalList(memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 목표_리포트_조회_성공() {
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();
        DailyPlan plan1 = DailyPlanFixture.DAILY_PLAN_ONE;
        DailyPlan plan2 = DailyPlanFixture.DAILY_PLAN_TWO;
        CategoryGoal categoryGoal = CategoryGoalFixture.CATEGORY_GOAL_ONE;

        Long goalId = mockGoal.getId();
        String memberId = mockMember.getId();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByIdWithCategoryGoals(goalId, memberId)).thenReturn(Optional.of(mockGoal));

        GoalReportResponse response = goalService.getReport(goalId, memberId);

        assertEquals(response.getCategoryGoalReports().get(0).getCategoryBudget(), categoryGoal.getBudget());

        verify(memberRepository).findById(memberId);
        verify(goalRepository).findByIdWithCategoryGoals(goalId, memberId);
    }

    @Test
    void 목표_리포트_멤버_존재하지_않는_경우() {
        Long goalId = 1L;
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getReport(goalId, memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);

        verify(memberRepository).findById(memberId);
    }

    @Test
    void 목표_리포트_목표_존재하지_않는_경우() {
        Long goalId = 1L;
        String memberId = "1";

        Member mockMember = mock(Member.class);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByIdWithCategoryGoals(goalId, memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getReport(goalId, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", GOAL_NOT_FOUND);

        verify(memberRepository).findById(memberId);
        verify(goalRepository).findByIdWithCategoryGoals(goalId, memberId);
    }

    @Test
    void 목표_상세_조회_성공(){
        Goal mockGoal = GoalFixture.create();
        Long goalId = mockGoal.getId();
        String memberId = "1";
        Member mockMember = mock(Member.class);
        DailyPlan plan1 = DailyPlanFixture.DAILY_PLAN_ONE;
        DailyPlan plan2 = DailyPlanFixture.DAILY_PLAN_TWO;

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(mockGoal));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        GoalGetResponse response = goalService.getGoalWithTotalCost(goalId, memberId);

        assertEquals(response.getTotalCost(), plan1.getTotalCost() + plan2.getTotalCost());
        assertEquals(response.getTitle(), mockGoal.getTitle());

        verify(goalRepository).findById(goalId);
    }
}
