package com.pm.tradesimulator.service.market;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component("trendFollowing")
public class TrendFollowingStrategy implements PriceUpdateStrategies {

    private static final int WINDOW_SIZE = 5;
    private static final double MOMENTUM_STRENGTH = 0.6;

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

        double momentum = 0.0;

        if (history.size() >= 2) {
            // calculate average momentum over recent prices
            double first = history.get(0).doubleValue();
            double last  = history.get(history.size() - 1).doubleValue();
            momentum     = ((last - first) / first) * MOMENTUM_STRENGTH;
        }

        // add small random noise
        double noise = (rng.nextDouble() * 2 - 1) * 0.01;
        double delta = 1.0 + momentum + noise;

        // cap at ±5% per tick to avoid runaway prices
        delta = Math.max(0.95, Math.min(1.05, delta));

        return current.multiply(BigDecimal.valueOf(delta))
                .setScale(2, RoundingMode.HALF_UP);
    }
}