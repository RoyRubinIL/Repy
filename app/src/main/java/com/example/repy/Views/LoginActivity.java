package com.example.repy.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.R;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton logInButton;
    private TextView signUpRedirectText;
    private EditText emailField, passwordField;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
        setContentView(R.layout.activity_login);

        initializeViews();
        userManager = new UserManager();

        checkIfUserAlreadyLogIn();

        logInButton.setOnClickListener(v -> login());
        signUpRedirectText.setOnClickListener(v -> redirectToSignup());
    }

    private void checkIfUserAlreadyLogIn() {
        if (userManager.isUserLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }
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

    private void initializeViews() {
        logInButton = findViewById(R.id.login_BTN_logIn);
        signUpRedirectText = findViewById(R.id.login_LBL_signUpRedirect);
        emailField = findViewById(R.id.login_EDT_email);
        passwordField = findViewById(R.id.login_EDT_password);
    }


    private void login() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty()) {
            emailField.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            return;
        }

        userManager.loginUser(email, password, this, new UserManager.OnLoginListener() {
            @Override
            public void onLoginSuccess(String uid) {
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onLoginFailure(Exception e) {
                passwordField.setError(e.getMessage());
            }
        });
    }

    private void redirectToSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }
}
