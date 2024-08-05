package com.example.repy.Models;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
