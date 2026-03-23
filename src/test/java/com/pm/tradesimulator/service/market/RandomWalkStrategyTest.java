package com.pm.tradesimulator.service.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class RandomWalkStrategyTest {

    private RandomWalkStrategy strategy;

    @BeforeEach
    void setUp() {
        // Arrange
        strategy = new RandomWalkStrategy();
    }

    @Test
    void next_price_stays_within_two_percent_band() {
        // Arrange
        BigDecimal initialPrice = new BigDecimal("100.00");
        Random seededRandom = new Random(42);

        // Act + Assert
        BigDecimal price = initialPrice;
        for (int i = 0; i < 1000; i++) {
            BigDecimal newPrice = strategy.nextPrice("AAPL", price, seededRandom); // ← fix
            BigDecimal maxPrice = price.multiply(new BigDecimal("1.02"))
                    .setScale(2, RoundingMode.CEILING);
            BigDecimal minPrice = price.multiply(new BigDecimal("0.98"))
                    .setScale(2, RoundingMode.FLOOR);

            assertTrue(newPrice.compareTo(minPrice) >= 0,
                    "Price dropped below -2%: " + newPrice);
            assertTrue(newPrice.compareTo(maxPrice) <= 0,
                    "Price exceeded +2%: " + newPrice);

            price = newPrice;
        }
    }

    @Test
    void next_price_is_deterministic_with_seeded_random() {
        // Arrange
        BigDecimal initialPrice = new BigDecimal("100.00");
        Random seededRandom1 = new Random(42);
        Random seededRandom2 = new Random(42);

        // Act
        BigDecimal price1 = strategy.nextPrice("AAPL", initialPrice, seededRandom1); // ← fix
        BigDecimal price2 = strategy.nextPrice("AAPL", initialPrice, seededRandom2); // ← fix

        // Assert
        assertEquals(price1, price2);
    }

    @Test
    void next_price_is_never_negative() {
        // Arrange
        BigDecimal initialPrice = new BigDecimal("0.10");
        Random seededRandom = new Random(42);

        // Act
        BigDecimal newPrice = strategy.nextPrice("AAPL", initialPrice, seededRandom); // ← fix

        // Assert
        assertTrue(newPrice.compareTo(BigDecimal.ZERO) > 0);
    }
}