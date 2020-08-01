package com.wt.sample.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.wt.sample.R;
import com.wt.sample.data.TokenResponse;
import com.wt.sample.rest.RestClient;
import com.wt.sample.utill.Utill;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity extends AppCompatActivity {



    public static final String SESSION_URL_CODE = "session_url_code";
    public static final String SESSION_ID_CODE = "session_id_code";
    public static final String SESSION_TOKEN_CODE = "token_code";
    public static final String DISPLAY_NAME_CODE = "display_name_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        // Done actions sign in button click
        Intent intent = getIntent();
        String displayName = intent.getStringExtra(LoginActivity.DISPLAY_NAME_CODE);
        String url = intent.getStringExtra(LoginActivity.SESSION_URL_CODE);
        String token = intent.getStringExtra(LoginActivity.SESSION_TOKEN_CODE);

        // Click Listener for sign in button
        findViewById(R.id.ingles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getSessionToken(url, token, "1", displayName);
            }
        });

        findViewById(R.id.sociales).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getSessionToken(url, token, "2", displayName);
            }
        });

        findViewById(R.id.bttools).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(MainPageActivity.this
                , Tools.class);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });

        findViewById(R.id.btChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(MainPageActivity.this
                        , ChatPageActivity.class);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });

    }


    private void getSessionToken(final String url, final String signInToken, final String sessionId, final String displayName) {
        RestClient.getService(url).getSessionToken(signInToken).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(final Call<TokenResponse> call, final Response<TokenResponse> response) {
                if (response != null) {
                    if (response.body() != null && response.isSuccessful()) {
                        if (response.body().isOk()) {
                            if (TextUtils.isEmpty(response.body().getToken()) || TextUtils.isEmpty(response.body().getSessionUrl())) {
                                onError("Invalid response, data is missing");
                            } else {
                                final Intent intent = new Intent(MainPageActivity.this, MainActivity.class);
                                intent.putExtra(SESSION_URL_CODE, (response.body().getSessionUrl()));
                                intent.putExtra(SESSION_TOKEN_CODE, (response.body().getToken()));
                                intent.putExtra(SESSION_ID_CODE, sessionId);
                                intent.putExtra(DISPLAY_NAME_CODE, displayName);
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Start MainActivity with params
                                        startActivity(intent);
                                      //  showProgress(false);
                                    }
                                });
                            }
                        } else {
                            onError(response.body().getErr());
                        }
                    } else {
                        onError("Error empty response or response is not successful");
                    }
                } else {
                    onError("Error empty response");
                }
            }

            @Override
            public void onFailure(final Call<TokenResponse> call, final Throwable t) {
                onError("Failed to execute login request");
            }
        });
    }
    private void onError(final String error) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Utill.showError(MainPageActivity.this, error);
                showProgress(false);
            }
        });
    }

    private void showProgress(final boolean isVisible) {
        findViewById(R.id.progress).setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}