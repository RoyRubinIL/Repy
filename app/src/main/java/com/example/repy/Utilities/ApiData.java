package com.example.repy.Utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.repy.Models.City;
import com.example.repy.Models.Country;
import com.example.repy.Models.CountryCurrencyResponse;
import com.example.repy.Models.CountryFlagResponse;
import com.example.repy.Models.CountryResponse;
import com.example.repy.Network.ApiClient;
import com.example.repy.Network.ApiService;
import com.example.repy.Network.CountryRequest;
import com.example.repy.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiData {

    private ApiService apiService;
    private List<Country> countryList;

    public ApiData() {
        this.apiService = ApiClient.getCountriesCitiesClient().create(ApiService.class);
        this.countryList = new ArrayList<>();
    }

    public void setupCountrySpinner(Context context, Spinner countrySpinner, Spinner citySpinner, TextView countryFlagTextView) {
        List<String> countryNames = new ArrayList<>();
        countryNames.add("Select Country");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, countryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        loadCountries(context, adapter,countrySpinner, citySpinner, countryFlagTextView);
    }

    private void loadCountries(Context context, ArrayAdapter<String> adapter,Spinner countrySpinner, Spinner citySpinner, TextView countryFlagTextView) {
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
                    Log.e("ApiData", "Failed to load countries: " + response.message());
                    Toast.makeText(context, "Failed to load countries: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Log.e("ApiData", "Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    citySpinner.setEnabled(true);
                    loadCities(countrySpinner.getSelectedItem().toString(), citySpinner);
                    loadCountryFlag(countrySpinner.getSelectedItem().toString(), countryFlagTextView);
                } else {
                    citySpinner.setEnabled(false);
                    setupCitySpinner(context, new ArrayList<>(), citySpinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                citySpinner.setEnabled(false);
                setupCitySpinner(context, new ArrayList<>(), citySpinner);
            }
        });
    }

    public void loadCities(String countryName, Spinner citySpinner) {
        for (Country country : countryList) {
            if (country.getName().equals(countryName)) {
                List<City> cityList = new ArrayList<>();
                for (String cityName : country.getCities()) {
                    City city = new City();
                    city.setName(cityName);
                    cityList.add(city);
                }
                setupCitySpinner(citySpinner.getContext(), cityList, citySpinner);
                break;
            }
        }
    }

    public void loadCurrency(Context context, String countryName, TextView currencyTextView) {
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
                            Log.e("ApiData", "No currency data found for " + countryName);
                            Toast.makeText(context, "No currency data found for " + countryName, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("ApiData", "Failed to load currency: " + response.message());
                        Toast.makeText(context, "Failed to load currency: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CountryCurrencyResponse> call, Throwable t) {
                    Log.e("ApiData", "Error: " + t.getMessage());
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void loadCountryFlag(String countryName, TextView countryFlagTextView) {
        apiService.getCountryFlag(countryName).enqueue(new Callback<CountryFlagResponse>() {
            @Override
            public void onResponse(Call<CountryFlagResponse> call, Response<CountryFlagResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CountryFlagResponse.CountryFlagData> flagDataList = response.body().getData();
                    for (CountryFlagResponse.CountryFlagData flagData : flagDataList) {
                        if (flagData.getName().equalsIgnoreCase(countryName)) {
                            String unicodeFlag = flagData.getUnicodeFlag();
                            countryFlagTextView.setText(unicodeFlag);
                            return;
                        }
                    }
                    Log.e("ApiData", "No flag data found for " + countryName);
                    Toast.makeText(countryFlagTextView.getContext(), "No flag data found for " + countryName, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ApiData", "Failed to load country flag: " + response.message());
                    Toast.makeText(countryFlagTextView.getContext(), "Failed to load country flag: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryFlagResponse> call, Throwable t) {
                Log.e("ApiData", "Error: " + t.getMessage());
                Toast.makeText(countryFlagTextView.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupCitySpinner(Context context, List<City> cityList, Spinner citySpinner) {
        List<String> cityNames = new ArrayList<>();
        cityNames.add("Select City");
        for (City city : cityList) {
            cityNames.add(city.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, cityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
    }

    public Country getCountry(String countryName) {
        for (Country country : countryList) {
            if (country.getName().equalsIgnoreCase(countryName)) {
                return country;
            }
        }
        return null; // Return null if the country is not found
    }


    public City getCity(String countryName, String cityName) {
        Country country = getCountry(countryName);
        if (country != null) {
            for (String cityStr : country.getCities()) {
                if (cityStr.equalsIgnoreCase(cityName)) {
                    City city = new City();
                    city.setName(cityStr);
                    return city;
                }
            }
        }
        return null;
    }

}
