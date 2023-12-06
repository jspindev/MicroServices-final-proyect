package com.finalExercise.inventoryservice.dto;

import com.finalExercise.inventoryservice.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

//The InventoryMapper class converts between Inventory, InventoryRequest, and InventoryResponse.
@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    Inventory toEntity(InventoryRequest inventoryRequest);

    InventoryResponse toDTO(Inventory inventory);

    List<InventoryResponse> toDTOList(List<Inventory> inventories);
}