package com.levi9.socialnetwork.Service.impl;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private Environment env;

    @Async
    public void sendNotificaitionAsync(User user) throws MailException, InterruptedException {

        Thread.sleep(1000);
        System.out.println("Sending mail...");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setFrom(from);
        mail.setSubject("New post on your group");
        mail.setText("Hi " + user.getName() + ",\n\nyou have new post on your group.");
        javaMailSender.send(mail);

        System.out.println("Email sended!");
    }

    @Override
    public void sendEmail(String to, String email, String subject) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(email, true);
            helper.setFrom(from);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to send email");
        }
    }

    @Override
    public String registerEmail(String name, String link) throws IOException {
        File htmlTemplateFile = new File("src/main/resources/static/EmailTemplate.html");
        String htmlString = FileUtils.readFileToString(htmlTemplateFile);
        htmlString = htmlString.replace("$name", name);
        htmlString = htmlString.replace("$link", link);
        return htmlString;
    }

    @Async
    public void sendNotificationAboutEventAsync(Event event, User user) throws MailException, InterruptedException {
        System.out.println("Sending mail...");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setFrom(from);
        mail.setSubject("Your event start for less than one hour");
        mail.setText("Hi " + user.getName() + ",\n\n");
        mail.setText("Event in which you confirmed presence starts in less than one hour");
        javaMailSender.send(mail);

        System.out.println("Email sended!");
        
    }
	
}
