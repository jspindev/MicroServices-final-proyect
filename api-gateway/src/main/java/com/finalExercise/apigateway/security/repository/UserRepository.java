package com.finalExercise.apigateway.security.repository;

import com.finalExercise.apigateway.security.model.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User,Long> {

    Mono<User> findByUsername(String username);
}
