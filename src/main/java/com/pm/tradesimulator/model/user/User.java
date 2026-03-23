package com.pm.tradesimulator.model.user;

import com.pm.tradesimulator.model.portfolio.Portfolio;
import com.pm.tradesimulator.service.notification.ConsoleNotifier;
import com.pm.tradesimulator.service.notification.NotificationService;

public class User {

    private final String id;
    private final String name;
    private final Portfolio portfolio;
    private NotificationService notificationService;

    public User(String id, String name) {
        this.id                  = id;
        this.name                = name;
        this.portfolio           = new Portfolio();
        this.notificationService = new ConsoleNotifier();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}