package com.matdori.matdori.exception;

import com.matdori.matdori.domain.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {

    // 상민
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e){
        // 서버 에러 상태 메시지와 body에 에러상태 메시지(문자열)을 넣어 반환해줌
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(e.getMessage()));
    }


    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<?> invalidEmailExceptionHandler(InvalidEmailException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name())); // Error code 이름. ex) INVALID_EMAIL_FORMAT
    }

    @ExceptionHandler(DuplicatedUserException.class)
    public ResponseEntity<?> duplicatedExceptionHandler(DuplicatedUserException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistUserException.class)
    public ResponseEntity<?> notExistExceptionHandler(NotExistUserException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(ExpiredSessionException.class)
    public ResponseEntity<?> expiredSessionException(ExpiredSessionException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(InsufficientPrivilegesException.class)
    public ResponseEntity<?> insufficientPrivilegesException(InsufficientPrivilegesException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }
    @ExceptionHandler(InvalidCookieException.class)
    public ResponseEntity<?> invalidCookieException(InvalidCookieException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    // --------------------------------------------------------


}
