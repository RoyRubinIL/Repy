package com.example.repy.Interfaces;

import com.example.repy.APIRequests.ChatGptRequest;
import com.example.repy.APIRequests.CountryRequest;
import com.example.repy.APIResponses.ChatGptResponse;
import com.example.repy.APIResponses.CountryCurrencyResponse;
import com.example.repy.APIResponses.CountryFlagResponse;
import com.example.repy.APIResponses.CountryResponse;

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
            "Authorization: Bearer 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"
    })
    @POST("v1/chat/completions")
    Call<ChatGptResponse> getChatCompletion(@Body ChatGptRequest chatGptRequest);
}