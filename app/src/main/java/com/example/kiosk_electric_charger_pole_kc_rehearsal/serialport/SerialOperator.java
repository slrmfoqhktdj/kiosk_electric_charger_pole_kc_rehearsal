package com.example.kiosk_electric_charger_pole_kc_rehearsal.serialport;

import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.Arrays;

import android_serialport_api.ComBean;
import android_serialport_api.SerialOperatorHelper;

public class SerialOperator extends SerialOperatorHelper {

    private String tag = "SerialOperator";

    public SerialOperator() {

    }

    @Override
    protected void onDataReceived(ComBean ComRecData) {
        // TODO Auto-generated method stub
//		DispQueue.AddQueue(ComRecData);//线程定时刷新显示(推荐)
//        System.out.println(Arrays.toString(ComRecData.bRec));
        Log.d(tag,Arrays.toString(ComRecData.bRec));
        Logger.i("SerialOperator : "+Arrays.toString(ComRecData.bRec));
        //비상정지눌릴때
        //케이블 분리
        //전력손실, 하드 꺼질때
        //커넥터 잠금/해제
        //하드웨어 on/off   --- ocpp reset 명령
        //
    }

}
