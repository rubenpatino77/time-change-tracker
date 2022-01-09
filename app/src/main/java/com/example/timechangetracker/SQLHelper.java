package com.example.timechangetracker;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.service.autofill.UserData;

import androidx.annotation.Nullable;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Queue;

public class SQLHelper extends SQLiteOpenHelper {
    public SQLHelper(Context context) {
        super(context, "UserData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create Table UserDetails(locationName TEXT primary key, address TEXT, city TEXT, state TEXT, time TEXT, dayOfCheckbox INT, oneDayCheckbox INT, twoDayCheckbox INT, threeDayCheckbox INT, fourDayCheckbox INT, fiveDayCheckbox INT, sixDayCheckbox INT, sevenDayCheckbox INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL("drop Table if exists UserDetails");
    }

    public boolean insertUserData(String locationName, String address, String city, String state, String time, int dayOfCheckbox, int oneDayCheckbox, int twoDayCheckbox, int threeDayCheckbox, int fourDayCheckbox, int fiveDayCheckbox, int sixDayCheckbox, int sevenDayCheckbox){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("locationName", locationName);
        values.put("address", address);
        values.put("city", city);
        values.put("state", state);
        values.put("time", time);
        values.put("dayOfCheckbox", dayOfCheckbox);
        values.put("oneDayCheckbox", oneDayCheckbox);
        values.put("twoDayCheckbox", twoDayCheckbox);
        values.put("threeDayCheckbox", threeDayCheckbox);
        values.put("fourDayCheckbox", fourDayCheckbox);
        values.put("fiveDayCheckbox", fiveDayCheckbox);
        values.put("sixDayCheckbox", sixDayCheckbox);
        values.put("sevenDayCheckbox", sevenDayCheckbox);

        long add = database.insert("UserDetails", null, values);
        if(add == -1){
            return false;
        } else {
            return true;
        }
    }

    public boolean updateUserData(String locationName, String address, String city, String state, int dayOfCheckbox, int oneDayCheckbox, int twoDayCheckbox, int threeDayCheckbox, int fourDayCheckbox, int fiveDayCheckbox, int sixDayCheckbox, int sevenDayCheckbox){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dayOfCheckbox", dayOfCheckbox);
        values.put("oneDayCheckbox", oneDayCheckbox);
        values.put("twoDayCheckbox", twoDayCheckbox);
        values.put("threeDayCheckbox", threeDayCheckbox);
        values.put("fourDayCheckbox", fourDayCheckbox);
        values.put("fiveDayCheckbox", fiveDayCheckbox);
        values.put("sixDayCheckbox", sixDayCheckbox);
        values.put("sevenDayCheckbox", sevenDayCheckbox);

        Cursor cursor = database.rawQuery("Select * from UserDetails where locationName = ? AND address = ? AND city = ? AND state = ?", new String[] {locationName, address, city, state});
        if(cursor.getCount() > 0) {
            long add = database.update("UserDetails", values, "locationName=?", new String[]{locationName});
            if (add == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    public boolean deleteUserData(String locationName, String address, String city, String state){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("Select * from UserDetails where locationName = ? AND address = ? AND city = ? AND state = ?", new String[] {locationName, address, city, state});
        if(cursor.getCount() > 0) {
            long delete = database.delete("UserDetails", "locationName=?", new String[]{locationName});
            if (delete == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    public Cursor getData(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("Select * from UserDetails", null);
        return cursor;
    }

    public Integer[] getCheckboxes(String name, String address, String city, String state){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("Select * from UserDetails where locationName = ? AND address = ? AND city = ? AND state = ?", new String[] {name, address, city, state});
        Integer[] checkbox0 = new Integer[8];
        String[] test = new String[3];
        test[0] = cursor.getString(0);
        test[1] = cursor.getString(1);
        checkbox0[2] = cursor.getInt(2);
        checkbox0[3] = cursor.getInt(3);
        checkbox0[4] = cursor.getInt(4);
        checkbox0[5] = cursor.getInt(5);
        return checkbox0;
    }
}

