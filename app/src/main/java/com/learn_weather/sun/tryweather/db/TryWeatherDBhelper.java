package com.learn_weather.sun.tryweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sun on 2016/10/28.
 */

public class TryWeatherDBhelper extends SQLiteOpenHelper {
     private static final String TAG="TT";
    private final String localTag=getClass().getSimpleName() + "__";

    private final String CREATE_COUNTRIES="create table Countries " +
            "(id integer primary key autoincrement,name text)";
    private final String CREATE_PROVINCES="create table Provinces(" +
            "id integer primary key autoincrement,name text)";
    private final String CREATE_Municipalities="create table Municipalities(" +
            "id integer primary key autoincrement,name text,municipal_code text)";
    private final String CREATE_CITIES="create table Counties(" +
            "id integer primary key autoincrement,name text,county_code " +
            "text)";
    private final String CREATE_CITY_INFO="create table CityInfo(" +
            "id integer primary key autoincrement,city text,city_id text," +
            "country text,lat real,lon real,prov text)";


    public TryWeatherDBhelper(Context context, String name, SQLiteDatabase
            .CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d(TAG, localTag + "TryWeatherDBhelper: create Database?");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, localTag + "onCreate: begin to create tables of administrative_districts_db");
        db.execSQL(CREATE_COUNTRIES);
        db.execSQL(CREATE_PROVINCES);
        db.execSQL(CREATE_Municipalities);
        db.execSQL(CREATE_CITIES);
        db.execSQL(CREATE_CITY_INFO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
