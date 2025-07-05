package com.ayush.libraryManagementSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String message)
    {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("ujjawalkarayush@gmail.com");
        mail.setTo(toEmail);
        mail.setSubject(subject);
        mail.setText(message);

        mailSender.send(mail);
    }
}
