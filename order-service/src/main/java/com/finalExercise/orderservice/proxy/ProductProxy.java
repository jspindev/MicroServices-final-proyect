package com.finalExercise.orderservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name="product-service")
public interface ProductProxy {

    @GetMapping("/products/{id}/price")
    BigDecimal viewProductByPrice(@PathVariable String id);
}
