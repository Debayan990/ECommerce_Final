package com.cts.filter;

import com.cts.client.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final AuthServiceClient authServiceClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Skip authentication for auth service endpoints
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        // Check if this is a core service request
        if (path.startsWith("/api/items") || path.startsWith("/api/inventory") || path.startsWith("/api/notifications") || path.startsWith("/api/logs")) {
            String token = getTokenFromRequest(request);

            if (!StringUtils.hasText(token)) {
                return onError(exchange, "No token provided", HttpStatus.UNAUTHORIZED);
            }

            // Validate token via REST API call to auth service (BEST PRACTICE)
            return authServiceClient.validateTokenAndGetUserInfo(token)
                    .flatMap(userInfo -> {
                        if (userInfo == null || !userInfo.isValid()) {
                            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                        }

                        // For POST, PUT, DELETE requests, check if user has admin role
                        if (request.getMethod().name().equals("POST") || request.getMethod().name().equals("PUT") || request.getMethod().name().equals("DELETE")) {
                            boolean isAdmin = userInfo.getRoles().contains("ADMIN") ||
                                    userInfo.getRoles().contains("ROLE_ADMIN");
                            if (!isAdmin) {
                                return onError(exchange, "Admin privileges required", HttpStatus.FORBIDDEN);
                            }
                        }

                        // Add username and roles to request headers to pass
                        ServerHttpRequest mutatedRequest = request.mutate()
                                .header("X-User-Name", userInfo.getUsername())
                                .header("X-User-Roles", String.join(",", userInfo.getRoles()))
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    })
                    .onErrorResume(throwable -> onError(exchange, "Token validation failed", HttpStatus.UNAUTHORIZED));
        }

        return chain.filter(exchange);
    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        String body = "{\"error\":\"" + error + "\",\"status\":" + httpStatus.value() + "}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return -100; // High priority
    }
}
