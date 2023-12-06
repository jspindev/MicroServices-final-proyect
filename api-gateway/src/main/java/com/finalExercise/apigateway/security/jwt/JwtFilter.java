package com.finalExercise.apigateway.security.jwt;


import org.apache.http.HttpException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if(path.contains("auth/login") || path.contains("**/swagger/swagger-ui.html")
                || path.contains("**/v3/api-docs"))
        { //dejamos pasar, sin el token
            return chain.filter(exchange);
        }
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth==null){
            return Mono.error(new HttpException("No Token was found"));
        }else if(!auth.startsWith("Bearer ")){
            return Mono.error(new HttpException("Invalid Token"));
        }
        String token = auth.replace("Bearer ", "");
        exchange.getAttributes().put("token",token);
        return chain.filter(exchange);//Se añade token en exchange
    }
}
