package com.matdori.matdori.service;

import com.matdori.matdori.domain.Mail;
import com.matdori.matdori.util.SessionUtil;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MailService{
    private JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "inha_matdori@naver.com";

    /**
     * 인증 메일 보내기
     */
    public void sendAuthorizationMail(String toAddress){
        // 유저가 입력한 코드가 맞는 코드인지 검증하기 위해 임시저장할 세션.
        HttpSession session = SessionUtil.getSession();

        // 메일을 보내기 위한 객체 생성
        SimpleMailMessage message = new SimpleMailMessage();

        // 이메일 인증 코드 생성하기.
        String uuid = UUID.randomUUID().toString().substring(0, 10);

        // 받는 주소, 보내는 주소 설정하기.
        message.setTo(toAddress);
        message.setFrom(FROM_ADDRESS);
        message.setSubject("맛도리 인증메일입니다.");

        // 사용자가 인증 코드를 입력했을 때, 맞는지 검증하기 위해 임시 저장.
        session.setAttribute(uuid,toAddress);

        // 메일 보내기.
        message.setText("\n 다음 인증번호를 입력해주세요.\n" + uuid);
        mailSender.send(message);
    }
}
