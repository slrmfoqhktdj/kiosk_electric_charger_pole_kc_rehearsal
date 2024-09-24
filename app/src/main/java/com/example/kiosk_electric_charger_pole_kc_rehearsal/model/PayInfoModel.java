package com.example.kiosk_electric_charger_pole_kc_rehearsal.model;

public class PayInfoModel {
    public final static int SEND_REQUEST_CODE = 1;
    public final static int SEND_REQUEST_NORMAL = 2;
    public final static int SEND_REQUEST_QR = 3;
    public final static int SEND_REQUEST_RFID = 4;
    public final static int SEND_REQUEST_CANCLE = 5;
    public final static int SEND_REQUEST_SETTING = 6;
    public final static int SEND_REQUEST_INFO = 7;
    public final static char fs = 0x1C;
    public final static String pay_apprtid = "2393300001";            // 승인시 catid - 10
    public final static String pay_select_apply = "0200";     // 거래구분     - 4
    public final static String pay_category = "10";     // 거래 유형    - 2
    public final static String pay_WCC = "I";           // WCC  - 1
    public final static String pay_txt = "PRO";             // 전문TEXT - V(3)
    public final static String pay_devicegb = "H1";         // 기종구분 - V(2)
    public final static String pay_bugase = "0" ;    // 부가세 - V(12)
    public final static String pay_bongsa = "0" ;    // 봉사료 - V(12)
    public final static String pay_myunse = "000000000";        // 면세금액 - V(9)
    public final static String pay_prvnum = "00000000000000000001";         // POS 자체 전문관리번호    - 20
    public final static String installments = "00";              // 할부   -2

}
