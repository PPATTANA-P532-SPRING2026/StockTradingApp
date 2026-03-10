package com.pm.tradesimulator.service.trading;

import com.pm.tradesimulator.model.order.Order;
import com.pm.tradesimulator.model.order.OrderStatus;
import com.pm.tradesimulator.model.order.OrderType;
import com.pm.tradesimulator.model.order.Side;
import com.pm.tradesimulator.model.portfolio.Portfolio;
import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.notification.NotificationService;
import com.pm.tradesimulator.service.trading.TradingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingServiceTest {

    @Mock
    private MarketFeedServices marketFeedService;

    @Mock
    private NotificationService notificationService;

    private Portfolio portfolio;
    private TradingService tradingService;

    @BeforeEach
    void setUp() {
        // Arrange
        portfolio = new Portfolio();
        tradingService = new TradingService(portfolio, marketFeedService,
                notificationService);
    }

    @Test
    void market_buy_reduces_cash_and_increases_holdings() {
        // Arrange
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("100.00"));

        // Act
        tradingService.placeOrder(OrderType.MARKET, "AAPL",
                Side.BUY, 10, null);

        // Assert
        assertEquals(new BigDecimal("9000.00"), portfolio.getCash());
        assertTrue(portfolio.hasShares("AAPL", 10));
    }

    @Test
    void market_sell_increases_cash_and_reduces_holdings() {
        // Arrange
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("100.00"));
        tradingService.placeOrder(OrderType.MARKET, "AAPL",
                Side.BUY, 10, null);

        // Act
        tradingService.placeOrder(OrderType.MARKET, "AAPL",
                Side.SELL, 5, null);

        // Assert
        assertEquals(new BigDecimal("9500.00"), portfolio.getCash());
        assertTrue(portfolio.hasShares("AAPL", 5));
    }

    @Test
    void buy_with_insufficient_funds_throws_exception() {
        // Arrange
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("2000.00"));

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                tradingService.placeOrder(OrderType.MARKET, "AAPL",
                        Side.BUY, 10, null));
    }

    @Test
    void sell_unowned_shares_throws_exception() {
        // Arrange
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("100.00"));

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                tradingService.placeOrder(OrderType.MARKET, "AAPL",
                        Side.SELL, 10, null));
    }

    @Test
    void executed_order_fires_notification() {
        // Arrange
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("100.00"));

        // Act
        tradingService.placeOrder(OrderType.MARKET, "AAPL",
                Side.BUY, 10, null);

        // Assert
        verify(notificationService, times(1)).notify(anyString());
    }

    @Test
    void market_order_status_is_executed_after_placement() {
        // Arrange
        when(marketFeedService.getPrice("AAPL"))
                .thenReturn(new BigDecimal("100.00"));

        // Act
        Order order = tradingService.placeOrder(OrderType.MARKET, "AAPL",
                Side.BUY, 10, null);

        // Assert
        assertEquals(OrderStatus.EXECUTED, order.getStatus());
    }

    @Test
    void limit_order_status_is_pending_after_placement() {
        // Arrange — no mock needed

        // Act
        Order order = tradingService.placeOrder(OrderType.LIMIT, "AAPL",
                Side.BUY, 10,
                new BigDecimal("170.00"));

        // Assert
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, tradingService.getPendingOrders().size());
    }
}