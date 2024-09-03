package com.example.myvideo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class SplashActivity extends AppCompatActivity {
    private final ActivityResultLauncher<String[]> permissionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean readImagesGranted = result.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES,false);
                Boolean readVideoGranted = result.getOrDefault(Manifest.permission.READ_MEDIA_VIDEO,false);
                if (readImagesGranted && readVideoGranted) {
                    startMainActivity();
                } else {
                    Toast.makeText(this, "Please Grant Storage Permission", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.teal_700));
        getWindow().setStatusBarColor(getResources().getColor(R.color.teal_700));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isMediaAccessGranted()) {
                requestMediaPermissions();
            } else {
                startMainActivity();
            }
        } else {
            startMainActivity();

        }
    }

    private void startMainActivity() {
        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(() -> startActivity(new Intent(SplashActivity.this, MainActivity.class)), 100);
    }

    private boolean isMediaAccessGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void requestMediaPermissions() {
        permissionResultLauncher.launch(new String[] {
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
        });
    }
}
