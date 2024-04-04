package com.umc5th.muffler.domain.member.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import com.umc5th.muffler.entity.QCategory;
import com.umc5th.muffler.entity.QCategoryGoal;
import com.umc5th.muffler.entity.QDailyPlan;
import com.umc5th.muffler.entity.QExpense;
import com.umc5th.muffler.entity.QGoal;
import com.umc5th.muffler.entity.QMember;
import com.umc5th.muffler.entity.QMemberAlarm;
import com.umc5th.muffler.entity.QRoutine;
import com.umc5th.muffler.entity.QWeeklyRepeatDay;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private List<NotEnrolledMember> findNotEnrolledMember(LocalDate date, Predicate alarmAgreePredicate) {
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;
        QMember member = QMember.member;
        QExpense expense = QExpense.expense;
        QGoal goal = QGoal.goal;
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        return queryFactory.select(
                        Projections.constructor(NotEnrolledMember.class,
                                memberAlarm.token.as("alarmToken")))
                .from(memberAlarm)
                .join(member).on(member.memberAlarm.id.eq(memberAlarm.id))
                .join(goal).on(goal.member.id.eq(memberAlarm.member.id))
                .join(dailyPlan).on(dailyPlan.goal.id.eq(goal.id))
                .where(
                        memberAlarm.token.isNotNull(),
                        alarmAgreePredicate,
                        dailyPlan.date.eq(date).and(dailyPlan.isZeroDay.eq(false)),
                        member.id.notIn(
                                JPAExpressions.select(expense.member.id)
                                        .from(expense)
                                        .where(expense.date.eq(date)))
                )
                .fetch();
    }

    @Override
    public List<NotEnrolledMember> findTodayNotEnrolledMember(LocalDate today) {
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;
        return findNotEnrolledMember(today, memberAlarm.isTodayEnrollRemindAgree.eq(true));
    }

    @Override
    public List<NotEnrolledMember> findYesterdayNotEnrolledMember(LocalDate yesterday) {
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;
        return findNotEnrolledMember(yesterday, memberAlarm.isYesterdayEnrollRemindAgree.eq(true));
    }

    @Override
    public void deleteMemberAndRelatedEntities(String memberId) {
        // Routine 삭제
        QRoutine routine = QRoutine.routine;
        QWeeklyRepeatDay weeklyRepeatDay = QWeeklyRepeatDay.weeklyRepeatDay;

        List<Long> routineIds = queryFactory
                .select(routine.id)
                .from(routine)
                .where(routine.member.id.eq(memberId)).fetch();

        queryFactory.delete(weeklyRepeatDay)
                .where(weeklyRepeatDay.routine.id.in(routineIds)).execute();

        queryFactory.delete(routine)
                .where(routine.id.in(routineIds)).execute();

        // Expense 삭제
        QExpense expense = QExpense.expense;

        queryFactory.delete(expense)
                .where(expense.member.id.eq(memberId)).execute();

        // Goal 삭제
        QGoal goal = QGoal.goal;
        QCategoryGoal categoryGoal = QCategoryGoal.categoryGoal;
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        List<Long> goalIds = queryFactory
                .select(goal.id)
                .from(goal)
                .where(goal.member.id.eq(memberId)).fetch();

        queryFactory.delete(categoryGoal)
                .where(categoryGoal.goal.id.in(goalIds)).execute();

        queryFactory.delete(dailyPlan)
                .where(dailyPlan.goal.id.in(goalIds)).execute();

        queryFactory.delete(goal)
                .where(goal.id.in(goalIds)).execute();

        // Category 삭제
        QCategory category = QCategory.category;

        queryFactory.delete(category)
                .where(category.member.id.eq(memberId)).execute();

        // Member와 MemberAlarm 삭제
        QMember member = QMember.member;
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;

        Long memberAlarmId = queryFactory
                .select(memberAlarm.id)
                .from(memberAlarm)
                .where(memberAlarm.member.id.eq(memberId)).fetchFirst();

        queryFactory.delete(member)
                .where(member.id.eq(memberId)).execute();

        queryFactory.delete(memberAlarm)
                .where(memberAlarm.id.eq(memberAlarmId)).execute();
    }
}
