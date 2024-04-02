package com.umc5th.muffler.domain.member.controller;

import com.umc5th.muffler.domain.member.dto.AlarmAgreeUpdateRequest;
import com.umc5th.muffler.domain.member.dto.AlarmAgreementResponse;
import com.umc5th.muffler.domain.member.dto.TokenEnrollRequest;
import com.umc5th.muffler.domain.member.service.MemberAlarmService;
import com.umc5th.muffler.global.response.Response;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/alarm")
public class MemberAlarmController {
    private final MemberAlarmService memberAlarmService;

    @PutMapping("/agree")
    public Response<Void> fetchAlarmAgree(Principal principal, @RequestBody @Valid AlarmAgreeUpdateRequest request) {
        memberAlarmService.fetchAlarmAgree(principal.getName(), request);
        return Response.success();
    }

    @PatchMapping("/token")
    public Response<Void> enrollAlarmToken(Principal principal, @RequestBody @Valid TokenEnrollRequest request) {
        memberAlarmService.enrollAlarmToken(principal.getName(), request);
        return Response.success();
    }

    @DeleteMapping("/token")
    public Response<Void> deleteAlarm(Principal principal) {
        memberAlarmService.deleteAlarmToken(principal.getName());
        return Response.success();
    }

    @GetMapping("/agree")
    public Response<AlarmAgreementResponse> getAlarmAgreement(Principal principal) {
        return Response.success(memberAlarmService.getAlarmAgreement(principal.getName()));
    }
}
