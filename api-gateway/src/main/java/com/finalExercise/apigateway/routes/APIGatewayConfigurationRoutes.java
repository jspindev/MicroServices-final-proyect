package com.finalExercise.apigateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;

@Configuration
public class APIGatewayConfigurationRoutes {
    //Configuramos rutas
    @Bean
    public RouteLocator gatewayRouter (RouteLocatorBuilder builder){
        return builder.routes()
                .route(p->p.path("/orders/**").uri("lb://order-service"))
                .route(p->p.path("/products/**").uri("lb://product-service"))
                .route(p->p.path("/inventories/**").uri("lb://inventory-service"))
                .build();
    }
}
