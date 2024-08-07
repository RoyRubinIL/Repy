package com.example.repy.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Country {
    @SerializedName("country")
    private String name;

    @SerializedName("cities")
    private List<String> cities;

    @SerializedName("iso2")
    private String iso2;

    public String getName() {
        return name;
    }

    public List<String> getCities() {
        return cities;
    }

    public String getIso2() {
        return iso2;
    }
}
