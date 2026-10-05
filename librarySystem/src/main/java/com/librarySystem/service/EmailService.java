package com.librarySystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendBookApprovalEmail(String email,
                                      String studentName,
                                      String bookName,
                                      LocalDate dueDate) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(email);

        message.setSubject("Book Request Approved");

        message.setText(
                "Dear " + studentName +
                        "\n\nYour request for book '" + bookName +
                        "' has been approved." +
                        "\nDue Date: " + dueDate +
                        "\n\nPlease return the book before the due date. After the completion due date we will be charge amount 100 rs/day  "
        );

        mailSender.send(message);
    }
}