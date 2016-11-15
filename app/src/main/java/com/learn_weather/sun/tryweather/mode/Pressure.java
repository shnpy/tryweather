package com.learn_weather.sun.tryweather.mode;

/**
 * Created by Sun on 2016/11/11.
 */

public class Pressure {
    public static final double Pa=1;
    public static final double hPa=100;
    public static final double kPa=1000;
    public static final double atm=101325;
    private static final double mmHg=133.32236842105;
    private static final double MPa=10^6;
    private static final double bar=10^5;

    private double value;
    private double unitType;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value=value;
    }

    public double getUnitType() {
        return unitType;
    }

    public void setUnitType(double unitType) {
        this.unitType=unitType;
    }

    public Pressure changeUnit(double toType){
        double oldType=unitType;
        double oldValue=value;
        unitType=toType;
        value=oldValue*oldType/toType;
        return this;
    }

    public Pressure alterUnit(Pressure pressure,double toType){
        Pressure newPressure=new Pressure();
        newPressure.unitType=toType;
        newPressure.value=value*unitType/toType;
        return newPressure;
    }

}
