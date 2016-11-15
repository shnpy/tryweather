package com.learn_weather.sun.tryweather.mode;

/**
 * Created by Sun on 2016/11/10.
 */

public class OpenWeatherCity {
    private int cityId;
    private String cityName;
    private String country;
    private double longitude;
    private double latitude;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId=cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName=cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country=country;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude=longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude=latitude;
    }
}
