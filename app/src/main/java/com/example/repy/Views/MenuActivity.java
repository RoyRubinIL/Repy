package com.example.repy.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.User;
import com.example.repy.R;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    private TextView generateAppeal, monitorAppeals, appealsHistory, manageProfile, profileName;
    private MaterialButton logOutButton;
    private UserManager userManager;
    private DataManager dataManager;
    private ImageView profileAvatar;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_menu);

        initializeViews();
        userManager = new UserManager();
        currentUser = userManager.getCurrentUser();
        dataManager = DataManager.getInstance();
        setName();
        loadProfileImage();
        generateAppeal.setOnClickListener(v -> moveToGenerateAppealActivity());
        monitorAppeals.setOnClickListener(v -> moveToMonitorActivity());
        appealsHistory.setOnClickListener(v -> moveToAppealsHistoryActivity());
        manageProfile.setOnClickListener(v -> moveToProfileActivity());
        logOutButton.setOnClickListener(v -> moveToStartActivity());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initializeViews() {
        generateAppeal = findViewById(R.id.txt_generate_appeal);
        monitorAppeals = findViewById(R.id.txt_monitor_appeals);
        appealsHistory = findViewById(R.id.txt_appeals_history);
        manageProfile = findViewById(R.id.txt_manage_profile);
        logOutButton = findViewById(R.id.btn_logout);
        profileAvatar = findViewById(R.id.profile_IMG_avatar);
        profileName = findViewById(R.id.hello_user);
    }

    private void loadProfileImage(){
        if (currentUser != null) {
            String uid = currentUser.getUid();
            dataManager.getUserData(uid, new DataManager.CallBack<User>() {
                @Override
                public void res(User user) {
                    String profileImageUrl = user.getProfileImage();
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        dataManager.loadProfileImage(MenuActivity.this, profileImageUrl, profileAvatar);
                    }
                }
            });
        }
    }

    private void setName() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            dataManager.getUserData(uid, new DataManager.CallBack<User>() {
                @Override
                public void res(User user) {
                    if (user != null) {
                        profileName.setText("Hello " + user.getName() + ",");
                    } else {
                        profileName.setText("Hello User");
                    }
                }
            });
        } else {
            profileName.setText("Hello User");
        }
    }


    private void moveToGenerateAppealActivity() {
        Intent intent = new Intent(MenuActivity.this, GenerateAppealActivity.class);
        startActivity(intent);
    }

    private void moveToAppealsHistoryActivity() {
        Intent intent = new Intent(MenuActivity.this, AppealsHistoryActivity.class);
        startActivity(intent);
    }

    private void moveToMonitorActivity() {
        Intent intent = new Intent(MenuActivity.this, MonitorActivity.class);
        startActivity(intent);
    }

    private void moveToProfileActivity() {
        Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void moveToStartActivity() {
        userManager.logOutUser();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
