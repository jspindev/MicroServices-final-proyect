package com.finalExercise.orderservice.proxy;

import com.finalExercise.orderservice.dto.OrderLineItemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="inventory-service")
public interface InventoryProxy {
    @GetMapping("/inventories/searchsku/{skuCode}")
    OrderLineItemRequest retrieveInventoryItem(@PathVariable String skuCode);

    @PostMapping("/inventories/modifiedquantity/{skuCode}/quantity/{quantity}")
    ResponseEntity<Object> modifiedQuantity(@PathVariable String skuCode, @PathVariable int quantity);
}
