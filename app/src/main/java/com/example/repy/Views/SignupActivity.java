package com.example.repy.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.Address;
import com.example.repy.Models.City;
import com.example.repy.Models.Country;
import com.example.repy.Models.CountryResponse;
import com.example.repy.Models.User;
import com.example.repy.Network.ApiClient;
import com.example.repy.Network.ApiService;
import com.example.repy.Models.CountryFlagResponse;
import com.example.repy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner countrySpinner, citySpinner;
    private EditText passwordEditText, signUpEmail, signUpAddressStreetNum, signUpAddressStreet, signUpId, signUpName;
    private ImageButton showPasswordButton;
    private MaterialButton signUpButton, addProfileImage;
    private TextView loginRedirectText;
    private TextView countryFlagTextView;
    private ApiService apiService;
    private boolean isPasswordVisible = false;
    private Address userAddress;
    private User newUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private List<Country> countryList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();

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

        apiService = ApiClient.getClient().create(ApiService.class);

        setupCountrySpinner();

        showPasswordButton.setOnClickListener(v -> togglePasswordVisibility());
        signUpButton.setOnClickListener(v -> createUser());
        loginRedirectText.setOnClickListener(v -> redirectToLogin());
        addProfileImage.setOnClickListener(v -> openGallery());
    }

    private void initializeViews() {
        countrySpinner = findViewById(R.id.country_spinner);
        citySpinner = findViewById(R.id.city_spinner);
        passwordEditText = findViewById(R.id.sign_up_password);
        showPasswordButton = findViewById(R.id.show_password_button);
        signUpButton = findViewById(R.id.sign_up_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        addProfileImage = findViewById(R.id.add_profile_image);
        countryFlagTextView = findViewById(R.id.country_flag_text);
        signUpEmail = findViewById(R.id.sign_up_email);
        signUpAddressStreetNum = findViewById(R.id.sign_up_address_street_number);
        signUpAddressStreet = findViewById(R.id.sign_up_address_street);
        signUpId = findViewById(R.id.sign_up_id);
        signUpName = findViewById(R.id.sign_up_name);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            showPasswordButton.setImageResource(R.drawable.ic_show_pass);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            showPasswordButton.setImageResource(R.drawable.ic_hide_pass);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
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
                ShapeableImageView profileImageView = findViewById(R.id.profile_IMG_avatar);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupCountrySpinner() {
        List<String> countryNames = new ArrayList<>();
        countryNames.add("Select Country");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        loadCountries(adapter);
    }

    private void loadCountries(ArrayAdapter<String> adapter) {
        apiService.getCountries().enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    countryList = response.body().getCountries();
                    for (Country country : countryList) {
                        adapter.add(country.getName());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Failed to load countries: " + response.message());
                    Toast.makeText(SignupActivity.this, "Failed to load countries: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    citySpinner.setEnabled(true);
                    loadCities(countrySpinner.getSelectedItem().toString());
                    loadCountryFlag(countrySpinner.getSelectedItem().toString());
                } else {
                    citySpinner.setEnabled(false);
                    setupCitySpinner(new ArrayList<>());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                citySpinner.setEnabled(false);
                setupCitySpinner(new ArrayList<>());
            }
        });
    }

    private void loadCities(String countryName) {
        for (Country country : countryList) {
            if (country.getName().equals(countryName)) {
                cityList = new ArrayList<>();
                for (String cityName : country.getCities()) {
                    City city = new City();
                    city.setName(cityName);
                    cityList.add(city);
                }
                setupCitySpinner(cityList);
                break;
            }
        }
    }

    private void loadCountryFlag(String countryName) {
        apiService.getCountryFlag(countryName).enqueue(new Callback<CountryFlagResponse>() {
            @Override
            public void onResponse(Call<CountryFlagResponse> call, Response<CountryFlagResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CountryFlagResponse.CountryFlagData> flagDataList = response.body().getData();
                    for (CountryFlagResponse.CountryFlagData flagData : flagDataList) {
                        if (flagData.getName().equalsIgnoreCase(countryName)) {
                            String unicodeFlag = flagData.getUnicodeFlag();
                            Log.d(TAG, "Unicode flag for " + countryName + ": " + unicodeFlag);
                            countryFlagTextView.setText(unicodeFlag);
                            return;
                        }
                    }
                    Log.e(TAG, "No flag data found for " + countryName);
                    Toast.makeText(SignupActivity.this, "No flag data found for " + countryName, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to load country flag: " + response.message());
                    Toast.makeText(SignupActivity.this, "Failed to load country flag: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryFlagResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCitySpinner(List<City> cityList) {
        List<String> cityNames = new ArrayList<>();
        cityNames.add("Select City");
        for (City city : cityList) {
            cityNames.add(city.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
    }

    private void createUser() {
        try {
            String userID = signUpId.getText().toString().trim();
            String email = signUpEmail.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String name = signUpName.getText().toString().trim();
            String streetNumStr = signUpAddressStreetNum.getText().toString().trim();
            String street = signUpAddressStreet.getText().toString().trim();
            String countryName = countrySpinner.getSelectedItem().toString();
            String cityName = citySpinner.getSelectedItem().toString();

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

            int streetNum = 0;
            if (streetNumStr.isEmpty()) {
                signUpAddressStreetNum.setError("Street number is required");
                isValid = false;
            } else {
                try {
                    streetNum = Integer.parseInt(streetNumStr);
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

            if (!isValid) {
                return;
            }

            Country selectedCountry = null;
            for (Country country : countryList) {
                if (country.getName().equals(countryName)) {
                    selectedCountry = country;
                    break;
                }
            }

            City selectedCity = null;
            for (City city : cityList) {
                if (city.getName().equals(cityName)) {
                    selectedCity = city;
                    break;
                }
            }

            if (selectedCountry == null || selectedCity == null) {
                throw new Exception("Invalid country or city selection");
            }

            userAddress = new Address(selectedCountry, selectedCity, street, streetNum);
            newUser = new User(userID, name, userAddress, email, password);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");
            reference.child(String.valueOf(newUser.getId())).setValue(newUser);

            Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Password")) {
                passwordEditText.setError(e.getMessage());
            } else {
                Log.e(TAG, "Error creating user: " + e.getMessage());
                Toast.makeText(this, "Error creating user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating user: " + e.getMessage());
            Toast.makeText(this, "Error creating user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
