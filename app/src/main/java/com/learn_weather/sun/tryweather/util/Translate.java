package com.learn_weather.sun.tryweather.util;

import android.util.Log;

import java.net.URLEncoder;
import java.util.Random;

/**
 * Created by Sun on 2016/10/31.
 */

public class Translate {
     private static final String TAG="TT";
    private static final String localTag= "Translate__";

    private static final String APP_ID="20161031000031062";
    private static final String CIPHER_CODE="w83rqinZI_RusOoLupJ_";

    public static void fromEnTOZh(final String query,final TranslateListener
            translateListener) throws Exception{
        Random random=new Random();
        int salt=random.nextInt();
        String sign=generateSign(query, salt);
        String url="http://api.fanyi.baidu.com/api/trans/vip/translate?q=" +
                query + "&from=en&to=zh&appid=" +
                APP_ID + "&salt=" + salt + "&sign=" +sign;
       final  StringBuilder result=new StringBuilder();
        HttpUtil.sendRequest(url,new HttpListener() {
            @Override
            public void httpFinish(String response) {
                Log.d(TAG, localTag + "httpFinish: the result of respons has " +
                        "been returned!");
                result.append(AnalyzeData.analyzeJsonTranslate(response));
                Log.d(TAG, localTag + "httpFinish: translate result="+result.toString());
                translateListener.translateFinish(result.toString());

            }

            @Override
            public void httpError(Exception e) {

                translateListener.translateError(e);

            }
        });





    }

    public static void fromZhTOEn(final String query,final
                                    TranslateListener translateListener) throws
            Exception {
        Random random=new Random();
        int salt=random.nextInt();
        String sign=generateSign(query, salt);
        String queryEncoded;
        queryEncoded=URLEncoder.encode(query,"utf-8");
        String url="http://api.fanyi.baidu.com/api/trans/vip/translate?q=" +
                queryEncoded + "&from=zh&to=en&appid=" +
                APP_ID + "&salt=" + salt + "&sign=" +sign;
        final  StringBuilder result=new StringBuilder();
        HttpUtil.sendRequest(url,new HttpListener() {
            @Override
            public void httpFinish(String response) {
                result.append(AnalyzeData.analyzeJsonTranslate(response));
                Log.d(TAG, localTag + "httpFinish: translate result="+result.toString());
                translateListener.translateFinish(result.toString());
            }

            @Override
            public void httpError(Exception e) {
                translateListener.translateError(e);
            }
        });


    }




    private static String generateSign(String query, int salt) {
        String staple=APP_ID+query+salt+CIPHER_CODE;
        return AnalyzeData.stringToMD5(staple);
    }


}
