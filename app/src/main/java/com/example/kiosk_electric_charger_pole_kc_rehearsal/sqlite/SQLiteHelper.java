package com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mytestgo.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteHelper mInstance = null;

    private SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteHelper getInstance(Context context){
        if(mInstance ==null)
            mInstance = new SQLiteHelper(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS MeterValues");

        sqLiteDatabase.execSQL("create table MeterValues (Time text primary key, EAIR text,EAII text, CI text, CO text, PAI text, PO text, SoC text, TEM text, Idtag text, Type text);"); // Type : 1 = Aligned, 2 = Sampled
        sqLiteDatabase.execSQL("create table TransactionFactory (Time text primary key, TID text, Reason text, Meterstop text);");
        sqLiteDatabase.execSQL("create table NotificationFactory (CId text primary key, ErrorCode text, Status text, Info text, Time text , VendorId text, VendorErrorcode text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS MeterValues");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TransactionFactory");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS NotificationFactory");
//        onCreate(sqLiteDatabase);
    }



}