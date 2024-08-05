package com.example.repy.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Country {
    @SerializedName("country")
    private String name;

    @SerializedName("cities")
    private List<String> cities;

    public String getName() {
        return name;
    }

    public List<String> getCities() {
        return cities;
    }
}
