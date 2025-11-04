package com.blessed_brains.visionfitAi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class splashactivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 1500L; // 5 seconds

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable navigate = new Runnable() {
        @Override public void run() {
            startActivity(new Intent(splashactivity.this, MainActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starter);



        // Animate app name with fade-in effect and delay

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        handler.postDelayed(navigate, SPLASH_DURATION);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(navigate); // avoid leaks if activity is destroyed early
        super.onDestroy();
    }
}
