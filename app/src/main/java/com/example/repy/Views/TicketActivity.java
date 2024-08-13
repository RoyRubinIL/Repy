package com.example.repy.Views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.Address;
import com.example.repy.Models.City;
import com.example.repy.Models.Country;
import com.example.repy.Models.Ticket;
import com.example.repy.Models.TicketType;
import com.example.repy.R;
import com.example.repy.Utilities.ApiData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TicketActivity extends AppCompatActivity {

    private static final String TAG = "TicketActivity";

    private TextInputEditText dateOfIssueEditText, ticketIdEditText, carNumberEditText, causeEditText, amountEditText, streetEditText, streetNumberEditText;
    private MaterialTextView mirrorPlate, currencyTextView;
    private Spinner countrySpinner, citySpinner;
    private TextView countryFlagTextView;
    private MaterialButton submitButton;
    private TicketType ticketType;

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

        initializeViews();

        ticketType = (TicketType) getIntent().getSerializableExtra("ticketType");
        ApiData apiData = new ApiData();
        apiData.setupCountrySpinner(this, countrySpinner, citySpinner, countryFlagTextView);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    citySpinner.setEnabled(true);
                    String selectedCountry = countrySpinner.getSelectedItem().toString();
                    apiData.loadCities(selectedCountry, citySpinner);
                    apiData.loadCountryFlag(selectedCountry, countryFlagTextView);
                    apiData.loadCurrency(TicketActivity.this, selectedCountry, currencyTextView);
                } else {
                    citySpinner.setEnabled(false);
                    apiData.setupCitySpinner(TicketActivity.this, new ArrayList<>(), citySpinner);
                    currencyTextView.setText(""); // Clear the currency when no country is selected
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                citySpinner.setEnabled(false);
                apiData.setupCitySpinner(TicketActivity.this, new ArrayList<>(), citySpinner);
                currencyTextView.setText(""); // Clear the currency when nothing is selected
            }
        });

        dateOfIssueEditText.setOnClickListener(v -> showDatePickerDialog());
        setupCarNumberEditText();
        setupCauseEditText();

        submitButton.setOnClickListener(v -> createTicket());
    }

    private void initializeViews() {
        dateOfIssueEditText = findViewById(R.id.date_of_issue);
        ticketIdEditText = findViewById(R.id.ticket_id);
        carNumberEditText = findViewById(R.id.car_number);
        causeEditText = findViewById(R.id.multiLineTextInputEditText);
        amountEditText = findViewById(R.id.amount);
        streetEditText = findViewById(R.id.ticket_address_street);
        streetNumberEditText = findViewById(R.id.ticket_address_street_number);
        mirrorPlate = findViewById(R.id.car_num_mirror);
        countrySpinner = findViewById(R.id.country_spinner);
        citySpinner = findViewById(R.id.city_spinner);
        currencyTextView = findViewById(R.id.currency_text);
        countryFlagTextView = findViewById(R.id.country_flag_text);
        submitButton = findViewById(R.id.submit_button);
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
    }

    private void createTicket() {
        String date = dateOfIssueEditText.getText().toString();
        String id = ticketIdEditText.getText().toString();
        String countryName = countrySpinner.getSelectedItem().toString();
        String cityName = citySpinner.getSelectedItem().toString();
        String streetAddress = streetEditText.getText().toString();
        String streetNumStr = streetNumberEditText.getText().toString();
        int streetNum = Integer.parseInt(streetNumStr);
        String carNum = carNumberEditText.getText().toString();
        String ticketCause = causeEditText.getText().toString();
        double ticketAmount = Double.parseDouble(amountEditText.getText().toString());
        String ticketCurrency = currencyTextView.getText().toString();

        Country country = new Country();
        country.setName(countryName);

        City city = new City();
        city.setName(cityName);

        Address address = new Address(country, city, streetAddress, streetNum);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Ticket newTicket = new Ticket(null,dateFormat, carNum, id, ticketCause, address, ticketAmount, ticketCurrency, ticketType);

        moveToAppealActivity(newTicket);
    }

    private void moveToAppealActivity(Ticket newTicket){
        Intent intent = new Intent(TicketActivity.this, AppealActivity.class);
        startActivity(intent);
    }
}
