package com.pm.tradesimulator.service.market;
import com.pm.tradesimulator.model.market.Stock;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public MarketFeedServices(@Qualifier("randomWalk") PriceUpdateStrategies strategy, Random random) {
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

    public void setStrategy(PriceUpdateStrategies strategy) {
        this.strategy = strategy;
    }

    public String getStrategyName() {
        return strategy.getClass().getSimpleName();
    }

    public void register(PriceObserver observer) {
        observers.add(observer);
    }

    @Scheduled(fixedRate = 5000)
    public void tick() {
        for (Map.Entry<String, Stock> entry : stocks.entrySet()) {
            String ticker = entry.getKey();
            Stock stock   = entry.getValue();
            BigDecimal newPrice = strategy.nextPrice(
                    ticker, stock.getCurrentPrice(), random);
            stock.setCurrentPrice(newPrice);
        }
        notifyObservers();
    }

    private void notifyObservers() {
        Map<String, BigDecimal> prices = getCurrentPrices();  // builds it internally
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
