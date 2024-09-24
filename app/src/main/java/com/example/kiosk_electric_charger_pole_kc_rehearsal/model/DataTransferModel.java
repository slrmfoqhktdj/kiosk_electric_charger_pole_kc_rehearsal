package com.example.kiosk_electric_charger_pole_kc_rehearsal.model;

public class DataTransferModel {


    private final static  String i_pay = "i_pay";        // 결제 정보 전달

    private final static  String c_pay = "c_pay";        // 부분결제취소 정보 전달
    
    private final static  String g_pay = "g_pay";        // 전기 시세 가져오기



    public static String getI_Pay(){return i_pay;}

    public static String getC_Pay(){return c_pay;}

    public static String getG_Pay(){return g_pay;}



    /// 듣는 것

    public final static String NVCATSetup = "NVCATSetup";   //nvcat 설정
    
//    public final static String BootAccept = "BootAccept"; // 기기 부팅 메시지 대기에서 정상으로
    public final static String NVCATRestart = "NVCATRestart";   //nvcat 재시작

    public final static String NVCATCancle = "NVCATCancle"; // nvcat 결제 부분취소

    public final static String NVCATUpdate = "NVCATUpdate"; // nvcat 업데이트

    public final static String SettingUpdate = "SettingUpdate"; // 기기설정 업데이트

}
