package com.pm.tradesimulator.service.market;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component("meanReversion")
public class MeanReversionStrategy implements PriceUpdateStrategies {

    private static final int WINDOW_SIZE = 10;
    private static final double REVERSION_STRENGTH = 0.05;

    private final Map<String, List<BigDecimal>> priceHistory = new HashMap<>();

    @Override
    public BigDecimal nextPrice(String ticker, BigDecimal current, Random rng) {
        // update price history for this ticker
        priceHistory.computeIfAbsent(ticker, k -> new ArrayList<>()).add(current);
        List<BigDecimal> history = priceHistory.get(ticker);

        // keep only last WINDOW_SIZE prices
        if (history.size() > WINDOW_SIZE) {
            history.remove(0);
        }

        // calculate moving average
        BigDecimal sum = history.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal movingAverage = sum.divide(
                BigDecimal.valueOf(history.size()), 10, RoundingMode.HALF_UP);

        // calculate reversion pull toward mean
        double currentDouble = current.doubleValue();
        double meanDouble    = movingAverage.doubleValue();
        double pull          = (meanDouble - currentDouble) * REVERSION_STRENGTH;

        // add small random noise
        double noise = (rng.nextDouble() * 2 - 1) * 0.005;
        double delta = 1.0 + ((pull + noise) / currentDouble);

        return current.multiply(BigDecimal.valueOf(delta))
                .setScale(2, RoundingMode.HALF_UP);
    }
}