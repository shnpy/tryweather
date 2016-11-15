package com.learn_weather.sun.tryweather.mode;

/**
 * Created by Sun on 2016/11/11.
 */

public class Temperature {
    public final static double  ABSOLUTE_ZERO=-273.15;
    public final static int Kelvin=0;
    public final static int Celsius=1;
    public final static int Fahrenheit=2;
    private double value;
    private int unitType;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value=value;
    }

    public int getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType=unitType;
    }

    public Temperature changeUnit(int fromUnit, int toUnit,
                                  Temperature tem){
        Temperature newTem=new Temperature();
        newTem.setUnitType(toUnit);
        switch (fromUnit){
            case Kelvin:
                switch (toUnit){
                    case Celsius:
                        newTem.value=tem.value+ABSOLUTE_ZERO;
                        break;
                    case Fahrenheit:
                        newTem.value=(tem.value+ABSOLUTE_ZERO)*1.8+32;
                        break;
                    default:
                        break;
                }
                break;
            case Celsius:
                switch (toUnit){
                    case Kelvin:
                        newTem.value=tem.value-ABSOLUTE_ZERO;
                        break;
                    case Fahrenheit:
                        newTem.value=tem.value*1.8+32;
                        break;
                    default:
                        break;
                }
                break;
            case Fahrenheit:
                switch (toUnit){
                    case Kelvin:
                        newTem.value=(tem.value-32)/1.8-ABSOLUTE_ZERO;
                        break;
                    case Celsius:
                        newTem.value=(tem.value-32)/1.8;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;

        }
        return newTem;

    }



}
