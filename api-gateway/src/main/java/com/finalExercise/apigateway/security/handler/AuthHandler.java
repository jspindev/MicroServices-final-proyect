package com.finalExercise.apigateway.security.handler;

import com.finalExercise.apigateway.security.dto.CreateUserDto;
import com.finalExercise.apigateway.security.dto.LoginDto;
import com.finalExercise.apigateway.security.dto.TokenDto;
import com.finalExercise.apigateway.security.model.User;
import com.finalExercise.apigateway.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler {

    @Autowired
    private UserService userService;

    public AuthHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> login(ServerRequest request){
        Mono<LoginDto> dtoMono = request.bodyToMono(LoginDto.class);
        return dtoMono.flatMap(dto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(userService.login(dto), TokenDto.class));
    }
    public Mono<ServerResponse> create(ServerRequest request){
        Mono<CreateUserDto> dtoMono = request.bodyToMono(CreateUserDto.class);
        return dtoMono.flatMap(dto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(userService.create(dto), User.class));
    }
}
