package com.matdori.matdori.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@AllArgsConstructor
public class NotExistedDepartmentException extends RuntimeException{
    private ErrorCode errorCode;
}
