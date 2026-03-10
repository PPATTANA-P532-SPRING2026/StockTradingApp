package com.pm.tradesimulator.service.market;
import com.pm.tradesimulator.model.market.Stock;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@EnableScheduling
public class MarketFeedServices {

    private PriceUpdateStrategies strategy;

    // random instance injected so tests can use a seeded one
    private Random random;

    // all tracked stocks
    private Map<String, Stock> stocks = new HashMap<>();

    private List<PriceObserver> observers = new ArrayList<>();

    public MarketFeedServices(PriceUpdateStrategies strategy, Random random) {
        this.strategy = strategy;
        this.random = random;
        initializeStocks();
    }

    private void initializeStocks() {
        stocks.put("AAPL", new Stock("AAPL", new BigDecimal("102.50")));
        stocks.put("GOOG", new Stock("GOOG", new BigDecimal("110.00")));
        stocks.put("TSLA", new Stock("TSLA", new BigDecimal("295.00")));
        stocks.put("AMZN", new Stock("AMZN", new BigDecimal("174.00")));
        stocks.put("MSFT", new Stock("MSFT", new BigDecimal("430.00")));
    }

    public void register(PriceObserver observer) {
        observers.add(observer);
    }

    @Scheduled(fixedRate = 5000)
    public void tick() {
        // update every stock price using the strategy
        for (Stock stock : stocks.values()) {
            BigDecimal newPrice = strategy.nextPrice(stock.getCurrentPrice(), random);
            stock.setCurrentPrice(newPrice);
        }


        Map<String, BigDecimal> priceSnapshot = getCurrentPrices();
        notifyObservers(priceSnapshot);
    }


    private void notifyObservers(Map<String, BigDecimal> prices) {
        for (PriceObserver observer : observers) {
            observer.onPriceUpdate(prices);
        }
    }

    public Map<String, BigDecimal> getCurrentPrices() {
        Map<String, BigDecimal> prices = new HashMap<>();
        for (Stock stock : stocks.values()) {
            prices.put(stock.getTicker(), stock.getCurrentPrice());
        }
        return prices;
    }

    public BigDecimal getPrice(String ticker) {
        Stock stock = stocks.get(ticker);
        if (stock == null) {
            throw new IllegalArgumentException("Unknown ticker: " + ticker);
        }
        return stock.getCurrentPrice();
    }

}
