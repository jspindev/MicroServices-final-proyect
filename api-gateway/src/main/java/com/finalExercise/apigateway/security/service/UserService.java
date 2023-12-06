package com.finalExercise.apigateway.security.service;

import com.finalExercise.apigateway.security.dto.CreateUserDto;
import com.finalExercise.apigateway.security.dto.LoginDto;
import com.finalExercise.apigateway.security.dto.TokenDto;
import com.finalExercise.apigateway.security.enums.Role;
import com.finalExercise.apigateway.security.model.User;
import com.finalExercise.apigateway.security.jwt.JwtProvider;
import com.finalExercise.apigateway.security.repository.UserRepository;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService() {
    }

    public UserService(UserRepository userRepository, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<TokenDto> login(LoginDto dto){
            return userRepository.findByUsername(dto.getUsername())
                    .filter(user -> passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                    .map(user -> new TokenDto(jwtProvider.generateToken(user)))
                    .switchIfEmpty(Mono.error(new HttpException("Bad Credentials")));
    }

    @Transactional
    public Mono<User> create (CreateUserDto dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        //user.setRoles(Role.ROLE_ADMIN.name()+ ", "+ Role.ROLE_USER.name());
        user.setRoles(Role.ROLE_USER.name());
        Mono<Boolean> userExists = userRepository.findByUsername(user.getUsername()).hasElement();
        return userExists
                .flatMap(exists -> exists ?
                        Mono.error(new HttpException("User already exists"))
                        : userRepository.save(user));
    }
}
