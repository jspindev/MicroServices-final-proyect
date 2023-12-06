package com.finalExercise.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

public class OrderLineItemRequest {

    private String skuCode;
    @JsonIgnore
    private BigDecimal price;
    private int quantity;

    public OrderLineItemRequest(String skuCode, BigDecimal price, int quantity) {
        this.skuCode = skuCode;
        this.price = price;
        this.quantity = quantity;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


}
