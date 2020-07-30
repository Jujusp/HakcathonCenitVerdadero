package com.wt.sample.utill;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.wt.sample.R;

public class Utill {
    public static void showError(final Context context, final String errorMessage) {
        showToast(context, errorMessage, android.R.color.holo_red_dark);
    }

    public static void showMessage(final Context context, final String errorMessage) {
        showToast(context, errorMessage, R.color.colorAccent);
    }

    private static void showToast(final Context context, final String errorMessage, final int textColorId) {
        final Toast t = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
        t.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground));
        ((TextView) t.getView().findViewById(android.R.id.message)).setTextColor(ContextCompat.getColor(context, textColorId));
        ((TextView) t.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
        ((TextView) t.getView().findViewById(android.R.id.message)).setTextSize(18F);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }
}
