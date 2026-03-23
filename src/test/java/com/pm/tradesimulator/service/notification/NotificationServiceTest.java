package com.pm.tradesimulator.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    // mock that captures messages
    private static class MockNotificationService implements NotificationService {
        List<String> messages = new ArrayList<>();

        @Override
        public void notify(String message) {
            messages.add(message);
        }
    }

    private MockNotificationService mockNotifier;

    @BeforeEach
    void setUp() {
        // Arrange
        mockNotifier = new MockNotificationService();
    }

    //  existing tests

    @Test
    void notify_captures_message() {
        // Arrange
        String message = "Order executed: BUY 10 AAPL at $182.50";

        // Act
        mockNotifier.notify(message);

        // Assert
        assertEquals(1, mockNotifier.messages.size());
        assertEquals(message, mockNotifier.messages.get(0));
    }

    @Test
    void notify_captures_multiple_messages() {
        // Arrange
        String message1 = "Order executed: BUY 10 AAPL at $182.50";
        String message2 = "Order executed: SELL 5 TSLA at $245.00";

        // Act
        mockNotifier.notify(message1);
        mockNotifier.notify(message2);

        // Assert
        assertEquals(2, mockNotifier.messages.size());
        assertEquals(message1, mockNotifier.messages.get(0));
        assertEquals(message2, mockNotifier.messages.get(1));
    }

    @Test
    void console_notifier_does_not_throw() {
        // Arrange
        ConsoleNotifier consoleNotifier = new ConsoleNotifier();

        // Act + Assert
        assertDoesNotThrow(() -> consoleNotifier.notify("test message"));
    }

    //  new Week 2 decorator chain tests

    @Test
    void email_decorator_fires_then_delegates_to_wrapped() {
        // Arrange
        MockNotificationService mock = new MockNotificationService();
        EmailDecorator emailDecorator = new EmailDecorator(mock);
        String message = "Order executed: BUY 10 AAPL at $182.50";

        // Act
        emailDecorator.notify(message);

        // Assert — wrapped (mock) received the message
        assertEquals(1, mock.messages.size());
        assertEquals(message, mock.messages.get(0));
    }

    @Test
    void sms_decorator_fires_then_delegates_to_wrapped() {
        // Arrange
        MockNotificationService mock = new MockNotificationService();
        SmsDecorator smsDecorator = new SmsDecorator(mock);
        String message = "Order executed: SELL 5 TSLA at $245.00";

        // Act
        smsDecorator.notify(message);

        // Assert — wrapped (mock) received the message
        assertEquals(1, mock.messages.size());
        assertEquals(message, mock.messages.get(0));
    }

    @Test
    void dashboard_decorator_increments_badge_and_delegates() {
        // Arrange
        MockNotificationService mock = new MockNotificationService();
        DashboardDecorator dashboardDecorator = new DashboardDecorator(mock);
        String message = "Order executed: BUY 5 GOOG at $140.00";

        // Act
        dashboardDecorator.notify(message);

        // Assert — badge incremented and wrapped received message
        assertEquals(1, dashboardDecorator.getBadgeCount());
        assertEquals(1, mock.messages.size());
        assertEquals(message, mock.messages.get(0));
    }

    @Test
    void full_chain_email_sms_console_all_fire() {
        // Arrange — build chain: Email → SMS → Console(mock)
        MockNotificationService mock = new MockNotificationService();
        SmsDecorator smsDecorator     = new SmsDecorator(mock);
        EmailDecorator emailDecorator = new EmailDecorator(smsDecorator);
        String message = "Order executed: BUY 10 AAPL at $182.50";

        // Act — one notify() call
        emailDecorator.notify(message);

        // Assert — message reached the bottom of the chain (mock)
        assertEquals(1, mock.messages.size());
        assertEquals(message, mock.messages.get(0));
    }

    @Test
    void full_chain_all_four_channels_fire() {
        // Arrange — build chain: Email → SMS → Dashboard → Console(mock)
        MockNotificationService mock       = new MockNotificationService();
        DashboardDecorator dashboard       = new DashboardDecorator(mock);
        SmsDecorator sms                   = new SmsDecorator(dashboard);
        EmailDecorator email               = new EmailDecorator(sms);
        String message = "Order executed: BUY 10 AAPL at $182.50";

        // Act — one notify() call
        email.notify(message);

        // Assert — message reached bottom of chain
        assertEquals(1, mock.messages.size());
        assertEquals(message, mock.messages.get(0));
        // Assert — dashboard badge incremented
        assertEquals(1, dashboard.getBadgeCount());
    }
}