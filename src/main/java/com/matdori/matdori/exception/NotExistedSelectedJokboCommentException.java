package com.matdori.matdori.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotExistedSelectedJokboCommentException extends RuntimeException {

    private ErrorCode errorCode;
}
