package com.matdori.matdori.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class SessionUtil {
    @Autowired
    private final RedisTemplate<String,String> redis;
    private static RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void SessionUtil(){
        redisTemplate = this.redis;
    }
    public static void deleteAttribute(String key){
        redisTemplate.delete(key);
    }
    public static String getAttribute(String sessionId){
        String value = redisTemplate.opsForValue().get(sessionId);
        if(value != null) {   // 인증시간을 갱신시켜줌
            Duration expiration = Duration.ofMinutes(60 * 24 * 14);
            redisTemplate.opsForValue().set(sessionId, value, expiration);
        }
        return value;
    }

    public static void setAttribute(String key, String value){
        Duration expiration = Duration.ofMinutes(60 * 24 * 14);
        redisTemplate.opsForValue().set(key,value, expiration);
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
