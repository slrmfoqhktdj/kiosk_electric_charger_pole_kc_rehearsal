package com.example.kiosk_electric_charger_pole_kc_rehearsal.model;

import android.os.Environment;

public class DeviceModel {
//    private static final String Deviceid = "pole_device_02";
    private static final String Deviceid = "pole_kc_rehearsal";

    private static final String Devicevendor = "IF";
//    private static final String Devicelatitude = "35.187726962762";
//    private static final String Devicelongitude = "126.91088663129";
//    private static final String DeviceAddr = "광주광역시북구오치동";


//    private static final String Devicemodel = "pole";
    private static final String Devicemodel = "rehearsal";
    private static final String PAYvendor = "NVCAT";

//    private static final String WHEATHERvender = "TurbineCrew";
    private static final String WHEATHERvender = "Meterorological";

    private static final String DeviceLogFilePath = Environment.getExternalStorageDirectory().getPath()+"/log/kec";

//    private static final String APK_Log_Server = "http://118.219.233.218:5005/";
//    private static final String OCPP_Server = "ws://118.219.233.218:8999/ws";

    private static final String APK_Log_Server = "http://gracetech.co.kr:5005/";
    private static final String OCPP_Server = "ws://gracetech.co.kr:8999/ws";


    private static final String Meteorological_BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";

    private static final String Nx = "75";
    private static final String Ny = "60";


    private static final int ConnectId = 1;
    private static final String SerialPortName = "ttyS3";
    private static final String SerialBaudrateNum = "115200";

    public static String getDeviceid(){
        return Deviceid;
    }

    public static String getDevicemodel(){return Devicemodel;}

    public static String getDeviceLogFilePath(){return DeviceLogFilePath;}

    public static String getPAYvendor(){
        return PAYvendor;
    }

    public static String getWHEATHERvender(){
        return WHEATHERvender;
    }

    public static String getDevicevendor(){return Devicevendor;}

    public static int getConnectId(){return ConnectId;}

    public static String getSerialPortName(){return SerialPortName;}

    public static String getSerialBaudrateNum(){return SerialBaudrateNum;}

    public static String getAPK_Log_Server(){return APK_Log_Server;}

    public static String getOCPP_Server(){return OCPP_Server;}

    public static String getMeteorological_BASE_URL(){return Meteorological_BASE_URL;}

    public static String getNx(){return Nx;}
    public static String getNy(){return Ny;}
}
