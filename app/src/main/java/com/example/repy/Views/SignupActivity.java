package com.example.repy.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.Address;
import com.example.repy.Models.User;
import com.example.repy.R;
import com.example.repy.Utilities.ApiData;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner countrySpinner, citySpinner;
    private TextInputEditText passwordEditText, signUpEmail, signUpAddressStreetNum, signUpAddressStreet, signUpId, signUpName;
    private MaterialButton signUpButton, addProfileImage;
    private TextView loginRedirectText;
    private TextView countryFlagTextView;
    private Address userAddress;
    private User newUser;
    private ShapeableImageView profile_IMG_avatar;
    private String profileImage = "";
    private ApiData apiData;
    private UserManager userManager;
    private DataManager dataManager;

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
        setContentView(R.layout.activity_signup);

        initializeViews();

        apiData = new ApiData();
        userManager = new UserManager();
        dataManager = DataManager.getInstance();
        apiData.setupCountrySpinner(this, countrySpinner, citySpinner, countryFlagTextView);

        signUpButton.setOnClickListener(v -> createUser());
        loginRedirectText.setOnClickListener(v -> redirectToLogin());
        addProfileImage.setOnClickListener(v -> openGallery());
    }

    private void initializeViews() {
        countrySpinner = findViewById(R.id.country_spinner);
        citySpinner = findViewById(R.id.city_spinner);
        passwordEditText = findViewById(R.id.sign_up_password);
        signUpButton = findViewById(R.id.sign_up_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        addProfileImage = findViewById(R.id.add_profile_image);
        countryFlagTextView = findViewById(R.id.country_flag_text);
        signUpEmail = findViewById(R.id.sign_up_email);
        signUpAddressStreetNum = findViewById(R.id.sign_up_address_street_number);
        signUpAddressStreet = findViewById(R.id.sign_up_address_street);
        signUpId = findViewById(R.id.sign_up_id);
        signUpName = findViewById(R.id.sign_up_name);
        profile_IMG_avatar = findViewById(R.id.profile_IMG_avatar);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profile_IMG_avatar.setImageBitmap(bitmap);
                profileImage = imageUri.toString(); // Store the URI string for later use
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createUser() {
        String userID = signUpId.getText().toString().trim();
        String email = signUpEmail.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = signUpName.getText().toString().trim();
        String streetNumStr = signUpAddressStreetNum.getText().toString().trim();
        String street = signUpAddressStreet.getText().toString().trim();
        String countryName = countrySpinner.getSelectedItem().toString();
        String cityName = citySpinner.getSelectedItem().toString();

        boolean isValid = validateInput(userID, email, password, name, streetNumStr, street, countryName, cityName);
        if (!isValid) return;

        userAddress = new Address(apiData.getCountry(countryName), apiData.getCity(countryName, cityName), street, Integer.parseInt(streetNumStr));
        newUser = new User(null, userID, name, userAddress, email, password, profileImage);

        userManager.createUser(email, password, this, new UserManager.OnUserCreationListener() {
            @Override
            public void onUserCreated(String uid) {
                newUser.setUid(uid);  // Set the UID to the new user object
                dataManager.storeUserData(newUser, uid, res -> {
                    if (res) {
                        Toast.makeText(SignupActivity.this, "User data saved successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(SignupActivity.this, "Sign up failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String userID, String email, String password, String name, String streetNumStr, String street, String countryName, String cityName) {
        boolean isValid = true;

        if (userID.isEmpty()) {
            signUpId.setError("ID is required");
            isValid = false;
        }

        if (email.isEmpty()) {
            signUpEmail.setError("Email is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            isValid = false;
        }

        if (name.isEmpty()) {
            signUpName.setError("Name is required");
            isValid = false;
        }

        if (streetNumStr.isEmpty()) {
            signUpAddressStreetNum.setError("Street number is required");
            isValid = false;
        } else {
            try {
                Integer.parseInt(streetNumStr);
            } catch (NumberFormatException e) {
                signUpAddressStreetNum.setError("Invalid street number");
                isValid = false;
            }
        }

        if (street.isEmpty()) {
            signUpAddressStreet.setError("Street is required");
            isValid = false;
        }

        if ("Select Country".equals(countryName)) {
            TextView errorText = (TextView) countrySpinner.getSelectedView();
            errorText.setError("Country is required");
            isValid = false;
        }

        if ("Select City".equals(cityName)) {
            TextView errorText = (TextView) citySpinner.getSelectedView();
            errorText.setError("City is required");
            isValid = false;
        }

        return isValid;
    }
}
