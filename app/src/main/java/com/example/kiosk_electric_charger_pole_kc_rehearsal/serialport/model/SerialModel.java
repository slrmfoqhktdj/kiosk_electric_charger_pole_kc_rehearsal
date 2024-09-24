package com.example.kiosk_electric_charger_pole_kc_rehearsal.serialport.model;

public enum SerialModel {         // ConfigurationModel 값을 제외한 다른 key모음

//    public final static String RightChargerState = "RightChargerState";  //상태요청
//
//    public final static String RightChargerStart = "RightChargerStart";    //충전요청
//
//    public final static String RightChargerReset = "RightChargerReset";  //재부팅
//
//    public final static byte RightChargerStateReceive = 0x51;  //상태요청
//
//    public final static byte RightChargerStartReceive = 0x53;    //충전요청
//
//    public final static byte RightChargerResetReceive = 0x55;  //재부팅

    RightChargerState,  //상태요청

    RightChargerStart,    //충전요청

    RightChargerReset,  //재부팅

    RightChargerStop,  //충전중단요청
    
    RightChargerSOC; // soc요청

    public final static byte RightChargerStateReceive = 0x51;  //상태응답

    public final static byte RightChargerStartReceive = 0x53;    //충전읍답

    public final static byte RightChargerResetReceive = 0x55;  //재부팅응답

    public final static byte RightChargerStopReceive = 0x57;  //충전중단응답

    public final static byte RightChargerFirstStartReceive = 0x58;  //충전시작시 응답

    public final static byte RightChargerFirstStopReceive = 0x59;  //충전종료시 응답

    public final static byte RightChargerSOCReceive = 0x61;  //SOC시 응답

    public final static byte RightChargerDeviceStopReceive = 0x62;      //기기에서 충전 끊음

}
