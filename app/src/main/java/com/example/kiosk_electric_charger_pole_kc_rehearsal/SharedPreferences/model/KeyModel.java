package com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.model;

import android.os.Environment;

public class KeyModel {         // ConfigurationModel 값을 제외한 다른 key모음

    public final static String RightChargingEnabled = "RightChargingEnabled";  //오른쪽 커넥터 이용가능

    public final static String RightAuthorizationIdtag = "RightAuthorizationIdtag";    // 오른쪽 토큰 세션 idtag

    public final static String payPrice = "payPrice";  // 가격,단가
    public final static String card_read_state = "card_read_state";  // 카드 읽는 상태, 방식

    public final static String right_pay_number = "right_pay_number";  //왼쪽 충전기 충전 승인 번호
    public final static String right_pre_charge_amount = "right_pre_charge_amount";   // 오른쪽 예상 충전량
    public final static String right_payvalue = "right_payvalue";        // 오른쪽 결제 금액

    public final static String right_device_charging = "right_device_charging";  //오른쪽 충전기 충전 진행 여부

    public final static String right_charge_persent = "right_charge_persent";   //오른쪽 충전 퍼센트
    public final static String right_charge_time = "right_charge_time";      //오른쪽 충전 시간
    public final static String right_input_pay = "right_input_pay";         //오른쪽 실 결제 비용
    public final static String right_charge_amount = "right_charge_amount";    //오른쪽 충전량

    public final static String right_pay_day = "right_pay_day";    // 오른쪽 결제일
    public final static String right_pay_token = "right_pay_token";     // 오른쪽 결제 일련번호


//    RightChargingEnabled,  //오른쪽 커넥터 이용가능
//
//    RightAuthorizationIdtag,    // 오른쪽 토큰 세션 idtag
//
//    payPrice,  // 가격,단가
//    card_read_state,  // 카드 읽는 상태, 방식
//
//    right_pay_number,  //왼쪽 충전기 충전 승인 번호
//    right_pre_charge_amount ,  // 오른쪽 예상 충전량
//    right_payvalue ,       // 오른쪽 결제 금액
//
//    right_device_charging,  //오른쪽 충전기 충전 진행 여부
//
//    right_charge_persent ,   //오른쪽 충전 퍼센트
//    right_charge_time,      //오른쪽 충전 시간
//    right_input_pay,        //오른쪽 실 결제 비용
//    right_charge_amount,    //오른쪽 충전량
//
//    right_pay_day,   // 오른쪽 결제일
//    right_pay_token    // 오른쪽 결제 일련번호



    public final static String DeviceId = "DeviceId";   // 기기 고유값
    public static final String Devicevendor = "Devicevendor";
//    private static final String Devicelatitude = "35.187726962762";
//    private static final String Devicelongitude = "126.91088663129";
//    private static final String DeviceAddr = "광주광역시북구오치동";
    public static final String Devicemodel = "Devicemodel";
    public static final String PAYvendor = "PAYvendor";
    public static final String WHEATHERvender = "WHEATHERvender";

    public static final String Nx = "Nx";
    public static final String Ny = "Ny";

}
