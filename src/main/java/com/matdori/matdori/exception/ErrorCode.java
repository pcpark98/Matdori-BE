package com.matdori.matdori.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATED_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다.");
    private HttpStatus status;
    private String message;
}