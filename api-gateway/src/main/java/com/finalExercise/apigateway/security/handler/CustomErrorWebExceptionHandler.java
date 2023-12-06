package com.finalExercise.apigateway.security.handler;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNullApi;

import java.nio.charset.StandardCharsets;

@Configuration
public class CustomErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable th) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        CustomErrorResponse errorResponse = new CustomErrorResponse(th.getMessage(),
                exchange.getRequest().getPath().toString());
        //Convertimos en Bytes el error y lo envolvemos en la Respuesta del Server ServerWebExchange (request,response)
        byte[] bytes = errorResponse.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer df = exchange.getResponse().bufferFactory().wrap(bytes);

        //Devuelve ServerHttpResponse
        return exchange.getResponse().writeWith(Mono.just(df));
    }
}
