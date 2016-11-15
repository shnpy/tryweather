package com.learn_weather.sun.tryweather.util;

/**
 * Created by Sun on 2016/11/14.
 */

public class MathUtil {

    public static double speDec(double value, int n) {
        double carry=Math.pow(10, n);
        return Math.rint(value * carry) / carry;
    }


}
