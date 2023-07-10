package com.matdori.matdori.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATED_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 형식입니다."),
    INVALID_REQUIRED_PARAM(HttpStatus.BAD_REQUEST, "필수파라미터가 누락되었습니다."),
    NOT_EXISTED_USER(HttpStatus.UNAUTHORIZED, "존재하지 않는 유저입니다."),
    EXPIRED_SESSION(HttpStatus.UNAUTHORIZED, "세션이 만료됐습니다."),
    INSUFFICIENT_PRIVILEGES(HttpStatus.UNAUTHORIZED, "권한이 없는 정보입니다."),
    INVALID_REQUIRED_COOKIE(HttpStatus.UNAUTHORIZED, "쿠키가 누락되었습니다."),
    INCOMPLETE_EMAIL_VERIFICATION(HttpStatus.UNAUTHORIZED, "이메일 인증이 필요합니다.");

    private HttpStatus status;
    private String message;
}
