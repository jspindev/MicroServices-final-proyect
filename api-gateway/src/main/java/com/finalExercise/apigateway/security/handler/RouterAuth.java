package com.finalExercise.apigateway.security.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterAuth {

    private static final String PATH = "auth/";

    @Bean
    RouterFunction<ServerResponse> authRouter(AuthHandler handler){
        return RouterFunctions.route()
                .POST(PATH + "login", handler::login)
                .POST(PATH + "create", handler::create)
                .build();
    }

}
