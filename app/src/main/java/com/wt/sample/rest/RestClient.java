package com.wt.sample.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private static OkHttpClient client;
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS").setLenient().create();

    static {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    public static ApiService getService(final String authUrl) {
        return new Retrofit.Builder()
                .baseUrl(authUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client).build().create(ApiService.class);
    }
}