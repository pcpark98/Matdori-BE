package com.matdori.matdori.service;

import com.matdori.matdori.domain.EmailAuthorizationType;
import com.matdori.matdori.domain.User;
import com.matdori.matdori.exception.*;
import com.matdori.matdori.repositoy.UserRepository;
import com.matdori.matdori.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorizationService {
    private final UserRepository userRepository;

    public User login(String email, String password) {
        Optional<User> user = userRepository.login(email, password);
        // 로그인 가능 여부 체크
        if (!user.isPresent())
            throw new NotExistUserException(ErrorCode.NOT_EXISTED_USER);

        return user.get();
    }

    public static void checkSession(Long pathParmUserId) {
        String sessionCookie = SessionUtil.getSessionCookie(); // session의 key값을 가져온다.
        if(sessionCookie == null)  // 쿠키를 보내지 않은 경우
            throw new ExpiredSessionException(ErrorCode.INVALID_REQUIRED_COOKIE);
        else if(SessionUtil.getAttribute(sessionCookie) == null) // 세션이 만료된 경우
            throw new ExpiredSessionException(ErrorCode.EXPIRED_SESSION);
        else if (pathParmUserId != Long.parseLong(SessionUtil.getAttribute(sessionCookie))) // 가지고 있는 쿠키로 접근할 수 없는 resource일 경우
            throw new InsufficientPrivilegesException(ErrorCode.INSUFFICIENT_PRIVILEGES);
    }

    public static void logout(){
        String sessionCookie = SessionUtil.getSessionCookie(); // session의 key값을 가져온다.
        if(sessionCookie == null)  // 쿠키를 보내지 않은 경우
            throw new ExpiredSessionException(ErrorCode.INVALID_REQUIRED_COOKIE);
        else if(SessionUtil.getAttribute(sessionCookie) == null) // 세션이 만료된 경우
            throw new ExpiredSessionException(ErrorCode.EXPIRED_SESSION);

         SessionUtil.getSession().removeAttribute(sessionCookie);
    }

    public static void checkAuthorizationNumber(String number, EmailAuthorizationType type){
        if(number == null) // 인증번호를 보내지 않은 경우
            throw new InvalidRequiredParamException(ErrorCode.INVALID_REQUIRED_PARAM);
        else if(SessionUtil.getAttribute(number) == null) // 인증 가능 시간이 끝났거나 유효하지 않은 번호일 경우
            throw new ExpiredSessionException(ErrorCode.EXPIRED_SESSION);

        // 이메일 인증 없이 회원가입, 비밀번호 변경 하는 것을 막기 위한 로직
        // { email : type } 으로 세션 저장
        String email = SessionUtil.getAttribute(number);
        SessionUtil.setAttribute(email, type.name());
        SessionUtil.getSession().removeAttribute(number);
    }

    public static void checkEmailVerificationCompletion(String email, EmailAuthorizationType type){
        if (SessionUtil.getAttribute(email) == null || SessionUtil.getAttribute(email) != type.name())
            throw new IncompleteEmailVerificationException(ErrorCode.INCOMPLETE_EMAIL_VERIFICATION);

        SessionUtil.getSession().removeAttribute(email);
    }
}
