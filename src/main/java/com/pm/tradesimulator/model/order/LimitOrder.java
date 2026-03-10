package com.pm.tradesimulator.model.order;
import java.math.BigDecimal;
public class LimitOrder extends Order {
    private BigDecimal limitPrice;

    public LimitOrder(String ticker, Side side, int quantity, BigDecimal limitPrice) {
        super(ticker, side, quantity);
        this.limitPrice = limitPrice;
    }

    @Override
    public boolean isReadyToExecute(BigDecimal currentPrice) {
        return switch (getSide()) {
            case BUY -> currentPrice.compareTo(limitPrice) <= 0;
            case SELL -> currentPrice.compareTo(limitPrice) >= 0;
        };

    }
    public BigDecimal getLimitPrice() { return limitPrice; }
}
