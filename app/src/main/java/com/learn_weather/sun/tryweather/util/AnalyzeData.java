package com.learn_weather.sun.tryweather.util;

import android.util.Log;

import com.learn_weather.sun.tryweather.activity.PrepareDataActivity;
import com.learn_weather.sun.tryweather.mode.CityInfo;
import com.learn_weather.sun.tryweather.mode.County;
import com.learn_weather.sun.tryweather.mode.Municipality;
import com.learn_weather.sun.tryweather.mode.OWMTemperature;
import com.learn_weather.sun.tryweather.mode.OWMWeatherCondition;
import com.learn_weather.sun.tryweather.mode.OWMWeatherInfo;
import com.learn_weather.sun.tryweather.mode.OpenWeatherCity;
import com.learn_weather.sun.tryweather.mode.Pressure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Sun on 2016/10/30.
 */

public class AnalyzeData {
    private static final String TAG="ZZZZZTT";
    private static final String localTag="AnalyzeData__";


    public static ArrayList<CityInfo> analyzeJSONCityInfo(String response,
                                                          PrepareDataActivity
                                                                  prepareData) throws Exception {
        Log.d(TAG, localTag + "analyzeJSONCityInfo: begin");
        int counter=0;
        ArrayList<CityInfo> cityInfoList=new ArrayList<>();
        JSONObject jsonObject=new JSONObject(response);
        JSONArray cityArray=jsonObject.getJSONArray("city_info");
        for (int i=0; i < cityArray.length(); i++) {
            JSONObject current=cityArray.getJSONObject(i);
            String city=current.optString("city");
            String country=current.optString("cnty", "Disputed");
            String cityId=current.optString("id");
            double latitude=current.optDouble("lat");
            double longitude=current.optDouble("lon");
            String province=current.optString("prov", "unkown");
                /*if(current.has("prov")){
                    province=current.getString("prov");
                }else {
                    province="unknown";
                }*/
            CityInfo cityInfo=new CityInfo();
            cityInfo.setCity(city);
            cityInfo.setCityId(cityId);
            cityInfo.setCountry(country);
            cityInfo.setLatitude(latitude);
            cityInfo.setLongitude(longitude);
            cityInfo.setProvince(province);
            cityInfoList.add(cityInfo);
            PrepareDataActivity.progess.addAndGet(4);
            counter++;
            if (counter % 200 == 0) {
                prepareData.showProgress();
            }
        }
        return cityInfoList;
    }

    public static void analyzeJsonOWCities(String data,
                                           ArrayList<OpenWeatherCity>
                                                   cityList) throws Exception {


        JSONObject current=new JSONObject(data);
        int cityId=current.getInt("_id");
        String cityName=current.getString("name");
        String country=current.getString("country");
        JSONObject coord=current.getJSONObject("coord");
        double longitude=coord.getDouble("lon");
        double latitude=coord.getDouble("lat");
        OpenWeatherCity city=new OpenWeatherCity();
        city.setCityId(cityId);
        city.setCityName(cityName);
        city.setCountry(country);
        city.setLatitude(latitude);
        city.setLongitude(longitude);
        cityList.add(city);
    }

    public static String analyzeJsonTranslate(String response) {
        String result="";

        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray resultArray=jsonObject.getJSONArray("trans_result");
            JSONObject resultObject=resultArray.getJSONObject(0);
            result=resultObject.getString("dst");
        } catch (JSONException e) {
            Log.d(TAG, localTag + "analyzeJsonTranslate: fail to translate");
            e.printStackTrace();
        }
        Log.d(TAG, localTag + "analyzeJsonTranslate: the result of " +
                "translation is "+result);
        return result;
    }


    public static String[] handleProvincesData(String provincesData) {

        String[] provinces=provincesData.split(",");
        String[] allProvincesNames=new String[provinces.length];
        for (int i=0; i < provinces.length; i++) {
            String[] provinceRecord=provinces[i].split("\\|");
            allProvincesNames[i]=provinceRecord[1];
            PrepareDataActivity.progess.addAndGet(1);
        }
        return allProvincesNames;
    }

    public static synchronized String handleMunicipalData(String municipalitiesData, ArrayList<Municipality> resultList) {
        String municipalCodes="";
        String[] municipalities=municipalitiesData.split(",");
        for (String m : municipalities) {
            String[] municipalityRecord=m.split("\\|");
            String municipalCode=municipalityRecord[0];
            String municipalName=municipalityRecord[1];
            Municipality municipality=new Municipality();
            municipality.setCode(municipalCode);
            municipality.setName(municipalName);
            resultList.add(municipality);
            municipalCodes+=municipalCode + ",";
            PrepareDataActivity.progess.addAndGet(2);
        }
//        municipalCodes=municipalCodes.substring(0, municipalCodes.length()
// - 1);
        Log.d(TAG, localTag + "handleMunicipalData: municipalCodes=" +
                municipalCodes);
        return municipalCodes;
    }


    public static synchronized void handleCountiesData(String response,
                                                       ArrayList<County>
                                                               wholeCountyList) {

        String[] countyRecords=response.split(",");
        for (String c : countyRecords) {
            County county=new County();
            String[] countyRecord=c.split("\\|");
            county.setCountyCode(countyRecord[0]);
            county.setCountyName(countyRecord[1]);
            wholeCountyList.add(county);
            PrepareDataActivity.progess.addAndGet(2);
        }
    }


    public static ArrayList<OWMWeatherInfo> analyzeJsonOWM(String response)
    throws Exception{
        ArrayList<OWMWeatherInfo> weatherInfoList=new ArrayList<>();


            JSONObject responseObject=new JSONObject(response);
            JSONArray weatherList=responseObject.getJSONArray("list");

            for (int i=0; i < weatherList.length(); i++) {
                JSONObject weatherinfoObject=weatherList.getJSONObject(i);
                long dataTime=weatherinfoObject.optLong("dt");

                OWMTemperature temperature=new OWMTemperature();
                JSONObject tempObject=weatherinfoObject.getJSONObject("temp");
                temperature.setUnitType(temperature.Kelvin);
                temperature.setDay(tempObject.optDouble("day"));
                temperature.setMin(tempObject.optDouble("min"));
                temperature.setMax(tempObject.optDouble("max"));
                temperature.setNight(tempObject.optDouble("night"));
                temperature.setEve(tempObject.optDouble("eve"));
                temperature.setMorn(tempObject.optDouble("morn"));

                Pressure pressure=new Pressure();
                pressure.setUnitType(Pressure.hPa);
                pressure.setValue(weatherinfoObject.optDouble("pressure"));

                int humidity=weatherinfoObject.optInt("humidity");

                OWMWeatherCondition weatherCondition=new OWMWeatherCondition();
                JSONObject weatherObject=weatherinfoObject.getJSONArray
                        ("weather").getJSONObject(0);
                weatherCondition.setId(weatherObject.optInt("id"));
                weatherCondition.setMain(weatherObject.optString("main"));
                weatherCondition.setDescription(weatherObject.optString
                        ("description"));
                weatherCondition.setIcon(weatherObject.optString("icon"));

                double windSpeed=weatherinfoObject.optDouble("speed");
                double windDegree=weatherinfoObject.optDouble("deg");
                int clouds=weatherinfoObject.optInt("clouds");

                OWMWeatherInfo weatherInfo=new OWMWeatherInfo();
                weatherInfo.setDataTime(dataTime);
                weatherInfo.setOwmTemperature(temperature);
                weatherInfo.setHumidity(humidity);
                weatherInfo.setPressure(pressure);
                weatherInfo.setWeatherCondition(weatherCondition);
                weatherInfo.setWindSpeed(windSpeed);
                weatherInfo.setWindDegree(windDegree);
                weatherInfo.setClouds(clouds);
                weatherInfoList.add(weatherInfo);
            }
        return weatherInfoList;


    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash=MessageDigest.getInstance("MD5").digest(string.getBytes
                    ("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex=new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

}
