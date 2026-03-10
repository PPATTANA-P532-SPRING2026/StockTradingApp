package com.pm.tradesimulator.model.portfolio;
import com.pm.tradesimulator.model.order.Side;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Portfolio {
    private BigDecimal cash = new BigDecimal("10000.00");
    private Map<String, Holding> holdings = new HashMap<>();
    private List<TradeRecord> tradeHistory = new ArrayList<>();

    public BigDecimal getCash() { return cash; }

    public void deductCash(BigDecimal amount) {
        this.cash = this.cash.subtract(amount);
    }

    public void addCash(BigDecimal amount) {
        this.cash = this.cash.add(amount);
    }

    public Map<String, Holding> getHoldings() { return holdings; }

    public boolean hasShares(String ticker, int quantity) {
        Holding h = holdings.get(ticker);
        return h != null && h.getQuantity() >= quantity;
    }

    public void addShares(String ticker, int quantity, BigDecimal price) {
        Holding existing = holdings.get(ticker);

        if (existing == null) {
            // if it is the first time buying this ticker we create a new holding
            holdings.put(ticker, new Holding(quantity, price));
        } else {
            // if we already own this ticker we update quantity and average cost
            int newQty = existing.getQuantity() + quantity;

            BigDecimal oldTotal = existing.getAverageCost()
                    .multiply(BigDecimal.valueOf(existing.getQuantity()));
            BigDecimal newTotal = price.multiply(BigDecimal.valueOf(quantity));

            BigDecimal newAvg = oldTotal.add(newTotal)
                    .divide(BigDecimal.valueOf(newQty), 2, RoundingMode.HALF_UP);

            existing.setQuantity(newQty);
            existing.setAverageCost(newAvg);
        }
    }

    public void removeShares(String ticker, int quantity) {
        Holding existing = holdings.get(ticker);

        if (existing == null) {
            return;
        }

        int newQty = existing.getQuantity() - quantity;

        if (newQty == 0) {
            holdings.remove(ticker); // no shares left so we remove it from the map
        } else {
            existing.setQuantity(newQty);
        }
    }

    public List<TradeRecord> getTradeHistory() {
        return tradeHistory;
    }

    public void recordTrade(String ticker, Side side, int quantity,
                            BigDecimal price, BigDecimal total) {
        TradeRecord record = new TradeRecord(ticker, side, quantity, price, total);
        tradeHistory.add(record);
    }

}
