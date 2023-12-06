package com.finalExercise.inventoryservice.dto;

//This DTO is used for sending data when retrieving inventory items.
public class InventoryResponse {

    private Long id;
    private String skuCode;
    private int quantity;

    public InventoryResponse(Long id, String skuCode, int quantity) {
        this.id = id;
        this.skuCode = skuCode;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
