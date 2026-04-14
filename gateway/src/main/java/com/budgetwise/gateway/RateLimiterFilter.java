package com.budgetwise.gateway;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {
    ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String key = buildKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, k->createBucket());
        ConsumptionProbe consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);
        System.out.println("Key: " + key + " | remaining: " + consumptionProbe.getRemainingTokens() + " | consumed: " + consumptionProbe.isConsumed());
        if(consumptionProbe.isConsumed()){
            filterChain.doFilter(request, response);
        }
        else{
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests, try again later");
        }

    }
    private String buildKey(HttpServletRequest request){
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        return ip+":"+uri;
    }
    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(2).refillIntervally(2, Duration.ofSeconds(10)))
                .build();
    }
}

