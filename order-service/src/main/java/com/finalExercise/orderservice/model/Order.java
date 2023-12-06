package com.finalExercise.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //Se suelen poner en plural en las DB
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String orderNumber;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //Como se va a mapear para que sea bidireccional
    @JsonManagedReference
    private List<OrderLineItem> orderLineItemsList = new ArrayList<>();
    private BigDecimal price;

    public Order(){}
    public Order(Long id, String orderNumber, List<OrderLineItem> orderLineItemsList, BigDecimal price) {
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

    public List<OrderLineItem> getOrderLineItemsList() {
        return orderLineItemsList;
    }

    public void setOrderLineItemsList(List<OrderLineItem> orderLineItemsList) {
        this.orderLineItemsList = orderLineItemsList;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderLineItemsList=" + orderLineItemsList +
                ", price=" + price +
                '}';
    }
}
