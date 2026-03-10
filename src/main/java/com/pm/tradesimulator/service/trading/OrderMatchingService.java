package com.pm.tradesimulator.service.trading;

import com.pm.tradesimulator.model.order.Order;
import com.pm.tradesimulator.model.order.OrderStatus;
import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.market.PriceObserver;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class OrderMatchingService implements PriceObserver {

    private MarketFeedServices marketFeedService;
    private TradingService tradingService;

    public OrderMatchingService(MarketFeedServices marketFeedService,
                                TradingService tradingService) {
        this.marketFeedService = marketFeedService;
        this.tradingService = tradingService;
    }

    // we register with MarketFeedService at startup we use post construct so it only constructs it after the objects are fully initialized

    @PostConstruct
    public void init() {
        marketFeedService.register(this);
    }

    // to react to every price update

    @Override
    public void onPriceUpdate(Map<String, BigDecimal> prices) {
        List<Order> pendingOrders = tradingService.getPendingOrders();

        // collect orders to execute we don't modify list during iteration(it failed the test due to concurrent modification)
        List<Order> toExecute = new ArrayList<>();

        for (Order order : pendingOrders) {
            BigDecimal currentPrice = prices.get(order.getTicker());

            if (currentPrice == null) {
                continue;
            }

            if (order.getStatus() == OrderStatus.PENDING &&
                    order.isReadyToExecute(currentPrice)) {
                toExecute.add(order);
            }
        }

        // now we execute when iteration is finished now it is safe to modify the list
        for (Order order : toExecute) {
            try {
                tradingService.execute(order);
            } catch (IllegalArgumentException e) {
                order.setStatus(OrderStatus.CANCELLED);
                pendingOrders.remove(order);
            }
        }
    }
}
