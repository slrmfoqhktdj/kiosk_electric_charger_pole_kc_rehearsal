package com.example.kiosk_electric_charger_pole_kc_rehearsal;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.KeyControl;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.PrefsHelper;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.model.KeyModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.filemanage.CustomFormatStrategy;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.filemanage.DiskLogStrategy;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.DeviceModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.ReceiverModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.TransNXY;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.JsonClient;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback.SplashClientInterface;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.Objects;

import eu.chargetime.ocpp.model.core.ResetType;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    VideoView svv;

    boolean video_time_com = false;

    //영상길이
    long video_time = 7000;

    String tag = "SplashActivity";


    TextView boot_tv;

    boolean bootnotification_b = false; // 부트노티 승인

    JsonClient mClient;

    Handler boot_handler ;  // bootNotification - r
    Thread boot_thread;     // bootNotification - r

    // 부팅
    private final BroadcastReceiver bootNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (Objects.requireNonNull(intent.getAction())){
                case ReceiverModel.BootNotification:
                    boot_tv.setText("서버의 응답을 받았습니다.");

                    int defaultValue = 60;  // 초

                    int interval = intent.getIntExtra("interval",defaultValue);
                    int bootState = intent.getIntExtra("status",2);

                    if(interval == 0){
                        interval = defaultValue;
                    }

//            PrefsHelper.write(KeyModel.getBootNotificationState(),bootState);       // 부팅 반환 상태 저장

                    switch (bootState){
                        case 1:                        // 정상

                            bootnotification_b = true;
                            boot_tv.setText("프로그램을 시작합니다.");
                            Log.d("check","mClient is null? : "+mClient);

//                            if(mClient == null){
//                                mClient = JsonClient.getInstance(context);
//                            }
                            if(mClient != null){
                                mClient.setReconnectAbled(true);        // 연결이 끊기면 재연결 가능하게 세팅
                            }


                            if(video_time_com){ // 기본 시간이 완료 되었다면   (다른 상태에서 정상으로 변환되었을때를 위해)
                                nextActivity();
                            }

                            break;
                        case 2:                        // 대기    - 서버에서 트리거 보내줄때까지는 연결만 유지한체 대기


                            bootnotification_b = false;
                            mClient.setReconnectAbled(true);        // 연결이 끊기면 재연결 가능하게 세팅
                            boot_tv.setText("현재 점검중인 기기입니다.");
                            break;
                        default:                        // 거부

                            bootnotification_b = false;
                            mClient.setReconnectAbled(false);        // 연결이 끊기면 재연결 가능하지 않게
                            boot_tv.setText("서버에서 거부된 기기입니다.\n허락이 될 때까지 대기합니다.");
                            mClient.disconnect();       // 다음 보낼때까지 서버와 연결 끊기
                            mClient = null;

                            boot_thread(interval* 1000L);
                            break;
                    }



                    Log.d(tag,"boot notification - "+interval + " bootstat : "+ bootState + " boot bool : "+ bootnotification_b );
                    Logger.i("boot notification - "+interval + " bootstat : "+ bootState + " boot bool : "+ bootnotification_b );

                    break;


                case ReceiverModel.Receive_Reset :

                    ResetType resetType = (ResetType) intent.getSerializableExtra("resetType");

                    if(resetType !=null){

                        if(resetType.equals(ResetType.Soft)){                                                   // 리셋타입 소프트
                            // 어플만 재시작
                            SoftRestart();
                        }else{                                                                                  // 리셋타입 하드
                            //하드웨어 재시작
                            HardRestart();
                        }




                    }

                    break;

                case ReceiverModel.BootNotification_Trigger:          // trigger Message
                    boot_thread(0);
                    break;
                default:
                    break;

            }



//            mClient.disconnect();

//            sendFileList();

        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_splash);


        LocalBroadcastManager.getInstance(this).unregisterReceiver(bootNotificationReceiver);

        //SharedPreferences 등록
        PrefsHelper.init(this);

        //ocpp bootnotification 결과 알림
        IntentFilter sfilter01 = new IntentFilter();
        sfilter01.addAction(ReceiverModel.BootNotification);
        sfilter01.addAction(ReceiverModel.Receive_Reset);
        sfilter01.addAction(ReceiverModel.BootNotification_Trigger);
        LocalBroadcastManager.getInstance(this).registerReceiver(bootNotificationReceiver,sfilter01);

        svv = findViewById(R.id.splash_videoview);
        boot_tv = findViewById(R.id.boot_msg_tv);

        boot_handler = new Handler(Looper.getMainLooper());

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.igen2);

        // 로그파일 등록
        FormatStrategy formatStrategy = CustomFormatStrategy.newBuilder()
//                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
//                .methodCount(0)         // (Optional) How many method line to show. Default 2
//                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .logStrategy(new DiskLogStrategy())
                .tag("MyCustomTag")     // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

        //기기 세팅값 설정
        DeviceSettings();


        // 인트로 영상 길이 받아오기

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        video_time = Long.parseLong(time );

        try {
            retriever.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        Log.d(tag, "video time = "+ video_time);




        svv.setVideoURI(uri);


        svv.setOnPreparedListener(mp -> {
            mp.start();


            // 연결 및 부팅 메시지 보내기
            boot_thread(1000);



            new Handler().postDelayed(() -> {
                Log.d(tag, "start handler");
//                mp.stop();
                video_time_com = true;

                nextActivity();

            },video_time);


        });





//        // 관리자 권한 부여 --- 안되는것같기도하고
//        DevicePolicyManager dpm =
//                (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
//
//        ComponentName componentName = new ComponentName(this, KioskDeviceAdminReceiver.class);
//
//         if(!dpm.isAdminActive(componentName)){
//             Log.d(tag,"admin No");
//             enableAdmin(componentName);
//         }
//
// //        // 소유자 관리자권한 제거
//         devicePolicyManager.removeActiveAdmin(componentName);
//
//        // 소유자 권한 제거
//        dpm.clearDeviceOwnerApp(this.getPackageName());




    }

    //기기 세팅값 설정
    private void DeviceSettings(){
        if(KeyControl.getDeviceId().equals("")){
            KeyControl.setDeviceId(DeviceModel.getDeviceid());
        }

        if(KeyControl.getDevicemodel().equals("")){
            KeyControl.setDevicemodel(DeviceModel.getDevicemodel());
        }

        if(KeyControl.getDevicevendor().equals("")){
            KeyControl.setDevicevendor(DeviceModel.getDevicevendor());
        }

        if(KeyControl.getPAYvendor().equals("")){
            KeyControl.setPAYvendor(DeviceModel.getPAYvendor());
        }

        if(KeyControl.getWHEATHERvender().equals("")){
            KeyControl.setWHEATHERvender(DeviceModel.getWHEATHERvender());
        }

        if(KeyControl.getNx().equals("")){
            KeyControl.setNx(DeviceModel.getNx());
        }

        if(KeyControl.getNy().equals("")){
            KeyControl.setNy(DeviceModel.getNy());
        }

    }

    // 연결
    private void mClientInit(){

        try {
            mClient = JsonClient.getInstance(getApplicationContext());
//            mClient.connect();
            mClient.InitClient();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        svv.pause();
        svv.stopPlayback();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(bootNotificationReceiver);    // 부팅

//        mClient.disconnect();           // ws 연결 끊고
        Stop_boot_thread();     // 부팅 쓰레드 삭제종료
        mClient = null;
    }

    // 연결



    private void nextActivity(){
        Log.d(tag, "go to CenterActivity");
        if(bootnotification_b){
            if(video_time_com){ // 로딩 동영상 기본 시간 완료
                Intent intent = new Intent(SplashActivity.this, CenterActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }


    public void boot_thread(long interval){

        Log.d(tag,"client is booted : "+ !(mClient==null));


        mClientInit();


        boot_thread = new Thread(() -> boot_handler.postDelayed(()->{

            try {

//                mClient.setSplashClientInterfaceListener(new SplashClientInterface() {
//
//                    @Override
//                    public void onBootNotification(int interval, int bootState) {
//
//                        Log.d(tag,"boot notification - "+interval + " bootstat : "+ bootState + " boot bool : "+ bootnotification_b );
//                        Logger.i("boot notification - "+interval + " bootstat : "+ bootState + " boot bool : "+ bootnotification_b );
//
//                        new Handler(Looper.getMainLooper()).post(() -> boot_tv.setText("서버의 응답을 받았습니다."));
//
//                        int defaultValue = 60;  // 초
//
//                        if(interval == 0){
//                            interval = defaultValue;
//                        }
//
//                        switch (bootState){
//                            case 1:                        // 정상
//                                Log.d(tag,"정상 "+ bootnotification_b );
//                                bootnotification_b = true;
//
//                                new Handler(Looper.getMainLooper()).post(() -> boot_tv.setText("프로그램을 시작합니다."));
//
//                                mClient.setReconnectAbled(true);        // 연결이 끊기면 재연결 가능하게 세팅
//                                if(video_time_com){ // 기본 시간이 완료 되었다면   (다른 상태에서 정상으로 변환되었을때를 위해)
//                                    Log.d(tag,"정상 "+ video_time_com );
//                                    nextActivity();
//                                }
//                                break;
//                            case 2:                        // 대기    - 서버에서 트리거 보내줄때까지는 연결만 유지한체 대기
//                                bootnotification_b = false;
//                                mClient.setReconnectAbled(true);        // 연결이 끊기면 재연결 가능하게 세팅
//
//                                new Handler(Looper.getMainLooper()).post(() -> boot_tv.setText("현재 점검중인 기기입니다."));
//
//                                break;
//                            default:                        // 거부
//                                bootnotification_b = false;
//                                mClient.setReconnectAbled(false);        // 연결이 끊기면 재연결 가능하지 않게
//
//                                new Handler(Looper.getMainLooper()).post(() -> boot_tv.setText("서버에서 거부된 기기입니다.\n허락이 될 때까지 대기합니다."));
//
//                                mClient.disconnect();       // 다음 보낼때까지 서버와 연결 끊기
//                                mClient = null;
//
//                                boot_thread(interval* 1000L);
//                                break;
//                        }
//
//                    }
//
//                    @Override
//                    public void onReset(ResetType resetType) {
//                        if(resetType !=null){
//
//                            if(resetType.equals(ResetType.Soft)){                                                   // 리셋타입 소프트
//                                // 어플만 재시작
//                                SoftRestart();
//                            }else{                                                                                  // 리셋타입 하드
//                                //하드웨어 재시작
//                                HardRestart();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onBootNotification_Trigger() {
//                        boot_thread(0);
//                    }
//                });


                // 연결
                mClient.sendBootNotification();

            } catch (Exception e) {
                e.printStackTrace();
            }
        },interval));

        boot_thread.start();
    }


    public void Stop_boot_thread() {
        if (boot_thread != null){
            boot_handler.removeCallbacksAndMessages(null);
            boot_thread.interrupt();
            boot_thread = null;
        }
    }

    private void HardRestart(){
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



}
