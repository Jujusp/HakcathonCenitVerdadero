package com.wt.sample.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @Expose
    @SerializedName("login")
    final private String login;
    @Expose
    @SerializedName("password")
    final private String password;

    public LoginRequest(final String login, final String password) {
        this.login = login;
        this.password = password;
    }
}