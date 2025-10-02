package com.cts.service;

import com.cts.client.ItemServiceClient;
import com.cts.exception.ResourceNotFoundException;
import com.cts.exception.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemValidationService {

    private final ItemServiceClient itemServiceClient;    // Inject the Feign Client

    // Helper method for validation
    @CircuitBreaker(name = "item-service-client", fallbackMethod = "itemServiceFallback")
    @Retry(name = "item-service-client")
    public  void validateItemId(Long itemId, String token) {
            itemServiceClient.getItemById(itemId, token);
    }

    // Fallback method: will executed when circuit breaker is open
    public void itemServiceFallback(Long itemId, String token, Throwable throwable) {
        if (throwable instanceof FeignException.NotFound) {
            throw new ResourceNotFoundException("Item", "id", itemId);
        }
        throw new ServiceUnavailableException("Item service is currently unavailable. Please try again later.");
    }
}
