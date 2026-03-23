package com.pm.tradesimulator.controller;

import com.pm.tradesimulator.model.portfolio.Holding;
import com.pm.tradesimulator.model.portfolio.Portfolio;
import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.trading.TradingService;
import com.pm.tradesimulator.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    private final UserService userService;
    private final MarketFeedServices marketFeedServices;
    private final TradingService tradingService;

    public PortfolioController(UserService userService,
                               MarketFeedServices marketFeedServices,
                               TradingService tradingService) {
        this.userService         = userService;
        this.marketFeedServices  = marketFeedServices;
        this.tradingService      = tradingService;
    }

    @GetMapping("/portfolio")
    public Map<String, Object> getPortfolio() {
        Portfolio portfolio = userService.getActiveUser().getPortfolio();

        // build holdings with current price and value
        Map<String, Object> holdingsMap = new HashMap<>();
        BigDecimal totalValue = portfolio.getCash();

        for (Map.Entry<String, Holding> entry : portfolio.getHoldings().entrySet()) {
            String ticker   = entry.getKey();
            Holding holding = entry.getValue();

            BigDecimal currentPrice  = marketFeedServices.getPrice(ticker);
            BigDecimal currentValue  = currentPrice.multiply(
                    BigDecimal.valueOf(holding.getQuantity()));
            totalValue = totalValue.add(currentValue);

            Map<String, Object> h = new HashMap<>();
            h.put("quantity",     holding.getQuantity());
            h.put("averageCost",  holding.getAverageCost());
            h.put("currentPrice", currentPrice);
            h.put("currentValue", currentValue);
            holdingsMap.put(ticker, h);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("cash",          portfolio.getCash());
        response.put("holdings",      holdingsMap);
        response.put("totalValue",    totalValue);
        response.put("pendingOrders", portfolio.getPendingOrders());
        return response;
    }
}