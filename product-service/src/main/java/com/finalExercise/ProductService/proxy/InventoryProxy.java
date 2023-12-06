package com.finalExercise.ProductService.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="inventory-service")
public interface InventoryProxy {

    @PostMapping("/inventories/new/{skuCode}") //?quantity=10 default.
    ResponseEntity<Object> newInventoryFromProduct
    (@PathVariable String skuCode, @RequestParam(value="quantity",defaultValue = "10") int quantity);
}
