package com.finalExercise.orderservice.dto;

import java.math.BigDecimal;

public class OrderLineItemResponse {

    private Long id;
    private String skuCode;
    private BigDecimal price;
    private int quantity;

    public OrderLineItemResponse(Long id, String skuCode, BigDecimal price, int quantity) {
        this.id = id;
        this.skuCode = skuCode;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
