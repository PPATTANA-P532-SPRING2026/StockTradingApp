package com.pm.tradesimulator.controller;

import com.pm.tradesimulator.model.order.Order;
import com.pm.tradesimulator.model.order.OrderType;
import com.pm.tradesimulator.model.order.Side;
import com.pm.tradesimulator.model.portfolio.Portfolio;
import com.pm.tradesimulator.model.portfolio.TradeRecord;
import com.pm.tradesimulator.service.trading.TradingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TradeController {

    private TradingService tradingService;
    private Portfolio portfolio;

    public TradeController(TradingService tradingService, Portfolio portfolio) {
        this.tradingService = tradingService;
        this.portfolio = portfolio;
    }

    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, String> body) {
        try {
            String ticker = body.get("ticker");
            Side side = Side.valueOf(body.get("side").toUpperCase());
            OrderType type = OrderType.valueOf(body.get("type").toUpperCase());
            int quantity = Integer.parseInt(body.get("quantity"));
            BigDecimal limitPrice = body.get("limitPrice") != null
                    ? new BigDecimal(body.get("limitPrice"))
                    : null;

            Order order = tradingService.placeOrder(type, ticker, side, quantity, limitPrice);
            return ResponseEntity.ok(order);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable String id) {
        boolean cancelled = tradingService.cancelOrder(id);
        if (cancelled) {
            return ResponseEntity.ok("Order cancelled successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/trades")
    public List<TradeRecord> getTradeHistory() {
        return portfolio.getTradeHistory();
    }
}