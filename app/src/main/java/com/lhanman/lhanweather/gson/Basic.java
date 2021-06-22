package com.lhanman.lhanweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherid;

    public Update update;

    public class Update
    {
        @SerializedName("loc")
        public String updateTime;
    }
}
