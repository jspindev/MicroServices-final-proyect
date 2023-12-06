package com.finalExercise.orderservice.dto;

import com.finalExercise.orderservice.model.OrderLineItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderLineItemMapper {
    OrderLineItemMapper INSTANCE = Mappers.getMapper(OrderLineItemMapper.class);

    OrderLineItemResponse toDTO (OrderLineItem item);
    OrderLineItem toEntity (OrderLineItemRequest dto);
    List<OrderLineItem> toDTOList (List<OrderLineItem> items);
}
