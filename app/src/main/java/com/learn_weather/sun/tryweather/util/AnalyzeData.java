package com.learn_weather.sun.tryweather.util;

import android.util.Log;

import com.learn_weather.sun.tryweather.activity.PrepareDataActivity;
import com.learn_weather.sun.tryweather.mode.CityInfo;
import com.learn_weather.sun.tryweather.mode.County;
import com.learn_weather.sun.tryweather.mode.Municipality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sun on 2016/10/30.
 */

public class AnalyzeData {
    private static final String TAG="TT";
    private static final String localTag="AnalyzeData__";


    public static ArrayList<CityInfo> analyzeJSONCityInfo(String response,
                                                          PrepareDataActivity
                                                                  prepareData)
            throws Exception{
        Log.d(TAG, localTag + "analyzeJSONCityInfo: begin");
        int counter=0;
        ArrayList<CityInfo> cityInfoList=new ArrayList<>();
            JSONObject jsonObject=new JSONObject(response);
            JSONArray cityArray=jsonObject.getJSONArray("city_info");
            for (int i=0; i < cityArray.length(); i++) {
                JSONObject current=cityArray.getJSONObject(i);
                String city=current.optString("city");
                String country=current.optString("cnty","Disputed");
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
                if (counter%200==0) {
                    prepareData.showProgress();
                }
            }
        return cityInfoList;
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

    public static synchronized String handleMunicipalData(String municipalitiesData,
                                            ArrayList<Municipality> resultList) {
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
//        municipalCodes=municipalCodes.substring(0, municipalCodes.length() - 1);
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


}
