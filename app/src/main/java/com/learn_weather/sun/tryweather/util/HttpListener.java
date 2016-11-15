package com.learn_weather.sun.tryweather.util;

/**
 * Created by Sun on 2016/10/29.
 */

public interface HttpListener {

    public void httpFinish(String response);
    public void httpError(Exception e);
}
