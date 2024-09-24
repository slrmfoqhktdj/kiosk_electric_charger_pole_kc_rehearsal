package com.example.kiosk_electric_charger_pole_kc_rehearsal.serialport;
//
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.DeviceModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.ReceiverModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.serialport.model.SerialModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.JsonClient;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback.CenterClientInterface;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback.SerialClientInterface;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;

import eu.chargetime.ocpp.model.core.Reason;

public class SerialPortClient {

    private static Context mcontext;
    private static SerialOperator mySerialOperator;

    private static SerialPortClient serialPortClient = null;

//    private final ArrayList<SerialClientInterface> r_callbacksList = new ArrayList<>();

    static int sendtype = 1;//发送类型 0 = 文本 1 = hex

    static String tag = "SerialPort";

    static int send = 0;

    static int take = 0;

    private static Reason reason = Reason.Local;
    private SerialPortClient(Context context){
        mcontext = context;
    }

    public static SerialPortClient getInstance(Context context){


        if(serialPortClient == null){
            serialPortClient = new SerialPortClient(context);
        }

        return serialPortClient;
    }

//    public SerialPortClient setSerialClientInterfaceListener(SerialClientInterface callback){
//        if (!r_callbacksList.contains(callback)) {
//            r_callbacksList.add(callback);
//        }
//        return this;
//    }

    public void Serialport_Open(){
        if (mySerialOperator != null){
            mySerialOperator.close();
            mySerialOperator = null;
        }


        mySerialOperator = new SerialOperator();
        mySerialOperator.setBaudRate(DeviceModel.getSerialBaudrateNum());
        mySerialOperator.setPort("/dev/" + DeviceModel.getSerialPortName());
        mySerialOperator.setHandler(mHandler);
//        Log.d("직렬 포트", "open\r");
//        Logger.i("직렬 포트 open");

        try {
            mySerialOperator.open();

            Log.d(tag,"직렬 포트를 성공적으로 열었습니다.");
            Logger.i("직렬 포트를 성공적으로 열었습니다.");
            Toast.makeText(mcontext,"직렬 포트를 성공적으로 열었습니다.",Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            e.printStackTrace();
            Logger.e("직렬 포트 err : "+e);
        }
    }

    public void Serialport_Close(){
        if (mySerialOperator != null) {
            mySerialOperator.stopSend();
            mySerialOperator.close();
            mySerialOperator = null;
        }
    }

    public String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }


    public void Serialport_SendMessage(SerialModel message,@Nullable Reason reason){
        if (mySerialOperator == null || !mySerialOperator.isOpen()) {
            Log.d(tag,"먼저 직렬 포트를 열어주세요");
            Toast.makeText(mcontext,"기기와의 통신 포트가 열려있지 않습니다.",Toast.LENGTH_LONG).show();
            return;
        }

        Logger.i(tag,"Serialport_SendMessage : "+ message);

        byte[] data;
        byte[] crcdata;


        switch (message){
            case RightChargerState:
                Log.d("test","Right ChargerState");
                crcdata = CRC16Util.sendData(new byte[]{0x01});
                data = new byte[]{0x02,0x50,0x01,0x01, crcdata[1], crcdata[2] ,0x03};

                // crcdata = 01 f1 d1
                try {
                    mySerialOperator.send(data);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(tag,"send Serial Message fail : "+ e);

                }

                break;
            case RightChargerStart:
                crcdata = CRC16Util.sendData(new byte[]{0x01});
                data = new byte[]{0x02,0x52,0x01,0x01, crcdata[1], crcdata[2] ,0x03};

                try {
                    mySerialOperator.send(data);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(tag, "send Serial Message fail : "+ e);
                }
                break;
            case RightChargerReset:

                crcdata = CRC16Util.sendData(new byte[]{0x01});
                data = new byte[]{0x02,0x54,0x01,0x01, crcdata[1], crcdata[2] ,0x03};

                try {
                    mySerialOperator.send(data);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(tag, "send Serial Message fail : "+ e);
                }
                break;

            case RightChargerStop:
                this.reason = reason;
                crcdata = CRC16Util.sendData(new byte[]{0x01});
                data = new byte[]{0x02,0x56,0x01,0x01, crcdata[1], crcdata[2] ,0x03};

                try {
                    mySerialOperator.send(data);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(tag, "send Serial Message fail : "+ e);
                }
                break;

            case RightChargerSOC:

                crcdata = CRC16Util.sendData(new byte[]{0x01});
                data = new byte[]{0x02,0x60,0x01,0x01, crcdata[1], crcdata[2] ,0x03};

                try {
                    mySerialOperator.send(data);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(tag, "send Serial Message fail : "+ e);
                }
                break;
            default:

                break;
        }


//        Example_Answer(message);



//        if (sendtype == 0) {
//            mySerialOperator.send(message.getBytes());
//        } else {
//            message.replace("\r", "");
//            message.replace("\n", "");
//            message.replace(" ", "");
//
//            if (message.length() % 2 != 0)
//                message += "0";
//            mySerialOperator.send(hexStringToBytes(message));
//
//        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }



    public static String bytes2HexString(byte[] b, int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }


    static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

//            Log.d(tag,"msg = "+msg);
//            Logger.i("serial port msg = "+msg);
            if (msg.what == 0) {//拿到数据,完成主界面更新  - 데이터 가져오기 및 기본 인터페이스 업데이트 완료
//              String data = (String)msg.obj;
                byte[] bytes = (byte[]) msg.obj;

                if (bytes[1] == SerialModel.RightChargerStateReceive) {     // 상태응답

                    try{
                        byte[] databyte = new byte[]{bytes[3], bytes[4], bytes[5], bytes[6], bytes[7], bytes[8], bytes[9]};
                        byte[] crc = new byte[]{bytes[10], bytes[11]};
                        Log.d(tag, SerialModel.RightChargerState +" = " + CRC16Util.receiveData(databyte, crc));
                        //    케이블상태 1: 12v(케이블이랑 차랑 연결 안됨) , 2 : 9v(연결되고 충전 준비) , 3: 6v(충전중) , 4: 3v - error
                        //    plc 연결상태 1 일때만 정보값을 받을수있음
                        //    충전량 kwh
                        if(CRC16Util.receiveData(databyte, crc)){ // crc검증 맞으면


                            boolean ChargingEmergency = bytes[3] == 0x01; // 비상정지

                            int ChargeState = 0xff & bytes[4]; //    케이블상태

                            boolean Charging = bytes[5] == 0x01; // 충전상태

                            boolean PLCConnection = bytes[6]==0x01; // plc연결상태  plc 연결상태 1 일때만 정보값을 받을수있음

//                            String ChargePersent = String.valueOf(0xff & bytes[7]); // soc

                            String ChargeAmount = String.valueOf(0xff & bytes[7]) +(0xff & bytes[8]);        // 누적충전량


                            Intent intent = new Intent(ReceiverModel.SerialRightChargerState);
                            intent.putExtra("ChargingEmergency", ChargingEmergency);
                            intent.putExtra("ChargeState", ChargeState);
                            intent.putExtra("Charging", Charging);
                            intent.putExtra("PLCConnection", PLCConnection);
//                            intent.putExtra("ChargePersent", ChargePersent);
                            intent.putExtra("ChargeAmount", ChargeAmount);


                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);



                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Logger.e("SerialPort Error : "+ e);
                    }

                } else if (bytes[1] == SerialModel.RightChargerStartReceive) {    // 충전응답
                    try{
                        byte[] databyte = new byte[]{bytes[3]};
                        byte[] crc = new byte[]{bytes[4], bytes[5]};
                        Log.d(tag, SerialModel.RightChargerStart +" = " + CRC16Util.receiveData(databyte, crc));

                        boolean type = false;

                        if(CRC16Util.receiveData(databyte, crc)){   // crc검증 맞으면
                            if(bytes[3]==0x01) {    // 데이터  - 충전 성공시 = 0x01 , 충전 실패시 = 0x00
                                type= true;
                            }

                            Intent intent = new Intent(ReceiverModel.SerialRightChargerStart);
                            intent.putExtra("type", type);
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Logger.e("SerialPort Error : "+ e);
                    }

                } else if (bytes[1] == SerialModel.RightChargerResetReceive) {    // 재부팅
                    try{
                        byte[] databyte = new byte[]{bytes[3]};
                        byte[] crc = new byte[]{bytes[4], bytes[5]};
                        Log.d(tag, SerialModel.RightChargerReset +" = " + CRC16Util.receiveData(databyte, crc));
                        boolean type = false;

                        if(CRC16Util.receiveData(databyte, crc)){   // crc검증 맞으면
                            if(bytes[3]==0x00) {    // 데이터  - 재부팅 전 응답 = 0x01 , 재부팅 후 응답 = 0x00
                                type= true;
                            }

                            Intent intent = new Intent(ReceiverModel.SerialRightChargerReset);
                            intent.putExtra("type", type);
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Logger.e("SerialPort Error : "+ e);
                    }
                } else if (bytes[1] == SerialModel.RightChargerStopReceive) {    // 충전중단
                    try{
                        byte[] databyte = new byte[]{bytes[3]};
                        byte[] crc = new byte[]{bytes[4], bytes[5]};
                        Log.d(tag, SerialModel.RightChargerStopReceive +" = " + CRC16Util.receiveData(databyte, crc));
                        boolean type = false;

                        if(CRC16Util.receiveData(databyte, crc)){   // crc검증 맞으면
                            if(bytes[3]==0x01) {    // 데이터  - 중단 성공시 = 0x01 , 실패시 = 0x00
                                type= true;
                            }

                            Intent intent = new Intent(ReceiverModel.SerialRightChargerStop);
                            intent.putExtra("type", type);
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Logger.e("SerialPort Error : "+ e);
                    }
                } else if (bytes[1] == SerialModel.RightChargerFirstStopReceive) {    // 충전중단완료
                    try{
                        byte[] databyte = new byte[]{bytes[3]};
                        byte[] crc = new byte[]{bytes[4], bytes[5]};
                        Log.d(tag, SerialModel.RightChargerFirstStopReceive +" = " + CRC16Util.receiveData(databyte, crc));

                        int W = 0xff & bytes[3]; // 마지막 전력

                        if(CRC16Util.receiveData(databyte, crc)){   // crc검증 맞으면
                            Intent intent3 = new Intent(ReceiverModel.SerialRightChargerFirstStop);
                            intent3.putExtra("reason", reason);
                            intent3.putExtra("last",W);
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent3);
                        }

                        reason = Reason.Local;

                    }catch (Exception e){
                        e.printStackTrace();
                        Logger.e("SerialPort Error : "+ e);
                    }
                } else if (bytes[1] == SerialModel.RightChargerFirstStartReceive) {    // 충전시작완료
                    try{
                        byte[] databyte = new byte[]{bytes[3]};
                        byte[] crc = new byte[]{bytes[4], bytes[5]};
                        Log.d(tag, SerialModel.RightChargerFirstStartReceive +" = " + CRC16Util.receiveData(databyte, crc));

                        int W = 0xff & bytes[3]; // 처음 전력

                        if(CRC16Util.receiveData(databyte, crc)){   // crc검증 맞으면
                            Intent intent3 = new Intent(ReceiverModel.SerialRightChargerFirstStart);
                            intent3.putExtra("first",W);
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent3);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Logger.e("SerialPort Error : "+ e);
                    }
                }else if (bytes[1] == SerialModel.RightChargerSOCReceive) {    // 충전시작완료
                    try{
                        byte[] databyte = new byte[]{bytes[3]};
                        byte[] crc = new byte[]{bytes[4], bytes[5]};
                        Log.d(tag, SerialModel.RightChargerSOCReceive +" = " + CRC16Util.receiveData(databyte, crc));

                        int soc = 0xff & bytes[3]; // 충전상태

                        if(CRC16Util.receiveData(databyte, crc)){   // crc검증 맞으면
                            Intent intent3 = new Intent(ReceiverModel.SerialRightChargerSOC);
                            intent3.putExtra("soc",soc);
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent3);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Logger.e("SerialPort Error : "+ e);
                    }
                }else if (bytes[1] == SerialModel.RightChargerDeviceStopReceive) {    // 충전시작완료
                    try{
                        byte[] databyte = new byte[]{bytes[3]};
                        byte[] crc = new byte[]{bytes[4], bytes[5]};
                        Log.d(tag, SerialModel.RightChargerDeviceStopReceive +" = " + CRC16Util.receiveData(databyte, crc));


                        if(CRC16Util.receiveData(databyte, crc)){   // crc검증 맞으면
                            Intent intent3 = new Intent(ReceiverModel.SerialRightChargerDeviceStop);
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent3);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Logger.e("SerialPort Error : "+ e);
                    }
                }



//                Intent intent3 = new Intent("Page");
//                intent3.putExtra("page", PageAddrModel.ChargeCompleteFgm_r);
//                LocalBroadcastManager.getInstance(context).sendBroadcast(intent3);


                int arg1 = msg.arg1;
                String strRecv;
                if (sendtype == 0) {
                    strRecv = new String(bytes, 0, arg1);
                } else {
                    strRecv = bytes2HexString(bytes, arg1);
                    StringBuilder stringBuilder2 = new StringBuilder(strRecv);

                    for (int i = strRecv.length(); i > 0; i--) {
                        if (i % 2 == 0)
                            stringBuilder2.insert(i, " ");
                    }

                    strRecv = stringBuilder2.toString();
                }

//                    SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm:ss]");
//                    final String Send = dateformat.format(System.currentTimeMillis());

//              txt_recv.setText(data);

//                    StringBuilder sMsg = new StringBuilder();
//                    sMsg.append("\r\n");
//                    sMsg.append(Send + strRecv);

                Log.d(tag, strRecv);
                Logger.i("SerialPort msg : %s ", strRecv);
//                    txt_comrecv.append(sMsg);
                send += arg1;

//                Log.d(tag, send + "보내고 " + take + "받기");

//                Logger.i("SerialPort send : %d 보내고 %d 받기", send, take);
//                    txt_log.setText(String.format("发%d收%d", send, take));

                //txt_log.setText(Long.toString(ss));

                //비상정지눌릴때
                //케이블 분리
                //전력손실, 하드 꺼질때
                //커넥터 잠금/해제
                //하드웨어 on/off   --- ocpp reset 명령
                //충전 시작
                //metervalue 저장
                //충전 끝
            } else {
                Log.d(tag, "default : " + msg.what);
            }
        }

    };


//    int soc_cnt = 0;

//    void Example_Answer(SerialModel type){
//        try {
//            Thread.sleep(1000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        Intent intent;
//
//        switch (type){
//            case RightChargerState:
//
//                boolean ChargingEmergency = false; // 비상정지
//
//                int ChargeState =  3; //    케이블상태
//
//                boolean Charging = true; // 충전상태
//
//                boolean PLCConnection = true; // plc연결상태  plc 연결상태 1 일때만 정보값을 받을수있음
//
//
//                String ChargeAmount = "12";        // 누적충전량
//
//
//                intent = new Intent(ReceiverModel.SerialRightChargerState);
//                intent.putExtra("ChargingEmergency", ChargingEmergency);
//                intent.putExtra("ChargeState", ChargeState);
//                intent.putExtra("Charging", Charging);
//                intent.putExtra("PLCConnection", PLCConnection);
//                intent.putExtra("ChargeAmount", ChargeAmount);
//
//
//                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//
//                break;
//            case RightChargerStart:
//
//                Log.d("tag","rightChargerStart");
//
//                new Thread(this::sendFirst_1).start();
//
//                intent = new Intent(ReceiverModel.SerialRightChargerStart);
//                intent.putExtra("type", true);
//                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//
//
//                break;
//            case RightChargerReset:
//
//                intent = new Intent(ReceiverModel.SerialRightChargerReset);
//                intent.putExtra("type", true);
//                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//
//                break;
//
//            case RightChargerStop:
//
//                new Thread(this::sendFirst_2).start();
//
//                intent = new Intent(ReceiverModel.SerialRightChargerStop);
//                intent.putExtra("type", true);
//                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//
//                soc_cnt = 0;
//
//                break;
//
//            case RightChargerSOC:
//                int soc = 0;
//
//                if(soc_cnt == 0){
//                    soc = 0;
//                    soc_cnt++;
//                }else if( soc_cnt ==1){
//                    soc = 110;
//                    soc_cnt++;
//                }else if(soc_cnt ==2){
//                    soc = 120;
//                    soc_cnt++;
//                }else if(soc_cnt ==3){
//                    soc = 60;
//                    soc_cnt++;
//                }else {
//                    soc = 90;
//                }
//
//                intent = new Intent(ReceiverModel.SerialRightChargerSOC);
//                intent.putExtra("soc",soc);
//                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//
//                break;
//            default:
//
//                break;
//        }
//
//    }
//
//    void sendFirst_1(){
//
//        Log.d("example","sendFirst_1");
//
//        try {
//            Thread.sleep(1000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        float W = 1f; // 처음 전력
//
//
//        Intent intent = new Intent(ReceiverModel.SerialRightChargerFirstStart);
//        intent.putExtra("first",W);
//        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//    }
//
//    void sendFirst_2(){
//
//        Log.d("example","sendFirst_2");
//        try {
//            Thread.sleep(1000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//        float sW = 3f; // 마지막 전력
//
//        Intent intent3 = new Intent(ReceiverModel.SerialRightChargerFirstStop);
//        intent3.putExtra("reason", reason);
//        intent3.putExtra("last",sW);
//        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent3);
//
//        reason = Reason.Local;
//    }


}
