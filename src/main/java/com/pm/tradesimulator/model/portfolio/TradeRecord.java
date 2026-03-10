package com.pm.tradesimulator.model.portfolio;

import com.pm.tradesimulator.model.order.Side;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeRecord {
    private String ticker;
    private Side side;
    private int quantity;
    private BigDecimal price;
    private BigDecimal total;
    private LocalDateTime timestamp;

    public TradeRecord(String ticker, Side side, int quantity,
                       BigDecimal price, BigDecimal total) {
        this.ticker = ticker;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.timestamp = LocalDateTime.now();
    }

    public String getTicker() { return ticker; }

    public Side getSide() { return side; }

    public int getQuantity() { return quantity; }

    public BigDecimal getPrice() { return price; }

    public BigDecimal getTotal() { return total; }

    public LocalDateTime getTimestamp() { return timestamp; }
}
