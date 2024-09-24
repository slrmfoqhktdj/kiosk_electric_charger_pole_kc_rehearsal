package com.example.kiosk_electric_charger_pole_kc_rehearsal.model;

public class ReceiverModel {

    public final static String BootNotification = "BootNotificationBC";    //bootnotification 응답
    public final static String BootNotification_Trigger = "resendBootNotification";    //  trigger에 의한 bootnotification 응답
    public final static String Receive_Reset = "reset_Receive";    //reset 명령 응답



    public final static String SerialRightChargerStartCommand = "SerialRightChargerStartCommand";  // 시리얼 포트 충전 시작 메시지

    public final static String SerialRightChargerState = "SerialRightChargerState";  // 시리얼 포트와 통신된 상태응답값

    public final static String SerialRightChargerStart = "SerialRightChargerStart";  // 시리얼 포트와 통신된 충전응답값

    public final static String SerialRightChargerReset = "SerialRightChargerReset";  // 시리얼 포트와 통신된 재부팅응답값

    public final static String SerialRightChargerStopCommand = "SerialRightChargerStopCommand";  // 시리얼 포트 충전 중단 메시지

    public final static String SerialRightChargerStop = "SerialRightChargerStop";  // 시리얼 포트와 통신된 충전중단응답값

    public final static String SerialRightChargerFirstStart = "SerialRightChargerFirstStart";  // 시리얼 포트와 통신된 충전시작시

    public final static String SerialRightChargerFirstStop = "SerialRightChargerFirstStop";  // 시리얼 포트와 통신된 충전종료시

    public final static String SerialRightChargerSOC = "SerialRightChargerSOC";  // 시리얼 포트와 통신된 SOC 정보값

    public final static String SerialRightChargerDeviceStop = "SerialRightChargerDeviceStop";  // 시리얼 포트와 통신된 기기 충전 선 종료시







    public final static String  SendChargeStop  =   "ChargeTimer";        //타이머 중지
    public final static String  GetPay  =   "Get_Pay";    // 전기 시세값 저장
    public final static String  SendChargeStart  =  "Start_Charging";        // 충전시작
    public final static String  GetAuthorize  =   "authorize_Receive";          // 충전 권한 획득
    public final static String  GetChargeStop  =   "chargeEnd_Receiver";              //충전 중지
    public final static String  GetChageAvailablitity  =    "changeAvailablitity_Receive";  //컨넥터 사용 가능 여부 변경
    public final static String  GetNvcatOption  =    "nvcat_option_Receive";             //nvcat 옵션 바꿈
    public final static String  GetNvcatRestart  =    "nvcat_restart";             //nvcat 재시작
    public final static String  GetNvcatCanclePay  =   "nvcat_cancle_pay";             //사용자 설정 부분취소
    public final static String  TransactionCanclePay  =   "StartTransactionCanclePay";        // 현거래 부분취소
    public final static String  ServerConn  =   "ServerConn";               // 서버 연결 여부
    public final static String  SetDiagnosticsStatus  =   "SetDiagnosticsStatus";
    public final static String  SetFirmwareStatus  =    "SetFirmwareStatus";

    public final static String  FirmwareUpdate  =    "FirmwareUpdate";          // 업데이트
    public final static String  StatusNotification_Trigger  =   "TriggerStatusNotification";//  trigger에 의한 StatusNotification 응답
    public final static String  MeterValues_Trigger  =   "TriggerMeterValues";//  trigger에 의한 MeterValues 응답
    public final static String  FirmwareStatusNotification_Trigger  =   "TriggerFirmwareStatusNotification";//  trigger에 의한 FirmwareStatusNotification 응답
    public final static String  DiagnosticsStatusNotification_Trigger  =   "TriggerDiagnosticsStatusNotification";//  trigger에 의한 DiagnosticsStatusNotification 응답



}
