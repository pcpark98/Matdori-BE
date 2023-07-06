package com.matdori.matdori.service;

import com.matdori.matdori.domain.User;
import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.ExpiredSessionException;
import com.matdori.matdori.exception.InsufficientPrivilegesException;
import com.matdori.matdori.exception.NotExistUserException;
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

    public static void logout(HttpSession session){
        session.invalidate();
    }
}
