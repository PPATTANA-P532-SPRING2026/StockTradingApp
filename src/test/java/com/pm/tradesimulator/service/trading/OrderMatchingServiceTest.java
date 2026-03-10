package com.pm.tradesimulator.service.trading;

import com.pm.tradesimulator.model.order.OrderStatus;
import com.pm.tradesimulator.model.order.OrderType;
import com.pm.tradesimulator.model.order.Side;
import com.pm.tradesimulator.model.portfolio.Portfolio;
import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.notification.NotificationService;
import com.pm.tradesimulator.service.trading.OrderMatchingService;
import com.pm.tradesimulator.service.trading.TradingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderMatchingServiceTest {

    @Mock
    private MarketFeedServices marketFeedService;

    @Mock
    private NotificationService notificationService;

    private Portfolio portfolio;
    private TradingService tradingService;
    private OrderMatchingService orderMatchingService;

    @BeforeEach
    void setUp() {
        // Arrange
        portfolio = new Portfolio();
        tradingService = new TradingService(portfolio, marketFeedService,
                notificationService);
        orderMatchingService = new OrderMatchingService(marketFeedService,
                tradingService);
    }

    @Test
    void limit_buy_does_not_trigger_when_price_above_limit() {
        // Arrange
        tradingService.placeOrder(OrderType.LIMIT, "AAPL",
                Side.BUY, 10,
                new BigDecimal("170.00"));

        // Act — price 182.50 is above limit 170.00
        Map<String, BigDecimal> prices = new HashMap<>();
        prices.put("AAPL", new BigDecimal("182.50"));
        orderMatchingService.onPriceUpdate(prices);

        // Assert — still pending
        assertEquals(1, tradingService.getPendingOrders().size());
        assertEquals(OrderStatus.PENDING,
                tradingService.getPendingOrders().get(0).getStatus());
    }

    @Test
    void limit_buy_triggers_when_price_at_or_below_limit() {
        // Arrange
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("168.00"));
        tradingService.placeOrder(OrderType.LIMIT, "AAPL",
                Side.BUY, 10,
                new BigDecimal("170.00"));

        // Act — price 168.00 is below limit 170.00
        Map<String, BigDecimal> prices = new HashMap<>();
        prices.put("AAPL", new BigDecimal("168.00"));
        orderMatchingService.onPriceUpdate(prices);

        // Assert — order executed
        assertEquals(0, tradingService.getPendingOrders().size());
    }

    @Test
    void limit_sell_does_not_trigger_when_price_below_limit() {
        // Arrange — buy shares first
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("100.00"));
        tradingService.placeOrder(OrderType.MARKET, "AAPL",
                Side.BUY, 10, null);
        tradingService.placeOrder(OrderType.LIMIT, "AAPL",
                Side.SELL, 10,
                new BigDecimal("200.00"));

        // Act — price 190.00 is below limit 200.00
        Map<String, BigDecimal> prices = new HashMap<>();
        prices.put("AAPL", new BigDecimal("190.00"));
        orderMatchingService.onPriceUpdate(prices);

        // Assert — still pending
        assertEquals(1, tradingService.getPendingOrders().size());
    }

    @Test
    void limit_sell_triggers_when_price_at_or_above_limit() {
        // Arrange — buy shares first
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("100.00"));
        tradingService.placeOrder(OrderType.MARKET, "AAPL",
                Side.BUY, 10, null);

        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("205.00"));
        tradingService.placeOrder(OrderType.LIMIT, "AAPL",
                Side.SELL, 10,
                new BigDecimal("200.00"));

        // Act — price 205.00 is above limit 200.00
        Map<String, BigDecimal> prices = new HashMap<>();
        prices.put("AAPL", new BigDecimal("205.00"));
        orderMatchingService.onPriceUpdate(prices);

        // Assert — order executed
        assertEquals(0, tradingService.getPendingOrders().size());
        assertTrue(portfolio.getCash()
                .compareTo(new BigDecimal("9000.00")) > 0);
    }
}