package be.vives.loic.shopandcook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import be.vives.loic.shopandcook.R;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DURATION_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }, SPLASH_DURATION_MS);
    }
}
