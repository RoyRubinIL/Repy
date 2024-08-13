package com.example.repy.Network;

import com.example.repy.Models.Appeal;
import com.example.repy.Models.CountryCurrencyResponse;
import com.example.repy.Models.CountryFlagResponse;
import com.example.repy.Models.CountryResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("countries")
    Call<CountryResponse> getCountries();

    @GET("countries/flag/unicode")
    Call<CountryFlagResponse> getCountryFlag(@Query("country") String countryName);

    @POST("countries/currency")
    Call<CountryCurrencyResponse> getCountryCurrency(@Body CountryRequest countryRequest);

    @Headers({
            "Content-Type: application/json",
            "Authorization: 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"
    })
    @POST("v1/chat/completions")
    Call<JsonObject> getChatCompletion(@Body JsonObject body);
}
