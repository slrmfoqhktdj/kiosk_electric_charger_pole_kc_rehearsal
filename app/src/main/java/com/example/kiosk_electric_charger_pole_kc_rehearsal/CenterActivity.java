package com.example.kiosk_electric_charger_pole_kc_rehearsal;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.ConfigurationControl;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.KeyControl;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.PrefsHelper;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.activity.ErrorDialog;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.activity.LoadingDialog;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.adapter.SectionPageAdapter;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.filemanage.Filedownload;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.fragment.ChargeCompleteFragment;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.fragment.ChargeInfoFragment;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.fragment.ChargeSelectFragment;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.DataTransferModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.DeviceModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.MainViewModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.PageAddrModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.ReceiverModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.Retrofit_Factory;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.Retrofit_Factory_Meteorological;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.Retrofit_Interface;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.Retrofit_Interface_Meteorological;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.serialport.SerialPortClient;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.serialport.model.SerialModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.soundpool.SoundPoolPlayer;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.SQLiteControl;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.SQLiteHelper;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.model.TransactionFactoryModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.JsonClient;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback.CenterClientInterface;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback.SerialClientInterface;
import com.orhanobut.logger.Logger;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import eu.chargetime.ocpp.model.core.AvailabilityType;
import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.core.Reason;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import eu.chargetime.ocpp.model.firmware.FirmwareStatus;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CenterActivity extends AppCompatActivity {

    private String tag = "CenterActivity";



    private ViewPager mViewPager;
    private SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());

    //db
    private SQLiteHelper helper;
    private SQLiteControl sqlite;

    private MainViewModel viewModel;

    private final Retrofit_Interface_Meteorological m_retrofitService = Retrofit_Factory_Meteorological.create();    // 기상청 날씨 API 다이렉트

    //중앙 정보값 타이머
    private Timer center_data_Retrofit_timer;

    //기상청 api 반복초
    private int Meteorological_time = 600;

    private SerialPortClient serialClient;  // 충전기 시리얼 통신

    private boolean booting = true; // 기기가 처음 켜졌을때 statusnotification을 보낼것인가

    private JsonClient mClient;                     // ws 클라이언트


    private SoundPoolPlayer soundPoolPlayer;        // 사운드

    //MeterValues 타이머
    private Timer Aligned_mv_r_Timer;     // Alignedmetervalues - r
    private Timer Sampled_mv_r_Timer;    // Sampledmetervalues - r

    private boolean isSampled = false;

    private Timer right_timer;  // 충전시간 타이머

    private Timer right_request_soc ;

    private Timer page_timeout_timer;   //홈으로돌아가는 타이머

    //홈으로 돌아가는 타이머 초
    private int page_timeout_sec = 0;

    //충전 타이머 초
    private int right_sec = 0;

    //예상 충전 타이머 초
    private int right_pre_sec = 0;

    private ArrayList<Integer> right_sec_array = new ArrayList<>();

    private ArrayList<Integer> right_soc_array = new ArrayList<>();

    private boolean pre_charge_compare = false; // 충전 예상시간과 충전현황과의 비교여부

    private Timer command_timer; // 메시지 제한시간
    
    private int command_sec = 10;   // 메시지 제한 초

    private Timer soc_timer; // soc 비교

    private Handler un_stopT_handler; // 미발송 stopTransaction 보내기
    private Handler un_statusN_handler; // 미발송 statusnotifiaction 보내기

    int max_soc = 95;// soc 최대값

    int origin_soc = 0;

//    int socSendTime = 300000;
    int socSendTime = 300;         // 초반 soc 가져오는 간격


    int socCnt = -1;

    int socCnt_max = 3;  // soc보내는 횟수

    private boolean ResetType_b = true;       // 충전 중지 이유, update,하드웨어, 소프트웨어  충전중이라면 false/ 그냥  다른이유,전력손실 시에는 true . 앱이 종료시에 true면 충전중지 이유 보냄


    private int ResetRetries = 0; // reset실패시 재시도 횟수

    LoadingDialog loadingDialog;    // 로딩 다이어로그

    ErrorDialog errorDialog;    // 메시지 다이어로그

    private Timer center_timer;

    TextView main_title_time_tv,main_title_temp_tv,main_title_humidity_tv,main_title_ws_tv,main_title_wd_tv,main_title_precipitation_tv;

    private Timer update_Timer;  //업데이트 타이머

    int UpdateFirmwareRetries = 0;     // 재시도 횟수
    long UpdateFirmwareRetriesInterval = 0;    //재시도 간격
    private final Retrofit_Interface s_retrofitService = Retrofit_Factory.create();

    private final int Weather_retry_cnt_origin = 3;   //  날씨서버 응답 실패시 재시도 횟수 고정
    private int Weather_retry_cnt = 3;  //  날씨서버 응답 실패시 재시도 횟수  변수
    private final int Weather_retry_interval = 2; // 날씨서버 응답 실패시 재시도 간격(초)

//    private boolean isStarted = false;  // 충전이 시작되지 않았음에도 커넥터를 연결하면 충전으로 넘어가버리는 현상을 막기위해

    private final BroadcastReceiver ChargingReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch(Objects.requireNonNull(intent.getAction())){






                case ReceiverModel.SerialRightChargerState:


//                    if(ChargeState==1){
//                        Stop_Command_time();
//                        errorDialog.showDialog("실패","케이블이 차량과 연결되어있지 않습니다.");
//
//                        if(mViewPager.getCurrentItem()== PageAddrModel.ChargeInfoFgm_r){
//                            Intent intent5 = new Intent("Page");
//                            intent5.putExtra("page", PageAddrModel.ChargeCompleteFgm_r);
//                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent5);
//
//                            Stop_Sampled_mv_r_Timer();
//                            right_soc_array.clear();
//                            right_sec_array.clear();
//                            Stop_SOC_Cal();
//                            Stop_right_charge_time();
//                            Stop_right_soc_time();
//                            pre_charge_compare = false;
//                            right_pre_sec = 0;
//                            origin_soc = 0;
//                            socCnt = 0;
//                        }
//
//                        return;
//                    }else if(ChargeState ==2){
//                        Stop_Command_time();
//                        serialClient.Serialport_SendMessage(SerialModel.RightChargerStart);
//                        return;
//                    }else if(ChargeState ==4){
//                        Stop_Command_time();
//                        errorDialog.showDialog("실패","케이블의 상태에 오류가 발생했습니다.");
//
//                        if(mViewPager.getCurrentItem()== PageAddrModel.ChargeInfoFgm_r){
//                            Intent intent5 = new Intent("Page");
//                            intent5.putExtra("page", PageAddrModel.ChargeCompleteFgm_r);
//                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent5);
//
//                            Stop_Sampled_mv_r_Timer();
//                            right_soc_array.clear();
//                            right_sec_array.clear();
//                            Stop_SOC_Cal();
//                            Stop_right_charge_time();
//                            Stop_right_soc_time();
//                            pre_charge_compare = false;
//                            right_pre_sec = 0;
//                            origin_soc = 0;
//                            socCnt = 0;
//                        }
//                        return;
//                    }


                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    boolean ChargingEmergency = intent.getBooleanExtra("ChargingEmergency",false);
                    //    케이블상태 1: 12v(케이블이랑 차랑 연결 안됨) , 2 : 9v(연결되고 충전 준비) , 3: 6v(충전중) , 4: 3v - error
                    int ChargeState = intent.getIntExtra("ChargeState",4);
                    boolean Charging = intent.getBooleanExtra("Charging",false);
                    boolean PLCConnection = intent.getBooleanExtra("PLCConnection",true);
//                    String ChargePersent = intent.getStringExtra("ChargePersent");
                    String ChargeAmount = intent.getStringExtra("ChargeAmount");

//                    Log.d(tag,"SerialRightChargerState = ChargingEmergency : "+ChargingEmergency + " PLCConnection : "+PLCConnection+" ChargeState : "+ ChargeState + " Charging : "+ Charging + " ChargePersent : "+ ChargePersent + " ChargeAmount : "+ChargeAmount);
                    Log.d(tag,"SerialRightChargerState = ChargingEmergency : "+ChargingEmergency + " PLCConnection : "+PLCConnection+" ChargeState : "+ ChargeState + " Charging : "+ Charging +  " ChargeAmount : "+ChargeAmount);

                    Stop_Command_time();

                    Log.d(tag,"page = "+mViewPager.getCurrentItem());

                    if(ChargingEmergency){


                        if(mViewPager.getCurrentItem()== PageAddrModel.ChargeInfoFgm_r){

                            allStop();

                            Intent intent5 = new Intent("Page");
                            intent5.putExtra("page", PageAddrModel.ChargeSelectFgm);
                            intent5.putExtra("ChargePointStatus", ChargePointStatus.Faulted);
                            intent5.putExtra("ChargePointErrorCode", ChargePointErrorCode.OtherError);

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent5);
                        }

                        Toast.makeText(context,"응급상황에 의한 충전실패입니다.", Toast.LENGTH_LONG).show();

                    }

                    if(Charging){

                        int receive_chargeamount = Integer.parseInt(ChargeAmount)*1000;

                        if(receive_chargeamount>0){    // amount가 0일 경우

                        }



//                        viewModel.getright_charge_persent().postValue(Integer.parseInt(ChargePersent));
                        viewModel.setRight_charge_amount(Float.valueOf(ChargeAmount));

                        try {
                            ZonedDateTime zonedDateTime = returnZonedDateTime();



                            // db 저장
                            sqlite.InsertMetervalues(String.valueOf(zonedDateTime),String.format("%.3f",Float.valueOf(ChargeAmount)),viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(),String.valueOf(viewModel.getright_charge_persent().getValue()),viewModel.getRight_TEM().getValue(),viewModel.getRight_pay_number().getValue(),isSampled?"2":"1");
                            if(!mClient.isClosed())
                                mClient.sendMeterValues(DeviceModel.getConnectId(),Integer.parseInt(viewModel.getRight_pay_number().getValue()), String.format("%.3f",Float.valueOf(ChargeAmount)),isSampled,zonedDateTime,viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(), String.valueOf(viewModel.getright_charge_persent().getValue()), viewModel.getRight_TEM().getValue());


                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.e("Start_Sampled_mv error : "+e);
                        }


                    }else{


                        if(mViewPager.getCurrentItem()== PageAddrModel.ChargeInfoFgm_r){

                            allStop();

                            Intent intent5 = new Intent("Page");
                            intent5.putExtra("page", PageAddrModel.ChargeSelectFgm);
                            intent5.putExtra("ChargePointStatus", ChargePointStatus.SuspendedEV);
                            intent5.putExtra("ChargePointErrorCode", ChargePointErrorCode.WeakSignal);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent5);

                        }

                    }




                    break;

                case ReceiverModel.SerialRightChargerStart:

                    boolean StartReceive = intent.getBooleanExtra("type",false);

                    Stop_Command_time();

                    Log.d(tag,"SerialRightChargerStart :"+ StartReceive);

                    try {

//                        if(!isStarted){
//                            return;
//                        }

                        if(StartReceive){   // 충전 메시지 성공시

                            Start_Command_time("충전 준비중입니다. 잠시만 기다려주세요.");

                        }else{ // 충전 메시지 실패시 재시도
//                            isStarted = false;
                            Toast.makeText(context,"충전 요청에 실패했습니다.", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e){
//                        isStarted = false;
                        Log.e(tag,ReceiverModel.SerialRightChargerStart+e);
                    }
                    break;

                case ReceiverModel.SerialRightChargerReset:

                    boolean ResetReceive = intent.getBooleanExtra("type",false);

                    try {
                        if(ResetReceive){   //부팅후

                            Toast.makeText(context,"충전부분 리부팅이 완료되어 시스템 리부팅을 진행합니다.",Toast.LENGTH_LONG).show();
                            Stop_Command_time();
                        }else{   // 부팅전

                            Start_Command_time("충전부분 리부팅이 먼저 진행된 후 시스템 리부팅을 진행합니다.");

                        }
                    } catch (Exception e){
                        Log.e(tag,ReceiverModel.SerialRightChargerReset+e);
                    }
                    break;

                case ReceiverModel.SerialRightChargerStartCommand:

                    Log.d(tag,"SerialRightChargerStartCommand");
                    Logger.i("SerialRightChargerStartCommand");

//                    isStarted = true;

                    Start_Command_time("서버와의 상태를 확인합니다.");
                    try {
//                        mClient.connect();
                        mClient.sendDataTransfer(KeyControl.getDevicevendor(), DataTransferModel.getG_Pay(),null);      // 단가 받아오기
                    } catch (Exception e) {
                        e.printStackTrace();;
                    }



//                    Start_Command_time();
//                    ShowLoadingDialog("기기와의 상태를 확인합니다.");

//                    serialClient.Serialport_SendMessage(SerialModel.RightChargerState);


                    serialClient.Serialport_SendMessage(SerialModel.RightChargerStart,null);

                    break;


                case ReceiverModel.GetPay:     // 전기 시세값 저장

                    Log.i(tag," GetPay_Receiver ");
                    Logger.i(" GetPay_Receiver ");

                    float timertype_Get_Pay = intent.getFloatExtra("pay",0.f);

                    Stop_Command_time();

                    if(!(timertype_Get_Pay == 0)){
                        viewModel.setPayPrice(timertype_Get_Pay);

                        Start_Command_time("서버에 충전허가를 요청합니다.");


                        Random random = new Random();
                        String pay_num = String.valueOf(random.nextInt(999999999-100000000+1)+100000000);       // 결제가 없기떄문에 랜덤 부여
                        viewModel.getRight_pay_number().postValue(pay_num);

                        try {
//                        mClient.connect();
                            mClient.sendAuthorize(pay_num);
                        } catch (Exception e) {
                            e.printStackTrace();;
                        }


                    }else{
//                        isStarted = false;
                        Toast.makeText(getApplicationContext(),"값을 불러오는데 오류가 생겼습니다.",Toast.LENGTH_LONG).show();
                    }

                    break;


                case ReceiverModel.GetAuthorize:           // 충전 권한 획득

                    Log.i(tag," authorize_Receiver ");
                    Logger.i(" authorize_Receiver ");
                    try {

                        Stop_Command_time();

                        Log.i(tag,"now charging status : "+viewModel.getright_device_charging().getValue());
                        Logger.i("now charging status : "+viewModel.getright_device_charging().getValue());
                        if(Boolean.FALSE.equals(viewModel.getright_device_charging().getValue())){       // 충전중이 아닐경우
                            String itag = intent.getStringExtra("idtag");                   // 임시 id

                            if(viewModel.getRight_pay_token().getValue().equals("")){          // 이전 결제 내역이 없을경우 (remote start)
                                Log.i(tag,"remote authorize receive itag : "+ itag);

                                viewModel.setRight_pay_number(itag);            // remote때
//                            viewModel.getRight_pay_number().setValue(itag);
                            }else{                                                              // 결제 내역이 있을 경우 (결제는 했는데 충전으로 넘어가지 않을 경우)
                                itag = viewModel.getRight_pay_number().getValue();
                            }

                            if(Boolean.FALSE.equals(viewModel.getright_device_charging_enable().getValue())){
                                Set_Send_StatusNotification(ChargePointStatus.SuspendedEV,ChargePointErrorCode.NoError,"","","");
                                return;
                            }
//                            mClient.sendStartTransaction(DeviceModel.getConnectId(),itag,11000);        // kwh 를 wh로 바꿔야함

                            // 시작 메시지
                            Start_Command_time("기기와의 상태를 확인합니다.");
                            serialClient.Serialport_SendMessage(SerialModel.RightChargerState,null);
                        }




                    } catch (Exception e) {
                        Log.e(tag, ReceiverModel.GetAuthorize+ e);
                        Logger.e( ReceiverModel.GetAuthorize+ e);
                    }

                    break;


                case ReceiverModel.SerialRightChargerStopCommand:

                    Log.d(tag,"SerialRightChargerStopCommand");

                    String pay_num = String.valueOf(intent.getIntExtra("pay_num",0));
                    Reason reason = (Reason) intent.getSerializableExtra("reason");             // 중단 이유

                    if(reason.equals(Reason.Reboot)||reason.equals(Reason.HardReset)||reason.equals(Reason.SoftReset)){                           // reboot,hardReset,SoftReset일때는 앱이 꺼질때 중단 안보내야함
                        ResetType_b = false;
                    }


                    if(Boolean.TRUE.equals(viewModel.getright_device_charging().getValue())){           // 충전중이라면
                        Log.i("Exception check ", pay_num +" "+viewModel.getRight_pay_number().getValue());

                        if(pay_num.equals(viewModel.getRight_pay_number().getValue())||reason.equals(Reason.Local)){    // 멈추는게 remote여서 pay_num이 맞거나, 기기 자체에서 멈추는 local일경우
                            serialClient.Serialport_SendMessage(SerialModel.RightChargerStop,reason);

                        }

                    }


                    break;



                case ReceiverModel.SerialRightChargerStop:

                    boolean StopReceive = intent.getBooleanExtra("type",false);

                    Stop_Command_time();

                    Stop_Sampled_mv_r_Timer();
                    Stop_Aligned_mv_r_Timer();
                    right_soc_array.clear();
                    right_sec_array.clear();

                    Log.d(tag,"SerialRightChargerStop :"+ StopReceive);

                    try {
                        if(StopReceive){   // 중단 메시지 성공시

                            Start_Command_time("충전 중단중입니다. 잠시만 기다려주세요.");

                        }else{ // 충전 메시지 실패시 재시도

                            Toast.makeText(context,"중단 요청에 실패했습니다.", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e){
                        Log.e(tag,ReceiverModel.SerialRightChargerStop+e);
                    }
                    break;

                case ReceiverModel.SerialRightChargerFirstStart:

                    Stop_Command_time();

                    int W = intent.getIntExtra("first",1);

                    Log.d(tag,"SerialRightChargerFirstStart :"+ W);

                    if(W==0){
                        W=1;
                    }


                    try {

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sim = new SimpleDateFormat("yyyyMMddHHmmss");

                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("Device_Id",KeyControl.getDeviceId());
                        jsonObject.put("Charge_Pay","");    //충전 결제액
                        jsonObject.put("Pay_By","sample");     // 결제 수단    = 카드구분
                        jsonObject.put("Credit_Num","");   //카드 번호
                        jsonObject.put("Pay_Num",viewModel.getRight_pay_number().getValue());    // 승인 번호
                        jsonObject.put("Pay_Day",sim.format(date).toString());  // 결제일


                        mClient.sendDataTransfer(KeyControl.getDeviceId(),DataTransferModel.getI_Pay(),String.valueOf(jsonObject));     // 테스트 결제 저장

                        mClient.sendStartTransaction(DeviceModel.getConnectId(),viewModel.getRight_pay_number().getValue(),W*1000);


                        Set_Send_StatusNotification(ChargePointStatus.Charging,ChargePointErrorCode.NoError,"","","");


                        Intent intent3 = new Intent("Page");
                        intent3.putExtra("page",PageAddrModel.ChargeInfoFgm_r);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent3);

                    } catch (Exception e) {

                        Toast.makeText(context,"서버에 충전요청이 실패했습니다.", Toast.LENGTH_LONG).show();

                        Log.e(tag,ReceiverModel.SerialRightChargerFirstStart+e);
                        Logger.e(ReceiverModel.SerialRightChargerFirstStart+e);
                    }





                    Start_SOC_Cal();
                    Start_right_charge_time();
                    Start_right_soc_time();
                    break;


                case ReceiverModel.SerialRightChargerFirstStop:

                    Stop_Command_time();

                    Reason Stopreason = (Reason) intent.getSerializableExtra("reason");
                    int LW = intent.getIntExtra("last",1);

                    Log.d(tag,"SerialRightChargerFirstStop :"+ LW);



                    try {

                        Set_Send_StatusNotification(ChargePointStatus.Finishing ,ChargePointErrorCode.NoError,"","","");
                        mClient.sendStopTransaction(LW,Integer.parseInt(Objects.requireNonNull(viewModel.getRight_pay_number().getValue())),Stopreason);
                    } catch (Exception e) {
                        Log.e(tag,ReceiverModel.SerialRightChargerFirstStop+e);
                        Logger.e(ReceiverModel.SerialRightChargerFirstStop+e);
                    }

                    Intent intent4 = new Intent("Page");
                    intent4.putExtra("page", PageAddrModel.ChargeCompleteFgm_r);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent4);


                    allStop();

                    break;

                case ReceiverModel.SerialRightChargerSOC:

                    int soc = intent.getIntExtra("soc",0);

                    Log.d(tag,"SerialRightChargerSOC :"+ soc);

                    Log.d("example","right_sec : "+right_sec);


                    if(soc == 120){
                        errorDialog.showDialog("경고", "현재 차량은 충전 %를 지원하지 않습니다.");
                        viewModel.getright_charge_persent().postValue(soc);
                        Stop_right_soc_time();
                        Stop_SOC_Cal();
                        return;
                    }else if(soc ==110){
                        errorDialog.showDialog("경고", "soc의 값을 불러오는데 차질이 생겼습니다. 잠시 후 다시 시도합니다.");
                        return;
                    }else if(soc <= 100){
                        viewModel.getright_charge_persent().postValue(soc);

                        socCnt = socCnt+1;

                        if(socCnt == 0){
                            origin_soc = soc;
                            return;
                        }

                        if(soc < max_soc){

                            if(soc_timer==null){
                                pre_charge_compare = false;
                                socCnt = 3;
                                Start_right_soc_time();
                            }

                            right_sec_array.add(right_sec);
                            right_soc_array.add(soc-origin_soc);




                            if(right_sec_array.size()>1){
                                right_pre_sec = linearRegression(right_sec_array,right_soc_array, (max_soc-origin_soc));
                                viewModel.getright_charge_pre_time().postValue(String.format("%02d:%02d:%02d", right_pre_sec/3600,right_pre_sec/60, right_pre_sec%60));
                            }


                            if(socCnt >=socCnt_max){    // 마지막 soc 요청부터
                                pre_charge_compare = true;
                            }
                        }else {
                            serialClient.Serialport_SendMessage(SerialModel.RightChargerState,null);
                            Log.d("example","charge complete !!!! ");


                            Intent intent1 = new Intent(ReceiverModel.SerialRightChargerStopCommand);
                            intent1.putExtra("reason", Reason.Local );
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

                            SerialRightChargerStopCommandFunction("",Reason.Local);

                        }
                    }






                    break;

                case ReceiverModel.SerialRightChargerDeviceStop:

                    Log.d(tag,"SerialRightChargerDeviceStop");


                    allStop();

                    Intent intent5 = new Intent("Page");
                    intent5.putExtra("page", PageAddrModel.ChargeCompleteFgm_r);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent5);


                    break;








                case ReceiverModel.Receive_Reset:                // 재시작

                    allStop();


                    ResetRetries = Integer.parseInt(ConfigurationControl.getResetRetries());

                    ResetType resetType = (ResetType) intent.getSerializableExtra("resetType");

                    ResetFunction(resetType);           // 리셋 행동

                    break;




                case ReceiverModel.GetChageAvailablitity:

                    int con = intent.getIntExtra("Connector_id",1);
                    AvailabilityType T = (AvailabilityType) intent.getSerializableExtra("Type");




                    if(T == AvailabilityType.Inoperative){   // 사용중지메시지일 경우



                        KeyControl.setRightChargingEnabled(false);

                        if(Boolean.FALSE.equals(viewModel.getright_device_charging().getValue())){        // 충전중이 아닐때
                            //뷰모델 바꾸기
                            viewModel.getright_device_charging_enable().setValue(false);

                            try{
                                Set_Send_StatusNotification(ChargePointStatus.Unavailable,ChargePointErrorCode.NoError,"","","");
                            }catch (Exception e){
                                Log.e(tag, ReceiverModel.GetChageAvailablitity + e);
                                Logger.e( ReceiverModel.GetChageAvailablitity + e);
                            }
                        }

                    }else if(T == AvailabilityType.Operative){ // 사용가능메시지일 경우

                        try {
                            Set_Send_StatusNotification(ChargePointStatus.Available,ChargePointErrorCode.NoError,"","","");
//                            mClient.sendStatusNotification(DeviceModel.getConnectId(),ChargePointErrorCode.NoError,ChargePointStatus.Available,null,null,null);
                        } catch (Exception e) {
                            Log.e(tag, ReceiverModel.GetChageAvailablitity + e);
                            Logger.e( ReceiverModel.GetChageAvailablitity + e);
                        }

                        KeyControl.setRightChargingEnabled(true);
                        //뷰모델 바꾸기
                        viewModel.getright_device_charging_enable().setValue(true);
                    }



                    break;


                case ReceiverModel.ServerConn :

                    boolean s_bool = intent.getBooleanExtra("s_bool",false);


                    if(viewModel.getServerConn().getValue() != s_bool){
                        Log.i(tag,"ServerConn : "+ s_bool);
                        Logger.i( "ServerConn : "+ s_bool);
                        viewModel.getServerConn().postValue(s_bool);
                    }


                    break;



                case ReceiverModel.MeterValues_Trigger:
                    try {
                        mClient.sendTriggerMeterValues(DeviceModel.getConnectId(),Integer.parseInt(viewModel.getRight_pay_number().getValue()),String.format("%.3f",viewModel.getright_charge_amount().getValue()),viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(),String.valueOf(viewModel.getright_charge_persent().getValue()),viewModel.getRight_TEM().getValue());
                    } catch (Exception e) {
                        Log.e(tag,ReceiverModel.MeterValues_Trigger+e);
                        Logger.e(ReceiverModel.MeterValues_Trigger+e);
                    }
                    break;

                case ReceiverModel.StatusNotification_Trigger:
                    try {
                        mClient.sendStatusNotification(DeviceModel.getConnectId(),viewModel.getChargePoint_Status().getValue(),viewModel.getChargePoint_ErrorCode().getValue(),viewModel.getStatus_info().getValue(),viewModel.getStatus_vendorid().getValue(),viewModel.getStatus_vendorerrorcode().getValue());
                    } catch (Exception e) {
                        Log.e(tag,ReceiverModel.StatusNotification_Trigger+e);
                        Logger.e( ReceiverModel.StatusNotification_Trigger+e);
                    }
                    break;

                case ReceiverModel.SetFirmwareStatus:
                    FirmwareStatus f = (FirmwareStatus) intent.getSerializableExtra("FirmwareStatus");

                    if(f == null){
                        f = FirmwareStatus.Idle;
                    }

                    if(f.equals(FirmwareStatus.Downloading)||f.equals(FirmwareStatus.Installing)){
                        viewModel.getStatus_firmwarestatus().postValue(f);
                    }else{
                        viewModel.getStatus_firmwarestatus().postValue(FirmwareStatus.Idle);
                    }

                    break;

                case ReceiverModel.FirmwareStatusNotification_Trigger:
                    try {
                        mClient.sendFirmwareStatusNotification(viewModel.getStatus_firmwarestatus().getValue());
                    } catch (Exception e){
                        Log.e(tag,ReceiverModel.FirmwareStatusNotification_Trigger+e);
                        Logger.e( ReceiverModel.FirmwareStatusNotification_Trigger+e);
                    }
                    break;

                case ReceiverModel.SetDiagnosticsStatus:
                    DiagnosticsStatus d = (DiagnosticsStatus) intent.getSerializableExtra("DiagnosticsStatus");

                    if(d == null){
                        d = DiagnosticsStatus.Idle;
                    }

                    if(d.equals(DiagnosticsStatus.Uploading)){
                        viewModel.getStatus_diagnosticsstatus().postValue(d);
                    }else{
                        viewModel.getStatus_diagnosticsstatus().postValue(DiagnosticsStatus.Idle);
                    }

                    break;

                case ReceiverModel.DiagnosticsStatusNotification_Trigger:
                    try {
                        mClient.sendDiagnosticsStatusNotification(viewModel.getStatus_diagnosticsstatus().getValue());
                    } catch (Exception e){
                        Log.e(tag,ReceiverModel.DiagnosticsStatusNotification_Trigger+e);
                        Logger.e(ReceiverModel.DiagnosticsStatusNotification_Trigger+e);
                    }
                    break;

                case ReceiverModel.FirmwareUpdate:
                    UpdateFirmwareRetries = intent.getIntExtra("UpdateFirmwareRetries",0);
                    UpdateFirmwareRetriesInterval = intent.getLongExtra("UpdateFirmwareRetriesInterval",0);
                    ZonedDateTime UpdateFirmwareRetrieveDate = (ZonedDateTime) intent.getSerializableExtra("UpdateFirmwareRetrieveDate");
                    String UpdateFirmwareLocation = intent.getStringExtra("UpdateFirmwareLocation");
                    Logger.i( "ReceiverModel.FirmwareUpdate");

                    Stop_update_Timer();

                    try{
                        if(Boolean.TRUE.equals(viewModel.getright_device_charging().getValue())){
                            Start_update_Timer(UpdateFirmwareRetrieveDate,UpdateFirmwareLocation);
                        }else{
                            apk_update(UpdateFirmwareLocation);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Start_update_Timer(UpdateFirmwareRetrieveDate,UpdateFirmwareLocation);
                    }

                    break;


            }
        }
    };



    Handler complete_next_handler = null;

    public void PageMove_ChargeSelectFgm(ChargePointStatus chargePointStatus, ChargePointErrorCode chargePointErrorCode,String info,String vendorid, String VendorErrorCode){       // 처음페이지로 돌아올때 돌아오는 이유가 제각각이기 때문에 상태,이유를 받는다.

        viewModel.setRight_device_charging(false);

//        Stop_TimeOut_time();    //
        try {

            if(!KeyControl.getRightChargingEnabled()){      // 만약 커넥터사용중단 명령 ChangeAvailability 이 들어왔을경우
                //뷰모델 바꾸기
                viewModel.getright_device_charging_enable().setValue(false);

                Set_Send_StatusNotification(ChargePointStatus.Unavailable,ChargePointErrorCode.NoError,"","","");
            }else{      // 만약 커넥터사용중단 명령 ChangeAvailability 이 없을경우 원래 전달받은 내용으로
                if(chargePointStatus != null) {
                    Set_Send_StatusNotification(chargePointStatus, chargePointErrorCode, info==null?"":info, vendorid==null?"":vendorid, VendorErrorCode==null?"":VendorErrorCode);
                }
            }


//            mClient.sendStatusNotification(DeviceModel.getConnectId(),chargePointErrorCode,chargePointStatus,null,vendorid,VendorErrorCode);
        } catch (Exception e) {
            e.printStackTrace();;
        }

        if(complete_next_handler != null){
            complete_next_handler.removeCallbacksAndMessages(null);
            complete_next_handler = null;
        }

        Stop_right_charge_time();

        mViewPager.setCurrentItem(PageAddrModel.ChargeSelectFgm);

    }


    public void PageMove_ChargeInfoFgm(){

//        Stop_TimeOut_time();

        viewModel.setRight_device_charging(true);

        Start_Sampled_mv_r_Timer(Integer.parseInt(Objects.requireNonNull(viewModel.getRight_pay_number().getValue()))) ;                                                 // Sampled metervalue
        Start_Aligned_mv_r_Timer(Integer.parseInt(viewModel.getRight_pay_number().getValue()));

        Stop_Command_time();
        mViewPager.setCurrentItem(PageAddrModel.ChargeInfoFgm_r);
    }

    public void PageMove_ChargeCompleteFgm(){
        Stop_Sampled_mv_r_Timer();
        Stop_Aligned_mv_r_Timer();

        mViewPager.setCurrentItem(PageAddrModel.ChargeCompleteFgm_r);

        // 10초후 처음 페이지로 이동
        complete_next_handler = new Handler(Looper.getMainLooper());
        complete_next_handler.postDelayed(() -> {

            //페이지이동
            PageMove_ChargeSelectFgm(ChargePointStatus.Available, ChargePointErrorCode.NoError,null,null,null);
        },5000);
    }

    // 페이지 이동
    private final BroadcastReceiver pageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            int page = intent.getIntExtra("page",0);
            Log.d(tag,"pageReceiver");
            Logger.i("pageReceiver");
            switch (page){

                    //정보페이지
                case PageAddrModel.ChargeInfoFgm_r:
                    soundPoolPlayer.play(R.raw.charge_info_fragment_sound);
                    PageMove_ChargeInfoFgm();
                    break;

                    // 완료 페이지
                case PageAddrModel.ChargeCompleteFgm_r:
                    soundPoolPlayer.play(R.raw.charge_complete_fragment_sound);
                    PageMove_ChargeCompleteFgm();

                    return;
                default:                // 메인 fragment

                    ChargePointStatus chargePointStatus = (ChargePointStatus) intent.getSerializableExtra("ChargePointStatus");
                    ChargePointErrorCode chargePointErrorCode = (ChargePointErrorCode) intent.getSerializableExtra("ChargePointErrorCode");
                    String vendorId = intent.getStringExtra("VendorId");
                    String vendorErrorCode = intent.getStringExtra("VendorErrorCode");
                    PageMove_ChargeSelectFgm(chargePointStatus,chargePointErrorCode,null,vendorId,vendorErrorCode);
                    break;
            }


//            mViewPager.setCurrentItem(page,false);    //애니메이션 없애기
        }
    };











    // 다른 앱 위에 그리기 권한 얻기
    public void getPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우

            if (!Settings.canDrawOverlays(this)) {              // 체크
                Log.i(tag,"앱위에 그리기 권한 없음");
                Logger.i("앱위에 그리기 권한 없음");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

    }
//
//    private WindowManager windowManager;
//    private View view;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_center);


//        startLockTask();    // 화면 고정
//        stopLockTask();

        // 권한 허용
        getPermission();

        //db
        helper = SQLiteHelper.getInstance(getApplicationContext());
//        sqlite = new SQLiteControl(helper);
        sqlite = SQLiteControl.getInstance(helper);


        mViewPager = findViewById(R.id.viewpager);

        setupViewPager(mViewPager);

        mViewPager.setCurrentItem(PageAddrModel.ChargeSelectFgm);

        soundPoolPlayer = SoundPoolPlayer.getInstance();    // 사운드
        soundPoolPlayer.initSounds(getApplicationContext());

        //페이지 전환
        IntentFilter sfilter01 = new IntentFilter();
        sfilter01.addAction("Page");
        LocalBroadcastManager.getInstance(this).registerReceiver(pageReceiver, sfilter01);
//        registerReceiver(pageReceiver,sfilter01);

        IntentFilter chargingFilter = new IntentFilter();



        chargingFilter.addAction(ReceiverModel.SerialRightChargerState);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerStart);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerReset);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerStartCommand);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerStopCommand);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerStop);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerFirstStart);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerFirstStop);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerSOC);
        chargingFilter.addAction(ReceiverModel.SerialRightChargerDeviceStop);

        chargingFilter.addAction(ReceiverModel.GetPay);
        chargingFilter.addAction(ReceiverModel.GetAuthorize);

        chargingFilter.addAction(ReceiverModel.Receive_Reset);
        chargingFilter.addAction(ReceiverModel.GetChageAvailablitity);
        chargingFilter.addAction(ReceiverModel.ServerConn);
        chargingFilter.addAction(ReceiverModel.MeterValues_Trigger);
        chargingFilter.addAction(ReceiverModel.StatusNotification_Trigger);
        chargingFilter.addAction(ReceiverModel.SetFirmwareStatus);
        chargingFilter.addAction(ReceiverModel.FirmwareStatusNotification_Trigger);
        chargingFilter.addAction(ReceiverModel.SetDiagnosticsStatus);
        chargingFilter.addAction(ReceiverModel.DiagnosticsStatusNotification_Trigger);
        chargingFilter.addAction(ReceiverModel.FirmwareUpdate);

        LocalBroadcastManager.getInstance(this).registerReceiver(ChargingReceiver, chargingFilter);


        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        main_title_time_tv = findViewById(R.id.main_title_time_tv);     //시간
        main_title_temp_tv = findViewById(R.id.main_title_temp_tv);     //온도
        main_title_humidity_tv = findViewById(R.id.main_title_humidity_tv); //습도
        main_title_ws_tv = findViewById(R.id.main_title_ws_tv);     //풍속
        main_title_wd_tv = findViewById(R.id.main_title_wd_tv);     //풍향
//        main_title_ap_tv = findViewById(R.id.main_title_ap_tv);     //대기압
        main_title_precipitation_tv = findViewById(R.id.main_title_precipitation_tv);     //강수량
//        main_title_voltage_tv = findViewById(R.id.main_title_voltage_tv);     //발전량


        //livedata 바뀔시에 이벤트
        final Observer<String> ev_main_title_time_tv = b -> {
            main_title_time_tv.setText(b);
        };
        final Observer<String> ev_main_title_temp_tv = b -> {
            main_title_temp_tv.setText(b+" ℃");
        };
        final Observer<String> ev_main_title_humidity_tv = b -> {
            main_title_humidity_tv.setText(b+" %");
        };
        final Observer<String> ev_main_title_ws_tv = b -> {
            main_title_ws_tv.setText(b+" m/s");
        };
        final Observer<String> ev_main_title_wd_tv = b -> {
            main_title_wd_tv.setText(b+" °");
        };
//        final Observer<String> ev_main_title_ap_tv = b -> {
//            main_title_ap_tv.setText(b+" hPa");
//        };
        final Observer<String> ev_main_title_precipitation_tv = b -> {
            main_title_precipitation_tv.setText(b+" mm");
        };
//        final Observer<String> ev_main_title_voltage_tv = b -> {
//            main_title_voltage_tv.setText(b+" Wh");
//        };

        viewModel.getMain_title_time_tv().observe(this,ev_main_title_time_tv);
        viewModel.getMain_title_temp_tv().observe(this,ev_main_title_temp_tv);
        viewModel.getMain_title_humidity_tv().observe(this,ev_main_title_humidity_tv);
        viewModel.getMain_title_ws_tv().observe(this,ev_main_title_ws_tv);
        viewModel.getMain_title_wd_tv().observe(this,ev_main_title_wd_tv);
//        viewModel.getMain_title_ap_tv().observe(this,ev_main_title_ap_tv);
        viewModel.getMain_title_precipitation_tv().observe(this,ev_main_title_precipitation_tv);


        un_stopT_handler = new Handler(Looper.getMainLooper());
        un_statusN_handler = new Handler(Looper.getMainLooper());

        //로딩 다이어로그
        loadingDialog = new LoadingDialog(this);
        errorDialog = new ErrorDialog(this);

        mClient = JsonClient.getInstance(getApplicationContext());

        viewModel.getServerConn().setValue(!mClient.isClosed());  // 서버 연결되어있는지 .

        Log.d(tag, "device_charging_enable : " + viewModel.getright_device_charging_enable().getValue());
        Log.d(tag,"viewModel.getright_device_charging().getValue() = "+ viewModel.getright_device_charging().getValue());
        Log.d(tag,"KeyControl.getRightChargingEnabled() = "+ KeyControl.getRightChargingEnabled());


        final Observer<Boolean> ocpp_server_conn = b -> {
            Log.i(tag,"ServerConn observer : "+ b);             //-=------------ 이부분이 false로 됨. splashactivity 에서 넘어올때 다 연결된 상태에서 넘어와야할듯
            Logger.i("ServerConn observer : "+ b);
            if(!b){  // 연결이 끊기고
                if(Boolean.FALSE.equals(viewModel.getright_device_charging().getValue())){           // 충전중이 아니면
                    if(mViewPager.getCurrentItem() != PageAddrModel.ChargeSelectFgm){    // 홈 페이지가 아니면
                        PageMove_ChargeSelectFgm(ChargePointStatus.Faulted, ChargePointErrorCode.WeakSignal,"reconnected",null,null);  // 서버 연결 끊기면 처음 페이지로
                    }
//                mClient.InitClient();
                }
            }else{   // 연결이 되면

                Log.i("app is start ", String.valueOf(viewModel.getright_device_charging().getValue()));
                Logger.i("app is start , is charging = "+ String.valueOf(viewModel.getright_device_charging().getValue()));


                if(booting){ // 기기가 처음 켜졌을때
                    try {

                        if(Boolean.TRUE.equals(viewModel.getright_device_charging().getValue())){        // 오류로 인해 종료 시점이 충전중이였을경우

                            sqlite.InsertUnknownStopTrasaction(String.valueOf(returnZonedDateTime()),viewModel.getRight_pay_number().getValue(),String.valueOf((int)(viewModel.getright_charge_amount().getValue()*1000)),String.valueOf(Reason.PowerLoss));

                            errorDialog.showDialog( "오류 발생", "충전도중 기기에 오류가 발생하여 종료되었습니다. 본사에 연락을 해주세요.");
                            viewModel.setRight_device_charging(false);     // 충전여부 다시 초기화
                            viewModel.getright_device_charging_enable().postValue(false);
                            viewModel.getChargePoint_Status().postValue(ChargePointStatus.Unavailable);
                            Set_Send_StatusNotification(ChargePointStatus.Unavailable,ChargePointErrorCode.OtherError,"device is exited with error while is charging",null,null);


                        }else{      // 아니였을 경우
                            if(KeyControl.getRightChargingEnabled()){       // 커넥터 사용 가능 일 경우

                                Set_Send_StatusNotification(ChargePointStatus.Available,ChargePointErrorCode.NoError," "," "," ");
                            }else{
                                Set_Send_StatusNotification(ChargePointStatus.Unavailable,ChargePointErrorCode.NoError,"","","");
                            }
                        }

                        booting = false;
                        //                   mClient.sendStatusNotification(DeviceModel.getConnectId(),ChargePointErrorCode.NoError,ChargePointStatus.Available,null,null,null);
                    } catch (Exception e) {
                        e.printStackTrace();;
                    }
                }

//                SendUnkownStopTransactionHandler();           // 못보냈던 자료들 다 보내기
                SendUnknownStatusNotificationHandler();      // 못보냈던 자료들 다 보내기

            }
            if(KeyControl.getRightChargingEnabled()){   // 커넥터 사용 가능 일 경우
                viewModel.getright_device_charging_enable().setValue(b);        // 점검중 페이지
            }else{
                viewModel.getright_device_charging_enable().setValue(false);
            }

        };

        viewModel.getServerConn().observe(this,ocpp_server_conn);



        getAppVersion();

//        Start_hb_thread();


        Log.d(tag, "KeyControl.getAuthorizationCache : " + KeyControl.getAuthorizationCache());


        String[] a = new String[]{"Time", "EAIR", "EAII", "CI", "CO", "PAI", "PO", "SoC", "Idtag", "Type"};

        sqlite.SelectMetervaluesAll(a);
        sqlite.SelectTransactionFactoryAll();
        sqlite.SelectNotificationFactoryAll();



        // 충전기 시리얼 통신
        serialClient = SerialPortClient.getInstance(getApplicationContext());
        serialClient.Serialport_Open();
//        serialClient.Serialport_SendMessage(SerialModel.RightChargerState);



//        mClient.setCenterClientInterfaceListener(new CenterClientInterface() {
//            @Override
//            public void onGetPay(float timertype_Get_Pay) {
//                Log.i(tag," GetPay_Receiver ");
//                Logger.i(" GetPay_Receiver ");
//
//                Stop_Command_time();
//
//                if(!(timertype_Get_Pay == 0)){
//                    viewModel.setPayPrice(timertype_Get_Pay);
//
//                    Start_Command_time("서버에 충전허가를 요청합니다.");
//
//
//                    Random random = new Random();
//                    String pay_num = String.valueOf(random.nextInt(999999999-100000000+1)+100000000);       // 결제가 없기떄문에 랜덤 부여
//                    viewModel.getRight_pay_number().postValue(pay_num);
//
//                    try {
////                        mClient.connect();
//                        mClient.sendAuthorize(pay_num);
//                    } catch (Exception e) {
//                        e.printStackTrace();;
//                    }
//
//
//                }else{
//                    Toast.makeText(getApplicationContext(),"값을 불러오는데 오류가 생겼습니다.",Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onGetAuthorize(String itag) {
//                Log.i(tag," authorize_Receiver ");
//                Logger.i(" authorize_Receiver ");
//                try {
//
//                    Stop_Command_time();
//
//                    Log.i(tag,"now charging status : "+viewModel.getright_device_charging().getValue());
//                    Logger.i("now charging status : "+viewModel.getright_device_charging().getValue());
//                    if(Boolean.FALSE.equals(viewModel.getright_device_charging().getValue())){       // 충전중이 아닐경우
//
//                        if(viewModel.getRight_pay_token().getValue().equals("")){          // 이전 결제 내역이 없을경우 (remote start)
//                            Log.i(tag,"remote authorize receive itag : "+ itag);
//
//                            viewModel.setRight_pay_number(itag);            // remote때
////                            viewModel.getRight_pay_number().setValue(itag);
//                        }else{                                                              // 결제 내역이 있을 경우 (결제는 했는데 충전으로 넘어가지 않을 경우)
//                            itag = viewModel.getRight_pay_number().getValue();
//                        }
//
//                        if(Boolean.FALSE.equals(viewModel.getright_device_charging_enable().getValue())){
//                            Set_Send_StatusNotification(ChargePointStatus.SuspendedEV,ChargePointErrorCode.NoError,"","","");
//                            return;
//                        }
////                            mClient.sendStartTransaction(DeviceModel.getConnectId(),itag,11000);        // kwh 를 wh로 바꿔야함
//
//                        // 시작 메시지
//                        Start_Command_time("기기와의 상태를 확인합니다.");
//                        serialClient.Serialport_SendMessage(SerialModel.RightChargerState,null);
//                    }
//
//
//
//
//                } catch (Exception e) {
//                    Log.e(tag, ReceiverModel.GetAuthorize+ e);
//                    Logger.e( ReceiverModel.GetAuthorize+ e);
//                }
//            }
//
//            @Override
//            public void onSerialRightChargerStopCommand(String pay_num, Reason reason) {
//
//                SerialRightChargerStopCommandFunction(pay_num,reason);
//            }
//
//            @Override
//            public void onReceive_Reset(ResetType resetType) {
//                allStop();
//
//
//                ResetRetries = Integer.parseInt(ConfigurationControl.getResetRetries());
//
//                ResetFunction(resetType);           // 리셋 행동
//            }
//
//            @Override
//            public void onGetChageAvailablitity(AvailabilityType T) {
//
//                if(T == AvailabilityType.Inoperative){   // 사용중지메시지일 경우
//
//                    KeyControl.setRightChargingEnabled(false);
//
//                    if(Boolean.FALSE.equals(viewModel.getright_device_charging().getValue())){        // 충전중이 아닐때
//                        //뷰모델 바꾸기
//                        viewModel.getright_device_charging_enable().setValue(false);
//
//                        try{
//                            Set_Send_StatusNotification(ChargePointStatus.Unavailable,ChargePointErrorCode.NoError,"","","");
//                        }catch (Exception e){
//                            Log.e(tag, ReceiverModel.GetChageAvailablitity + e);
//                            Logger.e( ReceiverModel.GetChageAvailablitity + e);
//                        }
//                    }
//
//                }else if(T == AvailabilityType.Operative){ // 사용가능메시지일 경우
//
//                    try {
//                        Set_Send_StatusNotification(ChargePointStatus.Available,ChargePointErrorCode.NoError,"","","");
////                            mClient.sendStatusNotification(DeviceModel.getConnectId(),ChargePointErrorCode.NoError,ChargePointStatus.Available,null,null,null);
//                    } catch (Exception e) {
//                        Log.e(tag, ReceiverModel.GetChageAvailablitity + e);
//                        Logger.e( ReceiverModel.GetChageAvailablitity + e);
//                    }
//
//                    KeyControl.setRightChargingEnabled(true);
//                    //뷰모델 바꾸기
//                    viewModel.getright_device_charging_enable().setValue(true);
//                }
//            }
//
//            @Override
//            public void onServerConn(boolean s_bool) {
//
//                Log.i(tag,"ServerConn : "+ s_bool);
//                Logger.i( "ServerConn : "+ s_bool);
//                viewModel.getServerConn().postValue(s_bool);
//            }
//
//            @Override
//            public void onMeterValues_Trigger() {
//                try {
//                    mClient.sendTriggerMeterValues(DeviceModel.getConnectId(),Integer.parseInt(viewModel.getRight_pay_number().getValue()),String.format("%.3f",viewModel.getright_charge_amount().getValue()),viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(),String.valueOf(viewModel.getright_charge_persent().getValue()),viewModel.getRight_TEM().getValue());
//                } catch (Exception e) {
//                    Log.e(tag,ReceiverModel.MeterValues_Trigger+e);
//                    Logger.e(ReceiverModel.MeterValues_Trigger+e);
//                }
//            }
//
//            @Override
//            public void onStatusNotification_Trigger() {
//                try {
//                    mClient.sendStatusNotification(DeviceModel.getConnectId(),viewModel.getChargePoint_Status().getValue(),viewModel.getChargePoint_ErrorCode().getValue(),viewModel.getStatus_info().getValue(),viewModel.getStatus_vendorid().getValue(),viewModel.getStatus_vendorerrorcode().getValue());
//                } catch (Exception e) {
//                    Log.e(tag,ReceiverModel.StatusNotification_Trigger+e);
//                    Logger.e( ReceiverModel.StatusNotification_Trigger+e);
//                }
//            }
//
//            @Override
//            public void onSetFirmwareStatus(FirmwareStatus f) {
//                if(f == null){
//                    f = FirmwareStatus.Idle;
//                }
//
//                if(f.equals(FirmwareStatus.Downloading)||f.equals(FirmwareStatus.Installing)){
//                    viewModel.getStatus_firmwarestatus().postValue(f);
//                }else{
//                    viewModel.getStatus_firmwarestatus().postValue(FirmwareStatus.Idle);
//                }
//            }
//
//            @Override
//            public void onFirmwareStatusNotification_Trigger() {
//                try {
//                    mClient.sendFirmwareStatusNotification(viewModel.getStatus_firmwarestatus().getValue());
//                } catch (Exception e){
//                    Log.e(tag,ReceiverModel.FirmwareStatusNotification_Trigger+e);
//                    Logger.e( ReceiverModel.FirmwareStatusNotification_Trigger+e);
//                }
//            }
//
//            @Override
//            public void onSetDiagnosticsStatus(DiagnosticsStatus d) {
//                if(d == null){
//                    d = DiagnosticsStatus.Idle;
//                }
//
//                if(d.equals(DiagnosticsStatus.Uploading)){
//                    viewModel.getStatus_diagnosticsstatus().postValue(d);
//                }else{
//                    viewModel.getStatus_diagnosticsstatus().postValue(DiagnosticsStatus.Idle);
//                }
//            }
//
//            @Override
//            public void onDiagnosticsStatusNotification_Trigger() {
//                try {
//                    mClient.sendDiagnosticsStatusNotification(viewModel.getStatus_diagnosticsstatus().getValue());
//                } catch (Exception e){
//                    Log.e(tag,ReceiverModel.DiagnosticsStatusNotification_Trigger+e);
//                    Logger.e(ReceiverModel.DiagnosticsStatusNotification_Trigger+e);
//                }
//            }
//
//            @Override
//            public void onFirmwareUpdate(int UpdateFirmwareRetries_, long UpdateFirmwareRetriesInterval_, ZonedDateTime UpdateFirmwareRetrieveDate, String UpdateFirmwareLocation) {
//                UpdateFirmwareRetries = UpdateFirmwareRetries_;
//                UpdateFirmwareRetriesInterval = UpdateFirmwareRetriesInterval_;
//
//                Stop_update_Timer();
//
//                try{
//                    if(Boolean.TRUE.equals(viewModel.getright_device_charging().getValue())){
//                        Start_update_Timer(UpdateFirmwareRetrieveDate,UpdateFirmwareLocation);
//                    }else{
//                        apk_update(UpdateFirmwareLocation);
//                    }
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                    Start_update_Timer(UpdateFirmwareRetrieveDate,UpdateFirmwareLocation);
//                }
//            }
//        });

//        serialClient.setSerialClientInterfaceListener(new SerialClientInterface() {
//            @Override
//            public void onSerialRightChargerState(boolean ChargingEmergency, int ChargeState, boolean Charging, boolean PLCConnection, String ChargeAmount) {
//
//                Log.d(tag,"SerialRightChargerState = ChargingEmergency : "+ChargingEmergency + " PLCConnection : "+PLCConnection+" ChargeState : "+ ChargeState + " Charging : "+ Charging +  " ChargeAmount : "+ChargeAmount);
//
//                Stop_Command_time();
//
//                Log.d(tag,"page = "+mViewPager.getCurrentItem());
//
//                if(ChargingEmergency){
//
//
//                    if(mViewPager.getCurrentItem()== PageAddrModel.ChargeInfoFgm_r){
//
//                        allStop();
//
//                        Intent intent5 = new Intent("Page");
//                        intent5.putExtra("page", PageAddrModel.ChargeSelectFgm);
//                        intent5.putExtra("ChargePointStatus", ChargePointStatus.Faulted);
//                        intent5.putExtra("ChargePointErrorCode", ChargePointErrorCode.OtherError);
//
//                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent5);
//                    }
//
//                    Toast.makeText(getApplicationContext(),"응급상황에 의한 충전실패입니다.", Toast.LENGTH_LONG).show();
//
//                }
//
//                if(Charging){
//
//                    int receive_chargeamount = Integer.parseInt(ChargeAmount)*1000;
//
//                    if(receive_chargeamount>0){    // amount가 0일 경우
//
//                    }
//
//
//
////                        viewModel.getright_charge_persent().postValue(Integer.parseInt(ChargePersent));
//                    viewModel.setRight_charge_amount(Float.valueOf(ChargeAmount));
//
//                    try {
//                        ZonedDateTime zonedDateTime = returnZonedDateTime();
//
//
//
//                        // db 저장
//                        sqlite.InsertMetervalues(String.valueOf(zonedDateTime),String.format("%.3f",Float.valueOf(ChargeAmount)),viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(),String.valueOf(viewModel.getright_charge_persent().getValue()),viewModel.getRight_TEM().getValue(),viewModel.getRight_pay_number().getValue(),isSampled?"2":"1");
//                        if(!mClient.isClosed())
//                            mClient.sendMeterValues(DeviceModel.getConnectId(),Integer.parseInt(viewModel.getRight_pay_number().getValue()), String.format("%.3f",Float.valueOf(ChargeAmount)),isSampled,zonedDateTime,viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(), String.valueOf(viewModel.getright_charge_persent().getValue()), viewModel.getRight_TEM().getValue());
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Logger.e("Start_Sampled_mv error : "+e);
//                    }
//
//
//                }else{
//
//
//                    if(mViewPager.getCurrentItem()== PageAddrModel.ChargeInfoFgm_r){
//
//                        allStop();
//
//                        Intent intent5 = new Intent("Page");
//                        intent5.putExtra("page", PageAddrModel.ChargeSelectFgm);
//                        intent5.putExtra("ChargePointStatus", ChargePointStatus.SuspendedEV);
//                        intent5.putExtra("ChargePointErrorCode", ChargePointErrorCode.WeakSignal);
//                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent5);
//
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onSerialRightChargerStart(boolean StartReceive) {
//
//                Stop_Command_time();
//
//                Log.d(tag,"SerialRightChargerStart :"+ StartReceive);
//
//                try {
//                    if(StartReceive){   // 충전 메시지 성공시
//
//                        Start_Command_time("충전 준비중입니다. 잠시만 기다려주세요.");
//
//                    }else{ // 충전 메시지 실패시 재시도
//
//                        Toast.makeText(getApplicationContext(),"충전 요청에 실패했습니다.", Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (Exception e){
//                    Log.e(tag,ReceiverModel.SerialRightChargerStart+e);
//                }
//            }
//
//            @Override
//            public void onSerialRightChargerReset(boolean ResetReceive) {
//                try {
//                    if(ResetReceive){   //부팅후
//
//                        Toast.makeText(getApplicationContext(),"충전부분 리부팅이 완료되어 시스템 리부팅을 진행합니다.",Toast.LENGTH_LONG).show();
//                        Stop_Command_time();
//                    }else{   // 부팅전
//
//                        Start_Command_time("충전부분 리부팅이 먼저 진행된 후 시스템 리부팅을 진행합니다.");
//
//                    }
//                } catch (Exception e){
//                    Log.e(tag,ReceiverModel.SerialRightChargerReset+e);
//                }
//            }
//
//            @Override
//            public void onSerialRightChargerStartCommand() {
//                Log.d(tag,"SerialRightChargerStartCommand");
//                Logger.i("SerialRightChargerStartCommand");
//
//                Start_Command_time("서버와의 상태를 확인합니다.");
//                try {
////                        mClient.connect();
//                    mClient.sendDataTransfer(KeyControl.getDevicevendor(), DataTransferModel.getG_Pay(),null);      // 단가 받아오기
//                } catch (Exception e) {
//                    e.printStackTrace();;
//                }
//
//
//                serialClient.Serialport_SendMessage(SerialModel.RightChargerStart,null);
//            }
//
//            @Override
//            public void onSerialRightChargerStop(boolean StopReceive) {
//
//                Stop_Command_time();
//
//                Stop_Sampled_mv_r_Timer();
//                Stop_Aligned_mv_r_Timer();
//                right_soc_array.clear();
//                right_sec_array.clear();
//
//                Log.d(tag,"SerialRightChargerStop :"+ StopReceive);
//
//                try {
//                    if(StopReceive){   // 중단 메시지 성공시
//
//                        Start_Command_time("충전 중단중입니다. 잠시만 기다려주세요.");
//
//                    }else{ // 충전 메시지 실패시 재시도
//
//                        Toast.makeText(getApplicationContext(),"중단 요청에 실패했습니다.", Toast.LENGTH_LONG).show();
//                    }
//
//
//                } catch (Exception e){
//                    Log.e(tag,ReceiverModel.SerialRightChargerStop+e);
//                }
//            }
//
//            @Override
//            public void onSerialRightChargerFirstStart(int W) {
//                Stop_Command_time();
//
//                Log.d(tag,"SerialRightChargerFirstStart :"+ W);
//
//                if(W==0){
//                    W=1;
//                }
//
//                try {
//
////                    long now = System.currentTimeMillis();
////                    Date date = new Date(now);
////                    SimpleDateFormat sim = new SimpleDateFormat("yyyyMMddHHmmss");
////
////                    JSONObject jsonObject = new JSONObject();
////
////                    jsonObject.put("Device_Id",DeviceModel.getDeviceid());
////                    jsonObject.put("Charge_Pay","");    //충전 결제액
////                    jsonObject.put("Pay_By","sample");     // 결제 수단    = 카드구분
////                    jsonObject.put("Credit_Num","");   //카드 번호
////                    jsonObject.put("Pay_Num",viewModel.getRight_pay_number().getValue());    // 승인 번호
////                    jsonObject.put("Pay_Day",sim.format(date).toString());  // 결제일
////
////
////                    mClient.sendDataTransfer(KeyControl.getDeviceId(),DataTransferModel.getI_Pay(),String.valueOf(jsonObject));     // 테스트 결제 저장
//                    mClient.sendStartTransaction(DeviceModel.getConnectId(),viewModel.getRight_pay_number().getValue(),W*1000);
//
//                    Intent intent3 = new Intent("Page");
//                    intent3.putExtra("page",PageAddrModel.ChargeInfoFgm_r);
//
//                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent3);
//
//                } catch (Exception e) {
//
//                    Toast.makeText(getApplicationContext(),"서버에 충전요청이 실패했습니다.", Toast.LENGTH_LONG).show();
//
//                    Log.e(tag,ReceiverModel.SerialRightChargerFirstStart+e);
//                    Logger.e(ReceiverModel.SerialRightChargerFirstStart+e);
//                }
//
//                Start_SOC_Cal();
//                Start_right_charge_time();
//                Start_right_soc_time();
//            }
//
//            @Override
//            public void onSerialRightChargerFirstStop(Reason Stopreason, int LW) {
//                Stop_Command_time();
//
//                Log.d(tag,"SerialRightChargerFirstStop :"+ LW);
//
//                try {
//                    mClient.sendStopTransaction(LW,Integer.parseInt(Objects.requireNonNull(viewModel.getRight_pay_number().getValue())),Stopreason);
//                } catch (Exception e) {
//                    Log.e(tag,ReceiverModel.SerialRightChargerFirstStop+e);
//                    Logger.e(ReceiverModel.SerialRightChargerFirstStop+e);
//                }
//
//                Intent intent4 = new Intent("Page");
//                intent4.putExtra("page", PageAddrModel.ChargeCompleteFgm_r);
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent4);
//
//
//                allStop();
//            }
//
//            @Override
//            public void onSerialRightChargerSOC(int soc) {
//                Log.d(tag,"SerialRightChargerSOC :"+ soc);
//
//                if(soc == 120){
//                    errorDialog.showDialog("경고", "현재 차량은 충전 %를 지원하지 않습니다.");
//                    viewModel.getright_charge_persent().postValue(soc);
//                    Stop_right_soc_time();
//                    Stop_SOC_Cal();
//                    return;
//                }else if(soc ==110){
//                    errorDialog.showDialog("경고", "soc의 값을 불러오는데 차질이 생겼습니다. 잠시 후 다시 시도합니다.");
//                    return;
//                }else if(soc <= 100){
//                    viewModel.getright_charge_persent().postValue(soc);
//
//                    socCnt = socCnt+1;
//
//                    if(socCnt == 0){
//                        origin_soc = soc;
//                        return;
//                    }
//
//                    if(soc < max_soc){
//
//                        if(soc_timer==null){
//                            pre_charge_compare = false;
//                            socCnt = 3;
//                            Start_right_soc_time();
//                        }
//
//                        right_sec_array.add(right_sec);
//                        right_soc_array.add(soc-origin_soc);
//
//
//
//
//                        if(right_sec_array.size()>1){
//                            right_pre_sec = linearRegression(right_sec_array,right_soc_array, (max_soc-origin_soc));
//                            viewModel.getright_charge_pre_time().postValue(String.format("%02d:%02d:%02d", right_pre_sec/3600,right_pre_sec/60, right_pre_sec%60));
//                        }
//
//
//                        if(socCnt >=socCnt_max){    // 마지막 soc 요청부터
//                            pre_charge_compare = true;
//                        }
//                    }else {
//                        serialClient.Serialport_SendMessage(SerialModel.RightChargerState,null);
//                        Log.d("example","charge complete !!!! ");
//
//
//                        Intent intent1 = new Intent(ReceiverModel.SerialRightChargerStopCommand);
//                        intent1.putExtra("reason", Reason.Local );
//                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
//
//                        SerialRightChargerStopCommandFunction("",Reason.Local);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onSerialRightChargerDeviceStop() {
//                Log.d(tag,"SerialRightChargerDeviceStop");
//
//                allStop();
//
//                Intent intent5 = new Intent("Page");
//                intent5.putExtra("page", PageAddrModel.ChargeCompleteFgm_r);
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent5);
//            }
//        });


        Start_Center_time();

        // 중앙 데이터 타이머 - 10분
        Start_Center_Set_Weather_timer();

    }










    public void setupViewPager(ViewPager viewPager) {

        adapter.addFragment(new ChargeSelectFragment());
        adapter.addFragment(new ChargeInfoFragment());
        adapter.addFragment(new ChargeCompleteFragment());

        viewPager.setAdapter(adapter);
    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.i(tag,"onPause");
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i(tag,"onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(serialClient != null){
            serialClient.Serialport_Close();        // 시리얼 포트 닫기            
        }


        if(Boolean.TRUE.equals(viewModel.getright_device_charging().getValue())){        // 충전중일때 취소
            try {
                if(ResetType_b){
                    serialClient.Serialport_SendMessage(SerialModel.RightChargerStop,Reason.PowerLoss);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e(tag,"onDestroy");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(pageReceiver);           // 페이지 이동

        LocalBroadcastManager.getInstance(this).unregisterReceiver(ChargingReceiver);       // 기능 리시버

        mClient.disconnect();           // ws 연결 끊고
        mClient = null;

        Stop_Command_time();

        Stop_Center_time();

        Stop_SOC_Cal();

        allStop();

        sqlite.db_close();  // db 닫기

        soundPoolPlayer.closeSounds();  //sound 닫기

        //loadingDialog 닫기
        if(loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }

        errorDialog.destoryClass();

        Stop_Center_Set_Weather_timer();    // 중앙 데이터 타이머 종료


    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    private void SendUnkownStopTransactionHandler(){
        new Thread(() -> un_stopT_handler.post(()->{
            try {
                ArrayList<TransactionFactoryModel> arrayList = sqlite.SelectTransactionFactory();

                Log.d(tag,"SendUnkownStopTransactionHandler" + arrayList);
                Logger.i("SendUnkownStopTransactionHandler" + arrayList);

                if(arrayList.size()==0){
                    return;
                }

                for(int i = 0; i< arrayList.size(); i++){
                    try {
                        mClient.sendUnknownStopTransaction(arrayList.get(i).getMeterstop(),arrayList.get(i).getTID(),arrayList.get(i).getReason(),arrayList.get(i).getTime());
//                        Thread.sleep(1000);
//
//                        if(i == arrayList.size()-1){        // 마지막 보낼때 그후에 상태 내보냄
//
//                            mClient.sendUnknownStatusNotification();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        })).start();


    }


    private void SendUnknownStatusNotificationHandler(){
        new Thread(() -> un_statusN_handler.post(()->{
            try {
                mClient.sendUnknownStatusNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
        })).start();
    }

    private void Set_Send_StatusNotification(ChargePointStatus status,ChargePointErrorCode errorcode,String info,String vendorid,String vendorerrorcode){

        if (status == null) {
            status = viewModel.getChargePoint_Status().getValue();
        }

        //현재 상태가 계속 같은상태가 아니라면 보내기

        if(!(viewModel.getChargePoint_Status().getValue().equals(status)&&errorcode.equals(viewModel.getChargePoint_ErrorCode().getValue())&& info.equals(viewModel.getStatus_info().getValue())&&vendorid.equals(viewModel.getStatus_vendorid().getValue())&&vendorerrorcode.equals(viewModel.getStatus_vendorerrorcode().getValue())  )){
            viewModel.getChargePoint_Status().setValue(status);
            viewModel.getChargePoint_ErrorCode().postValue(errorcode);
            viewModel.getStatus_info().postValue(info);
            viewModel.getStatus_vendorid().postValue(vendorid);
            viewModel.getStatus_vendorerrorcode().postValue(vendorerrorcode);

            try{
                mClient.sendStatusNotification(DeviceModel.getConnectId(),status,errorcode,info,vendorid,vendorerrorcode);
            }catch (Exception e){
                Log.i(tag,"Set_Send_StatusNotification : "+e);
                Logger.i("Set_Send_StatusNotification : "+e);
                e.printStackTrace();
            }
        }


    }


    private void Start_Sampled_mv_r_Timer(int t_id){

        int Interval = Integer.parseInt(ConfigurationControl.getMeterValueSampleInterval());

        if(Interval == 0){
            return;
        }

        if(Sampled_mv_r_Timer !=null){
            return;
        }

        Sampled_mv_r_Timer = new Timer();
        TimerTask aligned_timerTask = new TimerTask() {
            @Override
            public void run() {

                Log.i("Start_Sampled_mv","interval = "+ Interval);
                Logger.i("Start_Sampled_mv interval = "+ Interval);

                isSampled = true;

                try {
                    serialClient.Serialport_SendMessage(SerialModel.RightChargerState,null);


                    ZonedDateTime zonedDateTime = returnZonedDateTime();

                    // db 저장
                    sqlite.InsertMetervalues(String.valueOf(zonedDateTime),String.format("%.3f",viewModel.getright_charge_amount().getValue()),viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(),String.valueOf(viewModel.getright_charge_persent().getValue()),viewModel.getRight_TEM().getValue(),viewModel.getRight_pay_number().getValue(),"2");

                    if(!mClient.isClosed())
                        mClient.sendMeterValues(DeviceModel.getConnectId(),t_id, String.format("%.3f",viewModel.getright_charge_amount().getValue()),true,zonedDateTime,viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(), String.valueOf(viewModel.getright_charge_persent().getValue()), viewModel.getRight_TEM().getValue());

                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("Start_Sampled_mv error : "+e);
                }


            }
        };

        Sampled_mv_r_Timer.schedule(aligned_timerTask,Interval* 1000L,Interval* 1000L);

    }

    private void Stop_Sampled_mv_r_Timer(){
        if(Sampled_mv_r_Timer != null){

            Sampled_mv_r_Timer.cancel();
            Sampled_mv_r_Timer = null;


        }
    }


    private void Start_Aligned_mv_r_Timer(int t_id){

        int Interval = Integer.parseInt(ConfigurationControl.getClockAlignedDataInterval());

        if(Interval == 0){
            return;
        }
        if(Aligned_mv_r_Timer !=null){
            return;
        }

        Aligned_mv_r_Timer = new Timer();
        TimerTask aligned_timerTask = new TimerTask() {
            @Override
            public void run() {

//                long a = System.currentTimeMillis()/1000;         //1970년 1월 1일 0시 기준이후 현재까지의 시간을 미리초로 나타냄
                String[] t = new SimpleDateFormat("HH:mm:ss").format(new Date()).split(":");
                int h = Integer.parseInt(t[0]);
                int m = Integer.parseInt(t[1]);
                int s = Integer.parseInt(t[2]);
                int a = (h * 3600) + (m * 60) + s;
//                Log.d(tag,"time = "+ h + " , "+ m + " , "+ s + " , " + a);

                if(a % Interval == 0){

                    Log.i("Start_Aligned_mv","interval = "+ Interval);
                    Logger.i("Start_Aligned_mv interval = "+ Interval);

                    isSampled = false;
                    try {

                        serialClient.Serialport_SendMessage(SerialModel.RightChargerState,null);

                        ZonedDateTime zonedDateTime = returnZonedDateTime();

                        // db 저장
                        sqlite.InsertMetervalues(String.valueOf(zonedDateTime),String.format("%.3f",viewModel.getright_charge_amount().getValue()),viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(),String.valueOf(viewModel.getright_charge_persent().getValue()),viewModel.getRight_TEM().getValue(),viewModel.getRight_pay_number().getValue(),"1");
                        if(!mClient.isClosed())
                            mClient.sendMeterValues(DeviceModel.getConnectId(),t_id, String.format("%.3f",viewModel.getright_charge_amount().getValue()),false,zonedDateTime,viewModel.getRight_EAII().getValue(),viewModel.getRight_CI().getValue(),viewModel.getRight_CO().getValue(),viewModel.getRight_PAI().getValue(),viewModel.getRight_PO().getValue(), String.valueOf(viewModel.getright_charge_persent().getValue()),viewModel.getRight_TEM().getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.e("Start_Aligned_mv error : "+e);
                    }


                }

            }
        };

        Aligned_mv_r_Timer.schedule(aligned_timerTask,1000,1000);


    }

    private void Stop_Aligned_mv_r_Timer(){
        if(Aligned_mv_r_Timer != null){

            Aligned_mv_r_Timer.cancel();
            Aligned_mv_r_Timer = null;
        }


    }

    private void Start_Center_time(){
        if(center_timer !=null){
            return;
        }

        center_timer = new Timer();
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {
                viewModel.getMain_title_time_tv().postValue(returnTime());

            }
        };

        center_timer.schedule(right_timerTask,0,30000);
    }

    private void Stop_Center_time(){
        if(center_timer != null){
            center_timer.cancel();
            center_timer = null;
        }
    }


    //시간 반환
    private String returnTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    private ZonedDateTime returnZonedDateTime(){
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    private void Start_Command_time(String msg){

        if(command_timer != null){
            return;
        }

        ShowLoadingDialog(msg);
        command_timer = new Timer();
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {
                DismissLoadingDialog();
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(() -> {
                    // 사용하고자 하는 코드
                    Toast.makeText(getApplicationContext(),"메시지에 응답이 없습니다. 기기 연결 상태를 확인해주세요.",Toast.LENGTH_LONG).show();
//                    isStarted =false;

                }, 0);
                Stop_Command_time();
            }
        };

        command_timer.schedule(right_timerTask,command_sec*1000L,command_sec*1000L);
    }


    private void Stop_Command_time(){
        DismissLoadingDialog();

        if(command_timer != null){
            command_timer.cancel();
            command_timer = null;
        }


    }


//    private void Start_TimeOut_time(){
//        page_timeout_timer = new Timer();
//        TimerTask right_timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                int page_timeout_sec_max = Integer.parseInt(ConfigurationControl.getConnectionTimeOut());
//
//                if(page_timeout_sec_max <= page_timeout_sec){
//                    runOnUiThread(() -> PageMove_ChargeSelectFgm(ChargePointStatus.Available,ChargePointErrorCode.NoError,"UserTimeOut",null,null));       // 코드가 메인 ui쓰레드에서 실행되게
//                }else{
//                    page_timeout_sec ++;
//                }
//            }
//        };
//
//        page_timeout_timer.schedule(right_timerTask,0,1000);
//    }
//
//    private void Stop_TimeOut_time(){
//        if(page_timeout_timer != null){
//            page_timeout_timer.cancel();
//            page_timeout_timer = null;
//            page_timeout_sec = 0;
//        }
//    }



    private void Start_SOC_Cal(){

        if(soc_timer !=null){
            return;
        }

        soc_timer = new Timer();
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(tag,"Start_SOC_Cal");
                if(socCnt < socCnt_max){

                    serialClient.Serialport_SendMessage(SerialModel.RightChargerSOC,null);
                }else{
                    Stop_SOC_Cal();
                }

            }
        };

        soc_timer.schedule(right_timerTask,0,socSendTime*1000L);
    }


    private void Stop_SOC_Cal(){
        Log.d(tag,"Stop_SOC_Cal");
        if(soc_timer != null){
            soc_timer.cancel();
            soc_timer = null;
        }
    }



    //오른쪽 충전 타이머
    private void Start_right_charge_time(){
        if(right_timer !=null){
            return;
        }

        right_timer = new Timer();

        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(tag,"Start_right_charge_time");
                right_sec ++;

                viewModel.setRight_charge_time(String.format("%02d:%02d:%02d", right_sec/3600,right_sec/60, right_sec%60));

                // 임시 변화 시킴
                try{
                    viewModel.setRight_charge_amount(viewModel.getright_charge_amount().getValue()+0.0019f);
                }catch (Exception e){
                    e.printStackTrace();
                    Logger.i("charge amount err :"+ e.getMessage());
                }

            }
        };

        right_timer.schedule(right_timerTask,0,1000);
    }

    private void Stop_right_charge_time(){
        Log.d(tag,"Stop_right_charge_time");
        if(right_timer != null){


            //이전 충전값 초기화
            viewModel.setRight_charge_amount(0f); // 충전량

            right_timer.cancel();
            right_timer = null;
            right_sec = 0;
        }
    }


    private void Start_right_soc_time(){

        if(right_request_soc !=null){
            return;
        }

        right_request_soc = new Timer();
        Log.d(tag,"Start_right_soc_time");
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {

                if(pre_charge_compare){

                    float pre_soc = (right_sec/(float)right_pre_sec)*max_soc;

                    Log.d("example","pre_soc = "+pre_soc+ "right_sec = "+right_sec+"max_soc = "+max_soc+" right_pre_sec = "+ right_pre_sec);

                    viewModel.getright_charge_persent().postValue((int)pre_soc);

                    if(right_sec>right_pre_sec || pre_soc>=max_soc){    //예상시간이 넘어가면 스톱

                        Log.d("example","over - right_sec : "+right_sec+ " right_pre_sec : "+right_pre_sec+" pre_soc : "+pre_soc+ " max_soc : "+ max_soc);
                        serialClient.Serialport_SendMessage(SerialModel.RightChargerSOC,null);
                        Stop_right_soc_time();
                    }
                }

            }
        };

        right_request_soc.schedule(right_timerTask,0,1000);
    }

    private void Stop_right_soc_time(){
        Log.d(tag,"Stop_right_soc_time");
        if(right_request_soc != null){

            right_request_soc.cancel();
            right_request_soc = null;
        }
    }



    private void ResetFunction(ResetType resetType){

        try {
            if(resetType !=null){

                if(Boolean.TRUE.equals(viewModel.getright_device_charging().getValue())){           // 만약 충전중이라면

                    if(resetType.equals(ResetType.Soft)){                                                   // 리셋타입 소프트
                        serialClient.Serialport_SendMessage(SerialModel.RightChargerStop,Reason.SoftReset);
                    }else{                                                                                  // 리셋타입 하드
                        serialClient.Serialport_SendMessage(SerialModel.RightChargerStop,Reason.HardReset);
                    }
                }else{
                    if (resetType.equals(ResetType.Soft)) {                                                   // 리셋타입 소프트
                        // 어플만 재시작
                        SoftRestart();
                    } else {                                                                                  // 리셋타입 하드
                        //하드웨어 재시작
                        serialClient.Serialport_SendMessage(SerialModel.RightChargerReset,null);
                        HardRestart();

                    }
                }









            }else{              // 어플 업데이트 시
                if(Boolean.TRUE.equals(viewModel.getright_device_charging().getValue())){           // 만약 충전중이라면

                    serialClient.Serialport_SendMessage(SerialModel.RightChargerStop,Reason.Reboot);
                }
            }
        }catch (Exception e){
            Log.i(tag, String.valueOf(e));
            Logger.i( String.valueOf(e));
            if(ResetRetries > 0){
                ResetRetries --;
                ResetFunction(resetType);
            }
        }




    }


    private void HardRestart() {
        new Handler().postDelayed(() -> {
            // 기기 재부팅
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            pm.reboot(null);
        },2000);
    }

    private void SoftRestart(){
        PackageManager packageManager = getPackageManager();
        Intent intent3 = packageManager.getLaunchIntentForPackage(getPackageName());
        ComponentName componentName = intent3.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
        System.exit(0);
    }



    private void DismissLoadingDialog(){
        if(loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    private void ShowLoadingDialog(String msg){
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }

        loadingDialog.setText(msg);

    }

    private void ShowErrorDialog(String title,String msg){
        errorDialog.showDialog(title,msg);
    }

//    private int linearRegression(ArrayList<Integer> sec, ArrayList<Integer> soc, int targetPersent){
//        int sumX = 0,sumY = 0,sumXY = 0,sumX2 = 0;
//
//        int size = sec.size();
//
//        int x = 0;
//        int y = 0;
//
//        for (int i = 0; i< size; i++){
//            sumX += sec.get(i);
//            sumY += soc.get(i);
//            sumXY += sec.get(i) * soc.get(i);
//            sumX2 += sec.get(i) * sec.get(i);
//        }
//
////        Log.d("example","size = "+size );
////        Log.d("example","sumX = "+sumX );
////        Log.d("example","sumY = "+sumY );
////        Log.d("example","sumXY = "+sumXY );
////        Log.d("example","sumX2 = "+sumX2 );
//
//        x = (size*sumXY - sumX*sumY) / (size*sumX2 - sumX*sumX);
//        y = (sumY - x*sumX) / size;
//
//
//        return (targetPersent -y)/x;
//    }


    private int linearRegression(ArrayList<Integer> sec, ArrayList<Integer> soc, int targetPersent){
        int sumX = 0,sumY = 0,sumZ = 0;

        float size = sec.size();

        Log.d("example","size = "+size);

        float x = 0;
        float y = 0;


        for (int i = 0; i< size; i++){
            sumX += sec.get(i);
            sumY += soc.get(i);
        }

//        Log.d("example","size = "+size );
//        Log.d("example","sumX = "+sumX );
//        Log.d("example","sumY = "+sumY );
//        Log.d("example","sumXY = "+sumXY );
//        Log.d("example","sumX2 = "+sumX2 );

        x = sumX/size;
        y = sumY/size;



        return (int) ((int) (targetPersent*x)/y);
    }


    private void Start_Center_Set_Weather_timer(){
        if(center_data_Retrofit_timer !=null){
            return;
        }

        center_data_Retrofit_timer = new Timer();
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat time = new SimpleDateFormat("HH00");

                Set_Weather_Meteorological(day.format(date),time.format(date));  // 날씨 데이터 불러오기
//                Log.d(tag,"wheather : "+ day.format(date)+ " , "+ time.format(date));
//                Set_Node_Weather();
            }
        };

//        center_data_Retrofit_timer.schedule(right_timerTask,0,1000);
        center_data_Retrofit_timer.schedule(right_timerTask,3000,Meteorological_time*1000L);
//        center_data_Retrofit_timer.schedule(right_timerTask,0,5000);
    }

    private void Stop_Center_Set_Weather_timer(){
        if(center_data_Retrofit_timer != null){
            center_data_Retrofit_timer.cancel();
            center_data_Retrofit_timer = null;
        }
    }


    //기상청 api
    private void Set_Weather_Meteorological( String base_date, String base_time){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            Log.d(tag,"id : "+ Build.getSerial());
        }


        m_retrofitService.GetWhether(getResources().getString(R.string.Weather_key),base_date,base_time,KeyControl.getNx(),KeyControl.getNy(),1,10,"JSON").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
//                Log.d("api", (String) response.body());
                try {
                    JSONObject json = new JSONObject(String.valueOf(response.body()));
                    JSONObject json1 = new JSONObject(String.valueOf(json.get("response")));
                    JSONObject json2 = new JSONObject(String.valueOf(json1.get("body")));
                    JSONObject json3 = new JSONObject(String.valueOf(json2.get("items")));
                    JSONArray jsonArray = new JSONArray(String.valueOf(json3.get("item")));
                    for(int i = 0; i< jsonArray.length(); i++){

                        String Category = String.valueOf(jsonArray.getJSONObject(i).get("category"));
                        String ObsValue = String.valueOf(jsonArray.getJSONObject(i).get("obsrValue"));

                        if(Category.equals("REH")){ //습도
                            viewModel.getMain_title_humidity_tv().setValue(ObsValue);
                        }else if(Category.equals("RN1")){ //1시간 강수량
                            viewModel.getMain_title_precipitation_tv().setValue(ObsValue);
                        }else if(Category.equals("T1H")){ //기온
                            viewModel.getMain_title_temp_tv().setValue(ObsValue);
                        }else if(Category.equals("VEC")){ //풍향
                            viewModel.getMain_title_wd_tv().setValue(ObsValue);
                        }else if(Category.equals("WSD")){//풍속
                            viewModel.getMain_title_ws_tv().setValue(ObsValue);
                        }

                    }

                    Weather_retry_cnt = Weather_retry_cnt_origin;

                } catch (JSONException e) {
                    e.printStackTrace();


                    if(viewModel.getMain_title_humidity_tv().getValue().equals("측정중")){
                        viewModel.getMain_title_humidity_tv().setValue("Error");
                        viewModel.getMain_title_precipitation_tv().setValue("Error");
                        viewModel.getMain_title_temp_tv().setValue("Error");
                        viewModel.getMain_title_wd_tv().setValue("Error");
                        viewModel.getMain_title_ws_tv().setValue("Error");
                    }


                    if(Weather_retry_cnt>0){
                        Weather_retry_cnt = Weather_retry_cnt -1;
                        try {
                            Thread.sleep(Weather_retry_interval*1000L);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        long now = System.currentTimeMillis();
                        Date date;
                        SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
                        SimpleDateFormat time = new SimpleDateFormat("HH00");

                        if(Weather_retry_cnt ==2){  // 실패시 한시간, 두시간, 세시간 전의 데이터를 가져옴
                            date  = new Date(now - 3600* 1000);
                        }else if(Weather_retry_cnt ==1){
                            date  = new Date(now - 7200* 1000);
                        }else{
                            date  = new Date(now - 10800* 1000);
                        }

//                    Toast.makeText(getApplicationContext(),"날씨 서버가 불안정합니다. "+Meteorological_time+"초 후 다시 불러오기를 시도합니다.",Toast.LENGTH_LONG).show();

                        Set_Weather_Meteorological(day.format(date),time.format(date));  // 날씨 데이터 불러오기
                    }else{
                        Weather_retry_cnt = Weather_retry_cnt_origin;
                    }

//
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("api throwable", String.valueOf(t));

                if(viewModel.getMain_title_humidity_tv().getValue().equals("측정중")) {
                    viewModel.getMain_title_humidity_tv().setValue("Error");
                    viewModel.getMain_title_precipitation_tv().setValue("Error");
                    viewModel.getMain_title_temp_tv().setValue("Error");
                    viewModel.getMain_title_wd_tv().setValue("Error");
                    viewModel.getMain_title_ws_tv().setValue("Error");
                }

                Toast.makeText(getApplicationContext(),"네트워크 상태가 불안정합니다. "+Meteorological_time+"초 후 다시 불러오기를 시도합니다.",Toast.LENGTH_LONG).show();
            }
        });
    };


    private void Start_update_Timer(ZonedDateTime retrieveTime,String Location){
        if(update_Timer !=null){
            return;
        }

        update_Timer = new Timer();
        TimerTask aligned_timerTask = new TimerTask() {
            @Override
            public void run() {

                ZonedDateTime zonedDateTime = returnZonedDateTime();

                Duration a = Duration.between(zonedDateTime,retrieveTime);

                Log.i("Start_update_Timer","zonedDateTime = "+ zonedDateTime);
                Log.i("Start_update_Timer","duration = "+ a.getSeconds());
                if(a.getSeconds() <0){

                    Stop_update_Timer();
//                    apk_update(Location,downloadedfilename);


                    apk_update(Location);

                }


            }
        };

        update_Timer.schedule(aligned_timerTask,0,1000L);


    }

    private void Stop_update_Timer(){
        if(update_Timer != null){

            update_Timer.cancel();
            update_Timer = null;
        }

        UpdateFirmwareRetries = 0;
        UpdateFirmwareRetriesInterval = 0;
    }



    //    public void apk_update(String path,String downloadedfilename){
    public void apk_update(String path){
        // 해당 경로에 있는 파일 리스트
        File file_path = new File(PrefsHelper.apk_path);

        //폴더 없으면 생성
        if(!file_path.exists()){
            file_path.mkdirs();
        }


//        Intent intent = new Intent(ReceiverModel.Receive_Reset);         // 혹시나 충전 중이라면 멈추기
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


//        down_Apk_file("new_apk.zip",path,downloadedfilename);

        down_Apk_file(path);

    }


    //    private void down_Apk_file(String fileName,String path,String downloadedfilename){
    private void down_Apk_file(String path){
        Log.i(tag,"Path = "+path);

//        HashMap<String,String> input = new HashMap<>();
//        input.put("device_id", DeviceModel.getDeviceid());
//        input.put("filename",downloadedfilename);

        File file_path = new File(PrefsHelper.apk_path);
        List<String> f_list = Arrays.asList(file_path.list());

        for(String a : f_list){
            Log.i("apk-a",a);
        }

//        Retrofit_Interface s_retrofitService = Retrofit_Factory.create();
//        s_retrofitService.downApk(path,input).enqueue(new Callback<ResponseBody>() {
        s_retrofitService.downApk(path).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.i(tag,"response : "+!response.isSuccessful());
                try {
                    if(!response.isSuccessful()){
                        mClient.sendFirmwareStatusNotification(FirmwareStatus.DownloadFailed);
                    }else{
                        mClient.sendFirmwareStatusNotification(FirmwareStatus.Downloading);
                    }
                } catch (Exception e) {
                    Logger.e("down_Apk_file",e);
                    e.printStackTrace();
                }

                // 압축파일 다운
//                if(Filedownload.writeResponseBodyToDisk(response.body(),PrefsHelper.apk_path + File.separator+ fileName)){
                if(Filedownload.writeResponseBodyToDisk(response.body(),PrefsHelper.apk_path+ File.separator+PrefsHelper.downloaded_apk_name)){
                    Log.d("zip down", "if ");
                    Log.d(tag,"down location : "+ PrefsHelper.apk_path+ File.separator+PrefsHelper.downloaded_apk_name);
//                    // 압축 풀기
//                    if(Filedownload.unzip(PrefsHelper.apk_path + File.separator+ fileName,PrefsHelper.apk_path)){
                    try {

                        mClient.sendFirmwareStatusNotification(FirmwareStatus.Downloaded);


                        _installPackage(new FileInputStream(new File(PrefsHelper.apk_path + File.separator + PrefsHelper.downloaded_apk_name).getPath()));

//                        apkInstall(new File(PrefsHelper.apk_path + File.separator + PrefsHelper.downloaded_apk_name));

                    } catch (Exception e) {
                        Logger.e("down_Apk_file",e);
                        e.printStackTrace();
                    }
//                    }
                }else{
                    try {

                        mClient.sendFirmwareStatusNotification(FirmwareStatus.DownloadFailed);

                    } catch (Exception e) {
                        Logger.e("down_Apk_file",e);
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("download apk fail",t.getMessage());
                Logger.e("down_Apk_file",t.getMessage());
                try {
                    mClient.sendFirmwareStatusNotification(FirmwareStatus.DownloadFailed);
                } catch (Exception e) {
                    Logger.e("down_Apk_file",e);
                    e.printStackTrace();
                }

                if(UpdateFirmwareRetries != 0){

                    try {
                        Thread.sleep(UpdateFirmwareRetriesInterval);
                    } catch (InterruptedException e) {
                        Logger.e("down_Apk_file",e);
                        e.printStackTrace();
                    }

//                    down_Apk_file(fileName,path,downloadedfilename);
                    down_Apk_file(path);
                    UpdateFirmwareRetries --;
                }


            }
        });

    }


//    // 팝업창이 떠버림. 설치해달라고.
//    public void apkInstall(File apkfile){
////        Uri apkUri = Uri.fromFile(apkfile);
//
//        Uri apkUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName()+".fileprovider",apkfile);
//        try {
//            Intent packageinstaller = new Intent(Intent.ACTION_VIEW);
//            packageinstaller.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            packageinstaller.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            packageinstaller.setDataAndType( apkUri, "application/vnd.android.package-archive");
//            startActivity(packageinstaller);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void _installPackage(InputStream inputStream) throws IOException {
        Log.d(tag,"_installPackage");
        try{
            mClient.sendFirmwareStatusNotification(FirmwareStatus.Installing);
        }catch (Exception e){
            e.printStackTrace();
        }

        PackageInstaller packageInstaller = getPackageManager().getPackageInstaller();
        int sessionId = packageInstaller.createSession(new PackageInstaller
                .SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL));
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);

        try {
            long sizeBytes = 0;

            OutputStream out = null;
            out = session.openWrite("my_app_session", 0, sizeBytes);
            int total = 0;
            byte[] buffer = new byte[65536];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
                total += c;
                out.write(buffer, 0, c);
            }
            session.fsync(out);
            inputStream.close();
            out.close();

            try{
                mClient.sendFirmwareStatusNotification(FirmwareStatus.Installed);
            }catch (Exception e){
                e.printStackTrace();
            }


            session.commit(createIntentSender(sessionId));
            session.close();

        }catch (Exception e){
            e.printStackTrace();
            try{
                mClient.sendFirmwareStatusNotification(FirmwareStatus.InstallationFailed);


                if(UpdateFirmwareRetries != 0){

                    try {
                        Thread.sleep(UpdateFirmwareRetriesInterval);
                    } catch (InterruptedException ee) {
                        ee.printStackTrace();
                    }

                    _installPackage(inputStream);
                    UpdateFirmwareRetries --;
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }


//        Intent i = new Intent(LauncherReceiver.START_INTENT);
//        mcontext.sendBroadcast(i);

    }

    private IntentSender createIntentSender(int sessionId) {
        Log.d(tag,"createIntentSender");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                sessionId,
//                new Intent(LauncherReceiver.START_INTENT),
                new Intent(Intent.ACTION_MY_PACKAGE_REPLACED),
                PendingIntent.FLAG_IMMUTABLE);
//                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent.getIntentSender();

//        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.example.kiosk_electric_charger");
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        return pendingIntent.getIntentSender();
    }

    private void getAppVersion(){
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            Log.d("버전", versionName);
            Logger.i(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(tag, String.valueOf(e));
            Logger.e( String.valueOf(e));
            e.printStackTrace();
        }
    }

    //초기화
    private void allStop(){
        Stop_Sampled_mv_r_Timer();
        Stop_Aligned_mv_r_Timer();
        right_soc_array.clear();
        right_sec_array.clear();
        Stop_SOC_Cal();
        Stop_right_charge_time();
        Stop_right_soc_time();
        pre_charge_compare = false;
        right_pre_sec = 0;
        origin_soc = 0;
        socCnt = 0;

    }

    private void SerialRightChargerStopCommandFunction(String pay_num, Reason reason){
        Log.d(tag,"SerialRightChargerStopCommand");


        if(reason.equals(Reason.Reboot)||reason.equals(Reason.HardReset)||reason.equals(Reason.SoftReset)){                           // reboot,hardReset,SoftReset일때는 앱이 꺼질때 중단 안보내야함
            ResetType_b = false;
        }


        if(Boolean.TRUE.equals(viewModel.getright_device_charging().getValue())){           // 충전중이라면
            Log.i("Exception check ", pay_num +" "+viewModel.getRight_pay_number().getValue());

            if(pay_num.equals(viewModel.getRight_pay_number().getValue())||reason.equals(Reason.Local)){    // 멈추는게 remote여서 pay_num이 맞거나, 기기 자체에서 멈추는 local일경우
                serialClient.Serialport_SendMessage(SerialModel.RightChargerStop,reason);

            }

        }
    }



}
