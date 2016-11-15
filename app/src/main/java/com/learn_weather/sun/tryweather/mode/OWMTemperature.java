package com.learn_weather.sun.tryweather.mode;

import com.learn_weather.sun.tryweather.util.MathUtil;

/**
 * Created by Sun on 2016/11/8.
 */

public class OWMTemperature {
    public final static double ABSOLUTE_ZERO=-273.15;
    public final static int Kelvin=0;
    public final static int Celsius=1;
    public final static int Fahrenheit=2;
    private double day;
    private double max;
    private double min;
    private double night;
    private double eve;
    private double morn;
    private int unitType;

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max=max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min=min;
    }

    public double getDay() {
        return day;
    }

    public void setDay(double day) {
        this.day=day;
    }

    public double getNight() {
        return night;
    }

    public void setNight(double night) {
        this.night=night;
    }

    public double getEve() {
        return eve;
    }

    public void setEve(double eve) {
        this.eve=eve;
    }

    public double getMorn() {
        return morn;
    }

    public void setMorn(double morn) {
        this.morn=morn;
    }

    public int getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType=unitType;
    }

    /**
     * 华氏度=摄氏度×1.8+32
     * 摄氏度=(华氏度-32)÷1.8
     * @param toUnit
     */

    public void changeUnit(int toUnit) {
        int oldUnit=unitType;
        unitType=toUnit;
        switch (oldUnit) {
            case Kelvin:
                switch (toUnit) {
                    case Celsius:
                        day+=ABSOLUTE_ZERO;
                        eve+=ABSOLUTE_ZERO;
                        max+=ABSOLUTE_ZERO;
                        min+=ABSOLUTE_ZERO;
                        morn+=ABSOLUTE_ZERO;
                        night+=ABSOLUTE_ZERO;
                        break;
                    case Fahrenheit:
                        day=(day + ABSOLUTE_ZERO) * 1.8 + 32;
                        eve=(eve + ABSOLUTE_ZERO) * 1.8 + 32;
                        max=(max + ABSOLUTE_ZERO) * 1.8 + 32;
                        min=(min + ABSOLUTE_ZERO) * 1.8 + 32;
                        morn=(morn + ABSOLUTE_ZERO) * 1.8 + 32;
                        night=(night + ABSOLUTE_ZERO) * 1.8 + 32;
                        break;
                    default:
                        break;
                }
                break;
            case Celsius:
                switch (toUnit) {
                    case Kelvin:
                        day=day - ABSOLUTE_ZERO;
                        eve=eve - ABSOLUTE_ZERO;
                        max=max - ABSOLUTE_ZERO;
                        min=min - ABSOLUTE_ZERO;
                        morn=morn - ABSOLUTE_ZERO;
                        night=night - ABSOLUTE_ZERO;
                        break;
                    case Fahrenheit:
                        day=day * 1.8 + 32;
                        eve=eve * 1.8 + 32;
                        max=max * 1.8 + 32;
                        min=min * 1.8 + 32;
                        morn=morn * 1.8 + 32;
                        night=night * 1.8 + 32;
                        break;
                    default:
                        break;
                }
                break;
            case Fahrenheit:
                switch (toUnit) {
                    case Kelvin:
                        day=(day - 32) / 1.8 - ABSOLUTE_ZERO;
                        eve=(eve - 32) / 1.8 - ABSOLUTE_ZERO;
                        max=(max - 32) / 1.8 - ABSOLUTE_ZERO;
                        min=(min - 32) / 1.8 - ABSOLUTE_ZERO;
                        morn=(morn - 32) / 1.8 - ABSOLUTE_ZERO;
                        night=(night - 32) / 1.8 - ABSOLUTE_ZERO;
                        break;
                    case Celsius:
                        day=(day - 32) / 1.8;
                        eve=(eve - 32) / 1.8;
                        max=(max - 32) / 1.8;
                        min=(min - 32) / 1.8;
                        morn=(morn - 32) / 1.8;
                        night=(night - 32) / 1.8;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;

        }

    }

    public void specifyDecimal(int n) {
        day=MathUtil.speDec(day, n);
        max=MathUtil.speDec(max, n);
        min=MathUtil.speDec(min, n);
        night=MathUtil.speDec(night, n);
        eve=MathUtil.speDec(eve, n);
        morn=MathUtil.speDec(morn, n);
    }




}
