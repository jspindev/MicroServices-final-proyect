package com.finalExercise.inventoryservice.service;

import com.finalExercise.inventoryservice.model.Inventory;
import com.finalExercise.inventoryservice.dto.*;
import com.finalExercise.inventoryservice.repository.InventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.http.HttpException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private final InventoryRepository repository;

    @Autowired
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.repository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    public List<InventoryResponse> retrieveAllInventories() throws HttpException {
        List<Inventory> inventories = repository.findAll();
        if (inventories.isEmpty()) {
            throw new HttpException("No inventories found");
        }
        return inventoryMapper.toDTOList(inventories);
    }

    public InventoryResponse retrieveBySkuCode(String skuCode) throws HttpException {
        if (repository.findBySkuCode(skuCode).isEmpty()) {
            throw new HttpException("No inventory found for SkuCode: " + skuCode);
        }
        Inventory inventory = repository.findBySkuCode(skuCode).get();
        return inventoryMapper.toDTO(inventory);
    }

    public Optional<Inventory> retrieveBySkuCodeEntity(String skuCode) {
        return repository.findBySkuCode(skuCode);
    }

    @Transactional
    public InventoryResponse addItemToInventory(InventoryRequest inventoryRequest) throws HttpException {
        if (retrieveBySkuCodeEntity(inventoryRequest.getSkuCode()).isPresent()) {
            throw new HttpException("Error: SkuCode repeated");
        }
        try {
            Inventory inventory = inventoryMapper.toEntity(inventoryRequest);
            inventory.setSku(inventoryRequest.getSkuCode());
            inventory.setQuantity(inventoryRequest.getQuantity());
            repository.save(inventory);
            return inventoryMapper.toDTO(inventory);
        } catch (Exception e) {
            throw new HttpException("Error: Adding item to Inventory");
        }
    }

    // Modifica la Quantity de un item por su skuCode en el Inventory
    @Transactional
    public InventoryResponse modifyQuantityBySku(String skuCode, int quantity) throws HttpException {
        Optional<Inventory> inventoryOptional = retrieveBySkuCodeEntity(skuCode);

        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            if(-quantity > inventory.getQuantity()) {
                throw new HttpException("Error: Not that much quantity available");
            } else {
                inventory.setQuantity(inventory.getQuantity() + quantity);
                try {
                    repository.save(inventory);
                    return inventoryMapper.toDTO(inventory);
                }
                catch (Exception e) {
                    throw new HttpException("Error saving the new quantity");
                }
            }
        }
        else {
            throw new HttpException("Item not found with skuCode:" + skuCode);
        }
    }

    @Transactional
    public InventoryResponse deleteBySku(String skuCode) throws HttpException{
        Optional<Inventory> inventoryOptional = repository.findBySkuCode(skuCode);
        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            repository.deleteBySkuCode(skuCode);
            return inventoryMapper.toDTO(inventory);
        }
        throw new HttpException("Inventory not found with skuCode to DELETE:" + skuCode);
    }

    @Transactional
    public InventoryResponse deleteById(Long id) throws HttpException {
        Optional<Inventory> inventoryOptional = repository.findById(id);
        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            repository.deleteById(id);
            return inventoryMapper.toDTO(inventory);
        }
        throw new HttpException("Item not found with ID to DELETE:" + id);
    }

    public InventoryResponse retrieveById(Long id) throws HttpException {
        if (repository.findById(id).isEmpty()) {
            throw new HttpException("No inventory found for ID: " + id);
        }
        Inventory inventory = repository.findById(id).get();
        return inventoryMapper.toDTO(inventory);    }
}