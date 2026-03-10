package com.pm.tradesimulator.service.market;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class RandomWalkStrategy implements PriceUpdateStrategies{
    private double MAX_DELTA = 0.02;

    @Override
    public BigDecimal nextPrice(BigDecimal currentPrice, Random rng) {
        double delta = 1.0 + (rng.nextDouble() * 2 - 1) * MAX_DELTA;
        return currentPrice
                .multiply(BigDecimal.valueOf(delta))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
