package com.pm.tradesimulator.model.order;
import java.math.BigDecimal;


public class MarketOrder extends Order {
    public MarketOrder(String ticker, Side side, int quantity) {
        super(ticker, side, quantity);
    }

    @Override
    public boolean isReadyToExecute(BigDecimal currentPrice) {
        return true;
    }
}
