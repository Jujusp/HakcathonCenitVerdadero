package com.wt.sample.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @Expose
    @SerializedName("token")
    private String token;
    @Expose
    @SerializedName("msc_url")
    private String url;
    @Expose
    @SerializedName("ok")
    private int ok;
    @Expose
    @SerializedName("msg")
    private String msg;

    public String getToken() {
        return token;
    }

    public String getSessionUrl() {
        return url;
    }

    public boolean isOk() {
        return ok == 1;
    }

    public String getErr() {
        return msg;
    }
}