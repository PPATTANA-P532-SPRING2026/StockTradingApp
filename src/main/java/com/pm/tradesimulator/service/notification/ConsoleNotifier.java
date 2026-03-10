package com.pm.tradesimulator.service.notification;

public class ConsoleNotifier implements NotificationService {

    @Override
    public void notify(String message) {
        System.out.println("[NOTIFICATION] " + message);
    }
}
