package com.example.repy.Views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.repy.Models.Address;
import com.example.repy.Models.User;
import com.example.repy.R;
import com.example.repy.Utilities.ApiData;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.StorageManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.tashila.pleasewait.PleaseWaitDialog;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final int IMAGE_PICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST = 400;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;


    private Uri imageUri;

    private Spinner countrySpinner, citySpinner;
    private TextInputEditText passwordEditText, signUpEmail, signUpAddressStreetNum, signUpAddressStreet, signUpId, signUpName;
    private MaterialButton signUpButton, addProfileImage;
    private TextView loginRedirectText, countryFlagTextView;
    private Address userAddress;
    private User newUser;
    private CircleImageView profile_IMG_avatar;
    private String profileImageUrl = "";
    private ApiData apiData;
    private UserManager userManager;
    private DataManager dataManager;
    private StorageManager storageManager;
    private PleaseWaitDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
        setContentView(R.layout.activity_signup);

        initializeViews();

        apiData = new ApiData();
        userManager = new UserManager();
        dataManager = DataManager.getInstance();
        storageManager = StorageManager.getInstance(this);
        progressDialog = new PleaseWaitDialog(this);

        apiData.setupCountrySpinner(this, countrySpinner, citySpinner, countryFlagTextView);

        signUpButton.setOnClickListener(v -> createUser());
        loginRedirectText.setOnClickListener(v -> redirectToLogin());
        addProfileImage.setOnClickListener(v -> showImagePickDialog());
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
        countrySpinner = findViewById(R.id.signup_LST_countrySpinner);
        citySpinner = findViewById(R.id.signup_LST_citySpinner);
        passwordEditText = findViewById(R.id.signup_LBL_password);
        signUpButton = findViewById(R.id.signup_BTN_submit);
        loginRedirectText = findViewById(R.id.signup_LBL_loginRedirect);
        addProfileImage = findViewById(R.id.signup_BTN_addProfileImage);
        countryFlagTextView = findViewById(R.id.signup_LBL_countryFlag);
        signUpEmail = findViewById(R.id.signup_LBL_email);
        signUpAddressStreetNum = findViewById(R.id.signup_LBL_streetNumber);
        signUpAddressStreet = findViewById(R.id.signup_LBL_street);
        signUpId = findViewById(R.id.signup_LBL_id);
        signUpName = findViewById(R.id.signup_LBL_name);
        profile_IMG_avatar = findViewById(R.id.signup_IMG_avatar);
    }


    private void redirectToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST) {
                imageUri = data.getData();
                profile_IMG_avatar.setImageURI(imageUri);
            } else if (requestCode == IMAGE_PICK_CAMERA_REQUEST) {
                profile_IMG_avatar.setImageURI(imageUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileImage(final Uri uri) {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        storageManager.uploadProfileImage(userManager.getCurrentUser().getUid(), uri, imageUrl -> {
            progressDialog.dismiss();
            if (imageUrl != null) {
                profileImageUrl = imageUrl;
                profile_IMG_avatar.setImageURI(uri);
                Log.i(TAG, "Profile image uploaded successfully");

                // Update the user object with the new profile image URL and save it to the database
                newUser.setProfileImage(profileImageUrl);
                dataManager.updateUserImage(newUser.getUid(), profileImageUrl);
            } else {
                Log.e(TAG, "Failed to upload profile image");
            }
            proceedToMenuActivity(); // Proceed to the next activity regardless of the result
        });
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

        User tempUser = new User();

        String emailError = tempUser.validateEmail(email);
        if (emailError != null) {
            signUpEmail.setError(emailError);
            return;
        }

        String passwordError = tempUser.validatePassword(password);
        if (passwordError != null) {
            passwordEditText.setError(passwordError);
            return;
        }

        boolean isValid = validateInput(userID, email, password, name, streetNumStr, street, countryName, cityName);
        if (!isValid) return;

        userAddress = new Address(apiData.getCountry(countryName), apiData.getCity(countryName, cityName), street, Integer.parseInt(streetNumStr));
        newUser = new User(null, userID, name, userAddress, email, password, profileImageUrl);

        userManager.createUser(email, password, this, new UserManager.OnUserCreationListener() {
            @Override
            public void onUserCreated(String uid) {
                newUser.setUid(uid);
                dataManager.storeUserData(newUser, uid, res -> {
                    if (res) {
                        Log.i(TAG, "User data saved successfully!");
                        // Now upload the profile image if one was selected
                        if (imageUri != null) {
                            uploadProfileImage(imageUri);
                        } else {
                            proceedToMenuActivity();
                        }
                    } else {
                        Log.e(TAG, "Failed to save user data.");
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Sign up failed: " + exception.getMessage());
            }
        });
    }

    private void proceedToMenuActivity() {
        Intent intent = new Intent(SignupActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
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
