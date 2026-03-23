package com.pm.tradesimulator.service.notification;

import org.springframework.stereotype.Component;

@Component
public class SmsDecorator extends NotificationDecorator {

    public SmsDecorator(NotificationService wrapped) {
        super(wrapped);
    }

    @Override
    public void notify(String message) {
        // mock SMS — log short text message
        System.out.println("[SMS] +1-555-0100: " + message);
        // pass down the chain
        wrapped.notify(message);
    }
}