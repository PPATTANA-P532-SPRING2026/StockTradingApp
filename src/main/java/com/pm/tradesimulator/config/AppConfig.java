package com.pm.tradesimulator.config;

import com.pm.tradesimulator.service.market.PriceUpdateStrategies;
import com.pm.tradesimulator.service.market.RandomWalkStrategy;
import com.pm.tradesimulator.service.notification.ConsoleNotifier;
import com.pm.tradesimulator.service.notification.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Random;

@Configuration
public class AppConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                "http://localhost:8080",
                                "https://ppattana-p532-spring2026.github.io"
                        )
                        .allowedMethods("GET", "POST", "DELETE");
            }
        };
    }

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
