package com.pm.tradesimulator.service.trading;
import com.pm.tradesimulator.model.order.*;
import com.pm.tradesimulator.model.portfolio.Portfolio;
import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;




@Service
public class TradingService {
    private Portfolio portfolio;
    private MarketFeedServices marketFeedService;
    private NotificationService notificationService;
    private List<Order> pendingOrders = new ArrayList<>();

    public TradingService(Portfolio portfolio,
                          MarketFeedServices marketFeedService,
                          NotificationService notificationService) {
        this.portfolio = portfolio;
        this.marketFeedService = marketFeedService;
        this.notificationService = notificationService;
    }

    public Order placeOrder(OrderType type, String ticker,
                            Side side, int quantity, BigDecimal limitPrice) {

        // create the correct order type via factory
        Order order = OrderFactory.create(type, ticker, side, quantity, limitPrice);

        if (type == OrderType.MARKET) {
            // if it is a market order we execute immediately
            execute(order);
        } else {
            // if it is limit order we have to validate first then add to pending list
            validateOrder(order);
            pendingOrders.add(order);
        }

        return order;
    }

    public void execute(Order order) {
        String ticker = order.getTicker();
        int quantity = order.getQuantity();
        BigDecimal currentPrice = marketFeedService.getPrice(ticker);
        BigDecimal total = currentPrice.multiply(BigDecimal.valueOf(quantity));

        if (order.getSide() == Side.BUY) {
            // validate funds
            if (total.compareTo(portfolio.getCash()) > 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            portfolio.deductCash(total);
            portfolio.addShares(ticker, quantity, currentPrice);

        } else {
            // validate shares
            if (!portfolio.hasShares(ticker, quantity)) {
                throw new IllegalArgumentException("Insufficient shares");
            }
            portfolio.addCash(total);
            portfolio.removeShares(ticker, quantity);
        }

        // record the trade and notify
        portfolio.recordTrade(ticker, order.getSide(), quantity, currentPrice, total);
        order.setStatus(OrderStatus.EXECUTED);
        pendingOrders.remove(order);

        // fire notification
        String message = String.format("Order executed: %s %d %s at $%s (total: $%s)",
                order.getSide(), quantity, ticker, currentPrice, total);
        notificationService.notify(message);
    }

    //  validate the order before adding it to pending

    private void validateOrder(Order order) {
        if (order.getSide() == Side.SELL) {
            if (!portfolio.hasShares(order.getTicker(), order.getQuantity())) {
                throw new IllegalArgumentException("Insufficient shares for limit sell");
            }
        }
    }

    // this is to cancel a pending order

    public boolean cancelOrder(String orderId) {
        for (Order order : pendingOrders) {
            if (order.getId().equals(orderId)) {
                order.setStatus(OrderStatus.CANCELLED);
                pendingOrders.remove(order);
                return true;
            }
        }
        return false;
    }

    //getters

    public List<Order> getPendingOrders() {
        return pendingOrders;
    }
}
