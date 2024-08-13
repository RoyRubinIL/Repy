package com.example.repy.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String COUNTRIES_CITIES_URL = "https://countriesnow.space/api/v0.1/";
    private static final String CHAT_GPT_URL = "https://api.openai.com/";
    private static Retrofit retrofit_countries_cities;
    private static Retrofit retrofit_openAI;


    public static Retrofit getCountriesCitiesClient() {
        if (retrofit_countries_cities == null) {
            retrofit_countries_cities = new Retrofit.Builder()
                    .baseUrl(COUNTRIES_CITIES_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit_countries_cities;
    }

    public static Retrofit getOpenAIClient(String apiKey){
        if (retrofit_openAI == null) {
            retrofit_openAI = new Retrofit.Builder()
                    .baseUrl(CHAT_GPT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit_openAI;
    }
}
