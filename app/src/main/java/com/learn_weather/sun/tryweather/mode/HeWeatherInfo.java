package com.learn_weather.sun.tryweather.mode;

import java.util.Date;

/**
 * Created by Sun on 2016/11/8.
 */

public class HeWeatherInfo {

    private Astro astro;
    private HeWeathCodition heWeathCodition;
    private Date date;
    private int humidity;
    private float precipition;
    private float press;
    private HeTemperature temperature;
    private float visibility;
    private Wind wind;


    public Astro getAstro() {
        return astro;
    }

    public void setAstro(Astro astro) {
        this.astro=astro;
    }

    public HeWeathCodition getHeWeathCodition() {
        return heWeathCodition;
    }

    public void setHeWeathCodition(HeWeathCodition heWeathCodition) {
        this.heWeathCodition=heWeathCodition;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date=date;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity=humidity;
    }

    public float getPrecipition() {
        return precipition;
    }

    public void setPrecipition(float precipition) {
        this.precipition=precipition;
    }

    public float getPress() {
        return press;
    }

    public void setPress(float press) {
        this.press=press;
    }

    public HeTemperature getTemperature() {
        return temperature;
    }

    public void setTemperature(HeTemperature temperature) {
        this.temperature=temperature;
    }

    public float getVisibility() {
        return visibility;
    }

    public void setVisibility(float visibility) {
        this.visibility=visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind=wind;
    }
}
