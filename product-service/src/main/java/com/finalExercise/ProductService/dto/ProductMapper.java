package com.finalExercise.ProductService.dto;

import com.finalExercise.ProductService.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductResponse toDTO (Product product);
    Product toEntity (ProductRequest dto);
    List<ProductResponse> toDTOList (List<Product> products);
}
