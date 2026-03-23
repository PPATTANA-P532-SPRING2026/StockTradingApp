package com.pm.tradesimulator.service.trading;

import com.pm.tradesimulator.model.order.Order;
import com.pm.tradesimulator.model.order.OrderStatus;
import com.pm.tradesimulator.model.user.User;
import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.market.PriceObserver;
import com.pm.tradesimulator.service.user.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderMatchingService implements PriceObserver {

    private final MarketFeedServices marketFeedServices;
    private final TradingService tradingService;
    private final UserService userService;

    public OrderMatchingService(MarketFeedServices marketFeedServices,
                                TradingService tradingService,
                                UserService userService) {
        this.marketFeedServices = marketFeedServices;
        this.tradingService     = tradingService;
        this.userService        = userService;
    }

    @PostConstruct
    public void init() {
        marketFeedServices.register(this);
    }

    @Override
    public void onPriceUpdate(Map<String, BigDecimal> prices) {
        // check pending orders for ALL users
        for (User user : userService.getAllUsers()) {
            List<Order> pendingOrders = user.getPortfolio().getPendingOrders();
            List<Order> toExecute    = new ArrayList<>();

            for (Order order : pendingOrders) {
                BigDecimal currentPrice = prices.get(order.getTicker());
                if (currentPrice == null) continue;

                if (order.getStatus() == OrderStatus.PENDING &&
                        order.isReadyToExecute(currentPrice)) {
                    toExecute.add(order);
                }
            }

            // temporarily switch active user to execute their orders
            String previousUserId = userService.getActiveUserId();
            userService.setActiveUser(user.getId());

            for (Order order : toExecute) {
                try {
                    tradingService.execute(order);
                } catch (IllegalArgumentException e) {
                    order.setStatus(OrderStatus.CANCELLED);
                    pendingOrders.remove(order);
                }
            }

            // restore previous active user
            userService.setActiveUser(previousUserId);
        }
    }
}