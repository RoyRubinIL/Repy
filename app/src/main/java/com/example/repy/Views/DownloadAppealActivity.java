package com.example.repy.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;



import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.R;
import com.example.repy.Utilities.StorageManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;

public class DownloadAppealActivity extends AppCompatActivity {

    private MaterialButton returnButton, downloadAppeal;
    private UserManager userManager;
    private StorageManager storageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreen();
        setContentView(R.layout.activity_download_appeal);

        initializeViews();

        userManager = new UserManager();
        storageManager = StorageManager.getInstance(this);

        String appealUid = getIntent().getStringExtra("AppealUid");
        downloadAppeal.setOnClickListener(v -> downloadAppeal(appealUid));
        returnButton.setOnClickListener(v -> returnToMainMenu());
    }

    private void initializeViews() {
        downloadAppeal = findViewById(R.id.downAppeal_BTN_downloadAppeal);
        returnButton = findViewById(R.id.downAppeal_BTN_returnToMainMenu);
    }

    private void setupFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void downloadAppeal(String appealUid) {
        if (appealUid == null) {
            Log.e("DownloadAppealActivity", "Appeal UID is null. Cannot proceed with download.");
            return;
        }

        Log.d("DownloadAppealActivity", "Attempting to download appeal with UID: " + appealUid);
        storageManager.downloadAppealFile(userManager.getCurrentUserUid(), appealUid, this, success -> {
            if (success) {
                Log.d("DownloadAppealActivity", "Appeal file downloaded successfully.");
            } else {
                Log.e("DownloadAppealActivity", "Failed to download appeal file.");
            }
        });
    }


    private void returnToMainMenu() {
        Intent intent = new Intent(DownloadAppealActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
