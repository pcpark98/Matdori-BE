package com.matdori.matdori.exception;

import com.matdori.matdori.domain.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    @ExceptionHandler(InvalidRequiredParamException.class)
    public ResponseEntity<?> invalidRequiredParamException(InvalidRequiredParamException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e){
        return ResponseEntity.status(ErrorCode.INVALID_REQUIRED_PARAM.getStatus())
                .body(Response.error(ErrorCode.INVALID_REQUIRED_PARAM.name()));
    }
    @ExceptionHandler(IncompleteEmailVerificationException.class)
    public ResponseEntity<?> incompleteEmailVerificationException(IncompleteEmailVerificationException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> invalidPasswordException(InvalidPasswordException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(DuplicatedNicknameException.class)
    public ResponseEntity<?> duplicatedNicknameException(DuplicatedNicknameException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    // --------------------------------------------------------

    @ExceptionHandler(NotExistedJokboException.class)
    public ResponseEntity<?> notExistedJokboException(NotExistedJokboException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistedJokboCommentException.class)
    public ResponseEntity<?> notExistedJokboCommentException(NotExistedJokboCommentException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistedJokboImgException.class)
    public ResponseEntity<?> notExistedJokboImgException(NotExistedJokboImgException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistedNoticeException.class)
    public ResponseEntity<?> notExistedNoticeException(NotExistedNoticeException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistStoreException.class)
    public ResponseEntity<?> notExistStoreException(NotExistStoreException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistStoreCategoryException.class)
    public ResponseEntity<?> notExistStoreCategoryException(NotExistStoreCategoryException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistedDepartmentException.class)
    public ResponseEntity<?> notExistedDepartmentException(NotExistedDepartmentException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> missingServletRequestParameterException(MissingServletRequestParameterException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(ErrorCode.INVALID_REQUIRED_PARAM.name()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> invalidCredentialsException(InvalidCredentialsException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExisitedJokboFavoriteException.class)
    public ResponseEntity<?> notExisitedJokboFavorite(NotExisitedJokboFavoriteException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(InvalidStoreListingOrderException.class)
    public ResponseEntity<?> invalidStoreListingOrderException(InvalidStoreListingOrderException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(UnsupportedFileExtensionException.class)
    public ResponseEntity<?> unsupportedFileExtensionException(UnsupportedFileExtensionException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistedFileExtensionException.class)
    public ResponseEntity<?> notExistedFileExtension(NotExistedFileExtensionException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(NotExistedSelectedJokboException.class)
    public ResponseEntity<?> notExistedSelectedJokboException(NotExistedSelectedJokboException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }
}
