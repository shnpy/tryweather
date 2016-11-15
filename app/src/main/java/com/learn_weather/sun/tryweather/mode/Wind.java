package com.learn_weather.sun.tryweather.mode;

/**
 * Created by Sun on 2016/11/8.
 */

public class Wind {
    private int degree;
    private String direction;
    private String windScale;
    private Speed windSpeed;

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree=degree;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction=direction;
    }

    public String getWindScale() {
        return windScale;
    }

    public void setWindScale(String windScale) {
        this.windScale=windScale;
    }

    public Speed getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Speed windSpeed) {
        this.windSpeed=windSpeed;
    }
}
