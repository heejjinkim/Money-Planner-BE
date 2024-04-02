package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.CATEGORY_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_GOAL_INPUT;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanJdbcRepository;
import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalJdbcRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.GoalCreateRequestFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.exception.CategoryException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class GoalCreateServiceTest {

    @Autowired
    private GoalCreateService goalCreateService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private GoalRepository goalRepository;
    @MockBean
    private CategoryGoalJdbcRepository categoryGoalJdbcRepository;
    @MockBean
    private DailyPlanJdbcRepository dailyPlanJdbcRepository;

    @Test
    void 목표등록이_성공한경우() {
        GoalCreateRequest request = GoalCreateRequestFixture.create();
        Member member = MemberFixture.createWithCategory();
        Goal mockGoal = mock(Goal.class);

        when(memberRepository.findByIdAndFetchGoals(member.getId())).thenReturn(Optional.of(member));
        when(goalRepository.save(any())).thenReturn(mockGoal);

        goalCreateService.create(request, member.getId());

        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void 목표등록시_요청한유저가_존재하지않는경우() {
        String memberId = "1";
        GoalCreateRequest request = GoalCreateRequestFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalCreateService.create(request, memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 목표가_1일인기간을_입력한경우() {
        String memberId = "1";
        GoalCreateRequest request = GoalCreateRequestFixture.create(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1));

        when(memberRepository.findByIdAndFetchGoals(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatThrownBy(() -> goalCreateService.create(request, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_INPUT)
                .hasMessage("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
    }

    @Test
    void 목표등록시_시작기간이_끝기간보다_느린경우() {
        String memberId = "1";
        GoalCreateRequest request = GoalCreateRequestFixture.create(LocalDate.of(2024, 1, 2), LocalDate.of(2024, 1, 1));

        when(memberRepository.findByIdAndFetchGoals(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatThrownBy(() -> goalCreateService.create(request, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_INPUT)
                .hasMessage("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
    }

    @Test
    void 등록할목표기간이_기존목표기간과_겹치는경우() {
        GoalCreateRequest request = GoalCreateRequestFixture.create(LocalDate.of(2024, 1, 2), LocalDate.of(2024, 1, 3));
        Member member = MemberFixture.create();

        when(memberRepository.findByIdAndFetchGoals(member.getId())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> goalCreateService.create(request, member.getId()))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_INPUT)
                .hasMessage("기존 목표 기간과 겹칠 수 없습니다.");
    }

    @Test
    void 중복된카테고리의_목표를_저장하는경우() {
        GoalCreateRequest request = GoalCreateRequestFixture.createDuplicatedCategoryGoals();
        Member member = MemberFixture.create();

        when(memberRepository.findByIdAndFetchGoals(member.getId())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> goalCreateService.create(request, member.getId()))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_INPUT)
                .hasMessage("똑같은 카테고리의 목표를 만들 수 없습니다.");
    }

    @Test
    void 카테고리목표금액_총합이_전체목표금액을_초과하는경우() {
        GoalCreateRequest request = GoalCreateRequestFixture.createInvalidCategoryBudget();
        Member member = MemberFixture.create();

        when(memberRepository.findByIdAndFetchGoals(member.getId())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> goalCreateService.create(request, member.getId()))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_INPUT)
                .hasMessage("카테고리 목표 금액 총 합은 전체 목표 금액을 초과할 수 없습니다.");
    }

    @Test
    void 목표기간보다_계획기간이_짧은경우() {
        GoalCreateRequest request = GoalCreateRequestFixture.createInvalidDailyPlanPeriod();
        Member member = MemberFixture.create();

        when(memberRepository.findByIdAndFetchGoals(member.getId())).thenReturn(Optional.of(mock(Member.class)));

        assertThatThrownBy(() -> goalCreateService.create(request, member.getId()))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_INPUT)
                .hasMessage("전체 목표 기간 내 각각의 일일 계획이 존재해야 합니다.");
    }

    @Test
    void 전체목표금액이_일일계획금액총합과_일치하지않는경우() {
        GoalCreateRequest request = GoalCreateRequestFixture.createInvalidDailyBudgetSum();
        Member member = MemberFixture.create();

        when(memberRepository.findByIdAndFetchGoals(member.getId())).thenReturn(Optional.of(mock(Member.class)));

        assertThatThrownBy(() -> goalCreateService.create(request, member.getId()))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_INPUT)
                .hasMessage("일일 계획 금액의 총 합이 목표 전체 금액과 같아야 합니다.");
    }

    @Test
    void 존재하지않는_카테고리에_목표등록하는경우() {
        GoalCreateRequest request = GoalCreateRequestFixture.create();
        Member member = MemberFixture.create();

        when(memberRepository.findByIdAndFetchGoals(member.getId())).thenReturn(Optional.of(mock(Member.class)));
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalCreateService.create(request, member.getId()))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("errorCode", CATEGORY_NOT_FOUND);
    }

}
