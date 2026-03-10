package com.pm.tradesimulator.controller;

import com.pm.tradesimulator.model.portfolio.Portfolio;
import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.trading.TradingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    private Portfolio portfolio;
    private MarketFeedServices marketFeedService;
    private TradingService tradingService;

    public PortfolioController(Portfolio portfolio,
                               MarketFeedServices marketFeedService,
                               TradingService tradingService) {
        this.portfolio = portfolio;
        this.marketFeedService = marketFeedService;
        this.tradingService = tradingService;
    }

    @GetMapping("/portfolio")
    public Map<String, Object> getPortfolio() {
        Map<String, Object> response = new HashMap<>();

        // cash balance
        response.put("cash", portfolio.getCash());

        // holdings with current value
        Map<String, Object> holdingsMap = new HashMap<>();
        portfolio.getHoldings().forEach((ticker, holding) -> {
            Map<String, Object> holdingInfo = new HashMap<>();
            holdingInfo.put("quantity", holding.getQuantity());
            holdingInfo.put("averageCost", holding.getAverageCost());
            BigDecimal currentPrice = marketFeedService.getPrice(ticker);
            holdingInfo.put("currentPrice", currentPrice);
            holdingInfo.put("currentValue", currentPrice.multiply(
                    BigDecimal.valueOf(holding.getQuantity())));
            holdingsMap.put(ticker, holdingInfo);
        });
        response.put("holdings", holdingsMap);

        // total portfolio value is  cash + all holdings current value
        BigDecimal totalValue = portfolio.getCash();
        for (Map.Entry<String, Object> entry : holdingsMap.entrySet()) {
            Map<String, Object> h = (Map<String, Object>) entry.getValue();
            totalValue = totalValue.add((BigDecimal) h.get("currentValue"));
        }
        response.put("totalValue", totalValue);

        // pending orders
        response.put("pendingOrders", tradingService.getPendingOrders());

        return response;
    }
}
