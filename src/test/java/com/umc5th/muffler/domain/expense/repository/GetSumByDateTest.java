package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.ExpenseFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GetSumByDateTest {
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private MemberRepository memberRepository;
    @Test
    void getSumByDate() {
        Member member = MemberFixture.create("test");
        Category category = CategoryFixture.create(member);
        member.addCategory(category);
        member = memberRepository.save(member);

        Expense ex1 = ExpenseFixture.create(1L, LocalDate.of(2024, 1, 1), member, category);
        Expense ex2 = ExpenseFixture.create(2L, LocalDate.of(2024, 1, 1), member, category);
        Expense ex3 = ExpenseFixture.create(3L, LocalDate.of(2024, 1, 1), member, category);
        expenseRepository.save(ex1);
        expenseRepository.save(ex2);
        expenseRepository.save(ex3);

        Map<LocalDate, Long> totalCostDate = expenseRepository.findTotalCostDate(member.getId(), LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1));
        assertEquals(1, totalCostDate.size());
        assertEquals(300L, totalCostDate.get(LocalDate.of(2024, 1, 1)));
    }

}