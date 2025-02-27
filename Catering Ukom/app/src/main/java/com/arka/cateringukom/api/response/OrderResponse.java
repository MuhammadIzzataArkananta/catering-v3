package com.arka.cateringukom.api.response;

import com.arka.cateringukom.api.model.OrderItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("total_price")
    private int totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("order_items")
    private List<OrderItem> orderItems;

    // Getters
    public int getId() {
        return id;
    }
    public int getUserId() {
        return userId;
    }
    public int getTotalPrice() {
        return totalPrice;
    }
    public String getStatus() {
        return status;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}