package com.pm.tradesimulator.service.notification;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DashboardDecorator extends NotificationDecorator {

    // thread safe counter
    private final AtomicInteger badgeCount = new AtomicInteger(0);

    public DashboardDecorator(NotificationService wrapped) {
        super(wrapped);
    }

    @Override
    public void notify(String message) {
        // increment badge counter
        int count = badgeCount.incrementAndGet();
        System.out.println("[DASHBOARD] Badge count: " + count);
        // pass down the chain
        wrapped.notify(message);
    }

    public int getBadgeCount() {
        return badgeCount.get();
    }

    public void resetBadgeCount() {
        badgeCount.set(0);
    }
}