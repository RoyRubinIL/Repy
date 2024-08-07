package com.example.repy.Views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.City;
import com.example.repy.Models.Country;
import com.example.repy.Models.CountryCurrencyResponse;
import com.example.repy.Models.CountryFlagResponse;
import com.example.repy.Models.CountryResponse;
import com.example.repy.Network.ApiClient;
import com.example.repy.Network.ApiService;
import com.example.repy.Network.CountryRequest;
import com.example.repy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketActivity extends AppCompatActivity {

    private static final String TAG = "TicketActivity";

    private TextInputEditText dateOfIssueEditText, carNumberEditText, causeEditText;
    private MaterialTextView mirrorPlate;
    private Spinner countrySpinner, citySpinner;
    private TextView countryFlagTextView, currencyTextView;
    private ApiService apiService;
    private List<Country> countryList;
    private List<String> cityList;

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
        setContentView(R.layout.activity_ticket);

        dateOfIssueEditText = findViewById(R.id.date_of_issue);
        carNumberEditText = findViewById(R.id.car_number);
        causeEditText = findViewById(R.id.multiLineTextInputEditText);
        mirrorPlate = findViewById(R.id.car_num_mirror);
        countrySpinner = findViewById(R.id.country_spinner);
        citySpinner = findViewById(R.id.city_spinner);
        currencyTextView = findViewById(R.id.currency_text);
        countryFlagTextView = findViewById(R.id.country_flag_text);

        apiService = ApiClient.getClient().create(ApiService.class);

        dateOfIssueEditText.setOnClickListener(v -> showDatePickerDialog());
        setupCountrySpinner();
        setupCarNumberEditText();
        setupCauseEditText();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                TicketActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                    dateOfIssueEditText.setText(date);
                },
                year, month, day);

        // Set the maximum date to the current date
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        // Set the minimum date to 2 years ago
        calendar.add(Calendar.YEAR, -2);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void setupCarNumberEditText() {
        carNumberEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(10)});
        carNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String carNumber = s.toString();
                if (!carNumber.matches("[A-Z0-9-]*")) {
                    carNumberEditText.setError("Invalid car number format. Only uppercase letters, numbers, and '-' are allowed.");
                }
                else
                    mirrorPlate.setText(carNumber);
            }

            @Override
            public void afterTextChanged(Editable s) {
                mirrorPlate.setText(s.toString());
            }
        });
    }

    private void setupCauseEditText() {
        causeEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(256)});
        causeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                causeEditText.setHint(String.format("Cause (%d/256)", length));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
                    Toast.makeText(TicketActivity.this, "Failed to load countries: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(TicketActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    citySpinner.setEnabled(true);
                    loadCities(countrySpinner.getSelectedItem().toString());
                    loadCurrency(countrySpinner.getSelectedItem().toString());
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
                cityList = country.getCities();
                setupCitySpinner(cityList);
                break;
            }
        }
    }

    private void setupCitySpinner(List<String> cityList) {
        List<String> cityNames = new ArrayList<>();
        cityNames.add("Select City");
        cityNames.addAll(cityList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
    }

    private void loadCurrency(String countryName) {
        String iso2 = null;
        for (Country country : countryList) {
            if (country.getName().equals(countryName)) {
                iso2 = country.getIso2();
                break;
            }
        }
        if (iso2 != null) {
            apiService.getCountryCurrency(new CountryRequest(iso2)).enqueue(new Callback<CountryCurrencyResponse>() {
                @Override
                public void onResponse(Call<CountryCurrencyResponse> call, Response<CountryCurrencyResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CountryCurrencyResponse.CurrencyData currencyData = response.body().getData();
                        if (currencyData != null) {
                            String currency = currencyData.getCurrency();
                            currencyTextView.setText(currency);
                        } else {
                            Log.e(TAG, "No currency data found for " + countryName);
                            Toast.makeText(TicketActivity.this, "No currency data found for " + countryName, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Failed to load currency: " + response.message());
                        Toast.makeText(TicketActivity.this, "Failed to load currency: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CountryCurrencyResponse> call, Throwable t) {
                    Log.e(TAG, "Error: " + t.getMessage());
                    Toast.makeText(TicketActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
                    Toast.makeText(TicketActivity.this, "No flag data found for " + countryName, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to load country flag: " + response.message());
                    Toast.makeText(TicketActivity.this, "Failed to load country flag: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryFlagResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(TicketActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
