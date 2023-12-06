package com.finalExercise.apigateway.security.config;

import com.finalExercise.apigateway.security.jwt.JwtFilter;
import com.finalExercise.apigateway.security.repository.SecurityContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class MainSecurity {

    @Autowired
    private SecurityContextRepository securityContextRepository;

    public MainSecurity(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, JwtFilter jwtFilter){
        http.authorizeExchange((exchange) ->
                exchange
                        .pathMatchers("/auth/login", "/products/swagger/swagger-ui.html",
                                "/inventories/swagger/swagger-ui.html", "/orders/swagger/swagger-ui.html")
                        .permitAll()
                        .pathMatchers("/auth/create").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/**").hasAnyRole("USER", "ADMIN")
                        .anyExchange().authenticated());
        http.addFilterAfter(jwtFilter, SecurityWebFiltersOrder.FIRST);
        http.securityContextRepository(securityContextRepository);
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        http.logout(ServerHttpSecurity.LogoutSpec::disable);
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }
}
