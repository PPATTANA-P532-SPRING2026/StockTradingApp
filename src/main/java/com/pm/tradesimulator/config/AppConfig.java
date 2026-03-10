package com.pm.tradesimulator.config;

import com.pm.tradesimulator.service.market.PriceUpdateStrategies;
import com.pm.tradesimulator.service.market.RandomWalkStrategy;
import com.pm.tradesimulator.service.notification.ConsoleNotifier;
import com.pm.tradesimulator.service.notification.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class AppConfig {

    @Bean
    public PriceUpdateStrategies priceUpdateStrategies() {
        return new RandomWalkStrategy();
    }

    @Bean
    public NotificationService notificationService() {
        return new ConsoleNotifier();
    }

    @Bean
    public Random random() {
        return new Random();
    }

}
