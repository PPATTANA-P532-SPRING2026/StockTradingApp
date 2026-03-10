package com.pm.tradesimulator.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

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
}