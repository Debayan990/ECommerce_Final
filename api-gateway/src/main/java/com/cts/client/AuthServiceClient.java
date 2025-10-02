package com.cts.client;

import com.cts.dto.TokenValidationResponse;
import com.cts.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {

    @Value("${auth.service.url}")
    private String authServiceUrl;

    private final WebClient.Builder webClientBuilder;

    public Mono<UserInfo> validateTokenAndGetUserInfo(String token) {
        return webClientBuilder.build()
                .post()
                .uri(authServiceUrl + "/api/auth/validate")
                .bodyValue(token)
                .retrieve()
                .bodyToMono(TokenValidationResponse.class)
                .map(response -> new UserInfo(response.isValid(), response.getUsername(), response.getRoles()))
                .onErrorReturn(new UserInfo(false, null, null));
    }

    public Mono<List<String>> getUserRoles(String username) {
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/api/auth/user/{username}/roles", username)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .onErrorReturn(List.of());
    }

    public Mono<Boolean> isAdminUser(String username) {
        return getUserRoles(username)
                .map(roles -> roles.contains("ADMIN") || roles.contains("ROLE_ADMIN"));
    }
}
