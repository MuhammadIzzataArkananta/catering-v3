package com.arka.cateringukom.api.model;

import com.google.gson.annotations.SerializedName;

public class OrderItem {

    @SerializedName("order_id")
    private long orderId;

    @SerializedName("menu_id")
    private int menuId;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("quantity")
    private int quantity;

    // Constructor
    public OrderItem(int menuId, double subtotal, int quantity) {

//        this.orderId = orderId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
}

