package com.pm.tradesimulator.controller;

import com.pm.tradesimulator.service.market.MarketFeedServices;
import com.pm.tradesimulator.service.market.MeanReversionStrategy;
import com.pm.tradesimulator.service.market.PriceUpdateStrategies;
import com.pm.tradesimulator.service.market.RandomWalkStrategy;
import com.pm.tradesimulator.service.market.TrendFollowingStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StrategyController {

    private final MarketFeedServices marketFeedServices;
    private final RandomWalkStrategy randomWalkStrategy;
    private final MeanReversionStrategy meanReversionStrategy;
    private final TrendFollowingStrategy trendFollowingStrategy;

    public StrategyController(MarketFeedServices marketFeedServices,
                              RandomWalkStrategy randomWalkStrategy,
                              MeanReversionStrategy meanReversionStrategy,
                              TrendFollowingStrategy trendFollowingStrategy) {
        this.marketFeedServices     = marketFeedServices;
        this.randomWalkStrategy     = randomWalkStrategy;
        this.meanReversionStrategy  = meanReversionStrategy;
        this.trendFollowingStrategy = trendFollowingStrategy;
    }

    //  GET /api/strategy — returns current strategy name
    @GetMapping("/strategy")
    public ResponseEntity<?> getStrategy() {
        return ResponseEntity.ok(Map.of(
                "strategy", marketFeedServices.getStrategyName()
        ));
    }

    // POST /api/strategy — switches strategy at runtime
    @PostMapping("/strategy")
    public ResponseEntity<?> setStrategy(@RequestBody Map<String, String> body) {
        String name = body.get("strategy");

        PriceUpdateStrategies selected = switch (name) {
            case "meanReversion"  -> meanReversionStrategy;
            case "trendFollowing" -> trendFollowingStrategy;
            default               -> randomWalkStrategy;
        };

        marketFeedServices.setStrategy(selected);

        return ResponseEntity.ok(Map.of(
                "strategy", name,
                "message",  "Strategy updated to " + name
        ));
    }
}