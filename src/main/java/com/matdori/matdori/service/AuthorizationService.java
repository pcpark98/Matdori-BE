package com.matdori.matdori.service;

import com.matdori.matdori.domain.User;
import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.ExpiredSessionException;
import com.matdori.matdori.exception.InsufficientPrivilegesException;
import com.matdori.matdori.exception.NotExistUserException;
import com.matdori.matdori.repositoy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.SessionAttribute;

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

    public static void checkSession(HttpSession session, String sessionId, Long pathParmUserId) {
        Long userId = (Long) session.getAttribute(sessionId);
        if (userId == null)
            throw new ExpiredSessionException(ErrorCode.EXPIRED_SESSION);
        else if (userId != pathParmUserId)
            throw new InsufficientPrivilegesException(ErrorCode.INSUFFICIENT_PRIVILEGES);
    }

    public static void logout(HttpSession session){
        session.invalidate();
    }
}
