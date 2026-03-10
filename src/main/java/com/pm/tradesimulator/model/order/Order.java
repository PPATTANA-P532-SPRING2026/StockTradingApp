package com.pm.tradesimulator.model.order;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;




public abstract class Order {
    private final String id;
    private final String ticker;
    private final Side side;
    private final int quantity;
    private OrderStatus status;
    private final LocalDateTime timestamp;

    protected Order(String ticker, Side side, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.ticker = ticker;
        this.side = side;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
        this.timestamp = LocalDateTime.now();
    }
    public String getId() { return id; }

    public String getTicker() { return ticker; }

    public Side getSide() { return side; }

    public int getQuantity() { return quantity; }

    public OrderStatus getStatus() { return status; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setStatus(OrderStatus status) { this.status = status; }

    public abstract boolean isReadyToExecute(BigDecimal currentPrice);
}
