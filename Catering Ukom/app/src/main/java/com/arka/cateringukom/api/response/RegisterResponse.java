package com.arka.cateringukom.api.response;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("email")
    private String email;

    @SerializedName("message")
    private String message;

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}