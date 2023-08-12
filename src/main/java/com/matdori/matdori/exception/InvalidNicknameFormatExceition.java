package com.matdori.matdori.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidNicknameFormatExceition extends RuntimeException{
    private ErrorCode errorCode;
}
