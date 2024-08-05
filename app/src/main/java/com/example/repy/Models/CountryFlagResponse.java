package com.example.repy.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CountryFlagResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("msg")
    private String message;

    @SerializedName("data")
    private List<CountryFlagData> data; // Change to List

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<CountryFlagData> getData() {
        return data;
    }

    public static class CountryFlagData {
        @SerializedName("name")
        private String name;

        @SerializedName("iso2")
        private String iso2;

        @SerializedName("iso3")
        private String iso3;

        @SerializedName("unicodeFlag")
        private String unicodeFlag;

        public String getName() {
            return name;
        }

        public String getIso2() {
            return iso2;
        }

        public String getIso3() {
            return iso3;
        }

        public String getUnicodeFlag() {
            return unicodeFlag;
        }
    }
}
