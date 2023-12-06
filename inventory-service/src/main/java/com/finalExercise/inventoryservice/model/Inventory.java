package com.finalExercise.inventoryservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String skuCode;

    @Column(nullable = false)
    private int quantity;

    public Inventory(){}
    public Inventory(String skuCode, int quantity) {
        this.skuCode = skuCode;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setSku(String sku) {
        this.skuCode = sku;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
