package com.learn_weather.sun.tryweather.util;

import android.util.Log;

import java.util.Random;

/**
 * Created by Sun on 2016/10/31.
 */

public class Translate {
     private static final String TAG="TT";
    private static final String localTag= "Translate__";

    private static final String APP_ID="20161031000031062";
    private static final String CIPHER_CODE="w83rqinZI_RusOoLupJ_";

    public static String fromEnTOZh(final String query) {
        Random random=new Random();
        int salt=random.nextInt();
        String sign=generateSign(query, salt);
        String url="http://api.fanyi.baidu.com/api/trans/vip/translate?q=" +
                query + "&from=en&to=zh&appid=" +
                APP_ID + "&salt=" + salt + "&sign=" +sign;
       final  StringBuilder result=new StringBuilder();
        HttpUtil.sendRequest(url,new HttpListener() {
            @Override
            public void onFinish(String response) {
                result.append(AnalyzeData.analyzeJsonTranslate(response));
                Log.d(TAG, localTag + "onFinish: translate result="+result.toString());
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, localTag + "fail to translate!use the original " +
                        "language!");
                result.append(query);
                e.printStackTrace();

            }
        });

        return result.toString();

    }

    private static String generateSign(String query, int salt) {
        return "";
    }


}
