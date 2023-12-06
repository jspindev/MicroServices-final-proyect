package com.finalExercise.orderservice.dto;

import com.finalExercise.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponse toDTO (Order order);
    Order toEntity (OrderRequest dto);
    Order toEntity (OrderResponse dto);
    List<OrderResponse> toDTOList (List<Order> orders);

}
