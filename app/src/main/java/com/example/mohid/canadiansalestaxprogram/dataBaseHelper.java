package com.example.mohid.canadiansalestaxprogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mohid on 2016-08-09.
 */
public class dataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UserTax.db";
    public static final String TABLE_NAME = "UserInfoTax";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "BEFORE_TAX";
    public static final String COL_3 = "PST_TAX";
    public static final String COL_4 = "GST_TAX";
    public static final String COL_5 = "HST_TAX";
    public static final String COL_6 = "TOTAL_AMOUNT";
    public static final String COL_7 = "DATE_TRANS";


    public dataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, BEFORE_TAX INTEGER, PST_TAX INTEGER, GST_TAX INTEGER, HST_TAX INTEGER, TOTAL_AMOUNT INTEGER, DATE_TRANS TEXT) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String beforeTax, String pst, String gst, String hst, String total, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(COL_2, beforeTax);
        contentValue.put(COL_3, pst);
        contentValue.put(COL_4, gst);
        contentValue.put(COL_5, hst);
        contentValue.put(COL_6, total);
        contentValue.put(COL_7, date);
        long result = db.insert(TABLE_NAME, null, contentValue);
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }

    }
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("Select * from " + TABLE_NAME, null);
        return result;
    }
}
