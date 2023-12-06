package com.finalExercise.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderRequest {

    private String orderNumber;
    private List<OrderLineItemRequest> orderLineItemsList = new ArrayList<>();
    private BigDecimal price;

    public OrderRequest(String orderNumber, List<OrderLineItemRequest> orderLineItemsList, BigDecimal price) {
        this.orderNumber = orderNumber;
        this.orderLineItemsList = orderLineItemsList;
        this.price = price;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderLineItemRequest> getOrderLineItemsList() {
        return orderLineItemsList;
    }

    public void setOrderLineItemsList(List<OrderLineItemRequest> orderLineItemsList) {
        this.orderLineItemsList = orderLineItemsList;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
