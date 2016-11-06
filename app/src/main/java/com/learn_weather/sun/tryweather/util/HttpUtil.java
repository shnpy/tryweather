package com.learn_weather.sun.tryweather.util;

import android.util.Log;

import com.learn_weather.sun.tryweather.activity.PrepareDataActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sun on 2016/10/29.
 */

public class HttpUtil {
    private static final String TAG="TT";
    private static final String localTag="HttpUtil__";

    
    
    public static void sendRequest(final String address, final PrepareDataActivity
            prepareData, final HttpListener listener) {
        Log.d(TAG, localTag + "begin to connect url:" + address);
        new Thread(new Runnable() {
            @Override
            public void run() {

                URL url;
                HttpURLConnection connection=null;
                InputStream in;
                BufferedReader reader;

                try {
                    url=new URL(address);
                    connection=(HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(20000);
                    connection.setReadTimeout(20000);
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection
                            .HTTP_OK) {
                        Log.d(TAG, localTag + "fail to connect " + address);
                        throw new Exception("Fail to connect the url:" +
                                address);
                    } else {
                        PrepareDataActivity.progess.addAndGet(500);
                        Log.d(TAG, localTag + "succeed to connect " +
                                "url:"+address);
                        prepareData.showProgress();

                    }
                    in=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    Log.d(TAG, localTag + "sendRequest: before readline()" +
                            ":"+address);
                    int counter=0;
                    while ((line=reader.readLine()) != null) {
                        response.append(line);
                        PrepareDataActivity.progess.addAndGet(6);
                        counter++;
                        if(counter%100==0)
                            prepareData.showProgress();
                    }
                    prepareData.showProgress();
                    String resp=response.toString();
                    int length=resp.length();
                    String printResp;
                    if (length>100) {
                        printResp=resp.substring(0,20)+"..."+resp.substring
                                (length-20,length);
                    }else {
                        printResp=resp;
                    }
                    Log.d(TAG, localTag + "sendRequest:the loop of readline " +
                            "has completed!response=" +printResp);
                    in.close();
                    reader.close();
                    listener.onFinish(response.toString());
                } catch (Exception e) {

                    listener.onError(e);
                } finally {
                    connection.disconnect();
                }
            }
        }).start();
    }

    public static void sendRequest(final String address, final HttpListener listener) {
        Log.d(TAG, localTag + "begin to connect url:" + address);
        new Thread(new Runnable() {
            @Override
            public void run() {

                URL url;
                HttpURLConnection connection=null;
                InputStream in;
                BufferedReader reader;

                try {
                    url=new URL(address);
                    connection=(HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(20000);
                    connection.setReadTimeout(20000);
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection
                            .HTTP_OK) {
                        Log.d(TAG, localTag + "fail to connect " + address);
                        throw new Exception("Fail to connect the url:" +
                                address);
                    } else {
//                        PrepareDataActivity.progess.addAndGet(500);
                        Log.d(TAG, localTag + "succeed to connect " +
                                "url:"+address);
//                        prepareData.showProgress();

                    }
                    in=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    Log.d(TAG, localTag + "sendRequest: before readline()"+address);
                    int counter=0;
                    while ((line=reader.readLine()) != null) {
                        response.append(line);
//                        PrepareDataActivity.progess.addAndGet(6);
          /*              counter++;
                        if(counter%100==0)
                            prepareData.showProgress();*/
                    }
//                    prepareData.showProgress();
                    String resp=response.toString();
                    int length=resp.length();
                    String printResp;
                    if (length>100) {
                        printResp=resp.substring(0,20)+"..."+resp.substring
                                (length-20,length);
                    }else {
                        printResp=resp;
                    }
                    Log.d(TAG, localTag + "sendRequest:the loop of readline " +
                            "has completed!response=" +printResp);
                    in.close();
                    reader.close();
                    listener.onFinish(response.toString());
                } catch (Exception e) {

                    listener.onError(e);
                } finally {
                    connection.disconnect();
                }
            }
        }).start();
    }

}
