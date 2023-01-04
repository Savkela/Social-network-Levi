package com.levi9.socialnetwork.Service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;

import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Model.User;

public interface EmailService {

    @Async
    public void sendNotificaitionAsync(User user) throws MailException, InterruptedException;

    void sendEmail(String to, String email, String subject);

    String registerEmail(String name, String link) throws IOException;

	  @Async
	  public void sendNotificationAboutEventAsync(Event event, User user) throws MailException, InterruptedException;

}
