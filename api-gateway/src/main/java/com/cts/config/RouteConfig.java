package com.cts.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))

                .route("item-service", r -> r.path("/api/items/**")
                        .uri("lb://item-service"))

                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .uri("lb://inventory-service"))

                .route("notification-service", r -> r.path("/api/notifications/**")
                        .uri("lb://notification-service"))

                .route("audit-log-service", r -> r.path("/api/logs/**")
                        .uri("lb://audit-log-service"))
                .build();
    }
}
