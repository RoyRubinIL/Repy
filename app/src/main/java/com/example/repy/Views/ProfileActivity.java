package com.example.repy.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.Address;
import com.example.repy.Models.City;
import com.example.repy.Models.Country;
import com.example.repy.Models.User;
import com.example.repy.R;
import com.example.repy.Utilities.ApiData;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton returnButton, editButton;
    private ShapeableImageView profileImage;
    private TextInputEditText idField, nameField, streetField, streetNumberField, emailField, passwordField;
    private MaterialButton addProfileImageButton, confirmChangeButton;
    private MaterialTextView editModeText;
    private Spinner countrySpinner, citySpinner;
    private DataManager dataManager;
    private UserManager userManager;
    private ApiData apiData;
    private User currentUser;

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
        setContentView(R.layout.activity_profile);

        initializeViews();
        apiData = new ApiData();
        dataManager = DataManager.getInstance();
        userManager = new UserManager();

        loadUserDetails();

        returnButton.setOnClickListener(v -> returnToMainMenu());
        editButton.setOnClickListener(v -> enterEditMode());
        confirmChangeButton.setOnClickListener(v -> confirmChanges());
    }

    private void initializeViews(){
        profileImage = findViewById(R.id.profile_IMG_avatar);
        returnButton = findViewById(R.id.button_return);
        editButton = findViewById(R.id.button_edit);
        idField = findViewById(R.id.sign_up_id);
        nameField = findViewById(R.id.sign_up_name);
        streetField = findViewById(R.id.sign_up_address_street);
        streetNumberField = findViewById(R.id.sign_up_address_street_number);
        emailField = findViewById(R.id.sign_up_email);
        passwordField = findViewById(R.id.sign_up_password);
        countrySpinner = findViewById(R.id.country_spinner);
        citySpinner = findViewById(R.id.city_spinner);
        addProfileImageButton = findViewById(R.id.add_profile_image);
        confirmChangeButton = findViewById(R.id.confirm_change_button);
        editModeText = findViewById(R.id.edit_mode_text);
    }

    private void loadUserDetails() {
        String uid = userManager.getCurrentUserId();
        if (uid != null) {
            dataManager.getUserData(uid, user -> {
                if (user != null) {
                    currentUser = user;

                    // Load and display the user's profile image
                    if (currentUser.getProfileImage() != null) {
                        dataManager.loadProfileImage(this, currentUser.getProfileImage(), profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.img_default_profile_image);
                    }

                    idField.setText(currentUser.getId());
                    nameField.setText(currentUser.getName());
                    streetField.setText(currentUser.getAddress().getStreet());
                    streetNumberField.setText(String.valueOf(currentUser.getAddress().getStreetNum()));
                    emailField.setText(currentUser.getEmail());
                    passwordField.setText(currentUser.getPassword());
//                    setupSpinnersWithUserData();
                }
            });
        }
    }

//    private void setupSpinnersWithUserData() {
//        apiData.setupCountrySpinner(this, countrySpinner, citySpinner, null, () -> {
//            setSpinnerSelection(countrySpinner, currentUser.getAddress().getCountry().getName());
//            apiData.loadCities(currentUser.getAddress().getCountry().getName(), citySpinner, () -> {
//                setSpinnerSelection(citySpinner, currentUser.getAddress().getCity().getName());
//            });
//        });
//    }
//
//    private void setSpinnerSelection(Spinner spinner, String value) {
//        for (int i = 0; i < spinner.getCount(); i++) {
//            if (spinner.getItemAtPosition(i).equals(value)) {
//                spinner.setSelection(i);
//                break;
//            }
//        }
//    }

    private void enterEditMode() {
        setEditModeEnabled(true);
        editModeText.setVisibility(View.VISIBLE);
        addProfileImageButton.setVisibility(View.VISIBLE);
        confirmChangeButton.setVisibility(View.VISIBLE);
    }

    private void setEditModeEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        nameField.setFocusableInTouchMode(enabled);
        streetField.setEnabled(enabled);
        streetField.setFocusableInTouchMode(enabled);
        streetNumberField.setEnabled(enabled);
        streetNumberField.setFocusableInTouchMode(enabled);
        emailField.setEnabled(enabled);
        emailField.setFocusableInTouchMode(enabled);
        passwordField.setEnabled(enabled);
        passwordField.setFocusableInTouchMode(enabled);
        countrySpinner.setEnabled(enabled);
        countrySpinner.setClickable(enabled);
        citySpinner.setEnabled(enabled);
        citySpinner.setClickable(enabled);
    }

    private void confirmChanges() {
        try {
            String name = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String street = streetField.getText().toString().trim();
            String streetNumberStr = streetNumberField.getText().toString().trim();
            int streetNumber = Integer.parseInt(streetNumberStr);
            String countryName = countrySpinner.getSelectedItem().toString();
            String cityName = citySpinner.getSelectedItem().toString();

            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPassword(password);

            Address address = currentUser.getAddress();
            address.setStreet(street);
            address.setStreetNum(streetNumber);

            Country country = apiData.getCountry(countryName);
            City city = apiData.getCity(countryName, cityName);

            if (country != null && city != null) {
                address.setCountry(country);
                address.setCity(city);

                String newProfileImageUrl = "new_image_url"; // Replace with actual URL logic if image changes

                currentUser.setProfileImage(newProfileImageUrl);

                dataManager.storeUserData(currentUser, currentUser.getId(), success -> {
                    if (success) {
                        if (newProfileImageUrl != null) {
                            dataManager.loadProfileImage(ProfileActivity.this, newProfileImageUrl, profileImage);
                        }

                        Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        setEditModeEnabled(false);
                        editModeText.setVisibility(View.INVISIBLE);
                        addProfileImageButton.setVisibility(View.GONE);
                        confirmChangeButton.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Invalid country or city selection", Toast.LENGTH_LONG).show();
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void returnToMainMenu(){
        Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
