package com.example.repy.Views;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.repy.Models.Address;
import com.example.repy.Models.City;
import com.example.repy.Models.Country;
import com.example.repy.Models.User;
import com.example.repy.R;
import com.example.repy.Utilities.ApiData;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.StorageManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private ImageButton returnButton, editButton;
    private CircleImageView profileImage;
    private TextInputEditText idField, nameField, streetField, streetNumberField, emailField, passwordField;
    private TextView countryFlag;
    private MaterialButton addProfileImageButton, confirmChangeButton;
    private MaterialTextView editModeText, myProfileText;
    private Spinner countrySpinner, citySpinner;

    private DataManager dataManager;
    private UserManager userManager;
    private StorageManager storageManager;
    private ApiData apiData;
    private User currentUser;
    private Uri imageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    private static final String TAG = "ProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreen();
        setContentView(R.layout.activity_profile);

        initializeViews();
        initializeManagers();
        setupImagePickerLauncher();
        loadUserDetails();
        setupButtonListeners();
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

    private void initializeViews() {
        profileImage = findViewById(R.id.profile_IMG_avatar);
        returnButton = findViewById(R.id.profile_BTN_return);
        editButton = findViewById(R.id.profile_BTN_edit);
        idField = findViewById(R.id.profile_LBL_id);
        nameField = findViewById(R.id.profile_LBL_name);
        countryFlag = findViewById(R.id.profile_LBL_countryFlag);
        streetField = findViewById(R.id.profile_LBL_street);
        streetNumberField = findViewById(R.id.profile_LBL_streetNumber);
        emailField = findViewById(R.id.profile_LBL_email);
        passwordField = findViewById(R.id.profile_LBL_password);
        countrySpinner = findViewById(R.id.profile_LST_countrySpinner);
        citySpinner = findViewById(R.id.profile_LST_citySpinner);
        addProfileImageButton = findViewById(R.id.profile_BTN_addProfileImage);
        confirmChangeButton = findViewById(R.id.profile_BTN_confirmChange);
        editModeText = findViewById(R.id.profile_LBL_editMode);
        myProfileText = findViewById(R.id.profile_LBL_text);
    }

    private void initializeManagers() {
        apiData = new ApiData();
        dataManager = DataManager.getInstance();
        userManager = new UserManager();
        storageManager = StorageManager.getInstance(this);
    }

    private void setupImagePickerLauncher() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        updateProfileImg(imageUri);
                        //uploadProfileImage(imageUri);
                    }
                });
    }

    private void loadUserDetails() {
        String uid = userManager.getCurrentUserUid();
        if (uid != null) {
            dataManager.getUserData(uid, user -> {
                if (user != null) {
                    currentUser = user;
                    populateUserDetails();
                    setupSpinnersWithUserData();
                    setProfileImage();
                }
            });
        }
    }

    private void populateUserDetails() {
        idField.setText(currentUser.getId());
        nameField.setText(currentUser.getName());
        streetField.setText(currentUser.getAddress().getStreet());
        streetNumberField.setText(String.valueOf(currentUser.getAddress().getStreetNum()));
        emailField.setText(currentUser.getEmail());
        passwordField.setText(currentUser.getPassword());
    }

    private void setupButtonListeners() {
        returnButton.setOnClickListener(v -> returnToMainMenu());
        editButton.setOnClickListener(v -> enterEditMode());
        confirmChangeButton.setOnClickListener(v -> confirmChanges(imageUri));
        addProfileImageButton.setOnClickListener(v -> showImagePickDialog());
    }

    private void confirmChanges(Uri imageUri) {
        try {
            if (validateProfileInputs()) {
                updateUserDetails();
                saveUserData();
                uploadProfileImage(imageUri);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Validation failed: " + e.getMessage());
        }
    }

    public void setProfileImage() {
        String imageUrl = currentUser.getProfileImage();
        if (imageUrl != null) {
            Glide.with(ProfileActivity.this)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_default_profile_image)
                    .centerCrop()
                    .into(profileImage);
        }
    }

    public void updateProfileImg(Uri uri) {
        Glide.with(ProfileActivity.this)
                .load(uri)
                .placeholder(R.drawable.img_default_profile_image)
                .centerCrop()
                .into(profileImage);
    }

    private void uploadProfileImage(Uri uri) {
        String uid = userManager.getCurrentUserUid();
        currentUser.setProfileImage(uri.toString());
        if (uid != null) {
            storageManager.uploadProfileImage(uid, uri, imageUrl -> {
                if (imageUrl != null) {
                    dataManager.updateUserImage(uid, imageUrl);
                    Log.d(TAG, "Profile image updated successfully!");
                } else {
                    Log.e(TAG, "Failed to upload profile image.");
                }
            });
        } else {
            Log.e(TAG, "User not logged in.");
        }
    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (checkCameraPermission()) {
                    pickFromCamera();
                }
            } else if (which == 1) {
                if (checkStoragePermission()) {
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            return false;
        }
    }

    private boolean checkStoragePermission() {
        String[] permissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromCamera();
                } else {
                    Log.e(TAG, "Camera Permission Denied");
                }
                break;
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                } else {
                    Log.e(TAG, "Storage Permission Denied");
                }
                break;
        }
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Profile Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        Uri newProfileImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, newProfileImageUri);
        pickImageLauncher.launch(cameraIntent);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(galleryIntent);
    }

    private boolean validateProfileInputs() {
        boolean isValid = true;
        User emptyUser = new User();

        // Get the input values
        String userID = idField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String name = nameField.getText().toString().trim();
        String streetNumStr = streetNumberField.getText().toString().trim();
        String street = streetField.getText().toString().trim();
        String countryName = countrySpinner.getSelectedItem().toString();
        String cityName = citySpinner.getSelectedItem().toString();

        // Validate ID
        if (userID.isEmpty()) {
            idField.setError("ID is required");
            isValid = false;
        }

        // Validate Email
        if (email.isEmpty()) {
            emailField.setError("Email is required");
            isValid = false;
        }

        String emailError = emptyUser.validateEmail(email);
        if (emailError != null) {
            emailField.setError(emailError);
            isValid = false;
        }

        // Validate Password
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            isValid = false;
        }

        String passwordError = emptyUser.validatePassword(password);
        if (passwordError != null) {
            passwordField.setError(passwordError);
            isValid = false;
        }

        // Validate Name
        if (name.isEmpty()) {
            nameField.setError("Name is required");
            isValid = false;
        }

        // Validate Street Number
        if (streetNumStr.isEmpty()) {
            streetNumberField.setError("Street number is required");
            isValid = false;
        } else {
            try {
                Integer.parseInt(streetNumStr);
            } catch (NumberFormatException e) {
                streetNumberField.setError("Invalid street number");
                isValid = false;
            }
        }

        // Validate Street
        if (street.isEmpty()) {
            streetField.setError("Street is required");
            isValid = false;
        }

        // Validate Country
        if ("Select Country".equals(countryName)) {
            TextView errorText = (TextView) countrySpinner.getSelectedView();
            errorText.setError("Country is required");
            isValid = false;
        }

        // Validate City
        if ("Select City".equals(cityName)) {
            TextView errorText = (TextView) citySpinner.getSelectedView();
            errorText.setError("City is required");
            isValid = false;
        }



        return isValid;
    }


    private void updateUserDetails() {
        currentUser.setName(nameField.getText().toString().trim());
        currentUser.setEmail(emailField.getText().toString().trim());
        currentUser.setPassword(passwordField.getText().toString().trim());

        Address address = currentUser.getAddress();
        address.setStreet(streetField.getText().toString().trim());
        address.setStreetNum(Integer.parseInt(streetNumberField.getText().toString().trim()));

        Country country = apiData.getCountry(countrySpinner.getSelectedItem().toString());
        City city = apiData.getCity(country.getName(), citySpinner.getSelectedItem().toString());

        address.setCountry(country);
        address.setCity(city);
    }

    private void saveUserData() {
        dataManager.storeUserData(currentUser, currentUser.getUid(), success -> {
            if (success) {
                Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                exitEditMode();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinnersWithUserData() {
        apiData.setupCountrySpinnerWithSelection(this, countrySpinner, citySpinner, countryFlag, currentUser.getAddress().getCountry().getName(), () -> {
            apiData.loadCitiesWithSelection(currentUser.getAddress().getCountry().getName(), citySpinner, currentUser.getAddress().getCity().getName(), () -> {
                apiData.loadCountryFlag(currentUser.getAddress().getCountry().getName(), countryFlag);
                setSpinnersEnabled(false);
            });
        });
    }

    private void setUpSpinnerEditMode() {
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCountry = countrySpinner.getSelectedItem().toString();
                    apiData.loadCities(selectedCountry, citySpinner);
                    apiData.loadCountryFlag(selectedCountry, countryFlag);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no country is selected, if necessary
            }
        });
    }

    private void setSpinnersEnabled(boolean enabled) {
        countrySpinner.setEnabled(enabled);
        citySpinner.setEnabled(enabled);
    }

    private void enterEditMode() {
        setEditModeEnabled(true);
        setUpSpinnerEditMode();
        setSpinnersEnabled(true);

        findViewById(R.id.profile_LAY_main).setBackgroundColor(getResources().getColor(R.color.deep_blue, getTheme()));
        myProfileText.setTextColor(getResources().getColor(R.color.deep_blue, getTheme()));

        editModeText.setVisibility(View.VISIBLE);
        addProfileImageButton.setVisibility(View.VISIBLE);
        confirmChangeButton.setVisibility(View.VISIBLE);
        returnButton.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.INVISIBLE);
    }

    private void exitEditMode() {
        setEditModeEnabled(false);
        setSpinnersEnabled(false);

        findViewById(R.id.profile_LAY_main).setBackgroundColor(getResources().getColor(R.color.light_blue, getTheme()));
        myProfileText.setTextColor(getResources().getColor(R.color.light_blue, getTheme()));

        editModeText.setVisibility(View.INVISIBLE);
        addProfileImageButton.setVisibility(View.GONE);
        confirmChangeButton.setVisibility(View.GONE);
        returnButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
    }

    private void setEditModeEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        streetField.setEnabled(enabled);
        streetNumberField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        nameField.setFocusableInTouchMode(enabled);
        streetField.setFocusableInTouchMode(enabled);
        streetNumberField.setFocusableInTouchMode(enabled);
        emailField.setFocusableInTouchMode(enabled);
        passwordField.setFocusableInTouchMode(enabled);
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
        startActivity(intent);
    }
}
