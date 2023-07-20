package com.matdori.matdori.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@UtilityClass
public class SessionUtil {
    public static HttpSession getSession(){
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        return req.getSession();
    }
    public static String getAttribute(String sessionId){
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        return (String) req.getSession().getAttribute(sessionId);
    }

    public static void setAttribute(String key, String value){
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        req.getSession().setAttribute(key, value);
    }

    public static String getSessionCookie(){
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        Cookie[] cookies = req.getCookies();

        if(cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("sessionId"))
                    return c.getValue();
            }
        }

        return null;
    }
}
