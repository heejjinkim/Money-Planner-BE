package com.umc5th.muffler.global;

import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.message.service.AlarmService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// push alarm test용 테스트 완료 후 제거 예정
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final AlarmService alarmService;
    private final MemberRepository memberRepository;
    @GetMapping("/alarm")
    public ResponseEntity<String> testAlarm(Principal principal) {
        Member member = memberRepository.findById(principal.getName())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        String token = member.getMemberAlarm().getToken();
        if (token == null || token.isEmpty()) {
            throw new MemberException(ErrorCode.BAD_REQUEST);
        }
        alarmService.sendEndGoals(List.of(new FinishedGoal("goal title", "goal icon", token)));
        return ResponseEntity.ok("success");
    }
}
