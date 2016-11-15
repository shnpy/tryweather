package com.learn_weather.sun.tryweather.util;

/**
 * Created by Sun on 2016/11/13.
 */

public interface TranslateListener {

    public void translateFinish(String result);
    public void translateError(Exception e);
}
