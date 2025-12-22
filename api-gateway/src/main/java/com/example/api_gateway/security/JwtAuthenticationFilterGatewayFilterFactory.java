package com.example.api_gateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilterGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthenticationFilterGatewayFilterFactory.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilterGatewayFilterFactory(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Claims claims = jwtUtil.getClaims(token);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(
                            exchange.getRequest()
                                    .mutate()
                                    .header("X-USER", claims.getSubject())
                                    .build()
                    )
                    .build();

            return chain.filter(mutatedExchange);
        };
    }

    public static class Config {
        // no config for now
    }
}
