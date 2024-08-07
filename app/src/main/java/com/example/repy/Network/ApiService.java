package com.example.repy.Network;

import com.example.repy.Models.CountryCurrencyResponse;
import com.example.repy.Models.CountryFlagResponse;
import com.example.repy.Models.CountryResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("countries")
    Call<CountryResponse> getCountries();

    @GET("countries/flag/unicode")
    Call<CountryFlagResponse> getCountryFlag(@Query("country") String countryName);

    @POST("countries/currency")
    Call<CountryCurrencyResponse> getCountryCurrency(@Body CountryRequest countryRequest);
}
