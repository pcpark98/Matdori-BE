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
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    NOT_EXISTED_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    EXPIRED_SESSION(HttpStatus.UNAUTHORIZED, "세션이 만료됐습니다."),
    INSUFFICIENT_PRIVILEGES(HttpStatus.FORBIDDEN, "권한이 없는 정보입니다."),
    INVALID_REQUIRED_COOKIE(HttpStatus.BAD_REQUEST, "쿠키가 누락되었습니다."),
    INCOMPLETE_EMAIL_VERIFICATION(HttpStatus.UNAUTHORIZED, "이메일 인증이 필요합니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다."),
    NOT_EXISTED_JOKBO(HttpStatus.NOT_FOUND, "존재하지 않는 족보입니다."),
    NOT_EXISTED_JOKBO_COMMENT(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    NOT_EXISTED_JOKBO_IMG(HttpStatus.NOT_FOUND, "존재하지 않는 족보 이미지입니다."),
    NOT_EXISTED_NOTICE(HttpStatus.BAD_REQUEST, "존재하지 않는 공지사항입니다."),
    NOT_EXISTED_STORE(HttpStatus.NOT_FOUND, "존재하지 않는 가게입니다."),
    NOT_EXISTED_STORE_CATEGORY(HttpStatus.BAD_REQUEST, "존재하지 않는 가게 카테고리입니다."),
    NOT_EXISTED_DEPARTMENT(HttpStatus.BAD_REQUEST, "존재하지 않는 학과입니다."),
    NOT_EXISTED_JOKBO_FAVORITE(HttpStatus.NOT_FOUND, "존재하지 않는 족보 좋아요입니다."),
    INVALID_STORE_LISTING_ORDER(HttpStatus.BAD_REQUEST, "잘못된 정렬값입니다."),
    UNSUPPORTED_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 확장자입니다. jpg, jpeg, png 파일만 사용해주세요."),
    NOT_EXISTED_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "이미지 확장자가 존재하지 않습니다."),
    NOT_EXISTED_SELECTED_JOKBO(HttpStatus.BAD_REQUEST, "하나 이상의 족보를 선택해야 합니다."),
    NOT_EXISTED_SELECTED_JOKBO_COMMENT(HttpStatus.BAD_REQUEST, "하나 이상의 댓글을 선택해야 합니다."),
    NOT_EXISTED_JOKBO_COMMENT_FAVORITE(HttpStatus.BAD_REQUEST, "유저가 좋아요하지 않은 족보입니다."),
    NOT_EXISTED_SORTING_TYPE(HttpStatus.BAD_REQUEST, "존재하지 않는 정렬타입입니다.");

    private HttpStatus status;
    private String message;
}
