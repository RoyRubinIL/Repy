package com.example.repy.Network;

public class CountryRequest {
    private String iso2;

    public CountryRequest(String iso2) {
        this.iso2 = iso2;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }
}