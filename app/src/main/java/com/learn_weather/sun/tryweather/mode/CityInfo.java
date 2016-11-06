package com.learn_weather.sun.tryweather.mode;

/**
 * Created by Sun on 2016/10/30.
 */

public class CityInfo {
    private String city;
    private String cityId;
    private String country;
    private double latitude;
    private double longitude;
    private String province;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city=city;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId=cityId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country=country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude=latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude=longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province=province;
    }
}
