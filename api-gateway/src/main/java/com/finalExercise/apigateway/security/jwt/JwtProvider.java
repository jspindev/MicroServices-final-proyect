package com.finalExercise.apigateway.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    //Los coja de app.properties
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private int expiration;


    //Pasar una clase que implemente UserDetails
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles",userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+expiration* 1000L))
                .signWith(getKey(secret))
                .compact();
    }

    public Claims getClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getKey(secret))
                .build().parseClaimsJws(token).getBody();
    }

    //Si queremos el nombre de usuario
    public String getSubject(String token){
        return Jwts.parserBuilder().setSigningKey(getKey(secret))
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    //Si los Claims no lanzan ninguna excepcion es que es correcto el Token
    public boolean validate (String token){
        try {
            Jwts.parserBuilder().setSigningKey(getKey(secret))
                    .build().parseClaimsJws(token).getBody();
            return true;
        }catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                IllegalArgumentException e){
            e.printStackTrace();
        }
        return false;
    }

    private Key getKey (String secret){
        byte [] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
