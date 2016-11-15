package com.learn_weather.sun.tryweather.mode;

/**
 * Created by Sun on 2016/11/11.
 *
 * The weather information showed in http://openweathermap.org/forecast16
 */

public class OWMWeatherInfo {
    private long dataTime;
    private OWMTemperature owmTemperature;
    private Pressure pressure;
    private int humidity;
    private OWMWeatherCondition weatherCondition;
    private double windSpeed;
    private double windDegree;
    private double clouds;

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime=dataTime;
    }

    public OWMTemperature getOwmTemperature() {
        return owmTemperature;
    }

    public void setOwmTemperature(OWMTemperature owmTemperature) {
        this.owmTemperature=owmTemperature;
    }

    public Pressure getPressure() {
        return pressure;
    }

    public void setPressure(Pressure pressure) {
        this.pressure=pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity=humidity;
    }

    public OWMWeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(OWMWeatherCondition weatherCondition) {
        this.weatherCondition=weatherCondition;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed=windSpeed;
    }

    public double getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(double windDegree) {
        this.windDegree=windDegree;
    }

    public double getClouds() {
        return clouds;
    }

    public void setClouds(double clouds) {
        this.clouds=clouds;
    }
}
