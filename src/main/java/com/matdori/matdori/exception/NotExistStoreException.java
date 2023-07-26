package com.matdori.matdori.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotExistStoreException extends RuntimeException{
    private ErrorCode errorCode;
}
