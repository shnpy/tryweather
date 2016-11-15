package com.learn_weather.sun.tryweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learn_weather.sun.tryweather.mode.CityInfo;
import com.learn_weather.sun.tryweather.mode.County;
import com.learn_weather.sun.tryweather.mode.Municipality;
import com.learn_weather.sun.tryweather.mode.OpenWeatherCity;

/**
 * Created by Sun on 2016/10/28.
 */

public class TryWeatherDB {
    private static final String TAG="TT";
    private static final String localTag="TryWeatherDB__";

    public static final String DB_NAME="administrative_districts_db";
    public static final int VERSION=1;

    private static TryWeatherDB tryWDatabase;
    private SQLiteDatabase db;


    private TryWeatherDB(Context context) {
        SQLiteOpenHelper dbHelper=new TryWeatherDBhelper(context, DB_NAME,
                null, VERSION);
        db=dbHelper.getWritableDatabase();

    }


    public synchronized static TryWeatherDB getInstanceOfTryWeatherDB(Context context) {
        Log.d(TAG, localTag + "getInstanceOfTryWeatherDB: ");
        if (tryWDatabase == null) {
            tryWDatabase=new TryWeatherDB(context);
        }
        return tryWDatabase;
    }

    //将城市信息插入CityInfo表格
    public synchronized void insertIntoCityInfo(CityInfo cityInfo) throws
            Exception {
        String tableName="CityInfo";
       /* String insertStatement="insert into " + tableName + "(" +
                "city,city_id,country,lat,lon,prov) values(" +
                "'"+cityInfo.getCity() + "','" +
                cityInfo.getCityId() + "','" +
                cityInfo.getCountry() + "'," +
                cityInfo.getLatitude() + "," +
                cityInfo.getLongitude() + ",'" +
                cityInfo.getProvince() + "')";*/
        String insertStatement="insert into " + tableName +
                "(city,city_id,country,lat,lon,prov)values(?,?,?,?,?,?)";
        String[] values=new String[]{cityInfo.getCity(), cityInfo.getCityId()
                , cityInfo.getCountry(), cityInfo.getLatitude() + "",
                cityInfo.getLongitude() + "", cityInfo.getProvince()};
        db.execSQL(insertStatement, values);
    }

    //从CityInfo表格查询country字段填充入Country表格
    public void fillCountries() throws Exception {
        Log.d(TAG, localTag + "fillCountries: begin");
        String fillCountries="insert into Countries(name) select country from" +
                " CityInfo group by country";
        db.execSQL(fillCountries);


    }

    // 插入中国省列表
    public synchronized void insertIntoProvinces(String provinceName) throws
            Exception {

        String insertIntoProvinces="insert into Provinces(name) " +
                "values('" + provinceName + "')";
        db.execSQL(insertIntoProvinces);
    }

    //插入市级行政区列表
    public synchronized void insertIntoMunicipalities(Municipality
                                                              municipality)
            throws Exception {
        String municipalName=municipality.getName();
        String municipalCode=municipality.getCode();
        String insertIntoMunicipalities="insert into Municipalities" +
                "(name,municipal_code) values('" +
                municipalName + "','" + municipalCode + "')";
        db.execSQL(insertIntoMunicipalities);
    }

    //插入县级行政区列表
    public synchronized void insertIntoCounties(County county) throws
            Exception {
        String insertIntoCounties="insert into Counties(name,county_code)" +
                " values('" + county.getCountyName() + "','" + county
                .getCountyCode() + "')";
        db.execSQL(insertIntoCounties);
    }

    //删除指定表格中的所有内容

    public void clearTable(String table) throws Exception {
        String deleteRecords="delete from " + table;
        db.execSQL(deleteRecords);
        String resetSequence="update sqlite_sequence set seq=0 where " +
                "name='" + table + "';";
        db.execSQL(resetSequence);
    }

    public boolean clearDatabase(Context context) {

        return context.deleteDatabase("administrative_districts_db");
    }
    public void clearTables() throws Exception{
        db.beginTransaction();
        try {
            clearTable("CityInfo");
            clearTable("Countries");
            clearTable("Provinces");
            clearTable("Municipalities");
            clearTable("Counties");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public void createOWCities() throws Exception{
        String createOpenWeatherCities="CREATE TABLE OpenWeatherCities(id " +
                "integer primary key autoincrement,cityId integer,cityName " +
                "text,country text,longitude real,latitude real)";
        db.execSQL(createOpenWeatherCities);
    }
    public void insertIntoOWCities(OpenWeatherCity city) throws Exception{

        String insertStatement="insert into OpenWeatherCities values(null," +
                "?,?,?,?,?)";
        String[] values={city.getCityId()+"",city.getCityName(),
        city.getCountry(),city.getLongitude()+"",city.getLatitude()+""};
        db.execSQL(insertStatement,values);
    }



    public SQLiteDatabase getDb() {
        return db;
    }
}
