package com.finalExercise.inventoryservice.controller;


import com.finalExercise.inventoryservice.dto.InventoryMapper;
import com.finalExercise.inventoryservice.dto.InventoryRequest;
import com.finalExercise.inventoryservice.dto.InventoryResponse;
import com.finalExercise.inventoryservice.model.Inventory;
import com.finalExercise.inventoryservice.service.InventoryService;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/inventories")
public class InventoryController {

    @Autowired
    private InventoryService service;

    @Autowired
    private InventoryMapper mapper;
    @GetMapping()
    public List<InventoryResponse> retrieveAllInventories() throws HttpException {
        return service.retrieveAllInventories();
    }

    @GetMapping("/searchid/{id}")
    public InventoryResponse retrieveInventoryItemId(@PathVariable Long id) throws HttpException {
        return service.retrieveById(id);
    }
    @GetMapping("/searchsku/{skuCode}")
    public InventoryResponse retrieveInventoryItem(@PathVariable String skuCode) throws HttpException {
        return service.retrieveBySkuCode(skuCode);
    }

    // Agrega nuevo inventario
    @PostMapping()
    public ResponseEntity<Object> addInventory(@RequestBody InventoryRequest inventoryRequest) throws HttpException {
        String skuCode = inventoryRequest.getSkuCode();
        InventoryResponse inventory = service.addItemToInventory(inventoryRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{skuCode}").buildAndExpand(skuCode).toUri();

        return ResponseEntity.created(location).body(inventory);
    }

    // Agrega nuevo inventario cuando un producto nuevo es añadido con RequestParam quantity
    @PostMapping("/new/{skuCode}")
    public ResponseEntity<Object> newInventoryFromProduct(@PathVariable String skuCode, @RequestParam(value="quantity", defaultValue = "10") int quantity) throws HttpException {

        InventoryRequest inventoryRequest = new InventoryRequest(skuCode, quantity);
        InventoryResponse inventory = service.addItemToInventory(inventoryRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/inventories/{skuCode}").buildAndExpand(skuCode).toUri();

        return ResponseEntity.created(location).body(inventory);
    }

    // Modifica cantidad de un inventario
    @PostMapping("/modifiedquantity/{skuCode}/quantity/{quantity}")
    public ResponseEntity<Object> modifiedQuantity(@PathVariable String skuCode, @PathVariable int quantity) throws HttpException {
        InventoryResponse inventory =  service.modifyQuantityBySku(skuCode, quantity);
        return ResponseEntity.accepted().body(inventory);
    }

    @DeleteMapping("/deletesku/{skuCode}")
    public ResponseEntity<Object> deleteBySkuCode(@PathVariable String skuCode) throws HttpException {
        InventoryResponse inventory = service.deleteBySku(skuCode);
        return ResponseEntity.ok(inventory);
    }

    @DeleteMapping("/deleteid/{id}")
    public ResponseEntity<InventoryResponse> deleteById(@PathVariable Long id) throws HttpException {
        InventoryResponse inventory = service.deleteById(id);
        return ResponseEntity.ok(inventory);
    }
}