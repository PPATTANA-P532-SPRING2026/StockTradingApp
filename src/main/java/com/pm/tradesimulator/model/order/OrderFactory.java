package com.pm.tradesimulator.model.order;

import java.math.BigDecimal;

public class OrderFactory {

    public static Order create(OrderType type, String ticker,
                               Side side, int quantity, BigDecimal limitPrice) {
        return switch (type) {
            case MARKET -> new MarketOrder(ticker, side, quantity);
            case LIMIT  -> new LimitOrder(ticker, side, quantity, limitPrice);
        };
    }
}