package com.pm.tradesimulator.service.notification;

public abstract class NotificationDecorator implements NotificationService {

    protected NotificationService wrapped;

    public NotificationDecorator(NotificationService wrapped) {
        this.wrapped = wrapped;
    }

    // ← add this so NotificationController can rewire the chain
    public void setWrapped(NotificationService wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void notify(String message) {
        wrapped.notify(message);
    }
}