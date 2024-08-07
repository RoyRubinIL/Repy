package com.example.repy.Models;

import com.google.gson.annotations.SerializedName;

public class CountryCurrencyResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private CurrencyData data;

    public boolean isError() {
        return error;
    }

    public String getMsg() {
        return msg;
    }

    public CurrencyData getData() {
        return data;
    }

    public static class CurrencyData {
        @SerializedName("name")
        private String name;

        @SerializedName("currency")
        private String currency;

        @SerializedName("iso2")
        private String iso2;

        @SerializedName("iso3")
        private String iso3;

        public String getName() {
            return name;
        }

        public String getCurrency() {
            return currency;
        }

        public String getIso2() {
            return iso2;
        }

        public String getIso3() {
            return iso3;
        }
    }
}
