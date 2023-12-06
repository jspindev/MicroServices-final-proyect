package com.finalExercise.orderservice.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {

    private Long id;
    private String orderNumber;
    private List<OrderLineItemResponse> orderLineItemsList;
    private BigDecimal price;

    public OrderResponse(){}
    public OrderResponse(Long id, String orderNumber, List<OrderLineItemResponse> orderLineItemsList, BigDecimal price) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderLineItemsList = orderLineItemsList;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderLineItemResponse> getOrderLineItemsList() {
        return orderLineItemsList;
    }

    public void setOrderLineItemsList(List<OrderLineItemResponse> orderLineItemsList) {
        this.orderLineItemsList = orderLineItemsList;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
