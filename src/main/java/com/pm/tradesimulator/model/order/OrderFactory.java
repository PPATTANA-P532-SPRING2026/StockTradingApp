package com.pm.tradesimulator.model.order;

import java.math.BigDecimal;

public class OrderFactory {

    public static Order create(OrderType type, String ticker,
                               Side side, int quantity,
                               BigDecimal limitPrice) {

        if (type == null) {
            throw new IllegalArgumentException("OrderType cannot be null");
        }

        switch (type) {
            case MARKET:
                return new MarketOrder(ticker, side, quantity);
            case LIMIT:
                return new LimitOrder(ticker, side, quantity, limitPrice);
            default:
                throw new IllegalArgumentException("Unknown order type: " + type);
        }
    }
}