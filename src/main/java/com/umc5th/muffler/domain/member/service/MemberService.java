package com.umc5th.muffler.domain.member.service;

import static com.umc5th.muffler.entity.constant.SocialType.APPLE;
import static com.umc5th.muffler.entity.constant.SocialType.KAKAO;
import static com.umc5th.muffler.global.response.code.ErrorCode.BAD_REQUEST;
import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_TOKEN;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.UNSUPPORTED_SOCIAL_TYPE;

import com.umc5th.muffler.domain.category.repository.BatchUpdateCategoryRepository;
import com.umc5th.muffler.domain.member.dto.LoginRequest;
import com.umc5th.muffler.domain.member.dto.LoginResponse;
import com.umc5th.muffler.domain.member.dto.MemberInfo;
import com.umc5th.muffler.domain.member.dto.WithdrawRequest;
import com.umc5th.muffler.domain.member.dto.WithdrawalConverter;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.member.repository.WithdrawalReasonRepository;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.MemberAlarm;
import com.umc5th.muffler.entity.constant.Role;
import com.umc5th.muffler.entity.constant.SocialType;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.security.jwt.JwtTokenUtils;
import com.umc5th.muffler.global.security.jwt.TokenInfo;
import java.util.Collections;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final WithdrawalReasonRepository withdrawalRepository;
    private final BatchUpdateCategoryRepository batchUpdateCategoryRepository;
    private final AppleService appleService;
    private final KakaoService kakaoService;
    private final JwtTokenUtils jwtTokenUtils;
    private final EntityManager entityManager;

    public LoginResponse login(LoginRequest request) {
        String memberId = socialLogin(request);
        Member member = registerUserIfNeed(memberId, request.getSocialType());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getId(), null, Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));
        TokenInfo tokenInfo = jwtTokenUtils.generateToken(authentication);
        if (member.getName() == null) {
            return new LoginResponse(true, tokenInfo);
        }
        member.setRefreshToken(tokenInfo.getRefreshToken());
        return new LoginResponse(false, tokenInfo);
    }

    public MemberInfo join(String memberId, MemberInfo request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        member.setNameAndProfile(request.getName(), request.getProfileImg());
        return new MemberInfo(member.getName(), member.getProfileImg());
    }

    public TokenInfo refreshAccessToken(String refreshToken) {
        if (!jwtTokenUtils.validateToken(refreshToken)) {
            throw new CommonException(INVALID_TOKEN);
        }
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
        TokenInfo newToken = jwtTokenUtils.generateToken(authentication);

        member.setRefreshToken(newToken.getRefreshToken());
        return newToken;
    }

    public void withdraw(WithdrawRequest request, String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        withdrawalRepository.save(WithdrawalConverter.toEntity(member, request));
        socialWithdraw(request, memberId);
        memberRepository.deleteMemberAndRelatedEntities(member.getId());
    }

    private String socialLogin(LoginRequest request) {
        SocialType type = request.getSocialType();
        if (type == APPLE) {
            return appleService.login(request.getToken());
        }
        if (type == KAKAO) {
            return kakaoService.login(request.getToken());
        }
        throw new MemberException(UNSUPPORTED_SOCIAL_TYPE);
    }

    private Member registerUserIfNeed(String memberId, SocialType socialType) {
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            member = Member.builder()
                    .id(memberId)
                    .socialType(socialType)
                    .role(Role.USER)
                    .build();
            member.setMemberAlarm(MemberAlarm.builder().build()); // member에서 memberAlarm과 연관관계 설정을 위해서 -- 리뷰 후 주석 삭제 예정
            member = memberRepository.save(member);
            entityManager.flush();
            batchUpdateCategoryRepository.insertDefaultCategories(member.getId());
        }

        return member;
    }

    private void socialWithdraw(WithdrawRequest request, String memberId) {
        SocialType type = request.getSocialType();
        if (type == KAKAO) {
            kakaoService.leave(memberId);
            return;
        }
        if (type == APPLE) {
            if (request.getAuthenticationCode() == null) {
                throw new MemberException(BAD_REQUEST, "애플 AuthenticationCode가 없습니다.");
            }
            appleService.leave(request.getAuthenticationCode());
            return;
        }
        throw new MemberException(UNSUPPORTED_SOCIAL_TYPE);
    }
}
