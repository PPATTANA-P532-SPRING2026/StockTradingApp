package com.pm.tradesimulator.model.portfolio;

import java.math.BigDecimal;


public class Holding {
    private int quantity;
    private BigDecimal averageCost;

    public Holding(int quantity, BigDecimal averageCost) {
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    public int getQuantity() { return quantity; }

    public BigDecimal getAverageCost() { return averageCost; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }
}
