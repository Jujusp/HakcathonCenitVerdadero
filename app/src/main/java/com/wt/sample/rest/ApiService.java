package com.wt.sample.rest;

import com.wt.sample.data.LoginRequest;
import com.wt.sample.data.LoginResponse;
import com.wt.sample.data.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/token")
    Call<TokenResponse> getSessionToken(@Query("token") final String token);

    @POST("/login")
    Call<LoginResponse> login(@Body final LoginRequest loginRequest);
}