package com.pm.tradesimulator.model.market;
import java.math.BigDecimal;

public class Stock {
    private String ticker;
    private BigDecimal currentPrice;

    public Stock(String ticker, BigDecimal currentPrice) {
        this.ticker = ticker;
        this.currentPrice = currentPrice;
    }

    public String getTicker() { return ticker; }

    public BigDecimal getCurrentPrice() { return currentPrice; }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}
