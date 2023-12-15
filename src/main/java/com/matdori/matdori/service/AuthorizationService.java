package com.matdori.matdori.service;

import com.matdori.matdori.domain.EmailAuthorizationType;
import com.matdori.matdori.domain.User;
import com.matdori.matdori.exception.*;
import com.matdori.matdori.repositoy.UserRepository;
import com.matdori.matdori.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    /**
     * 로그인
     */
    public User login(String email, String password) {
        // 존재하는 회원인지 확인.
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);

        // 로그인 가능 여부 체크
        if (!user.isPresent())
            // 유저가 존재하지 않는 경우.
            throw new InvalidCredentialsException(ErrorCode.INVALID_CREDENTIALS);

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

    /**
     * 로그아웃
     */
    public static void logout(){
        String sessionCookie = SessionUtil.getSessionCookie(); // session의 key값을 가져온다.
        if(sessionCookie == null)  // 프론트에서 쿠키를 보내지 않은 경우
            throw new ExpiredSessionException(ErrorCode.INVALID_REQUIRED_COOKIE);
        else if(SessionUtil.getAttribute(sessionCookie) == null) // 세션이 만료된 경우
            throw new ExpiredSessionException(ErrorCode.EXPIRED_SESSION);

         SessionUtil.deleteAttribute(sessionCookie); // 세션을 날림. 다음에 프론트가 이전의 쿠키를 보내면 인증이 안됨.
    }

    /**
     * 이메일 인증 - 인증 번호 체크
     */
    public static void checkAuthorizationNumber(String number, EmailAuthorizationType type){
        if(number == null) // 인증번호를 보내지 않은 경우
            throw new InvalidRequiredParamException(ErrorCode.INVALID_REQUIRED_PARAM);

        else if(SessionUtil.getAttribute(number) == null) // 인증번호에 해당하는 세션 값이 있는지 체크하기.
            // 인증 가능 시간이 끝났거나 유효하지 않은 번호일 경우
            throw new ExpiredSessionException(ErrorCode.EXPIRED_SESSION);

        // 이메일 인증 없이 회원가입, 비밀번호 변경 하는 것을 막기 위한 로직
        // { email : type } 으로 세션 저장
        String email = SessionUtil.getAttribute(number);
        SessionUtil.setAttribute(email, type.name()); // type은 이메일 인증을 왜 하는지에 대한 케이스.
        SessionUtil.deleteAttribute(number); // 인증이 되었으면, 더 이상 필요없는 인증 번호이기 때문에 날리기.
    }

    public static void checkEmailVerificationCompletion(String email, EmailAuthorizationType type){
        if (SessionUtil.getAttribute(email) == null || SessionUtil.getAttribute(email) != type.name())
            throw new IncompleteEmailVerificationException(ErrorCode.INCOMPLETE_EMAIL_VERIFICATION);

        SessionUtil.deleteAttribute(email);

    }
}
