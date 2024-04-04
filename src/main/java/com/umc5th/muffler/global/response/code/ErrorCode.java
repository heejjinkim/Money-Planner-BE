package com.umc5th.muffler.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 일반 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "금지된 요청입니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "권한이 유효하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),


    // Member 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    UNSUPPORTED_SOCIAL_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"지원하지 않는 소셜 로그인 입니다."),


    // Goal 에러
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 목표입니다."),
    INVALID_GOAL_INPUT(HttpStatus.BAD_REQUEST, "올바르지 않은 목표 입력입니다."),
    NO_GOAL_IN_GIVEN_DATE(HttpStatus.BAD_REQUEST, "해당 날짜에 일치하는 목표가 없습니다"),
    CATEGORY_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 목표입니다."),


    // DailyPlan 에러
    DAILYPLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 일일 소비 계획입니다."),
    NO_DAILY_PLAN_GIVEN_DATE(HttpStatus.NOT_FOUND, "수정하려는 소비날짜에 맞는 일일 목표가 없습니다. "
            + "목표가 설정된 다른 날로 수정을 하거나 수정하려는 날에 목표를 설정해주세요."),


    // Category 에러
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "카테고리 이름이 중복되었습니다."),
    ACCESS_TO_OTHER_USER_CATEGORY(HttpStatus.UNAUTHORIZED, "다른 사람의 카테고리 아이디입니다."),
    ALREADY_INACTIVE_CATEGORY(HttpStatus.BAD_REQUEST, "이미 삭제된 카테고리 입니다."),
    CANNOT_DELETE_DEFAULT_CATEGORY(HttpStatus.BAD_REQUEST,"기본 카테고리는 삭제할 수 없습니다."),
    CANNOT_UPDATE_DEFAULT_ICON(HttpStatus.BAD_REQUEST, "디폴트 카테고리는 아이콘을 수정할 수 없습니다."),
    ETC_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "활성화 된 기타 카테고리를 찾을 수 없습니다."),
    CANNOT_UPDATE_ETC_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "기타 카테고리는 이름을 변경할 수 없습니다."),
    CATEGORY_UNEXPECTED_ORDER(HttpStatus.BAD_REQUEST, "카테고리 순서는 1부터 연속해서 증가해야 합니다. 중간에 누락된 번호가 있습니다"),
    CATEGORY_BATCH_INSERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "DB 연결 문제로 기본 카테고리를 생성할 수 없었습니다."),

    // Expense 에러
    EXPENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 소비 내역입니다."),
    CANNOT_UPDATE_OTHER_MEMBER_EXPENSE(HttpStatus.UNAUTHORIZED, "다른 유저의 소비 내역을 수정할 수 없습니다"),
    CANNOT_UPDATE_TO_ZERO_DAY(HttpStatus.CONFLICT, "제로 데이로 지정한 날로는 소비 내역을 옮길 수 없습니다."),


    // Routine 에러
    INVALID_ROUTINE_INPUT(HttpStatus.BAD_REQUEST, "올바르지 않은 루틴 입력입니다."),
    ROUTINE_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "루틴 타입이 없거나 유효하지 않습니다."),
    ROUTINE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 루틴입니다."),

    // Mail 에러
    FAIL_SEND_EMAIL(HttpStatus.BAD_REQUEST, "이메일 전송에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}