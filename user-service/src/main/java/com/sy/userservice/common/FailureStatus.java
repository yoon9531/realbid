package com.sy.userservice.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FailureStatus implements ApiStatus {

    // 사용자 관련 에러
    PASSWORD_NOT_CORRESPOND (HttpStatus.UNAUTHORIZED, "LOGIN4001", "비밀번호가 일치하지 않습니다."), // 인증 실패
    USER_NOT_LOGGED_IN(HttpStatus.UNAUTHORIZED, "USER4001", "로그인이 필요합니다. 다시 로그인해주세요."),
    USER_ALREADY_EXIST (HttpStatus.CONFLICT, "REGISTER4002", "이미 존재하는 사용자입니다."), // 리소스 충돌 (회원가입 시)
    USER_NOT_AUTHORIZED (HttpStatus.FORBIDDEN, "USER4003", "사용자 권한이 없습니다."), // 권한 부족
    USER_NOT_MATCH (HttpStatus.FORBIDDEN, "USER4004", "사용자가 일치하지 않습니다."), // 권한 부족
    USER_NOT_FOUND (HttpStatus.NOT_FOUND, "USER4006", "사용자가 존재하지 않습니다."), // 존재하지 않는 사용자
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER4008", "비밀번호가 유효하지 않습니다."), // 비밀번호 유효성 검사 실패
    NICKNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER4009", "이미 존재하는 닉네임입니다" ),
    PROFILE_IMAGE_EMPTY(HttpStatus.BAD_REQUEST, "USER4010", "프로필 이미지가 비어 있습니다."),

    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4001", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4002", "유효하지 않은 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4003", "지원하지 않는 토큰입니다."),
    EMPTY_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4004", "토큰이 비어있습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4005", "유효하지 않은 리프레시 토큰입니다.");


    private final HttpStatus httpStatus;
    // 토큰 관련 에러;
    private String code;
    private String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
