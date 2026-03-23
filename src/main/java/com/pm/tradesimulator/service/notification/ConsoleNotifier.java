package com.pm.tradesimulator.service.notification;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ConsoleNotifier implements NotificationService {

    @Override
    public void notify(String message) {
        System.out.println("[NOTIFICATION] " + message);
    }
}
