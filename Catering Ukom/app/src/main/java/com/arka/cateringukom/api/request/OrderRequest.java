package com.arka.cateringukom.api.request;

import com.arka.cateringukom.api.model.OrderItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderRequest {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("total_price")
    private int totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("order_items")
    private List<OrderItem> orderItems;

    public OrderRequest(int userId, int totalPrice, String status, List<OrderItem> orderItems) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderItems = orderItems;
    }

}
