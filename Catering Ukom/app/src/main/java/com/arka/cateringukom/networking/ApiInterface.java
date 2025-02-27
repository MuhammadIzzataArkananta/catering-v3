package com.arka.cateringukom.networking;

import com.arka.cateringukom.api.model.Menu;
import com.arka.cateringukom.api.request.OrderRequest;
import com.arka.cateringukom.api.response.LoginResponse;
import com.arka.cateringukom.api.response.OrderResponse;
import com.arka.cateringukom.api.response.RegisterResponse;
import com.arka.cateringukom.api.response.VerifyResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

;

public interface ApiInterface {
    @GET("menus")
    Call<List<Menu>> getMenus();  // Gunakan List<Menu>


    @FormUrlEncoded
    @POST("register")
    Call<RegisterResponse> register(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("password_confirmation") String passwordConfirmation
    );

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("logout")
    Call<Void> logout(@Header("Authorization") String token);


    @FormUrlEncoded
    @POST("verify-code")
    Call<VerifyResponse> verifyEmail(
            @Field("email") String email,
            @Field("verification_code") String verificationCode
    );

    @POST("orders")
    Call<OrderResponse> createOrder(
            @Header("Authorization") String token,
            @Body OrderRequest orderRequest
    );

    @GET("get-user-order")
    Call<List<OrderResponse>> getUserOrder(
            @Header("Authorization") String token
    );






}

