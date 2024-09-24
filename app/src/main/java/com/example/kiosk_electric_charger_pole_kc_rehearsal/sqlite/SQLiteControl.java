package com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.MeterValueMeasurandModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.model.NotificationFactoryModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.model.TransactionFactoryModel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.core.MeterValue;
import eu.chargetime.ocpp.model.core.Reason;
import eu.chargetime.ocpp.model.core.SampledValue;

public class SQLiteControl {

    private static final String MetervaluesTableName = "MeterValues";
    private static final String TransactionFactoryTableName = "TransactionFactory";
    private static final String NotificationFactoryTableName = "NotificationFactory";
    SQLiteHelper helper;
    SQLiteDatabase sqlite;

    private static SQLiteControl mInstance = null;

    public static SQLiteControl getInstance(SQLiteHelper _helper){
        if(mInstance ==null)
            mInstance = new SQLiteControl(_helper);
        return mInstance;
    }

    public SQLiteControl(SQLiteHelper _helper){
        this.helper = _helper;
    }


    // 테스트 함수- 전체 검색
    @SuppressLint("Range")
    public void SelectMetervaluesAll (String[] Colums){
        sqlite = helper.getReadableDatabase();

        Cursor c = sqlite.query(MetervaluesTableName,Colums,null,null,null,null,null);
        String logText = "";
        while(c.moveToNext()){
            for(int i = 0; i< Colums.length; i++){
                logText = logText + c.getString(c.getColumnIndex(Colums[i])) + " ";
            }

            Log.d("db ", logText);
            logText = "";
        }

        c.close();
    }

    // 테스트 함수- 전체 검색
    @SuppressLint("Range")
    public void SelectTransactionFactoryAll (){
        sqlite = helper.getReadableDatabase();
        String[] Colums = new String[]{"Time","TID","Reason","Meterstop"};
        Cursor c = sqlite.query(TransactionFactoryTableName,Colums,null,null,null,null,"Time");
        String logText = "";
        while(c.moveToNext()){
            for(int i = 0; i< Colums.length; i++){
                logText = logText + c.getString(c.getColumnIndex(Colums[i])) + " ";
            }

            Log.d("db ","TransactionFactoryAll "+ logText);
            logText = "";
        }

        c.close();
    }


    @SuppressLint("Range")
    public void SelectNotificationFactoryAll (){
        sqlite = helper.getReadableDatabase();
        String[] Colums = new String[]{"CId","ErrorCode","Status","Info","Time","VendorId","VendorErrorcode"};
        Cursor c = sqlite.query(NotificationFactoryTableName,Colums,null,null,null,null,null);
        String logText = "";
        while(c.moveToNext()){
            for(int i = 0; i< Colums.length; i++){
                logText = logText + c.getString(c.getColumnIndex(Colums[i])) + " ";
            }

            Log.d("db ", logText);
            logText = "";
        }

        c.close();
    }

    //테스트 함수 - 전체 삭제
    public void DeleteMetervaluesALl(){
        sqlite = helper.getWritableDatabase();
        sqlite.delete(MetervaluesTableName,null,null);
        sqlite.close();
    }


    @SuppressLint("Range")
    public MeterValue[] SelectMetervalues_Retrun (String[] Colums,String itag,String type){


        sqlite = helper.getReadableDatabase();

        Cursor c = sqlite.query(MetervaluesTableName,Colums,"Idtag = ? AND Type = ?",new String[]{itag,type},null,null,null);

        ArrayList<MeterValue> mvArrayList = new ArrayList<>();

        if(!(c.getCount() > 0 && c.moveToFirst())){         //  검색값이 없을경우
            return mvArrayList.toArray(new MeterValue[0]);
        }


        c.moveToPosition(-1);       // moveToNext는 첫행을 건너 뛰어버림. cursor의 포지션을 -1로 땅겨주던가 do-while문으로 바꿔줘야함
        while(c.moveToNext()){

            ArrayList<SampledValue> sdArrayList = new ArrayList<>();

            for(int i = 0; i< Colums.length; i++){

                if(Colums[i].equals("Time")){
                    continue;
                }

                SampledValue sv = new SampledValue(c.getString(c.getColumnIndex(Colums[i])));

                if(type.equals("1")){
                    sv.setContext("Sample.Clock");
                }else if(type.equals("2")){
                    sv.setContext("Sample.Periodic");
                }

                switch (Colums[i]){
                    case "EAIR":           //-"활성 전기 에너지"(Wh 또는 kWh) 레지스터에서 읽은 수치 값 , (그리드 공급으로부터) 수입된 에너지를 측정하는 전기 계량기        - 총 누적
                        sv.setMeasurand(MeterValueMeasurandModel.EAIR);
                        sv.setUnit("kWh");
                        break;
                    case "EAII":           //- 한 동안 (그리드 공급으로부터) 수입된 "활성 전기 에너지"(Wh 또는 kWh)의 절대량                                           - 기간 부분 누적
                        sv.setMeasurand(MeterValueMeasurandModel.EAII);
                        sv.setUnit("kWh");
                        break;
                    case "CI":                          //-EV에 순간적인 전류 흐름                   - 순간 전류
                        sv.setMeasurand(MeterValueMeasurandModel.CI);
                        sv.setUnit("A");
                        break;
                    case "CO":                        // -EV에 제공되는 최대 전류                   - 최대전류
                        sv.setMeasurand(MeterValueMeasurandModel.CO);
                        sv.setUnit("A");
                        break;
                    case "PAI":                     // - EV에서 가져온 순간 유효 전력. (W 또는 kW)   - 순간 전력
                        sv.setMeasurand(MeterValueMeasurandModel.PAI);
                        sv.setUnit("kW");
                        break;
                    case "PO":                          // -EV에 제공되는 최대 전력                   - 최대전력
                        sv.setMeasurand(MeterValueMeasurandModel.PO);
                        sv.setUnit("kW");
                        break;
                    case "SoC":                                     //-충전 차량의 충전 상태(%)
                        sv.setMeasurand(MeterValueMeasurandModel.SoC);
                        sv.setUnit("Percent");
                        break;
                    case "TEM":                                     //-충전 차량의 충전 상태(%)
                        sv.setMeasurand(MeterValueMeasurandModel.TEM);
                        sv.setUnit("Celsius");
                        break;

                }

                sdArrayList.add(sv);

            }


            SampledValue[] svs = sdArrayList.toArray(new SampledValue[sdArrayList.size()]);

            // String to ZonedDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(c.getString(c.getColumnIndex("Time")),formatter);

            MeterValue mv = new MeterValue(zonedDateTime, svs);
            mvArrayList.add(mv);
        }


        c.close();

        return mvArrayList.toArray(new MeterValue[mvArrayList.size()]);
    }



    public void DeleteMetervalues(String idtag){
        sqlite = helper.getWritableDatabase();
        sqlite.delete(MetervaluesTableName,"Idtag = ?",new String[]{idtag});
        sqlite.close();
    }

    public void DeleteMetervalues(String idtag,String Type){
        sqlite = helper.getWritableDatabase();
        sqlite.delete(MetervaluesTableName,"Idtag = ? AND Type = ?",new String[]{idtag,Type});
        sqlite.close();
    }

    public void InsertMetervalues(String time,String eair, String eaii , String ci , String co, String pai, String po , String soc , String tem, String idtag,String Type){
        sqlite = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Time",time);
        cv.put("EAIR",eair);
        cv.put("EAII",eaii);
        cv.put("CI",ci);
        cv.put("CO",co);
        cv.put("PAI",pai);
        cv.put("PO",po);
        cv.put("SoC",soc);
        cv.put("TEM",tem);
        cv.put("Idtag",idtag);
        cv.put("Type",Type);
        sqlite.insert(MetervaluesTableName,null,cv);

        sqlite.close();
    }

    public void db_close(){
        sqlite.close();
        helper.close();
    }


    public void InsertUnknownStopTrasaction(String time, String Tid, String meterstop, String reason){
        sqlite = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Time",time);
        cv.put("TID",Tid);
        cv.put("Reason",reason);
        cv.put("Meterstop",meterstop);

        sqlite.insert(TransactionFactoryTableName,null,cv);

        sqlite.close();
    }

    public void DeleteTransactionFactory(String Tid){
        sqlite = helper.getWritableDatabase();
        sqlite.delete(TransactionFactoryTableName,"TID = ?",new String[]{Tid});
        sqlite.close();
    }


    @SuppressLint("Range")
    public ArrayList<TransactionFactoryModel> SelectTransactionFactory(){
        sqlite = helper.getReadableDatabase();

        ArrayList<TransactionFactoryModel> arrayList = new ArrayList<>();
        String[] Colums = new String[]{"Time","TID","Reason","Meterstop"};
        Cursor c = sqlite.query(TransactionFactoryTableName,Colums,null,null,null,null,"Time");


        while(c.moveToNext()){
            TransactionFactoryModel factoryModel = new TransactionFactoryModel();

            for(int i = 0; i< Colums.length; i++){
                switch (Colums[i]){
                    case "Time":
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(c.getString(c.getColumnIndex(Colums[i])), formatter);
                        factoryModel.setTime(zonedDateTime);
                        break;
                    case "TID":
                        factoryModel.setTID(Integer.parseInt(c.getString(c.getColumnIndex(Colums[i]))));
                        break;
                    case "Reason":
                        factoryModel.setReason(ReasonRetrun(c.getString(c.getColumnIndex(Colums[i]))));
//                        factoryModel.setReason(Reason.valueOf(c.getString(c.getColumnIndex(Colums[i]))));
                        break;
                    case "Meterstop":
                        Log.d("aa", "Meterstop : "+ c.getString(c.getColumnIndex(Colums[i])));
                        factoryModel.setMeterstop(Integer.parseInt(c.getString(c.getColumnIndex(Colums[i]))));
                        break;
                }
            }

            arrayList.add(factoryModel);

        }

        c.close();


        return arrayList;
    }

    public void InsertUnknownNotificationFactory(String CId, String ErrorCode, String Status, String Info,String Time, String VendorId, String VendorErrorcode){
        sqlite = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("CId",CId);
        cv.put("ErrorCode",ErrorCode);
        cv.put("Status",Status);
        cv.put("Info",Info);
        cv.put("Time",Time);
        cv.put("VendorId",VendorId);
        cv.put("VendorErrorcode",VendorErrorcode);

        sqlite.insert(NotificationFactoryTableName,null,cv);

        sqlite.close();
    }

    @SuppressLint("Range")
    public boolean SelectNotificationFactoryBool (String CId){
        String[] Colums = new String[]{"CId"};
        sqlite = helper.getReadableDatabase();

        Cursor c = sqlite.query(NotificationFactoryTableName,Colums,"CId = ?",new String[]{CId},null,null,null);

        if(c.getCount()>0){
            c.close();
            return true;
        }


        c.close();
        return false;
    }

    public void DeleteNotificationFactory(String CId){
        sqlite = helper.getWritableDatabase();
        sqlite.delete(NotificationFactoryTableName,"CId = ?",new String[]{CId});
        sqlite.close();
    }
    //(CId text primary key, ErrorCode text, Status text, Info text)
    @SuppressLint("Range")
    public NotificationFactoryModel SelectNotificationFactory(){
        sqlite = helper.getReadableDatabase();

        NotificationFactoryModel factoryModel = new NotificationFactoryModel();
        String[] Colums = new String[]{"CId","ErrorCode","Status","Info","Time","VendorId","VendorErrorcode"};
        Cursor c = sqlite.query(NotificationFactoryTableName,Colums,null,null,null,null,null);

        while(c.moveToFirst()){

            for(int i = 0; i< Colums.length; i++){
                switch (Colums[i]){
                    case "CId":
                        factoryModel.setCId(Integer.parseInt(c.getString(c.getColumnIndex(Colums[i]))));
                        break;
                    case "ErrorCode":
                        factoryModel.setErrorCode(ChargePointErrorCodeRetrun(c.getString(c.getColumnIndex(Colums[i]))));
                        break;
                    case "Status":
                        factoryModel.setStatus(ChargePointStatusRetrun(c.getString(c.getColumnIndex(Colums[i]))));
                        break;
                    case "Info":
                        factoryModel.setInfo(c.getString(c.getColumnIndex(Colums[i])));
                        break;
                    case "Time":
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(c.getString(c.getColumnIndex(Colums[i])), formatter);
                        factoryModel.setTime(zonedDateTime);
                        break;
                    case "VendorId":
                        factoryModel.setVendorId(c.getString(c.getColumnIndex(Colums[i])));
                        break;
                    case "VendorErrorcode":
                        factoryModel.setVendorErrorcode(c.getString(c.getColumnIndex(Colums[i])));
                        break;
                }
            }

        }

        c.close();


        return factoryModel;
    }



    public Reason ReasonRetrun(String reason){
        switch (reason){
            case "UnlockCommand":
                return Reason.UnlockCommand;
            case "DeAuthorized":
                return Reason.DeAuthorized;
            case "EmergencyStop":
                return Reason.EmergencyStop;
            case "HardReset":
                return Reason.HardReset;
            case "SoftReset":
                return Reason.SoftReset;
            case "EVDisconnected":
                return Reason.EVDisconnected;
            case "Other":
                return Reason.Other;
            case "PowerLoss":
                return Reason.PowerLoss;
            case "Reboot":
                return Reason.Reboot;
            case "Remote":
                return Reason.Remote;
            default:
                return Reason.Local;

        }
    }

    public ChargePointErrorCode ChargePointErrorCodeRetrun(String Errorcode){

        switch (Errorcode){
            case "EVCommunicationError":
                return ChargePointErrorCode.EVCommunicationError;               //EV통신오류,차량과의 통신 실패는 모드 3 또는 기타 통신 프로토콜 문제일 수 있습니다.
            case "InternalError":
                return ChargePointErrorCode.InternalError;                  //내부 하드 또는 소프트웨어 구성 요소에 오류가 있습니다.
            case "OtherError":
                return ChargePointErrorCode.OtherError;                     //다른 유형의 오류입니다. VendorErrorCode에서 자세한 내용을 확인하세요.
            case "ConnectorLockFailure":
                return ChargePointErrorCode.ConnectorLockFailure;               //커넥터를 잠그거나 잠금 해제하지 못했습니다.
            case "GroundFailure":
                return ChargePointErrorCode.GroundFailure;                      //지락 회로 차단기가 활성화되었습니다
            case "HighTemperature":
                return ChargePointErrorCode.HighTemperature;                    //충전 포인트 내부 온도가 너무 높습니다.
            case "LocalListConflict":
                return ChargePointErrorCode.LocalListConflict;              //중앙 시스템에서 받은 인증 정보가 LocalAuthorizationList와 충돌합니다.
            case "OverCurrentFailure":
                return ChargePointErrorCode.OverCurrentFailure;             //과전류 보호 장치가 작동되었습니다.
            case "OverVoltage":
                return ChargePointErrorCode.OverVoltage;                //전압이 허용 가능한 수준 이상으로 상승했습니다.
            case "PowerMeterFailure":
                return ChargePointErrorCode.PowerMeterFailure;          //전기/에너지/전력 측정기를 읽지 못했습니다.
            case "ReaderFailure":
                return ChargePointErrorCode.ReaderFailure;              //idTag 리더에 오류가 발생했습니다.
            case "ResetFailure":
                return ChargePointErrorCode.ResetFailure;               //재설정을 수행할 수 없습니다.
            case "PowerSwitchFailure":
                return ChargePointErrorCode.PowerSwitchFailure;         //전원 스위치 제어 실패.
            case "UnderVoltage":
                return ChargePointErrorCode.UnderVoltage;           //전압이 허용 가능한 수준 아래로 떨어졌습니다.
            case "WeakSignal":
                return ChargePointErrorCode.WeakSignal;             //무선 통신 장치에서 신호가 약하다고 보고합니다.
            default:
                return ChargePointErrorCode.NoError;

        }
    }

    public ChargePointStatus ChargePointStatusRetrun(String status){
        switch (status){
            case "Charging":
                return ChargePointStatus.Charging;      //커넥터의 접촉기가 닫히면 차량이 충전됩니다
            case "Unavailable":
                return ChargePointStatus.Unavailable;   //가용성 변경 명령이나 ChargePoint가 재량에 따라 사용할 수 없는 상태로 전환되는 이벤트의 결과로 커넥터를 사용할 수 없게 되는 경우입니다. 가용성 변경 명령을 수신하면 상태가 즉시 변경되거나 변경이 예약될 수 있습니다. 예약된 경우 가용성 변경이 적용되면 상태 알림이 전송됩니다.
            case "Faulted":
                return ChargePointStatus.Faulted;       //충전 지점이나 커넥터에서 오류가 보고되어 에너지 공급을 사용할 수 없는 경우. (작동하지 않음).
            case "Finishing":
                return ChargePointStatus.Finishing;     //트랜잭션이 커넥터에서 중지되었지만 새 사용자가 커넥터를 아직 사용할 수 없는 경우(예: 케이블이 제거되지 않았거나 차량이 주차 공간을 떠나지 않았습니다.
            case "Preparing":
                return ChargePointStatus.Preparing;        //신규 사용자가 커넥터를 더 이상 사용할 수 없게 되었지만 아직 진행 중인 거래가 없는 경우. 일반적으로 커넥터는 사용자가 태그를 제시하거나 케이블을 삽입하거나 차량이 주차 공간을 차지할 때 준비 상태에 있습니다.
            case "Reserved":
                return ChargePointStatus.Reserved;      //지금 예약 명령 의 결과로 커넥터가 예약된 경우
            case "SuspendedEV":
                return ChargePointStatus.SuspendedEV;   //EV가 EVSE에 연결되어 있고 EVSE가 에너지를 제공하지만 EV가 에너지를 전혀 사용하지 않는 경우.     EV가 충전을 시작하지 않습니다
            case "SuspendedEVSE":
                return ChargePointStatus.SuspendedEVSE;     //EV가 EVSE에 연결되어 있지만 EVSE가 EV에 에너지를 제공하지 않는 경우. 스마트 충전 제한으로 인해 로컬 공급 전력 제약 또는 충전이 허용되지 않음을 나타내는 StartTransaction.conf의 결과 등입니다.    EVSE는 충전을 허용하지 않습니다.
            default:
                return ChargePointStatus.Available;     //신규 사용자가 커넥터를 사용할 수 있게 되는 경우(운영)

        }
    }


}
