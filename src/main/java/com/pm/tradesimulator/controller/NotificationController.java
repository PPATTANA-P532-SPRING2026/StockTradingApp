package com.pm.tradesimulator.controller;

import com.pm.tradesimulator.service.notification.ConsoleNotifier;
import com.pm.tradesimulator.service.notification.DashboardDecorator;
import com.pm.tradesimulator.service.notification.EmailDecorator;
import com.pm.tradesimulator.service.notification.NotificationService;
import com.pm.tradesimulator.service.notification.SmsDecorator;
import com.pm.tradesimulator.service.trading.TradingService;
import com.pm.tradesimulator.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final TradingService tradingService;
    private final ConsoleNotifier consoleNotifier;
    private final EmailDecorator emailDecorator;
    private final SmsDecorator smsDecorator;
    private final DashboardDecorator dashboardDecorator;

    private final UserService userService;

    public NotificationController(TradingService tradingService,
                                  ConsoleNotifier consoleNotifier,
                                  EmailDecorator emailDecorator,
                                  SmsDecorator smsDecorator,
                                  DashboardDecorator dashboardDecorator,
                                  UserService userService) {
        this.tradingService     = tradingService;
        this.consoleNotifier    = consoleNotifier;
        this.emailDecorator     = emailDecorator;
        this.smsDecorator       = smsDecorator;
        this.dashboardDecorator = dashboardDecorator;
        this.userService        = userService;
    }

    //  GET /api/notifications/badge — returns badge count
    @GetMapping("/notifications/badge")
    public ResponseEntity<?> getBadgeCount() {
        return ResponseEntity.ok(Map.of(
                "badgeCount", dashboardDecorator.getBadgeCount()
        ));
    }

    // POST /api/notifications/reset — resets badge count
    @PostMapping("/notifications/reset")
    public ResponseEntity<?> resetBadgeCount() {
        dashboardDecorator.resetBadgeCount();
        return ResponseEntity.ok(Map.of("badgeCount", 0));
    }

    //  POST /api/notifications/channels — configure channels
    @PostMapping("/notifications/channels")
    public ResponseEntity<?> setChannels(@RequestBody Map<String, List<String>> body) {
        List<String> channels = body.get("channels");

        NotificationService chain = consoleNotifier;

        if (channels.contains("dashboard")) {
            dashboardDecorator.setWrapped(chain);
            chain = dashboardDecorator;
        }
        if (channels.contains("sms")) {
            smsDecorator.setWrapped(chain);
            chain = smsDecorator;
        }
        if (channels.contains("email")) {
            emailDecorator.setWrapped(chain);
            chain = emailDecorator;
        }

        // set on active user instead of TradingService directly
        userService.getActiveUser().setNotificationService(chain);

        return ResponseEntity.ok(Map.of(
                "channels", channels,
                "message",  "Notification channels updated"
        ));
    }
}