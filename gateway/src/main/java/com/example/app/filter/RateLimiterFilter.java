package com.example.app.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimiterFilter extends AbstractGatewayFilterFactory<RateLimiterFilter.Config> {

    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public RateLimiterFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientId = getClientId(exchange);
            Bucket bucket = bucketCache.computeIfAbsent(clientId, this::createNewBucket);

            if (bucket.tryConsume(1)) {
                return chain.filter(exchange);
            }

            log.warn("Rate limit exceeded for client: {}", clientId);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("X-RateLimit-Rejected", "true");
            return exchange.getResponse().setComplete();
        };
    }

    private String getClientId(org.springframework.web.server.ServerWebExchange exchange) {
        String ip = exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                : "unknown";
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        return userId != null ? userId : ip;
    }

    private Bucket createNewBucket(String key) {
        Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    public static class Config {
        private int capacity = 100;
        private int refillTokens = 100;
        private Duration refillDuration = Duration.ofMinutes(1);

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getRefillTokens() {
            return refillTokens;
        }

        public void setRefillTokens(int refillTokens) {
            this.refillTokens = refillTokens;
        }

        public Duration getRefillDuration() {
            return refillDuration;
        }

        public void setRefillDuration(Duration refillDuration) {
            this.refillDuration = refillDuration;
        }
    }
}