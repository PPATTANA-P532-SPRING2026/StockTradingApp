package com.pm.tradesimulator.controller;

import com.pm.tradesimulator.service.market.MarketFeedServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MarketController {

    private MarketFeedServices marketFeedService;

    public MarketController(MarketFeedServices marketFeedService) {
        this.marketFeedService = marketFeedService;
    }

    @GetMapping("/market")
    public Map<String, BigDecimal> getPrices() {
        return marketFeedService.getCurrentPrices();
    }
}