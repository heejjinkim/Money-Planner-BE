package com.umc5th.muffler.global.security.jwt;

import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_TOKEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.util.ResponseUtils;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String TOKEN_ERROR_MESSAGE = "유효한 인증 토큰이 필요합니다.";
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isLogin(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            processTokenAuthentication(request);
        } catch (CommonException e) {
            log.error("Invalid Token", e);
            ResponseUtils.sendErrorResponse(response, SC_UNAUTHORIZED, TOKEN_ERROR_MESSAGE);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isLogin(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/member/login");
    }

    private void processTokenAuthentication(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.hasText(token) && jwtTokenUtils.validateToken(token)) {
            Authentication authentication = jwtTokenUtils.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return;
        }
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }
        log.error("Invalid token for requestURI: {}, Access from IP: {}", request.getRequestURI(), clientIp);
        throw new CommonException(INVALID_TOKEN);
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
