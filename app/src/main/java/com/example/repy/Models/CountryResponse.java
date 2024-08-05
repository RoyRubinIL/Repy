package com.example.repy.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CountryResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("msg")
    private String message;

    @SerializedName("data")
    private List<Country> countries;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<Country> getCountries() {
        return countries;
    }
}
