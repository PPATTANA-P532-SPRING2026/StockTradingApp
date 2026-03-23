package com.pm.tradesimulator.service.trading;

import com.pm.tradesimulator.model.order.*;
import com.pm.tradesimulator.model.portfolio.Portfolio;
import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.notification.NotificationService;
import com.pm.tradesimulator.service.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradingService {

    private final UserService userService;
    private final MarketFeedServices marketFeedServices;

    public TradingService(UserService userService,
                          MarketFeedServices marketFeedServices) {
        this.userService       = userService;
        this.marketFeedServices = marketFeedServices;
    }

    // ── get active user's portfolio ───────────────────────────────────
    private Portfolio getPortfolio() {
        return userService.getActiveUser().getPortfolio();
    }

    // ── get active user's notification service ────────────────────────
    private NotificationService getNotificationService() {
        return userService.getActiveUser().getNotificationService();
    }

    // ── get active user's pending orders ─────────────────────────────
    public List<Order> getPendingOrders() {
        return userService.getActiveUser().getPortfolio().getPendingOrders();
    }

    public Order placeOrder(OrderType type, String ticker,
                            Side side, int quantity,
                            BigDecimal limitPrice) {
        Order order = OrderFactory.create(type, ticker, side, quantity, limitPrice);

        if (type == OrderType.MARKET) {
            execute(order);
        } else {
            validateOrder(order);
            getPendingOrders().add(order);
        }
        return order;
    }

    public void execute(Order order) {
        String ticker   = order.getTicker();
        int quantity    = order.getQuantity();
        Portfolio portfolio = getPortfolio();
        BigDecimal currentPrice;
        BigDecimal total;

        if (order.getSide() == Side.BUY) {
            currentPrice = marketFeedServices.getPrice(ticker);
            total        = currentPrice.multiply(BigDecimal.valueOf(quantity));

            if (total.compareTo(portfolio.getCash()) > 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            portfolio.deductCash(total);
            portfolio.addShares(ticker, quantity, currentPrice);

        } else {
            if (!portfolio.hasShares(ticker, quantity)) {
                throw new IllegalArgumentException("Insufficient shares");
            }
            currentPrice = marketFeedServices.getPrice(ticker);
            total        = currentPrice.multiply(BigDecimal.valueOf(quantity));
            portfolio.addCash(total);
            portfolio.removeShares(ticker, quantity);
        }

        portfolio.recordTrade(ticker, order.getSide(),
                quantity, currentPrice, total);
        order.setStatus(OrderStatus.EXECUTED);
        getPendingOrders().remove(order);

        String message = String.format(
                "Order executed: %s %d %s at $%s (total: $%s)",
                order.getSide(), quantity, ticker, currentPrice, total);
        getNotificationService().notify(message);
    }

    private void validateOrder(Order order) {
        if (order.getSide() == Side.SELL) {
            if (!getPortfolio().hasShares(order.getTicker(), order.getQuantity())) {
                throw new IllegalArgumentException("Insufficient shares");
            }
        }
    }

    public boolean cancelOrder(String orderId) {
        return getPendingOrders().removeIf(o -> o.getId().equals(orderId));
    }

    public void setNotificationService(NotificationService notificationService) {
        userService.getActiveUser().setNotificationService(notificationService);
    }
}