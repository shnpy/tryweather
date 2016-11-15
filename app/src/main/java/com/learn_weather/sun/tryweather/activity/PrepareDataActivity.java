package com.learn_weather.sun.tryweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.learn_weather.sun.tryweather.R;
import com.learn_weather.sun.tryweather.db.TryWeatherDB;
import com.learn_weather.sun.tryweather.mode.CityInfo;
import com.learn_weather.sun.tryweather.mode.County;
import com.learn_weather.sun.tryweather.mode.Municipality;
import com.learn_weather.sun.tryweather.mode.OpenWeatherCity;
import com.learn_weather.sun.tryweather.util.AnalyzeData;
import com.learn_weather.sun.tryweather.util.HttpListener;
import com.learn_weather.sun.tryweather.util.HttpUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sun on 2016/11/1.
 */

public class PrepareDataActivity extends Activity {
    //设置log的TAG标签
    private static final String TAG="ZZZZZTT";
    private final String localTag=getClass().getSimpleName() + "__";


    private TryWeatherDB tryWeatherDB;

    private boolean citiInfoFinished=false;
    private boolean countiesFinished=false;
    private boolean errorHappened=false;

    private final int ERROR_HAPPENED=0;
    private final int FINISHED=1;
    private final int SHOW_PROGRESS=2;

    public static final int MAX_PROGRESS=1308921;


    private String errorReport;

    private TextView waitText;
    private TextView content;
    private TextView success;
    private TextView error;
    private TextView result;

    private ProgressBar progressBar; //进度条控件
    public static AtomicInteger progess=new AtomicInteger(0); //进度记录器

    private SharedPreferences pre;
    private SharedPreferences.Editor editor;

    long initialStartTime;
    long wholeDuraiton;
    long cityInfoStart;
    long cityInfoDuration;
    long countiesStart;
    long countiesDuration;

    AssetManager assetManager;


    private Message msg=new Message();

    private SQLiteDatabase db;

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR_HAPPENED:
                    error.setVisibility(View.VISIBLE);
                    result.setText(errorReport + "\n\ncleare the " +
                            "database!+\n please exit and retry or contact " +
                            "programmer to fix the bug!");
                    break;
                case FINISHED:
                    editor.putBoolean("initialized", true).commit();
                    success.setVisibility(View.VISIBLE);
                    wholeDuraiton=System.currentTimeMillis() - initialStartTime;
                    result.setText("all tables of " +
                            "administrative_districts_db  " + "has been " +
                            "filled!" +
                            "\n The duration of the whole initialization " +
                            "is " +
                            +wholeDuraiton +
                            "\n The duration of fill CityInfo is " +
                            cityInfoDuration +
                            "\n The duration of fill Counties is " +
                            countiesDuration +
                            "\n max value of progress is" + progess.get());
                    Log.d(TAG, localTag + "onCreate: all " +
                            "tables of administrative_districts_db  " +
                            "has been filled!");
                    Toast.makeText(PrepareDataActivity.this, "The " +
                            "initialization of database " +
                            "" + "has been fulfilled", Toast.LENGTH_LONG)
                            .show();
                    progressBar.setVisibility(View.INVISIBLE);

                    Intent intent=new Intent(PrepareDataActivity.this,
                            WeatherActivity.class);
                    startActivity(intent);
                    break;
                case SHOW_PROGRESS:
                    progressBar.setProgress(progess.get());
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        initialStartTime=System.currentTimeMillis();

        pre=PreferenceManager.getDefaultSharedPreferences(this);
        editor=pre.edit();
        tryWeatherDB=TryWeatherDB.getInstanceOfTryWeatherDB(this);
        db=tryWeatherDB.getDb();
        Log.d(TAG, localTag + "onCreate: aftert obtain Db");

//       clear();//初始化清零
        boolean initialized=pre.getBoolean("initialized", false);
        boolean needUpdated=pre.getBoolean("needUpdated", true);
//        boolean needUpdated=true;

        result=(TextView) findViewById(R.id.result);
        success=(TextView) findViewById(R.id.success);
        error=(TextView) findViewById(R.id.error);
        progressBar=(ProgressBar) findViewById(R.id.progress_bar);


        waitText=(TextView) findViewById(R.id.wait_text);
        content=(TextView) findViewById(R.id.content);
        content.setText("the state of database: " +
                "\ninitialized=" + initialized);


        if (!initialized) {
            waitText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            showProgress();
            fillCityInfoAndCountries();
            fillProvinces();
            Log.d(TAG, localTag + "provinces has been filled!");
            fillMunicipalitiesAndCounties();
            Log.d(TAG, localTag + " main thread after " +
                    "fillMunicipalitiesAndCounties()!");
        } else {
            Intent intent=new Intent(this, WeatherActivity.class);
            startActivity(intent);
        }

        if (needUpdated) {
            fillOWCities();


        }


    }

    private void fillOWCities() {
        assetManager=this.getAssets();
        try {
            tryWeatherDB.createOWCities();
            InputStream in=assetManager.open("city.list.json");
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            ArrayList<OpenWeatherCity> cityList=new ArrayList<>();
//            citiesJson.append("[");
            String line;
            while ((line=reader.readLine()) != null) {
                AnalyzeData.analyzeJsonOWCities(line,cityList);
            }
//            citiesJson.append("]");
            try {
                db.beginTransaction();
                for (OpenWeatherCity city : cityList) {
                    tryWeatherDB.insertIntoOWCities(city);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            editor.putBoolean("needUpdated", false).commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, localTag + "fillOWCities: error=" + e.toString() + " "
                    + e.getMessage());
            db.execSQL("drop table if exists OpenWeatherCities");
        }


    }

    /**
     * 启动Handler中处理UI界面的代码
     */
    private void startHandler() {
        if (citiInfoFinished && countiesFinished) {
            msg.what=FINISHED;
            handler.sendMessage(msg);
        } else if (errorHappened) {
            msg.what=ERROR_HAPPENED;
            handler.sendMessage(msg);
        }
    }


    //从api接口地址获取数据，填充CityInfo表格
    private void fillCityInfoAndCountries() {
        Log.d(TAG, localTag + "fillCityInfoAndCountries: begin");
        cityInfoStart=System.currentTimeMillis();
        final String address="https://api.heweather" +
                ".com/x3/citylist?search=allworld&key=" +
                WeatherActivity.HE_WEATHER_KEY;
        HttpUtil.sendRequest(address, this, new HttpListener() {
            @Override
            public void httpFinish(String response) {
                try {

                    ArrayList<CityInfo> cityInfoList=AnalyzeData
                            .analyzeJSONCityInfo(response, PrepareDataActivity
                            .this);
                    Log.d(TAG, localTag + "fillDB httpFinish: before fill the " +
                            "CityInfo table.");
                    int countOfRecords=0;

                    try {
                        db.beginTransaction();
                        for (CityInfo cityInfo : cityInfoList) {
                            tryWeatherDB.insertIntoCityInfo(cityInfo);
                            progess.addAndGet(1);
                            countOfRecords++;
                            if (countOfRecords % 500 == 0)
                                showProgress();
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }

                    Log.d(TAG, localTag + "CityInfo table has " +
                            "been filled.");

                    tryWeatherDB.fillCountries();
                    progess.addAndGet(500);
                    showProgress();
                    Log.d(TAG, localTag + "Counttries has been filled! ");
                    cityInfoDuration=System.currentTimeMillis() - cityInfoStart;
                    citiInfoFinished=true;
                    startHandler();

                } catch (Exception e) {
                    handleError(e, "insertIntoCityInfo");
                }

            }

            @Override
            public void httpError(Exception e) {
                handleError(e, "connect address:" + address);
            }
        });
    }


    //读取assets文件夹下面的provinces.txt内容把省列表存入数据库中
    private void fillProvinces() {
        assetManager=this.getAssets();
        try {
            InputStream in=assetManager.open("provinces.txt");
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder provincesBuilder=new StringBuilder();
            String line;
            while ((line=reader.readLine()) != null) {
                provincesBuilder.append(line);
                progess.addAndGet(4);
            }
            Log.d(TAG, localTag + "httpFinish: provinces=" +
                    provincesBuilder.toString());
            in.close();
            reader.close();
            String[] allProvincesNames=AnalyzeData.handleProvincesData
                    (provincesBuilder.toString());
            showProgress();
            for (String name : allProvincesNames) {
                tryWeatherDB.insertIntoProvinces(name);
                progess.addAndGet(1);
            }

        } catch (Exception e) {
            handleError(e, "fillProvinces()");
        }
    }

    private void fillMunicipalitiesAndCounties() {
        final StringBuilder municipalCodes=new StringBuilder();
        final AtomicInteger counterMunicipalities=new AtomicInteger(0);
        final ArrayList<Municipality> municipalityList=new ArrayList<>();


        for (int i=1; i < 35; i++) {
            if (errorHappened) {
                return;
            }
            String code;
            if (i < 10)
                code="0" + i;
            else
                code="" + i;
            final String address="http://www.weather.com" +
                    ".cn/data/list3/city" +
                    code + ".xml";

            HttpUtil.sendRequest(address, this, new HttpListener() {
                @Override
                public void httpFinish(String response) {
                    if (errorHappened) {
                        return;
                    }
                    municipalCodes.append(AnalyzeData.handleMunicipalData
                            (response, municipalityList));
                    showProgress();
                    int countOfSuccess=counterMunicipalities.addAndGet(1);

                    try {
                        if (countOfSuccess == 34) {
                            db.beginTransaction();
                            try {
                                for (Municipality municipality :
                                        municipalityList) {
                                    tryWeatherDB.insertIntoMunicipalities
                                            (municipality);
                                    progess.addAndGet(1);
                                }
                                showProgress();
                                db.setTransactionSuccessful();
                            } finally {
                                db.endTransaction();

                            }
                            String mCodes=municipalCodes.toString();
                            mCodes=mCodes.substring(0, mCodes.length() - 1);
                            Log.d(TAG, localTag +
                                    "fillMunicipalitiesAndCounties: " +
                                    "municipal codes=" + mCodes);
                            editor.putString("municipalCodes", mCodes).commit();
                            fillCounties();
                        }

                    } catch (Exception e) {
                        handleError(e, "insertIntoMunicipalities" + "()");
                    }
                    Log.d(TAG, localTag + "fillMunicipalitiesAndCounties()" +
                            ".httpFinish():" + "after insert one muncipality " +
                            "even if" +
                            " the error has happened.");
                }

                @Override
                public void httpError(Exception e) {
                    handleError(e, "connect address:" + address);
                }
            });
            Log.d(TAG, localTag + "fillMunicipalitiesAndCounties: the main " +
                    "thread in " +
                    "circle:" + i);
//            showProgress();
        }


    }

    private void fillCounties() {

        countiesStart=System.currentTimeMillis();
        String municipalCodes=pre.getString("municipalCodes", "none");
        final AtomicInteger counterOfCounties=new AtomicInteger(0);
        final ArrayList<County> wholeCountyList=new ArrayList<>();

        if (municipalCodes.equals("none")) {
            Log.d(TAG, localTag + "fillCounties: absence of municipal codes");
            return;
        }
        final String[] codes=municipalCodes.split(",");


        for (int i=0; i < codes.length; i++) {
            if (errorHappened) {
                return;
            }
            final String address="http://www.weather.com.cn/data/list3/city" +
                    codes[i] + ".xml";
            HttpUtil.sendRequest(address, this, new HttpListener() {
                @Override
                public void httpFinish(String response) {
                    if (errorHappened) {
                        return;
                    }
                    AnalyzeData.handleCountiesData(response, wholeCountyList);
                    int successNumber=counterOfCounties.addAndGet(1);
                    showProgress();

                    try {
                        if (successNumber == codes.length) {
                            Log.d(TAG, localTag + "httpFinish: begin to insert " +
                                    "into" +
                                    " Counties!");
                            showProgress();
                            db.beginTransaction();
                            try {
                                int counter=0;
                                for (County county : wholeCountyList) {
                                    tryWeatherDB.insertIntoCounties(county);
                                    progess.addAndGet(1);
                                    counter++;
                                    if (counter % 100 == 0) {
                                        showProgress();
                                    }
                                }
//                                showProgress();

                                db.setTransactionSuccessful();
                            } finally {
                                db.endTransaction();
                            }
                            Log.d(TAG, localTag + "the table of Counties has " +
                                    "been " +
                                    "filled!");
                            countiesDuration=System.currentTimeMillis() -
                                    countiesStart;
                            countiesFinished=true;
                            synchronized (this) {
                                long waitStart=System.currentTimeMillis();
                                while (System.currentTimeMillis() - waitStart
                                        < 42000) {
                                    wait(50);
                                    progess.addAndGet(1000);
                                    showProgress();
                                }
                            }


                        }
                    } catch (Exception e) {
                        handleError(e, "insertIntoCounties()");
                    }


                }

                @Override
                public void httpError(Exception e) {
                    handleError(e, "connect address:" + address);
                }
            });

        }
    }

    public synchronized void handleError(Exception e, String transaction) {

        e.printStackTrace();
        errorReport="error happend during " + transaction +
                "  error=" + e.toString() + " " + e.getMessage();
        Log.d(TAG, localTag + errorReport);

        if (tryWeatherDB.clearDatabase(this)) {
            Log.d(TAG, localTag + "handleError: has cleared database!");
        }
        editor.clear().apply();
        errorHappened=true;
        startHandler();
//        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
//        Log.d(TAG, localTag + "this line shouldn't been excuted!");
    }

    public void showProgress() {
        Message msgs=new Message();
        msgs.what=SHOW_PROGRESS;
        handler.sendMessage(msgs);
    }

    public void clear() {
        Log.d(TAG, localTag + "clear: ");

        try {
            tryWeatherDB.clearTables();
        } catch (Exception e) {
            handleError(e, "cleartTables()");
        }
        editor.clear().apply();

    }


    @Override

    protected void onStart() {
        super.onStart();
        Log.d(TAG, localTag + "  onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, localTag + "  onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, localTag + "onRestart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, localTag + "  onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, localTag + "  onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, localTag + "  onDestroy: ");
    }


}
