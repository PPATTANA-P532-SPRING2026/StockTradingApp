package com.pm.tradesimulator.service.market;

import java.math.BigDecimal;
import java.util.Random;

public interface PriceUpdateStrategies {
    BigDecimal nextPrice(BigDecimal currentPrice, Random rng);
}
