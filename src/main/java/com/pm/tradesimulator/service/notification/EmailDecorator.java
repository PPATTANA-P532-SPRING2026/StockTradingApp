package com.pm.tradesimulator.service.notification;

import org.springframework.stereotype.Component;

@Component
public class EmailDecorator extends NotificationDecorator {

    public EmailDecorator(NotificationService wrapped) {
        super(wrapped);
    }

    @Override
    public void notify(String message) {
        // mock email — log formatted email message
        System.out.println("[EMAIL] To: analyst@tradesimulator.com");
        System.out.println("[EMAIL] Subject: Trade Notification");
        System.out.println("[EMAIL] Body: " + message);
        // pass down the chain
        wrapped.notify(message);
    }
}