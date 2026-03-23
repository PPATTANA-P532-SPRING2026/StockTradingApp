package com.pm.tradesimulator.service.market;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component("randomWalk")
public class RandomWalkStrategy implements PriceUpdateStrategies{
    private double MAX_DELTA = 0.02;

    @Override
    public BigDecimal nextPrice(String ticker, BigDecimal currentPrice, Random rng) {
        double delta = 1.0 + (rng.nextDouble() * 2 - 1) * MAX_DELTA;
        return currentPrice
                .multiply(BigDecimal.valueOf(delta))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
