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
    public void sendAuthorizationMail(String toAddress){
        HttpSession session = SessionUtil.getSession();
        SimpleMailMessage message = new SimpleMailMessage();
        String uuid = UUID.randomUUID().toString().substring(0, 10);

        message.setTo(toAddress);
        message.setFrom(FROM_ADDRESS);
        message.setSubject("맛도리 인증메일입니다.");
        session.setAttribute(toAddress,uuid);
        message.setText("\n 다음 인증번호를 입력해주세요.\n" + uuid);
        mailSender.send(message);
    }
}
