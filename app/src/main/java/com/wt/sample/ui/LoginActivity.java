package com.wt.sample.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pixplicity.easyprefs.library.Prefs;
import com.wt.sample.R;
import com.wt.sample.data.LoginRequest;
import com.wt.sample.data.LoginResponse;
import com.wt.sample.data.TokenResponse;
import com.wt.sample.rest.RestClient;
import com.wt.sample.utill.Utill;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN_CODE = "login_code";
    public static final String PASS_CODE = "pass_code";
    public static final String SESSION_URL_CODE = "session_url_code";
    public static final String SESSION_ID_CODE = "session_id_code";
    public static final String SESSION_TOKEN_CODE = "token_code";
    public static final String DISPLAY_NAME_CODE = "display_name_code";

    private AppCompatEditText mAuthUrl;
    private AppCompatEditText mLogin;
    private AppCompatEditText mPass;
    private AppCompatEditText mSessionId;
    private AppCompatEditText mDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Init and auto fill edit fields with saved values
        mAuthUrl = findViewById(R.id.auth_url);
        if (TextUtils.isEmpty(Prefs.getString(SESSION_URL_CODE, ""))) {
            mAuthUrl.setText(R.string.auth_url);
        } else {
            mAuthUrl.setText(Prefs.getString(SESSION_URL_CODE, ""));
        }
        mLogin = findViewById(R.id.login);
        if (TextUtils.isEmpty(Prefs.getString(LOGIN_CODE, ""))) {
            mLogin.setText(R.string.login);
        } else {
            mLogin.setText(Prefs.getString(LOGIN_CODE, ""));
        }
        mPass = findViewById(R.id.password);
        if (TextUtils.isEmpty(Prefs.getString(PASS_CODE, ""))) {
            mPass.setText(R.string.pass);
        } else {
            mPass.setText(Prefs.getString(PASS_CODE, ""));
        }
        mSessionId = findViewById(R.id.session_id);
        mSessionId.setText(Prefs.getString(SESSION_ID_CODE, ""));
        mDisplayName = findViewById(R.id.display_name);
        mDisplayName.setText(Prefs.getString(DISPLAY_NAME_CODE, ""));

        // Click Listener for sign in button
        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                checkMediaPermissions();
            }
        });

        // Done actions sign in button click
        mDisplayName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkMediaPermissions();
                    return true;
                }
                return false;
            }
        });
    }

    private void checkMediaPermissions() {
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) + ContextCompat.checkSelfPermission(this, CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO, CAMERA}, 1);
            return;
        }
        // Permissions granted
        signIn();
    }

    // Check if user granted media permissions for app
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == grantResults[1]) {
            signIn();
            return;
        }
        // Permissions not allowed
        onError("Camera and Microphone permissions not allowed");
    }

    private void signIn() {
        showProgress(true);
        try {
            validateCredentials();
        } catch (Exception e) {
            onError("Error occurred while executing request");
            showProgress(false);
        }
    }

    private void validateCredentials() {
        final String url = String.valueOf(mAuthUrl.getText());
        if (TextUtils.isEmpty(url) || !URLUtil.isValidUrl(url)) {
            onError("Please, enter correct url");
            return;
        }
        Prefs.putString(SESSION_URL_CODE, url);
        final String login = String.valueOf(mLogin.getText());
        if (TextUtils.isEmpty(login)) {
            onError("\"Please, enter login value");
            return;
        }
        Prefs.putString(LOGIN_CODE, login);
        final String pass = String.valueOf(mPass.getText());
        if (TextUtils.isEmpty(pass)) {
            onError("Please, enter password value");
            return;
        }
        Prefs.putString(PASS_CODE, pass);
        final String sessionId = String.valueOf(mSessionId.getText());
        Prefs.putString(SESSION_ID_CODE, sessionId);
        final String displayName = String.valueOf(mDisplayName.getText());
        Prefs.putString(DISPLAY_NAME_CODE, displayName);
        login(url, login, pass, sessionId, displayName);
    }


    private void login(final String url, final String login, final String pass, final String sessionId, final String displayName) {
        RestClient.getService(url).login(new LoginRequest(login, pass)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(final Call<LoginResponse> call, final Response<LoginResponse> response) {
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().isOk()) {
                            if (TextUtils.isEmpty(response.body().getToken())) {
                                onError("Invalid response, data is missing");
                            } else {
                                toMainPage(url,response.body().getToken(),displayName);
                               // getSessionToken(url, response.body().getToken(), sessionId, displayName);
                            }
                        } else {
                            onError(response.body().getErr());
                        }
                    } else {
                        onError("Error empty response");
                    }
                } else {
                    onError("Error empty response");
                }
            }

            @Override
            public void onFailure(final Call<LoginResponse> call, final Throwable t) {
                onError("Failed to execute login request");
            }
        });
    }

    private void toMainPage(final String url, final String signInToken, final String displayName)
    {
        final Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
        intent.putExtra(DISPLAY_NAME_CODE, displayName);
        intent.putExtra(SESSION_URL_CODE, url);
        intent.putExtra(SESSION_TOKEN_CODE, signInToken);
        new Handler().post(new Runnable()
        {
            @Override
            public void run() {
                // Start MainActivity with params
                startActivity(intent);
                showProgress(false);
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
                                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra(SESSION_URL_CODE, (response.body().getSessionUrl()));
                                intent.putExtra(SESSION_TOKEN_CODE, (response.body().getToken()));
                                intent.putExtra(SESSION_ID_CODE, sessionId);
                                intent.putExtra(DISPLAY_NAME_CODE, displayName);
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Start MainActivity with params
                                        startActivity(intent);
                                        showProgress(false);
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
                Utill.showError(LoginActivity.this, error);
                showProgress(false);
            }
        });
    }

    private void showProgress(final boolean isVisible) {
        findViewById(R.id.progress).setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}