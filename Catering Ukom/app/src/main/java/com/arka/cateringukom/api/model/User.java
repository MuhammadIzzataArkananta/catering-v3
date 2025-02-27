package com.arka.cateringukom.api.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("verification_code")
    private String verification_code;

//    @SerializedName("is_verified")
//    private Boolean is_verified;

    // Konstruktor default (diperlukan untuk deserialisasi)
    public User() {
    }

    // Konstruktor untuk keperluan pembuatan objek baru (jika diperlukan)
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getVerificationCode() {
        return verification_code;
    }

//    public Boolean getIsVerified() {
//        return is_verified;
//    }
}