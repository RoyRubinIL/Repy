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
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TicketActivity extends AppCompatActivity {

    private static final String TAG = "TicketActivity";

    private TextInputEditText dateOfIssueEditText, ticketIdEditText, carNumberEditText, causeEditText, amountEditText, streetEditText, streetNumberEditText;
    private MaterialTextView mirrorPlate, currencyTextView;
    private Spinner countrySpinner, citySpinner;
    private TextView countryFlagTextView;
    private MaterialButton submitButton;
    private TicketType ticketType;
    private DataManager dataManager;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
        setContentView(R.layout.activity_ticket);

        initializeViews();

        dataManager = DataManager.getInstance();
        userManager = new UserManager();

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
        dateOfIssueEditText = findViewById(R.id.ticket_LBL_dateOfIssue);
        ticketIdEditText = findViewById(R.id.ticket_LBL_ticketId);
        carNumberEditText = findViewById(R.id.ticket_LBL_carNumber);
        causeEditText = findViewById(R.id.ticket_LBL_causeText);
        amountEditText = findViewById(R.id.ticket_LBL_amount);
        streetEditText = findViewById(R.id.ticket_LBL_street);
        streetNumberEditText = findViewById(R.id.ticket_LBL_streetNumber);
        mirrorPlate = findViewById(R.id.ticket_LBL_carNumberMirror);
        countrySpinner = findViewById(R.id.ticket_LST_countrySpinner);
        citySpinner = findViewById(R.id.ticket_LST_citySpinner);
        currencyTextView = findViewById(R.id.ticket_LBL_currency);
        countryFlagTextView = findViewById(R.id.ticket_LBL_countryFlag);
        submitButton = findViewById(R.id.ticket_BTN_submit);
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

    private boolean validateFields(TicketType ticketType, String dateString, String id, String countryName, String cityName,
                                   String streetAddress, String streetNumStr, String carNum, String ticketCause,
                                   String amountStr, String ticketCurrency) {

        if (dateString.isEmpty()) {
            System.err.println("Error: Date of issue is empty.");
            return false;
        }

        if (id.isEmpty()) {
            System.err.println("Error: Ticket ID is empty.");
            return false;
        }

        if (countryName.isEmpty()) {
            System.err.println("Error: Country is not selected.");
            return false;
        }

        if (cityName.isEmpty()) {
            System.err.println("Error: City is not selected.");
            return false;
        }

        if (carNum.isEmpty()) {
            System.err.println("Error: Car number is empty.");
            return false;
        }

        if (ticketCause.isEmpty()) {
            System.err.println("Error: Cause of the ticket is empty.");
            return false;
        }

        if (amountStr.isEmpty()) {
            System.err.println("Error: Ticket amount is empty.");
            return false;
        }

        if (ticketCurrency.isEmpty()) {
            System.err.println("Error: Ticket currency is not selected.");
            return false;
        }

        if (ticketType == TicketType.MUNICIPALITY) {
            if (streetAddress.isEmpty()) {
                System.err.println("Error: Street address is empty.");
                return false;
            }

            if (streetNumStr.isEmpty()) {
                System.err.println("Error: Street number is empty.");
                return false;
            }

            try {
                Integer.parseInt(streetNumStr);
            } catch (NumberFormatException e) {
                System.err.println("Error: Street number is not a valid number.");
                return false;
            }
        }

        return true;
    }



    private void createTicket() {
        String dateString = dateOfIssueEditText.getText().toString();
        String id = ticketIdEditText.getText().toString();
        String countryName = countrySpinner.getSelectedItem().toString();
        String cityName = citySpinner.getSelectedItem().toString();
        String streetAddress = streetEditText.getText().toString();
        String streetNumStr = streetNumberEditText.getText().toString();
        String carNum = carNumberEditText.getText().toString();
        String ticketCause = causeEditText.getText().toString();
        String amountStr = amountEditText.getText().toString();
        String ticketCurrency = currencyTextView.getText().toString();

        if (!validateFields(ticketType, dateString, id, countryName, cityName, streetAddress, streetNumStr, carNum, ticketCause, amountStr, ticketCurrency)) {
            return;
        }
        int streetNum = Integer.parseInt(streetNumStr);
        double ticketAmount = Double.parseDouble(amountStr);

        Country country = new Country();
        country.setName(countryName);

        City city = new City();
        city.setName(cityName);

        Address address = new Address(country, city, streetAddress, streetNum);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }
        Ticket newTicket = new Ticket(null,date, carNum, id, ticketCause, address, ticketAmount, ticketCurrency, ticketType);

        String ticketUid = dataManager.saveNewTicket(userManager.getCurrentUserUid(), newTicket);
        newTicket.setUid(ticketUid);
        moveToAppealActivity(newTicket);
    }

    private void moveToAppealActivity(Ticket newTicket){
        Intent intent = new Intent(TicketActivity.this, AppealActivity.class);
        intent.putExtra("newTicketID", newTicket.getTicketId());
        intent.putExtra("newTicketUid", newTicket.getUid());
        startActivity(intent);
    }
}
