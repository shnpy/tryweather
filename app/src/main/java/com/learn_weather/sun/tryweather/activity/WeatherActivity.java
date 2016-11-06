package com.learn_weather.sun.tryweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

/**
 * Created by Sun on 2016/10/24.


/**
 * 采用天气API网站
 *  http://www.heweather.com/documents/api
 *  以及
 *  http://www.thinkpage.cn/doc  http://www.thinkpage.cn/doc#error
 * 以及
 * http://openweathermap.org/forecast5#5days
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
 *
 */






public class WeatherActivity extends Activity {
    public final static String OWM_KEY="ff14cc1f3f6ea9e99963fe573e7c72f7";
    public final static String EXAMPLE_OF_APICALL="api.openweathermap" +
            ".org/data/2.5/forecast/city?id=524901&APPID=" + OWM_KEY;

    public  final static String
            HE_WEATHER_KEY="c169c0517ed241028ec13ba46ed1c764";
    public final static String
            THINKPAGE_WEATHER_KEY="dnmx7ip5jqu1xqkq";
    public final static String THINKPAGE_USER_ID="U1CADB8472";

    private final static String BAIDU_API_KEY="6e5cdbcb77d8ab4f4ad679abc545167a";

    //所有城市Id列表
 //https://api.heweather.com/x3/citylist?search=allchina&key=c169c0517ed241028ec13ba46ed1c764

    //翻译Api的链接
    //http://api.fanyi.baidu.com/api/trans/product/apidoc





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences
                (this);




        //setContentView();
    //    String countyName=locateCurrentPostion()//定位并显示当前位置，返回所在城市
      //  showWeather(countyName);
        //setMenu()
        //setMenuItemClickListener(){
        // }
    }

    private String locateCurrentPostion() {
        return null;
    }
}
