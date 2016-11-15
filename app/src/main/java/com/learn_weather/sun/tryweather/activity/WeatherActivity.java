package com.learn_weather.sun.tryweather.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.learn_weather.sun.tryweather.R;
import com.learn_weather.sun.tryweather.db.TryWeatherDB;
import com.learn_weather.sun.tryweather.mode.Gps;
import com.learn_weather.sun.tryweather.mode.OWMTemperature;
import com.learn_weather.sun.tryweather.mode.OWMWeatherCondition;
import com.learn_weather.sun.tryweather.mode.OWMWeatherInfo;
import com.learn_weather.sun.tryweather.mode.Pressure;
import com.learn_weather.sun.tryweather.util.AnalyzeData;
import com.learn_weather.sun.tryweather.util.HttpListener;
import com.learn_weather.sun.tryweather.util.HttpUtil;
import com.learn_weather.sun.tryweather.util.MathUtil;
import com.learn_weather.sun.tryweather.util.PositionUtil;
import com.learn_weather.sun.tryweather.util.Translate;
import com.learn_weather.sun.tryweather.util.TranslateListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

;

/**
 * Created by Sun on 2016/10/24.
 * <p>
 * <p>
 * /**
 * 采用天气API网站
 * http://www.heweather.com/documents/api
 * 以及
 * http://www.thinkpage.cn/doc  http://www.thinkpage.cn/doc#error
 * 以及
 * http://openweathermap.org/forecast5#5days
 * 例子
 * http://api.openweathermap.org/data/2
 * .5/forecast?id=524901&appid=ff14cc1f3f6ea9e99963fe573e7c72f7
 */

/**
 * 基本思路，进入页面，根据手机所在地址，定位所在城市，从数据库中查询获取该城市对应的
 * 城市ID，根据该城市 ID查询获取对应的天气数据和空气质量数据。
 * 页面上设置一个菜单（也可设置一个按钮切换到新的选择页面），菜单名称查看其它地区天气。菜单有
 * 五个选项本地，同省，同国，国内景点，世界城市，。同省选项仅当位于中国时适用。
 * 点击同省选项，显示同省下的市级viewList
 * 点击市，显示该市下的县级区域。如果是直辖市，直接显示区县级单位。当判断点击的是县级区域，
 * 则获取该县级区域的城市代码，查询对应的天气数据，并跳转到天气显示页面。
 * 同国类似，多一个省级列表。
 * viewlist里的itemview可定制，对省、市、县显示简称，简单介绍。（也可略过，直接用缺省的simpleitemview）
 * 如果点击世界，则先进入国家列表，需要定制itemview，一行最左侧是国旗图标，然后是中文国名下附英文国名
 * 然后是该国的简单介绍
 * 点击国名进入该国城市页面，itemview只需要显示该城市中英文名称即可
 * 点击城市显示天气状况。
 */


public class WeatherActivity extends Activity {
    private static final String TAG="ZZZZZTT";
    private final String localTag=getClass().getSimpleName() + "__";

    public final static String OWM_KEY="ff14cc1f3f6ea9e99963fe573e7c72f7";
    public final static String EXAMPLE_OF_APICALL="api.openweathermap" +
            ".org/data/2.5/forecast/city?id=524901&APPID=" + OWM_KEY;

    public final static String
            HE_WEATHER_KEY="c169c0517ed241028ec13ba46ed1c764";
    public final static String THINKPAGE_WEATHER_KEY="dnmx7ip5jqu1xqkq";
    public final static String THINKPAGE_USER_ID="U1CADB8472";

    private final static String
            BAIDU_API_KEY="6e5cdbcb77d8ab4f4ad679abc545167a";

    private SharedPreferences pre;
    private SharedPreferences.Editor editor;

    private Button refreshButton;


    private TextView currentCity;
    private TextView countryProvince;
    private TextView englishName;

    private TextView publishTime;
    private TextView todayDate;
    private TextView alarm;

    private TextView dayInfo;
    private TextView nightInfo;
    private TextView temp;
    private TextView wind;
    private TextView pressure;
    private TextView suggestion;

    private TextView ambientTemperature;
    private TextView lightSensor;
    private TextView accelerometerSensor;
    private TextView magnetSensor;
    private TextView pressureSensor;
    private TextView humiditySensor;

    private TextView secondDay;
    private TextView thirdDay;
    private TextView fourthDay;
    private TextView fifthDay;

    private ProgressBar loadingWeather;


    //所有城市Id列表
   /* https://api.heweather
     .com/x3/citylist?search=allchina&key=c169c0517ed241028ec13ba46ed1c764*/

    //翻译Api的链接
    //http://api.fanyi.baidu.com/api/trans/product/apidoc

    public LocationClient locationClient=null;
    public BDLocationListener myLocListener=new MyLocationListener();

    private static final int SHOW_WEATHER=1;
    private static final int LOCATE_FAILURE=2;
    private static final int ERROR_HAPPENED=3;

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_WEATHER:
                    loadingWeather.setVisibility(View.GONE);
                    currentCity.setText(pre.getString("current_city",
                            "vacant"));
                    countryProvince.setText(pre.getString("country_province",
                            "vacant"));
                    englishName.setText((pre.getString("english_name",
                            "vacant")));
                    todayDate.setText(pre.getString("today_date", "vacant"));
                    dayInfo.setText(pre.getString("today_dayinfo", "vacant"));
                    nightInfo.setText(pre.getString("today_nightinfo",
                            "vacant"));
                    temp.setText(pre.getString("today_temp", "vacant"));
                    wind.setText(pre.getString("today_wind", "vacant"));
                    pressure.setText(pre.getString("today_pressure", "vacant"));

                    secondDay.setText(pre.getString("second_day", "vacant"));
                    thirdDay.setText(pre.getString("third_day", "vacant"));
                    fourthDay.setText(pre.getString("fourth_day", "vcanant"));
                    fifthDay.setText(pre.getString("fifth_day", "vacant"));
                    break;

                case ERROR_HAPPENED:
                    Log.d(TAG, localTag + "handleMessage: error happened");
                    AlertDialog.Builder dialog=new AlertDialog.Builder
                            (WeatherActivity.this);
                    dialog.setTitle("程序遇到错误");
                    dialog.setMessage((String) msg.obj);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("从列表选择城市", new DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(WeatherActivity.this, "从列表中选择城市",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.setNegativeButton("退出", new DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, localTag + "终止程序！");
                            finish();
                            android.os.Process.killProcess(android.os.Process
                                    .myPid());
                            System.exit(0);
                        }
                    });
                    dialog.show();
                default:
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.show_weather);
        pre=PreferenceManager.getDefaultSharedPreferences(this);
        editor=pre.edit();

        refreshButton=(Button) findViewById(R.id.refesh);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingWeather.setVisibility(View.VISIBLE);
                locatePostion();
                Toast.makeText(WeatherActivity.this, "Loading weather " +
                        "information,please " +
                        "wait " +
                        "paitently", Toast.LENGTH_LONG).show();

            }
        });

        currentCity=(TextView) findViewById(R.id.current_city);
        countryProvince=(TextView) findViewById(R.id.country_province);
        englishName=(TextView) findViewById(R.id.english_name);

        publishTime=(TextView) findViewById(R.id.publish_time);

        todayDate=(TextView) findViewById(R.id.today_date);
        alarm=(TextView) findViewById(R.id.alarm);

        dayInfo=(TextView) findViewById(R.id.text_dayinfo);
        nightInfo=(TextView) findViewById(R.id.text_nightinfo);
        temp=(TextView) findViewById(R.id.temp);

        wind=(TextView) findViewById(R.id.wind);
        pressure=(TextView) findViewById(R.id.pressure);

        secondDay=(TextView) findViewById(R.id.second_day);
        thirdDay=(TextView) findViewById(R.id.third_day);
        fourthDay=(TextView) findViewById(R.id.fourth_day);
        fifthDay=(TextView) findViewById(R.id.fifth_day);

        loadingWeather=(ProgressBar) findViewById(R.id.loading_weather);

        long savedTime=pre.getLong("savedTime", 0);
        if ((System.currentTimeMillis() - savedTime) > 6 * 3600 * 1000) {
            loadingWeather.setVisibility(View.VISIBLE);
            locatePostion();
            Toast.makeText(this, "Loading weather information,please wait " +
                    "paitently", Toast.LENGTH_LONG).show();
        } else {
            showWeatherInfo();
        }
        //  showWeather(countyName);
        //setMenu()
        //setMenuItemClickListener(){
        // }
    }


    private void locatePostion() {
        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myLocListener);
        initLocation();
        Log.d(TAG, localTag + "locatePostion: start!");
        locationClient.start();
        Log.d(TAG, localTag + "after locationClient start");
    }


    private void initLocation() {
        Log.d(TAG, localTag + "initLocation: ");
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode
                .Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("wgs84");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要

       /* int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的*/

        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);
// 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        locationClient.setLocOption(option);
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            //Receive Location
            Log.d(TAG, localTag + "onReceiveLocation: location is returned!");
            String district=location.getDistrict();
            final String city=location.getCity();
            String province=location.getProvince();
            String country=location.getCountry();

            String cityCode=location.getCityCode();
            Log.d(TAG, localTag + "onReceiveLocation: cityCod=" + cityCode);
            int errorCode=location.getLocType();

            String locateError="";
            switch (errorCode) {
                case 61:
                    locateError="GPS定位结果，GPS定位成功";
                    Log.d(TAG, localTag + locateError);
                    break;
                case 62:
                    locateError="无法获取有效定位依据，定位失败，请检查运营商网络" +
                            "或者WiFi网络是否正常开启，尝试重新请求定位。";

                    break;
                case 63:
                    locateError="网络异常，没有成功向服务器发起请求，请确认当前测试" +
                            "手机网络是否通畅，尝试重新请求定位。";

                    break;

                case 65:
                    locateError="定位缓存的结果";
                    break;
                case 66:
                    locateError="离线定位结果。通过requestOfflineLoc" +
                            "aiton调用时对应的返回结果。";
                    break;
                case 67:
                    Log.d(TAG, localTag + "离线定位失败。通过requestOfflineLoc" +
                            "aiton调用时对应的返回结果。");
                    break;
                case 68:
                    locateError="网络连接失败时，查找本地离线定位时对" + "应的返回结果。";
                    break;
                case 161:
                    locateError="网络定位结果，网络定位成功。";
                    break;
                case 162:
                    locateError=" 请求串密文解析失败，一般是由于客户" +
                            "端SO文件加载失败造成，请严格参照开发指南或demo开发" +
                            "，放入对应SO文件。";
                    break;
                case 167:
                    locateError="服务端定位失败，请您检查是否禁用" + "获取位置信息权限，尝试重新请求定位。";
                    break;
                case 502:
                    locateError="AK参数错误，请按照说明文档重新" + "申请AK。";
                    break;
                case 505:
                    locateError="AK不存在或者非法，请按照说明文档重" + "新申请AK。";
                    break;
                case 601:
                    locateError="AK服务被开发者自己禁用，请按照说明文" + "档重新申请AK。";

                    break;
                case 602:
                    locateError="key mcode不匹配，您的AK配置过程中" +
                            "安全码设置有问题，请确保：SHA1正确，“;”分号是" +
                            "英文状态；且包名是您当前运行应用的包名，请按照说明" +
                            "文档重新申请AK。";
                    break;
                default:
                    locateError="AK验证失败，请按照说明文档重新申请" + "AK。";
                    break;
            }
            if (errorCode != 61 && errorCode != 161) {
                Log.d(TAG, localTag + "onReceiveLocation: errorCode=" +
                        errorCode);
                Message msg=new Message();
                msg.what=ERROR_HAPPENED;
                msg.obj=locateError + "\n需要从列表中选择城市吗？若取消则退出" +
                        "程序。";
                handler.sendMessage(msg);
                return;
            }
            editor.putString("current_city", district);

            if (city.equals("上海市") || city.equals("北京市") || city.equals
                    ("天津市") || city.equals("重庆市")) {
                editor.putString("country_province", country + city);
            } else {
                editor.putString("country_province", country + province + city);
            }
            /*String halfTitle=titleBuilder.toString();
            Log.d(TAG, localTag + "Chinese title=" + halfTitle);*/
            try {
                Translate.fromZhTOEn(district, new TranslateListener() {
                    @Override
                    public void translateFinish(String result) {
                        Log.d(TAG, localTag + "onReceiveLocation english " +
                                "name=" + result);
                        editor.putString("english_name", result);

                        String[] titleArray=result.split(" ");
                        String inquiryName=titleArray[0];
                        Log.d(TAG, localTag + "translateFinish: the name used" +
                                " to inquiry database is " + inquiryName);

                        double longitude=location.getLongitude();
                        double latitude=location.getLatitude();

                        Log.d(TAG, localTag + "onReceiveLocation: " +
                                "\ncity=" + inquiryName +
                                "\nlongitude(gcj02)=" + longitude +
                                "\nlatitude(gcj02)=" + latitude);
                        Gps wgs84Coordinate=PositionUtil.gcj_To_Gps84
                                (longitude, latitude);
                        double lon=wgs84Coordinate.getWgLon();
                        double lat=wgs84Coordinate.getWgLat();
                        Log.d(TAG, localTag + "Coordinate of " + inquiryName +
                                ":(" + lat + "," + lon + ")");

                        String address="http://api.openweathermap.org/data/2"
                                + "" +
                                ".5/forecast/daily?";


                        int cityId=inquiryId(inquiryName, lon, lat);
                        if (cityId != -1) {
                            address+="id=" + cityId;
                        } else {
                            address+="lat=" + lat + "&lon=" + lon;
                        }
                        address+="&cnt=5&appid=" + OWM_KEY + "&lang=zh_cn";
                        obtainWeatherInfo(address);

                    }

                    @Override
                    public void translateError(Exception e) {
                        handleError(e, " translate()");
                    }
                });
            } catch (Exception e) {
                handleError(e, "handle the result of translate and inquiry "
                        + "database!");
            }

        }

    }

    public int inquiryId(String cityName, double lon, double lat) {
        int cityId=-1;
        TryWeatherDB tryWeatherDB=TryWeatherDB.getInstanceOfTryWeatherDB(this);
        SQLiteDatabase db=tryWeatherDB.getDb();
        String[] args={cityName + "%", lon + "", lat + ""};
        Cursor cursor;
        try {
            cursor=db.rawQuery("select cityId from OpenWeatherCities " +
                    "where" + " cityName like ? and (longitude-?)*" +
                    "(longitude-?)" +
                    "<=0.1", args);
            Log.d(TAG, localTag + "inquiryId: the number of records required " +
                    "coditions is " + cursor.getCount());

            if (cursor.moveToFirst() && cursor.getCount() == 1) {
                cityId=cursor.getInt(cursor.getColumnIndex("cityId"));
            }
        } catch (Exception e) {
            handleError(e, "inquirying cityId from database");
        } finally {
            Log.d(TAG, localTag + "inquiryId: failure");
        }
        return cityId;
    }

    private void obtainWeatherInfo(String address) {

        HttpUtil.sendRequest(address, new HttpListener() {
            @Override
            public void httpFinish(String response) {
                try {
                    ArrayList<OWMWeatherInfo> weatherInfoList=AnalyzeData
                            .analyzeJsonOWM(response);
                    saveWeatherInfo(weatherInfoList);
//
                    showWeatherInfo();
                } catch (Exception e) {
                    handleError(e, "obtainWeatherInfo");
                }
            }

            @Override
            public void httpError(Exception e) {
                handleError(e, "obtainWeatherInfo()");
            }
        });

    }

    private void saveWeatherInfo(ArrayList<OWMWeatherInfo> weatherInfoList) {

        SimpleDateFormat todayDateFormat=new SimpleDateFormat
                ("yyyy年MM月dd日EE", Locale.CHINA);

        OWMWeatherInfo todayWeather=weatherInfoList.get(0);

        long dt=todayWeather.getDataTime();
        String todayDate=todayDateFormat.format(new Date(dt * 1000));
        editor.putString("today_date", todayDate);

        OWMWeatherCondition todayCondition=todayWeather.getWeatherCondition();
        String dayInfo=todayCondition.getDescription();
        OWMTemperature temperature=todayWeather.getOwmTemperature();
        temperature.changeUnit(OWMTemperature.Celsius);
        temperature.specifyDecimal(1);
        dayInfo+="  "+temperature.getMin() + "℃～" + temperature.getMax()
                + "℃";
        editor.putString("today_dayinfo", dayInfo);

        String nightInfo="夜晚温度:" + temperature.getNight() + "℃";
        editor.putString("today_nightinfo", nightInfo);

        String temp="早晨温度：" + temperature.getMorn() + "℃" + "\n傍晚温度：" +
                temperature.getEve() + "℃";
        editor.putString("today_temp", temp);
        String wind="风速：" + todayWeather.getWindSpeed() + "米/秒" + "  " +
                "\n风向：" + todayWeather.getWindDegree() + "°（正北风为0°，顺时针旋转）";
        editor.putString("today_wind", wind);
        Pressure pressure=todayWeather.getPressure();
        Pressure atm=pressure.alterUnit(pressure, Pressure.atm);
        String press="气压：" + MathUtil.speDec(pressure.getValue(), 2) + "hPa" +
                "(百帕)。相当于" + MathUtil.speDec(atm.getValue(), 3) + "大气压";
        editor.putString("today_pressure", press);


        OWMWeatherInfo secondWeather=weatherInfoList.get(1);
        OWMWeatherInfo thirdWeather=weatherInfoList.get(2);
        OWMWeatherInfo fourthWeather=weatherInfoList.get(3);
        OWMWeatherInfo fifthWeather=weatherInfoList.get(4);

        SimpleDateFormat futureDateFormat=new SimpleDateFormat("dd日EE",
                Locale.CHINA);
        String secondDate=futureDateFormat.format(new Date(secondWeather
                .getDataTime() * 1000));
        String thirdDate=futureDateFormat.format(new Date(thirdWeather
                .getDataTime() * 1000));
        String fourthDate=futureDateFormat.format(new Date(fourthWeather
                .getDataTime() * 1000));
        String fifthDate=futureDateFormat.format(new Date(fifthWeather
                .getDataTime() * 1000));

        OWMWeatherCondition secondCondition=secondWeather.getWeatherCondition();
        OWMWeatherCondition thirdCondition=thirdWeather.getWeatherCondition();
        OWMWeatherCondition fourthCondition=fourthWeather.getWeatherCondition();
        OWMWeatherCondition fifthCondition=fifthWeather.getWeatherCondition();

        String secondDescription=secondCondition.getDescription();
        String thirdDescription=thirdCondition.getDescription();
        String fourthDescription=fourthCondition.getDescription();
        String fifthDescription=fifthCondition.getDescription();

        OWMTemperature secondTemptature=secondWeather.getOwmTemperature();
        OWMTemperature thirdTemptature=thirdWeather.getOwmTemperature();
        OWMTemperature fourthTemptature=fourthWeather.getOwmTemperature();
        OWMTemperature fifthTemptature=fifthWeather.getOwmTemperature();

        secondTemptature.changeUnit(OWMTemperature.Celsius);
        secondTemptature.specifyDecimal(2);

        thirdTemptature.changeUnit(1);
        thirdTemptature.specifyDecimal(2);

        fourthTemptature.changeUnit(1);
        fourthTemptature.specifyDecimal(2);

        fifthTemptature.changeUnit(1);
        fifthTemptature.specifyDecimal(2);

        String secondTem=secondTemptature.getMin() + "℃～" + secondTemptature
                .getMax() + "℃";
        String thirdTem=thirdTemptature.getMin() + "℃～" + thirdTemptature
                .getMax() + "℃";
        String fourthTem=fourthTemptature.getMin() + "℃～" + fourthTemptature
                .getMax() + "℃";
        String fifthTem=fifthTemptature.getMin() + "℃～" + fifthTemptature
                .getMax() + "℃";

        String secondDay=secondDate + "\n" + secondDescription + "\n" +
                secondTem + "    ";
        String thirdDay=thirdDate + "\n" + thirdDescription + "\n" + thirdTem +
                "    ";
        String fourthDay=fourthDate + "\n" + fourthDescription + "\n" +
                fourthTem + "    ";
        String fifthDay=fifthDate + "\n" + fifthDescription + "\n" + fifthTem
                + "    ";

        editor.putString("second_day", secondDay);
        editor.putString("third_day", thirdDay);
        editor.putString("fourth_day", fourthDay);
        editor.putString("fifth_day", fifthDay);
        editor.putLong("savedTime", System.currentTimeMillis());
        editor.commit();


    }

    private void showWeatherInfo() {
        Message msg=new Message();
        msg.what=SHOW_WEATHER;
        handler.sendMessage(msg);
    }

    private void handleError(Exception e, String s) {
        e.printStackTrace();
        String errorMsg="error happed during " + s + "! error=" + e.toString
                () + " " + e.getMessage();
        Log.d(TAG, localTag + errorMsg);
        Message msg=new Message();
        msg.what=ERROR_HAPPENED;
        msg.obj=errorMsg;
        handler.sendMessage(msg);
    }


}
