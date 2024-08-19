package com.example.repy.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.repy.Models.User;
import com.example.repy.R;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity {

    private TextView generateAppeal, monitorAppeals, appealsHistory, manageProfile, profileName;
    private MaterialButton logOutButton;
    private UserManager userManager;
    private DataManager dataManager;
    private CircleImageView profileAvatar;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
        setContentView(R.layout.activity_menu);

        initializeViews();
        userManager = new UserManager();
        dataManager = DataManager.getInstance();
        currentUser = userManager.getCurrentUser();

        if (currentUser != null) {
            setName();
            setProfileImage();
        }

        generateAppeal.setOnClickListener(v -> moveToGenerateAppealActivity());
        monitorAppeals.setOnClickListener(v -> moveToMonitorActivity());
        appealsHistory.setOnClickListener(v -> moveToAppealsHistoryActivity());
        manageProfile.setOnClickListener(v -> moveToProfileActivity());
        logOutButton.setOnClickListener(v -> moveToStartActivity());
    }

    private void setupFullScreenMode() {
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

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in, redirect to login screen
            moveToStartActivity();
        } else {
            setName();
            setProfileImage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProfileImage();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeViews() {
        generateAppeal = findViewById(R.id.menu_LBL_generateAppeal);
        monitorAppeals = findViewById(R.id.menu_LBL_monitorAppeals);
        appealsHistory = findViewById(R.id.menu_LBL_appealsHistory);
        manageProfile = findViewById(R.id.menu_LBL_manageProfile);
        logOutButton = findViewById(R.id.menu_BTN_logout);
        profileAvatar = findViewById(R.id.menu_IMG_profileAvatar);
        profileName = findViewById(R.id.menu_LBL_helloUser);
    }

    private void setProfileImage() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            dataManager.getUserData(uid, new DataManager.CallBack<User>() {
                @Override
                public void res(User user) {
                    if (user != null && user.getProfileImage() != null) {
                        Glide.with(MenuActivity.this)
                                .load(user.getProfileImage())
                                .placeholder(R.drawable.img_default_profile_image)
                                .centerCrop()
                                .into(profileAvatar);
                    } else {
                        profileAvatar.setImageResource(R.drawable.img_default_profile_image);
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
