package com.arka.cateringukom.api.response;


import com.arka.cateringukom.api.model.User;
import com.google.gson.annotations.SerializedName;

public class VerifyResponse {

    @SerializedName("user")
    private User user;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return message;
    }
}

