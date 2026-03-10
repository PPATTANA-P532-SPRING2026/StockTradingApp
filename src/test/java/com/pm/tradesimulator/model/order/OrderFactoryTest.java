package com.pm.tradesimulator.model.order;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OrderFactoryTest {

    @Test
    void create_market_order_returns_market_order() {
        // Arrange
        String ticker = "AAPL";
        Side side = Side.BUY;
        int quantity = 10;

        // Act
        Order order = OrderFactory.create(OrderType.MARKET, ticker, side, quantity, null);

        // Assert
        assertInstanceOf(MarketOrder.class, order);
        assertEquals(ticker, order.getTicker());
        assertEquals(side, order.getSide());
        assertEquals(quantity, order.getQuantity());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void create_limit_order_returns_limit_order() {
        // Arrange
        BigDecimal limitPrice = new BigDecimal("170.00");

        // Act
        Order order = OrderFactory.create(OrderType.LIMIT, "AAPL",
                Side.BUY, 10, limitPrice);

        // Assert
        assertInstanceOf(LimitOrder.class, order);
        assertEquals(limitPrice, ((LimitOrder) order).getLimitPrice());
    }

    @Test
    void create_unknown_type_throws_exception() {
        // Arrange — nothing to arrange

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                OrderFactory.create(null, "AAPL", Side.BUY, 10, null));
    }
}