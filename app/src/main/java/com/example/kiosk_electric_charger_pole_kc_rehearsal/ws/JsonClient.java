package com.example.kiosk_electric_charger_pole_kc_rehearsal.ws;

import static eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType.BootNotification;
import static eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType.DiagnosticsStatusNotification;
import static eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType.FirmwareStatusNotification;
import static eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType.Heartbeat;
import static eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType.MeterValues;
import static eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType.StatusNotification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.ConfigurationControl;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.KeyControl;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.PrefsHelper;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.filemanage.Filedownload;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.filemanage.LogFile;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.ConfigurationModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.DataTransferModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.DeviceModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.MeterValueMeasurandModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.PageAddrModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.ReceiverModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.Retrofit_Factory;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.Retrofit_Interface;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.TransNXY;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.SQLiteControl;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.SQLiteHelper;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.model.NotificationFactoryModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback.CenterClientInterface;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback.SplashClientInterface;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import eu.chargetime.ocpp.ClientEvents;
import eu.chargetime.ocpp.IClientAPI;
import eu.chargetime.ocpp.feature.profile.ClientCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ClientCoreProfile;
import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementEventHandler;
import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementProfile;
import eu.chargetime.ocpp.feature.profile.ClientRemoteTriggerProfile;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AvailabilityStatus;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityConfirmation;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.core.ClearCacheConfirmation;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.ClearCacheStatus;
import eu.chargetime.ocpp.model.core.ConfigurationStatus;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.DataTransferStatus;
import eu.chargetime.ocpp.model.core.GetConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.KeyValueType;
import eu.chargetime.ocpp.model.core.MeterValue;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.Reason;
import eu.chargetime.ocpp.model.core.RegistrationStatus;
import eu.chargetime.ocpp.model.core.RemoteStartStopStatus;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import eu.chargetime.ocpp.model.core.ResetConfirmation;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetStatus;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.core.SampledValue;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import eu.chargetime.ocpp.model.core.UnlockConnectorConfirmation;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.core.UnlockStatus;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import eu.chargetime.ocpp.model.firmware.FirmwareStatus;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageStatus;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * ChargeTime.eu - Java-OCA-OCPP
 * Copyright (C) 2015-2016 Thomas Volden <tv@chargetime.eu>
 *
 * MIT License
 *
 * Copyright (C) 2016-2018 Thomas Volden
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class JsonClient {
    private IClientAPI client;
    private ClientCoreProfile core;

    private ClientFirmwareManagementProfile firmware;

    private ClientRemoteTriggerProfile trigger;

    private String tag = "JsonClient.java";

    private static JsonClient jsonClient = null;

    private Context mcontext;

    private int TransactionStartMessageAttempts = 0;
    private int TransactionStopMessageAttempts = 0;

    
    private boolean reconnectAbled = true; // 서버에서 연결이 거부되는 기기 였을경우 서버와 연결을 끊어야하는데 자동으로 끊어지면 연결해버리기에 만든것

    private SQLiteHelper helper;
    private SQLiteControl sqlite;

    private Thread triggerThread;   //trigger Message 쓰레드
    private Handler triggerHandler; //trigger Message 핸들러

//    private Thread DiagnosticsThread;   //Diagnostics Message 쓰레드
//    private Handler DiagnosticsHandler; //Diagnostics Message 핸들러

    private Handler hb_handler; // heartbeat
    private Thread hb_thread; // heartbeat

    int DiagnosticsRetries = 0;     // 재시도 횟수
    long DiagnosticsRetriesInterval = 0;    //재시도 간격
    
    private Timer update_Timer;  //업데이트 타이머

    int UpdateFirmwareRetries = 0;     // 재시도 횟수
    long UpdateFirmwareRetriesInterval = 0;    //재시도 간격

    private Retrofit_Interface s_retrofitService = Retrofit_Factory.create();

//    private final ArrayList<SplashClientInterface> s_callbacksList = new ArrayList<>();
//
//    private final ArrayList<CenterClientInterface> c_callbacksList = new ArrayList<>();

    private JsonClient(Context context){
        mcontext = context;
        helper = SQLiteHelper.getInstance(context);
        sqlite = SQLiteControl.getInstance(helper);

    }

//    private void addCore(ClientCoreProfile core){
//        this.core = core;
//    }



//    public static JsonClient getInstance(Context context, ClientCoreProfile core){
    public static JsonClient getInstance(Context context){


        if(jsonClient == null){
            jsonClient = new JsonClient(context);
//            jsonClient.addCore(core);
        }

        return jsonClient;
    }

//    public JsonClient setSplashClientInterfaceListener(SplashClientInterface callback){
//        if (!s_callbacksList.contains(callback)) {
//            s_callbacksList.add(callback);
//        }
//        return this;
//    }
//
//    public JsonClient setCenterClientInterfaceListener(CenterClientInterface callback){
//        if (!c_callbacksList.contains(callback)) {
//            c_callbacksList.add(callback);
//        }
//        return this;
//    }


    public void InitClient(){
        hb_handler = new Handler(Looper.getMainLooper());

        trigger = new ClientRemoteTriggerProfile(triggerMessageRequest -> {
            if(triggerHandler ==null){
                triggerHandler = new Handler(Looper.getMainLooper());
            }
//            if(DiagnosticsHandler ==null){
//                DiagnosticsHandler = new Handler(Looper.getMainLooper());
//            }


            switch (triggerMessageRequest.getRequestedMessage()){
                case BootNotification:
                    Start_trigger_thread(BootNotification);
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                case DiagnosticsStatusNotification:
                    Start_trigger_thread(DiagnosticsStatusNotification);
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                case FirmwareStatusNotification:
                    Start_trigger_thread(FirmwareStatusNotification);
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                case Heartbeat:
                    Start_trigger_thread(Heartbeat);
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                case MeterValues:
                    if(Boolean.TRUE.equals(KeyControl.getright_device_charging())){
                        Start_trigger_thread(MeterValues);
                        return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                    }else{
                        return new TriggerMessageConfirmation(TriggerMessageStatus.Rejected);
                    }
                case StatusNotification:
                    Start_trigger_thread(StatusNotification);
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                default:
                    return new TriggerMessageConfirmation(TriggerMessageStatus.NotImplemented);
            }
        });


        firmware = new ClientFirmwareManagementProfile(new ClientFirmwareManagementEventHandler() {
            @Override
            public GetDiagnosticsConfirmation handleGetDiagnosticsRequest(GetDiagnosticsRequest request) {

                Log.i(tag,"handleGetDiagnosticsRequest : "+request);
                Logger.i("handleGetDiagnosticsRequest");
//                Start_Diagnostics_thread(request);

                if(request.getRetries() != null){
                    DiagnosticsRetries = request.getRetries();
                }else{
                    DiagnosticsRetries = 0;
                }

                if(request.getRetryInterval() != null){
                    DiagnosticsRetriesInterval = request.getRetryInterval()*1000;
                }else{
                    DiagnosticsRetriesInterval = 0;
                }


                try {
                    if(create_logfile(request.getLocation(),request.getStartTime(),request.getStopTime())){
                        GetDiagnosticsConfirmation a = new GetDiagnosticsConfirmation();
                        a.setFileName(KeyControl.getDeviceId()+".txt");
//                        a.setFileName(DeviceModel.getDeviceid()+"_"+request.getStartTime()+"_"+ request.getStopTime()+".txt");

                        return a;
                    }else{
                        return new GetDiagnosticsConfirmation();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e( String.valueOf(e));
                    return new GetDiagnosticsConfirmation();
                }

            }

            @Override
            public UpdateFirmwareConfirmation handleUpdateFirmwareRequest(UpdateFirmwareRequest request) {

                Log.i(tag,"handleUpdateFirmwareRequest : "+request);
                Logger.i("handleUpdateFirmwareRequest");


                // 앱 버전 가져오기
                String versionName = "";
                PackageManager packageManager = mcontext.getPackageManager();
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(mcontext.getPackageName(), 0);
                    versionName = packageInfo.versionName;
                    Log.i(tag,versionName);
                    Logger.i(versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(tag, String.valueOf(e));
                    Logger.e( String.valueOf(e));
                    e.printStackTrace();;
                }




                try{
                    String[] a = request.getLocation().split("\\$\\$");
                    Log.i(tag,"location : "+a[0]);
                    Log.i(tag,"location : "+a[1]);
//                Log.d(tag,a[2]);

//                    Stop_update_Timer();

                    if(request.getRetries() != null){
                        UpdateFirmwareRetries = request.getRetries();
                    }

                    if(request.getRetryInterval() != null){
                        UpdateFirmwareRetriesInterval = request.getRetryInterval()*1000;
                    }

                    // 앱 다운로드 프로세스
                    // 버전이 다르면
//                if(!(versionName.equals(a[1]))){
                    if(!(versionName.equals(a[0]))){


                        Intent intent = new Intent(ReceiverModel.FirmwareUpdate);
                        intent.putExtra("UpdateFirmwareRetries",UpdateFirmwareRetries);
                        intent.putExtra("UpdateFirmwareRetriesInterval",UpdateFirmwareRetriesInterval);
                        intent.putExtra("UpdateFirmwareRetrieveDate",request.getRetrieveDate());
                        intent.putExtra("UpdateFirmwareLocation",a[1]);
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);



//                    Start_update_Timer(request.getRetrieveDate(),a[2],a[0]+"_"+a[1]);
                    }
                }catch (Exception e){
                    Log.e(tag, String.valueOf(e));
                    Logger.e( String.valueOf(e));
                    return new UpdateFirmwareConfirmation();
                }





                return new UpdateFirmwareConfirmation();
            }
        });

        // The core profile is mandatory
        core = new ClientCoreProfile(new ClientCoreEventHandler() {

            @Override
            public ClearCacheConfirmation handleClearCacheRequest(ClearCacheRequest request) {

                Log.i(tag,"handleClearCacheRequest : "+request);
                // ... handle event
                Logger.i("handleClearCacheRequest");

                KeyControl.clearAuthorizationCache();

                return new ClearCacheConfirmation(ClearCacheStatus.Accepted); // returning null means unsupported feature
            }


            @Override
            public ChangeAvailabilityConfirmation handleChangeAvailabilityRequest(ChangeAvailabilityRequest request) {

                Log.i(tag,"handleChangeAvailabilityRequest : "+request);
                // ... handle event
                Logger.i("handleChangeAvailabilityRequest");

                Intent intent = new Intent(ReceiverModel.GetChageAvailablitity);
                intent.putExtra("Connector_id",request.getConnectorId());
                intent.putExtra("Type",request.getType());
                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);



                return new ChangeAvailabilityConfirmation(AvailabilityStatus.Accepted);
            }

            @Override
            public ChangeConfigurationConfirmation handleChangeConfigurationRequest(ChangeConfigurationRequest request) {

                Log.i(tag,"handleChangeConfigurationRequest : "+request);
                Logger.i("handleChangeAvailabilityRequest");
                // ... handle event
//                int ResetRetries = Integer.parseInt(ConfigurationControl.getResetRetries());
//
//
//                ConfigurationStatus status = null;
//
//                for(int i = 0 ; i< ResetRetries; i++){
//                    status = ChangeConfiurationFunction(request);
//                    if(status != null){
//                        break;
//                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return new ChangeConfigurationConfirmation(ConfigurationStatus.Rejected);
//                    }
//
//                }
//
//                if(status == null){
//                    return new ChangeConfigurationConfirmation(ConfigurationStatus.Rejected);
//                }else{
//                    return new ChangeConfigurationConfirmation(status);
//                }


                ConfigurationStatus status = ChangeConfigurationFunction(request);


                if(status == null){
                    return new ChangeConfigurationConfirmation(ConfigurationStatus.Rejected);
                }else{
                    return new ChangeConfigurationConfirmation(status);
                }

            }

            @Override
            public GetConfigurationConfirmation handleGetConfigurationRequest(GetConfigurationRequest request) {

                Log.i(tag,"handleGetConfigurationRequest : "+request);
                Logger.i("handleChangeAvailabilityRequest");
                // ... handle event

                List<KeyValueType> keylist = new ArrayList<>();
                List<String> unknownlist = new ArrayList<>();


                if(request.getKey() != null && request.getKey().length != 0 ){
                    for(int i = 0; i< request.getKey().length; i++){

                        KeyValueType keyValueType = GetConfigurationFuction(request.getKey()[i]);
                        if(keyValueType == null){           // key 가 구현되어진 key가 아닐경우
                            unknownlist.add(request.getKey()[i]);
                        }else{
                            keylist.add(keyValueType);
                        }

                    }
                // 키가 없을시 모든값 다 보내기
                }else{
                    String[] all = ConfigurationModel.AllKey;
                    for(int i = 0; i< all.length; i++){

                        KeyValueType keyValueType = GetConfigurationFuction(all[i]);
                        keylist.add(keyValueType);

                    }

                }



                GetConfigurationConfirmation result = new GetConfigurationConfirmation();

                Log.i(tag, Arrays.toString(keylist.toArray(new KeyValueType[0])));

                result.setConfigurationKey(keylist.toArray(new KeyValueType[0]));
                result.setUnknownKey(unknownlist.toArray(new String[0]));

                return result; // returning null means unsupported feature
            }


            @Override
            public DataTransferConfirmation handleDataTransferRequest(DataTransferRequest request) {

                Log.i(tag,"handleDataTransferRequest : "+request);
                Logger.i("handleDataTransferRequest");
                // ... handle event


                if(!(request.getVendorId().equals(KeyControl.getPAYvendor()))){
                    return new DataTransferConfirmation(DataTransferStatus.UnknownVendorId);
                }



                switch (request.getMessageId()){
                    case DataTransferModel.NVCATSetup :
                        Log.i("NVCATSetup",request.getData());

                        try {
                            JSONObject object = new JSONObject(request.getData());

                            ArrayList<String> Keys = new ArrayList<>();
                            Keys.add("Catid");
                            Keys.add("Bizno");
                            Keys.add("Serverip");
                            Keys.add("ServerPort");

                            ArrayList<String> Values = new ArrayList<>();
                            Values.add((String) object.get("Catid"));
                            Values.add((String) object.get("Bizno"));
                            Values.add((String) object.get("Serverip"));
                            Values.add((String) object.get("ServerPort"));

                            Intent intent1 = new Intent(ReceiverModel.GetNvcatOption);
                            intent1.putStringArrayListExtra("Keys",Keys);
                            intent1.putStringArrayListExtra("Values",Values);
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent1);



                        } catch (JSONException e) {
                            Log.e(tag, String.valueOf(e));
                            e.printStackTrace();;
                        }


                        return new DataTransferConfirmation(DataTransferStatus.Accepted); // returning null means unsupported feature

                    case DataTransferModel.NVCATRestart :

                        Intent intent = new Intent(ReceiverModel.GetNvcatRestart);
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);

                        return new DataTransferConfirmation(DataTransferStatus.Accepted);


                    case DataTransferModel.NVCATCancle:
                        Log.i("NVCATCancle",request.getData());

                        try {
                            JSONObject object = new JSONObject(request.getData());


                            Intent intent2 = new Intent(ReceiverModel.GetNvcatCanclePay);
                            intent2.putExtra("PayNum",(String) object.get("PayNum"));
                            intent2.putExtra("OriginPay",(String) object.get("OriginPay"));
                            intent2.putExtra("PayDay",(String) object.get("PayDay"));
                            intent2.putExtra("PayToken",(String) object.get("PayToken"));
                            intent2.putExtra("CanclePay",(String) object.get("CanclePay"));
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent2);


                        } catch (JSONException e) {
                            Log.e(tag, String.valueOf(e));
                            e.printStackTrace();
                        }

                        return new DataTransferConfirmation(DataTransferStatus.Accepted);

                    case DataTransferModel.NVCATUpdate:
                        Log.i("NVCATUpdate",request.getData());

                        try {
                            JSONObject object = new JSONObject(request.getData());


                            Intent intent2 = new Intent("nvcat_update");
                            intent2.putExtra("path",(String) object.get("path"));
                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent2);



                        } catch (JSONException e) {
                            Log.e(tag, String.valueOf(e));
                            Logger.e( String.valueOf(e));
                            e.printStackTrace();
                        }

                        return new DataTransferConfirmation(DataTransferStatus.Accepted);

                    case DataTransferModel.SettingUpdate:
                        Log.i("SettingUpdate",request.getData());

                        try {
                            JSONObject object = new JSONObject(request.getData());

                            String[] xy = TransNXY.returnNXY(Double.parseDouble(object.getString("Lat")), Double.parseDouble(object.getString("Lon")));


                            KeyControl.setDeviceId(object.getString("id"));
                            KeyControl.setNx(xy[0]);
                            KeyControl.setNy(xy[1]);



                        } catch (JSONException e) {
                            Log.e(tag, String.valueOf(e));
                            Logger.e( String.valueOf(e));
                            e.printStackTrace();
                        }

                        return new DataTransferConfirmation(DataTransferStatus.Accepted);

                    default:

                        return new DataTransferConfirmation(DataTransferStatus.UnknownMessageId); // returning null means unsupported feature

                }




            }

            @Override
            public RemoteStartTransactionConfirmation handleRemoteStartTransactionRequest(RemoteStartTransactionRequest request) {

                Log.i(tag,"handleRemoteStartTransactionRequest : "+request);
                // ... handle event
                Logger.i("handleRemoteStartTransactionRequest");




                if(KeyControl.getRightChargingEnabled()){
                    if(ConfigurationControl.getAuthorizeRemoteTxRequests()){    // 원격 충전을 위해 승인을 받아야하는가
                        try {
                            sendAuthorize(request.getIdTag());
                        } catch (Exception e) {
                            Log.e(tag, String.valueOf(e));
                            Logger.e( String.valueOf(e));
                            e.printStackTrace();;
                        }
                    }else{
//                    Intent intent = new Intent("remote_Start_Charge_Receiver");
                        Intent intent = new Intent(ReceiverModel.GetAuthorize);       // 권한획득
                        intent.putExtra("idtag",request.getIdTag());
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                    }

                    return new RemoteStartTransactionConfirmation(RemoteStartStopStatus.Accepted); // returning null means unsupported feature
                }else{
                    return new RemoteStartTransactionConfirmation(RemoteStartStopStatus.Rejected); // returning null means unsupported feature
                }





            }

            @Override
            public RemoteStopTransactionConfirmation handleRemoteStopTransactionRequest(RemoteStopTransactionRequest request) {

                Log.i(tag,"handleRemoteStopTransactionRequest : "+request);
                // ... handle event
                Logger.i("handleRemoteStopTransactionRequest");

                Intent intent = new Intent(ReceiverModel.SerialRightChargerStopCommand);
                intent.putExtra("pay_num",request.getTransactionId());
                intent.putExtra("reason", Reason.Remote );
                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);



                return new RemoteStopTransactionConfirmation(RemoteStartStopStatus.Accepted); // returning null means unsupported feature
            }

            @Override
            public ResetConfirmation handleResetRequest(ResetRequest request) {

                Log.i(tag,"handleResetRequest : "+request);
                // ... handle event
                Logger.i("handleResetRequest");
                Intent intent = new Intent(ReceiverModel.Receive_Reset);       // 만약 충전중이라면 해제하고 진행
                intent.putExtra("resetType",request.getType());
                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);


                return new ResetConfirmation(ResetStatus.Accepted); // returning null means unsupported feature
            }

            @Override
            public UnlockConnectorConfirmation handleUnlockConnectorRequest(UnlockConnectorRequest request) {

                Log.i(tag,"handleUnlockConnectorRequest : "+request);
                Logger.i("handleUnlockConnectorRequest");
                // ... handle event
                //해당 커넥터 아이디가 존재하지 않을경우
                if(request.getConnectorId() != DeviceModel.getConnectId()){
                    return new UnlockConnectorConfirmation(UnlockStatus.NotSupported);
                }
                //충전 포인트가 커넥터 잠금 해제를 시도했지만 커넥터가 여전히 연결되어 있음을 감지했습니다.
                if(KeyControl.getright_device_charging()){
                    return new UnlockConnectorConfirmation(UnlockStatus.UnlockFailed);
                }



                return new UnlockConnectorConfirmation(UnlockStatus.Unlocked); // returning null means unsupported feature
            }


        });
//        client = new JSONClient(core);


        client = new JsonClientTest(core,firmware,trigger);

//        client.connect("ws://192.168.0.7:8999/ws", events);   // 내컴퓨터
        try {
            client.connect(DeviceModel.getOCPP_Server(),events); // 호스팅
        }catch (Exception e){
            Logger.e( String.valueOf(e));
            e.printStackTrace();
        }
//        client.connect("ws://gracetech.co.kr:8999/ws", events);
    }



    ClientEvents events = new ClientEvents() {
        @Override
        public void connectionOpened() {
            Intent intent = new Intent(ReceiverModel.ServerConn);
            intent.putExtra("s_bool",true);
            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);



            // 하트비트 시작 - 24시간
            Start_hb_thread();
            Log.e("connecnt err","ConnectOpened");
            Logger.i("connect ConnectOpened");
        }

        @Override
        public void connectionClosed() {
            Intent intent = new Intent(ReceiverModel.ServerConn);
            intent.putExtra("s_bool",false);
            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);


            // 하트비트 종료
            Stop_hb_thread();
            Log.e("connecnt err","ConnectClosed");
            Logger.i("connect ConnectClosed");
            try{
//                if(client.isClosed()){
//                        client.connect("ws://192.168.0.7:8999/ws", this);
                    Logger.i("connect client : "+client.isClosed());
                    Log.e("connecnt err","ConnectClosed1 : "+client.isClosed());
                    if(reconnectAbled){         // bootNotification 에서 허락이 됬을경우
                        Thread.sleep(5000);
                        Logger.i("connect reconnectAbled is true");
                        client.connect(DeviceModel.getOCPP_Server(),this);
                    }

//                }


            }catch (Exception e){
                Logger.e( String.valueOf(e));
                e.printStackTrace();
            }

        }
    };


//    public void  connect(){
//
//        InitClient();

//        ClientEvents events = new ClientEvents() {
//            @Override
//            public void connectionOpened() {
//                Log.e("connecnt err","ConnectOpened");
//            }
//
//            @Override
//            public void connectionClosed() {
//
//                client = null;
//
//                Log.e("connecnt err","ConnectClosed");
//
//                try{
//                    if(client == null){
////                        client.connect("ws://192.168.0.7:8999/ws", this);
//                        if(reconnectAbled){         // bootNotification 에서 허락이 됬을경우
//                            connect();
//                        }
//
//                    }
//
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//        };

//        client.connect("ws://192.168.0.7:8999/ws", events);

//    }

    public void sendFirmwareStatusNotification(FirmwareStatus firmwareStatus) throws Exception {
        Request request = firmware.createFirmwareStatusNotificationRequest(firmwareStatus);

        Intent intent = new Intent(ReceiverModel.SetFirmwareStatus);
        intent.putExtra("FirmwareStatus",firmwareStatus);
        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);

        client.send(request).whenComplete((s, ex) -> {
            if(ex != null){
                Log.i(tag,"FirmwareStatusNotificationConfirmation err : "+ex);
                Logger.e("FirmwareStatusNotificationConfirmation err : "+ex);
            }

            Log.i(tag,"FirmwareStatusNotificationConfirmation receive : "+s);

        });

    }

    public void sendDiagnosticsStatusNotification(DiagnosticsStatus diagnosticsStatus) throws Exception{
        Request request = firmware.createDiagnosticsStatusNotificationRequest(diagnosticsStatus);

        Intent intent = new Intent(ReceiverModel.SetDiagnosticsStatus);
        intent.putExtra("DiagnosticsStatus",diagnosticsStatus);
        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);

        client.send(request).whenComplete((s, ex) -> {
            if(ex != null){
                Log.i(tag,"DiagnosticsStatusNotificationConfirmation err : "+ex);
                Logger.e("DiagnosticsStatusNotificationConfirmation err : "+ex);
            }

            Log.i(tag,"DiagnosticsStatusNotificationConfirmation receive : "+s);

        });
    }





    public void sendAuthorize(String idtag) throws Exception {

        Request request = core.createAuthorizeRequest(idtag);
        Intent intent = new Intent(ReceiverModel.GetAuthorize);       // 권한획득
        intent.putExtra("idtag",idtag);


        if(client.isClosed()){  // 서버와 연결이 안되어 있으면
            if(ConfigurationControl.getLocalAuthorizeOffline()){    // offline일때 인증캐쉬/로컬인증리스트를 이용해 거래가능한지.
                if(ConfigurationControl.getAuthorizationCacheEnabled()){    // 인증캐쉬를 사용할수있게 허락이 되어있는지
                    if(KeyControl.getAuthorizationCacheBool(idtag)){    // 인증 캐쉬에 허락 정보가 있으면
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                    }else{                                                         // 인증 캐쉬에 허락 정보가 없으면
                        if(ConfigurationControl.getAllowOfflineTxForUnknownId()) {    // offline일때 알수없는 거래 id가  거래가능한지. --- 현재 서버와 연결이 끊기면  충전시도 자체가 안되게 막아놈. 필요가 없음

                            //  잘못됨. 고쳐야함. 이대로 시작해버리면 startTranscation이 안보내진채로 시작해버림
//                            Intent intent1 = new Intent(ReceiverModel.SendChargeStart);       // 충전시작
//                            intent1.putExtra("connect_id",DeviceModel.getConnectId());
//                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent1);

                        }else{

                            Toast.makeText(mcontext,"오프라인시 인증캐쉬에 있지 않아 해당 동작이 불가능합니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(mcontext,"인증캐쉬 설정이 되어있지 않아 해당 동작이 불가능합니다.",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(mcontext,"서버와의 연결이 안되어 해당 동작이 불가능합니다.",Toast.LENGTH_LONG).show();
            }
        }else{    // 서버와 연결이 되어 있으면


            if(ConfigurationControl.getAuthorizationCacheEnabled()){    // 인증캐쉬를 사용할수있게 허락이 되어있는지

                if(KeyControl.getAuthorizationCacheBool(idtag)){    // 인증 캐쉬에 허락 정보가 있으면

                    LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                }else{                  // 인증 캐쉬에 허락 정보가 없으면

                    client.send(request).whenComplete((s, ex) -> {

                        if(ex != null){
                            Log.i(tag,"sendAuthorize err : "+ex);
                            Logger.e("sendAuthorize err : "+ex);
                        }

                        Log.i(tag,"AuthorizeConfirm receive : "+s);
                        Logger.i("AuthorizeConfirm receive : "+s);
                        AuthorizeConfirmation a = (AuthorizeConfirmation) s;

                        KeyControl.addAuthorizationCache(idtag,a.getIdTagInfo());       // 캐쉬 저장

                        if(ConfigurationControl.getLocalPreAuthorize()){        // 응답전 미리 동작하기

                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        }

                        if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){
                            if(!ConfigurationControl.getLocalPreAuthorize()){       // 응답후 동작하기

                                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                            }
                        }else{
                            Toast.makeText(mcontext,"인증이 되지 않은 사용자입니다. ",Toast.LENGTH_LONG).show();
                        }

                    });
                }


            }else{   // 인증캐쉬를 사용할수있게 허락이 안되어 있을때

                client.send(request).whenComplete((s, ex) -> {
                    Log.i(tag,"AuthorizeConfirm receive : "+s);
                    Logger.i("AuthorizeConfirm receive : "+s);
                    if(ex != null){
                        Log.i(tag,"sendAuthorize err : "+ex);
                        Logger.e("sendAuthorize err : "+ex);
                    }

                    AuthorizeConfirmation a = (AuthorizeConfirmation) s;

                    KeyControl.addAuthorizationCache(idtag,a.getIdTagInfo());       // 캐쉬 저장

                    if(ConfigurationControl.getLocalPreAuthorize()){        // 응답전 미리 동작하기

                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                    }

                    if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){

                        if(!ConfigurationControl.getLocalPreAuthorize()){       // 응답후 동작하기

                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        }


                    }else{
                        Intent intent2 = new Intent(ReceiverModel.TransactionCanclePay);         // 결제금액 반환
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent2);

                        Toast.makeText(mcontext,"인증이 되지 않은 사용자입니다. ",Toast.LENGTH_LONG).show();
                    }

                });
            }

        }



























        // Use the feature profile to help create event
//        Request request = core.createAuthorizeRequest(idtag);

        // Client returns a promise which will be filled once it receives a confirmation.
//        client.send(request).whenComplete((s, ex) -> {
//            Log.d(tag,"AuthorizeConfirm receive : "+s);
//
//            AuthorizeConfirmation a = (AuthorizeConfirmation) s;
//
//            KeyControl.addAuthorizationCache(idtag,a.getIdTagInfo());       // 캐쉬 저장
//
//            if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){
//
//
//                Intent intent = new Intent(ReceiverModel.GetAuthorize);       // 권한획득
//                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//
//
//            }else{
//                Toast.makeText(mcontext,"허가를 받지않은 기기에서 충전을 시도했습니다.",Toast.LENGTH_LONG).show();
//            }
////            client.disconnect();
//
//
//        });
    }


//    public void sendStopAuthorize(String idtag,Reason reason) throws Exception  {




//        Request request = core.createAuthorizeRequest(idtag);

//        if(client.isClosed()){  // 서버와 연결이 안되어 있으면
//            if(ConfigurationControl.getLocalAuthorizeOffline()){    // offline일때 인증캐쉬/로컬인증리스트를 이용해 거래가능한지.
//                if(ConfigurationControl.getAuthorizationCacheEnabled()){    // 인증캐쉬를 사용할수있게 허락이 되어있는지
//                    if(KeyControl.getAuthorizationCacheBool(idtag)){    // 인증 캐쉬에 허락 정보가 있으면
//                        try {
////                            sendStopTransaction(11000,Integer.parseInt(idtag),reason);
//                            sendStopTransactionLocalBroadcast(Integer.parseInt(idtag),reason);
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }else{
//                        Toast.makeText(mcontext,"인증캐쉬에 있지 않아 해당 동작이 불가능합니다.",Toast.LENGTH_LONG).show();
//                    }
//                }else{
//                    Toast.makeText(mcontext,"인증캐쉬 설정이 되어있지 않아 해당 동작이 불가능합니다.",Toast.LENGTH_LONG).show();
//                }
//            }else{
//                Toast.makeText(mcontext,"서버와의 연결이 안되어 해당 동작이 불가능합니다.",Toast.LENGTH_LONG).show();
//            }
//        }else{    // 서버와 연결이 되어 있으면
//            if(ConfigurationControl.getAuthorizationCacheEnabled()){    // 인증캐쉬를 사용할수있게 허락이 되어있는지
//
//                Log.d(tag,"sendStopAuthorize : "+ KeyControl.getAuthorizationCacheBool(idtag));
//
//                if(KeyControl.getAuthorizationCacheBool(idtag)){    // 인증 캐쉬에 허락 정보가 있으면
//                    try {
//
////                        sendStopTransaction(11000,Integer.parseInt(idtag),reason);
//                        sendStopTransactionLocalBroadcast(Integer.parseInt(idtag),reason);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }else{                  // 인증 캐쉬에 허락 정보가 없으면
//
//
//                    client.send(request).whenComplete((s, ex) -> {
//                        Log.d(tag,"AuthorizeConfirm receive : "+s);
//
//                        AuthorizeConfirmation a = (AuthorizeConfirmation) s;
//
//                        KeyControl.addAuthorizationCache(idtag,a.getIdTagInfo());       // 캐쉬 저장
//
//                        if(ConfigurationControl.getLocalPreAuthorize()){        // 응답전 미리 동작하기
//                            try {
////                                sendStopTransaction(11000,Integer.parseInt(idtag),reason);
//                                sendStopTransactionLocalBroadcast(Integer.parseInt(idtag),reason);
//                            } catch (Exception e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//
//                        if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){
//                            try {
//
//                                if(!ConfigurationControl.getLocalPreAuthorize()){       // 응답후 동작하기
////                                    sendStopTransaction(11000,Integer.parseInt(idtag),reason);
//                                    sendStopTransactionLocalBroadcast(Integer.parseInt(idtag),reason);
//                                }
//
//                            } catch (Exception e) {
//                                throw new RuntimeException(e);
//                            }
//
//                        }else{
//                            Toast.makeText(mcontext,"사용이 막혀있거나 만료된 사용자입니다. ",Toast.LENGTH_LONG).show();
//                        }
//
//                    });
//                }
//
//
//            }else{   // 인증캐쉬를 사용할수있게 허락이 안되어 있을때
//
//                client.send(request).whenComplete((s, ex) -> {
//                    Log.d(tag,"AuthorizeConfirm receive : "+s);
//
//                    AuthorizeConfirmation a = (AuthorizeConfirmation) s;
//
//                    KeyControl.addAuthorizationCache(idtag,a.getIdTagInfo());       // 캐쉬 저장
//
//                    if(ConfigurationControl.getLocalPreAuthorize()){        // 응답전 미리 동작하기
//                        try {
////                            sendStopTransaction(11000,Integer.parseInt(idtag),reason);
//                            sendStopTransactionLocalBroadcast(Integer.parseInt(idtag),reason);
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){
//                        try {
//
//                            if(!ConfigurationControl.getLocalPreAuthorize()){       // 응답후 동작하기
////                                sendStopTransaction(11000,Integer.parseInt(idtag),reason);
//                                sendStopTransactionLocalBroadcast(Integer.parseInt(idtag),reason);
//                            }
//
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }else{
//                        Toast.makeText(mcontext,"인증이 되지 않은 사용자입니다. ",Toast.LENGTH_LONG).show();
//                    }
//
//                });
//            }





//        }



//    }

    public void sendBootNotification() throws Exception {

        // Use the feature profile to help create event

        String versionName = "";
        PackageManager packageManager = mcontext.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(mcontext.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();;
        }

        BootNotificationRequest request = new BootNotificationRequest(KeyControl.getDevicevendor(), KeyControl.getDevicemodel());
        request.setFirmwareVersion(versionName);
        request.setChargePointSerialNumber(KeyControl.getDeviceId());



        // Client returns a promise which will be filled once it receives a confirmation.
        // 통신이 안되면 시작이 안되야함. 거부가 뜨면 제시된 시간만큼 기다렸다가 다시 보내야함
        client.send(request).whenComplete((s, ex) ->{
//            System.out.println(s);
            Log.i(tag,"BootNotificationConfirm receive : "+s);
            Logger.i("BootNotificationConfirm receive : "+s);
            if(ex != null){
                Log.i(tag,"sendBootNotification err : "+ex);
                Logger.e("sendBootNotification err : "+ex);
            }

            BootNotificationConfirmation a = (BootNotificationConfirmation) s;


            Intent intent = new Intent(ReceiverModel.BootNotification);
            intent.putExtra("interval",a.getInterval() );
//
//            if(ex !=null){
//                for (int i = 0; i < s_callbacksList.size(); i++) {
//                    SplashClientInterface callbackListener = s_callbacksList.get(i);
//                    if (null != callbackListener) {
//                        int status = 3;
//                        callbackListener.onBootNotification(a.getInterval(), status);
//                    }
//                }
//                return;
//            }
//
//
            if(ex !=null){
                intent.putExtra("status",3);
                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                return;
            }


            if(a.getStatus() == RegistrationStatus.Accepted){
                intent.putExtra("status",1);
            }else if(a.getStatus() == RegistrationStatus.Pending){
                intent.putExtra("status",2);
            }else{
                intent.putExtra("status",3);
            }

            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);



//            for (int i = 0; i < s_callbacksList.size(); i++) {
//                SplashClientInterface callbackListener = s_callbacksList.get(i);
//                if (null != callbackListener) {
//                    int status = 3;
//                    if(a.getStatus() == RegistrationStatus.Accepted){
//                        status = 1;
//                    }else if(a.getStatus() == RegistrationStatus.Pending){
//                        status = 2;
//                    }
//
//                    callbackListener.onBootNotification(a.getInterval(),status);
//                }
//            }




        } );

    }

    public void sendHeartBeat() throws Exception {
        Log.i(tag,"sendHeartbeatRequst ");
        Logger.i("sendHeartbeatRequst ");
        // Use the feature profile to help create event
        Request request = core.createHeartbeatRequest();

        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {

            if(ex != null){
                Log.e(tag,"sendHeartbeatRequst err : "+ex);
                Logger.e("sendHeartbeatRequst err : "+ex);
            }

            Log.i(tag,"HeartbeatConfirm receive : "+s);
            Logger.i("HeartbeatConfirm receive : "+s);
        });
    }

    public void sendStartTransaction(int Connectorid, String idTag, int meterstart) throws Exception {
        Logger.i("sendStartTransaction");

        // Use the feature profile to help create event
        Request request = null;
        
        try{
            request = core.createStartTransactionRequest(Connectorid,idTag,meterstart, returnZonedDateTime());
        }catch (Exception e){
            e.printStackTrace();
        }


        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {
            if(ex !=null){

                if(TransactionStartMessageAttempts<Integer.parseInt(ConfigurationControl.getTransactionMessageAttempts())){           // 거부되었을때 2번정도 다시보냄
                    TransactionStartMessageAttempts++;
                    try {
                        Thread.sleep(Integer.parseInt(ConfigurationControl.getTransactionMessageRetryInterval())*1000L);

                        sendStartTransaction(Connectorid,idTag,meterstart);
                    } catch (Exception e) {
                        e.printStackTrace();;
                    }
                }else{
                    TransactionStartMessageAttempts = 0;

                    Toast.makeText(mcontext,"서버와의 통신에서 문제가 발생했습니다. 다음에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                    
                    Intent intent = new Intent(ReceiverModel.TransactionCanclePay);         // 결제금액 반환
                    LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//                    client.disconnect();
                }
            }


            Log.i(tag,"StartTransaction receive : "+s);
            Logger.i("StartTransaction receive : "+s);
            if(ex!= null){
                Log.e(tag,"StartTransaction receive err : "+ex);
                Logger.e("StartTransaction receive err : "+ex);
            }



            StartTransactionConfirmation a = (StartTransactionConfirmation) s;

            KeyControl.addAuthorizationCache(idTag,a.getIdTagInfo());       // 캐쉬 저장

            if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){
                Intent intent = new Intent(ReceiverModel.SendChargeStart);       // 충전시작
                intent.putExtra("connect_id",Connectorid);
                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//                client.disconnect();
            }else{
                Intent intent = new Intent(ReceiverModel.TransactionCanclePay);         // 결제금액 반환
                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);

                Toast.makeText(mcontext,"서버에서 충전을 거부하였습니다.",Toast.LENGTH_LONG).show();
            }

        });
    }

    public void sendStopTransaction(int meterstop,int transactionid,Reason reason) throws Exception {

        Log.d(tag, "sendStopTransaction !!!!!!!!!!!!!!!!!!!!!!!!");
        Logger.i( "sendStopTransaction ");
        ZonedDateTime time = returnZonedDateTime();

        Intent intent = new Intent(ReceiverModel.GetChargeStop);       // 충전중단
        intent.putExtra("pay_num",String.valueOf(transactionid));
        intent.putExtra("ChargePointStatus",ChargePointStatus.Finishing);
        intent.putExtra("isResetReason",reason);
        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);


        // 연결이 끊겨 있다면
        if(client.isClosed()){
            //db 저장
            sqlite.InsertUnknownStopTrasaction(String.valueOf(time),String.valueOf(transactionid),String.valueOf(meterstop),String.valueOf(reason));

            return;
        }


        int Type = 0;

        String[] AlignedData = {};
        String[] SampledData = {};
//
        String getStopTxnAlignedData = ConfigurationControl.getStopTxnAlignedData();
        String getStopTxnSampledData = ConfigurationControl.getStopTxnSampledData();

//
        if((!getStopTxnAlignedData.equals(""))){              //StopTxnAlignedData 가 빈문자열이 아니고 AlignedInterval이 0이 아니면(어차피 0이면 데이터가 저장되어있는게 없음)
            Type = 1;
            AlignedData = returnColum(getStopTxnAlignedData);
        }
//
        if((!getStopTxnSampledData.equals(""))){             //StopTxnSampledData 가 빈문자열이 아니고 SampledInterval이 0이 아니면(어차피 0이면 데이터가 저장되어있는게 없음)
            Type = 2;
            SampledData = returnColum(getStopTxnSampledData);
        }

        if((!getStopTxnSampledData.equals("")) && (!getStopTxnAlignedData.equals(""))){             //둘다 빈문자열이 아니면
            Type = 3;
        }



        Log.i(tag, "date : "+ time);

        StopTransactionRequest request = new StopTransactionRequest(meterstop,time,transactionid);
        request.setReason(reason);
        MeterValue[] gmv = null;
        switch (Type){
            case 1:
                gmv = sqlite.SelectMetervalues_Retrun (AlignedData, String.valueOf(transactionid),"1");
                if(gmv.length != 0){
                    request.setTransactionData(gmv);
                }
                break;
            case 2:
                gmv = sqlite.SelectMetervalues_Retrun (SampledData,String.valueOf(transactionid),"2");
                if(gmv.length != 0){
                    request.setTransactionData(gmv);
                }
                break;
            case 3:
                gmv = Stream.concat(Arrays.stream(sqlite.SelectMetervalues_Retrun (AlignedData,String.valueOf(transactionid),"1")),Arrays.stream(sqlite.SelectMetervalues_Retrun (SampledData,String.valueOf(transactionid),"2"))).toArray(MeterValue[]::new);
                if(gmv.length != 0){
                    request.setTransactionData(gmv);
                }
                break;
            default:
                break;
        }


        int finalType = Type;
        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {

            if(ex !=null){
                Log.e(tag, String.valueOf(ex));
                Logger.e( "sendStopTransaction " +ex);
                if(TransactionStopMessageAttempts<Integer.parseInt(ConfigurationControl.getTransactionMessageAttempts())){           // 거부되었을때 2번정도 다시보냄
                    TransactionStopMessageAttempts++;
                    try {
                        Thread.sleep(Integer.parseInt(ConfigurationControl.getTransactionMessageRetryInterval())*1000L);

                        sendStopTransaction( meterstop,transactionid,reason);
                    } catch (Exception e) {
                        e.printStackTrace();;
                    }
                }else{
                    TransactionStopMessageAttempts = 0;
                }
            }

            StopTransactionConfirmation a = (StopTransactionConfirmation) s;

            KeyControl.addAuthorizationCache(String.valueOf(transactionid),a.getIdTagInfo());       // 캐쉬 저장



            if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){
//                client.disconnect();

                //db 해당 metervalue 지우기
                switch (finalType){
                    case 1:
                        sqlite.DeleteMetervalues(String.valueOf(transactionid),"1");
                        break;
                    case 2:
                        sqlite.DeleteMetervalues(String.valueOf(transactionid),"2");
                        break;
                    case 3:
                        sqlite.DeleteMetervalues(String.valueOf(transactionid));
                        break;
                }


            }else{
                Toast.makeText(mcontext,"서버에서 충전종료를 거부하였습니다.",Toast.LENGTH_LONG).show();      // 거부되었을때 다시 통신시도해야함 (22p).


            }


        });


    }



//    public void sendUnknownStartTransaction(int Connectorid, String idTag, int meterstart, ZonedDateTime dateTime) throws Exception {
//
//
//        // Use the feature profile to help create event
//        Request request = null;
//
//        try{
//            request = core.createStartTransactionRequest(Connectorid,idTag,meterstart,dateTime);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//        // Client returns a promise which will be filled once it receives a confirmation.
//        client.send(request).whenComplete((s, ex) -> {
//
//            if(ex != null){
//                if(TransactionStartMessageAttempts<Integer.parseInt(ConfigurationControl.getTransactionMessageAttempts())){           // 거부되었을때 2번정도 다시보냄
//                    TransactionStartMessageAttempts++;
//                    try {
//                        Thread.sleep(Integer.parseInt(ConfigurationControl.getTransactionMessageRetryInterval())*1000L);
//
//                        sendUnknownStartTransaction(Connectorid,idTag,meterstart,dateTime);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }else{
//                    TransactionStartMessageAttempts = 0;
//                }
//            }
//
//            Log.i(tag,"StartTransaction receive : "+s);
//            Log.i(tag,"StartTransaction receive err : "+ex);
//
//
//            StartTransactionConfirmation a = (StartTransactionConfirmation) s;
//
//
//            if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){           /// db 저장되어있는것들 삭제
//
//
////                client.disconnect();
//            }else if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Invalid){      // 확인되지않는 알수없는 id 일떄
//                if(ConfigurationControl.getStopTransactionOnInvalidId()){              // StopTransactionOnInvalidId   키가 true이면 정상적으로 종료, Stop Transaction 요청 의 이유 필드는 DeAuthorized 로 설정되어야 합니다 . 충전 포인트에 충전 케이블을 잠글 가능성이 있는 경우 소유자가 식별자를 제시할 때까지 충전 케이블을 잠긴 상태로 유지해야 합니다.
//                    try {
////                                    sendStopTransaction(11000,Integer.parseInt(idtag),Reason.DeAuthorized);
//                        sendStopTransactionLocalBroadcast(Integer.parseInt(idTag),Reason.DeAuthorized);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }else{                                                                   // StopTransactionOnInvalidId   키가 false일때 차량으로의 에너지 전달만 중지합니다. 운영자는 EV가 주행할 수 있도록 최소한의 에너지로 EV를 충전하도록 선택할 수 있습니다. 이 양은 선택적 구성 키인 MaxEnergyOnInvalidId에 의해 제어됩니다
//
//                }
//            }
//
//        });
//    }


    public void sendUnknownStopTransaction(int meterstop,int transactionid,Reason reason,ZonedDateTime dateTime) throws Exception {

        Log.d(tag,"sendUnknownStopTransaction :"+transactionid);
        Logger.i("sendUnknownStopTransaction ");
        int Type = 0;

        String[] AlignedData = {};
        String[] SampledData = {};
//
        String getStopTxnAlignedData = ConfigurationControl.getStopTxnAlignedData();
        String getStopTxnSampledData = ConfigurationControl.getStopTxnSampledData();

//
        if((!getStopTxnAlignedData.equals(""))){              //StopTxnAlignedData 가 빈문자열이 아니고 AlignedInterval이 0이 아니면(어차피 0이면 데이터가 저장되어있는게 없음)
            Type = 1;
            AlignedData = returnColum(getStopTxnAlignedData);
        }
//
        if((!getStopTxnSampledData.equals(""))){             //StopTxnSampledData 가 빈문자열이 아니고 SampledInterval이 0이 아니면(어차피 0이면 데이터가 저장되어있는게 없음)
            Type = 2;
            SampledData = returnColum(getStopTxnSampledData);
        }

        if((!getStopTxnSampledData.equals("")) && (!getStopTxnAlignedData.equals(""))){             //둘다 빈문자열이 아니면
            Type = 3;
        }


        StopTransactionRequest request = new StopTransactionRequest(meterstop,dateTime,transactionid);
        request.setReason(reason);
        MeterValue[] gmv = null;
        switch (Type){
            case 1:
                gmv = sqlite.SelectMetervalues_Retrun (AlignedData, String.valueOf(transactionid),"1");
                if(gmv.length != 0){
                    request.setTransactionData(gmv);
                }
                break;
            case 2:
                gmv = sqlite.SelectMetervalues_Retrun (SampledData,String.valueOf(transactionid),"2");
                if(gmv.length != 0){
                    request.setTransactionData(gmv);
                }
                break;
            case 3:
                gmv = Stream.concat(Arrays.stream(sqlite.SelectMetervalues_Retrun (AlignedData,String.valueOf(transactionid),"1")),Arrays.stream(sqlite.SelectMetervalues_Retrun (SampledData,String.valueOf(transactionid),"2"))).toArray(MeterValue[]::new);
                if(gmv.length != 0){
                    request.setTransactionData(gmv);
                }
                break;
            default:
                break;
        }


        int finalType = Type;
        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {

            if(ex !=null){
                Log.e(tag, String.valueOf(ex));
                Logger.e( "sendUnknownStopTransaction "+ex);
                if(TransactionStopMessageAttempts<Integer.parseInt(ConfigurationControl.getTransactionMessageAttempts())){           // 거부되었을때 2번정도 다시보냄
                    TransactionStopMessageAttempts++;
                    try {
                        Thread.sleep(Integer.parseInt(ConfigurationControl.getTransactionMessageRetryInterval())*1000L);

                        sendUnknownStopTransaction( meterstop,transactionid,reason,dateTime);
                    } catch (Exception e) {
                        e.printStackTrace();;
                    }
                }else{
                    TransactionStopMessageAttempts = 0;
                }
            }

            StopTransactionConfirmation a = (StopTransactionConfirmation) s;


            if(a.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted){

                sqlite.DeleteTransactionFactory(String.valueOf(transactionid));     // 저장되어있던 것 지움
                
//                client.disconnect();

                //db 해당 metervalue 지우기
                switch (finalType){
                    case 1:
                        sqlite.DeleteMetervalues(String.valueOf(transactionid),"1");
                        break;
                    case 2:
                        sqlite.DeleteMetervalues(String.valueOf(transactionid),"2");
                        break;
                    case 3:
                        sqlite.DeleteMetervalues(String.valueOf(transactionid));
                        break;
                }


            }else{
                Toast.makeText(mcontext,"서버에서 충전종료를 거부하였습니다.",Toast.LENGTH_LONG).show();      // 거부되었을때 다시 통신시도해야함 (22p).
            }


        });


    }

    public void sendDataTransfer(String vendorId,String messageID,String data) throws Exception {

        Log.i(tag,"sendDataTransfer : "+vendorId+","+messageID+","+data);
        Logger.i("sendDataTransfer ");
        if(client.isClosed()){
            if(messageID.equals(DataTransferModel.getI_Pay())){         // 결제 정보 db 저장

                Intent intent = new Intent(ReceiverModel.TransactionCanclePay);
                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);     // -- 환불
                
                Toast.makeText(mcontext,"서버와 연결이 어려워 결제가 취소됩니다. 다음에 다시 시도해주세요.",Toast.LENGTH_LONG).show();

            }else if(messageID.equals(DataTransferModel.getC_Pay())){   // 결제 정보 db 저장, 부분 취소           --- db 저장해서 나중에 연결되면 다시 가능하게

            }else if(messageID.equals(DataTransferModel.getG_Pay())){   // 전기 시세 가져오기
                Intent intent = new Intent("Page");
                intent.putExtra("page", PageAddrModel.ChargeSelectFgm);
                intent.putExtra("ChargePointStatus",ChargePointStatus.Faulted);
                intent.putExtra("ChargePointErrorCode",ChargePointErrorCode.WeakSignal);


                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                Toast.makeText(mcontext,"서버와의 연결이 끊겨 가격측정이 어렵습니다. 다음에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
            }

            return;
        }





        DataTransferRequest request = new DataTransferRequest(vendorId);
        request.setMessageId(messageID);
        request.setData(data);


        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {

            if(ex!=null){
                Logger.e("DataTransfer receive : "+ex);
            }

            Log.i(tag,"DataTransfer receive : "+s);
            Logger.i("DataTransfer receive : "+s);
            DataTransferConfirmation a = (DataTransferConfirmation) s;

            try {
                JSONObject jsonObject = new JSONObject(a.getData());
                int type = jsonObject.getInt("MessageType");
                String _data = jsonObject.getString("Data");
                Log.i(tag, String.valueOf(type));
                Logger.i( String.valueOf(type));

                switch (type){
                    case 1: // 광고 리스트 받기
//                        JSONArray jsonArray = new JSONArray(_data);
//                        AcceptedADList(jsonArray);
                        break;
                        
                    case 2: //  결제 정보 insert 성공
//                        client.disconnect();
                        break;

                    case 3: //  시세 가져오기 성공
                        JSONObject jsonObject1 = new JSONObject(_data);
                        float pay = jsonObject1.getInt("Pay");

                        Intent intent = new Intent(ReceiverModel.GetPay);
                        intent.putExtra("pay",pay);
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);


//                        for (int i = 0; i < c_callbacksList.size(); i++) {
//                            CenterClientInterface callbackListener = c_callbacksList.get(i);
//                            if (null != callbackListener) {
//                                callbackListener.onGetPay(pay);
//                            }
//                        }

//                        client.disconnect();
                        break;
                        
                    case -1:    // 서버에 규격에 안맞거나 db에 저장이 안되거나 하는 오류  
                        //오류 낫을때 일정기간후 다시 보내거나 따로 log 저장해야함
//                        client.disconnect();
                        break;



                }



            } catch (JSONException e) {
                Logger.e("DataTransfer receive err : "+e);
                e.printStackTrace();
            }


        });
    }

    public void sendMeterValues(int connectorId,int transactionId, String amount, Boolean isSampled ,ZonedDateTime zonedDateTime,String EAII,String CI,String CO,String PAI,String PO,String SoC, String TEM) throws Exception {

        Log.d(tag,"value : "+ amount);


        Log.i(tag,"sendMeterValues : "+connectorId+","+transactionId+","+amount+","+isSampled+","+zonedDateTime+","+EAII+","+CI+","+CO+","+PAI+","+PO+","+TEM+","+SoC);
        Logger.i("sendMeterValues : connectorId - "+connectorId+", transactionId - "+transactionId+", amount - "+amount+", isSampled - "+isSampled+", zonedDateTime - "+zonedDateTime+", EAII- "+EAII+", CI- "+CI+",CO - "+CO+", PAI - "+PAI+", PO - "+PO+", TEM - "+TEM+", SoC - "+SoC);

        String[] Data;

        if(isSampled){
            Data = ConfigurationControl.getMeterValuesSampledData().split(",");
        }else{
            Data = ConfigurationControl.getMeterValuesAlignedData().split(",");
        }

        SampledValue[] svs = new SampledValue[Data.length];

        // 배열을 미리 쪼개놓고 불러오는식으로 해야할듯..?

        for(int i = 0; i < Data.length ; i++){
            SampledValue sv = new SampledValue("0");

            if(isSampled){
                sv.setContext("Sample.Periodic");
            }else{
                sv.setContext("Sample.Clock");
            }

            switch (Data[i]){
                case MeterValueMeasurandModel.EAIR:           //-"활성 전기 에너지"(Wh 또는 kWh) 레지스터에서 읽은 수치 값 , (그리드 공급으로부터) 수입된 에너지를 측정하는 전기 계량기        - 총 누적
                    sv.setMeasurand(MeterValueMeasurandModel.EAIR);
                    sv.setValue(amount);
                    sv.setUnit("kWh");
                    break;
                case MeterValueMeasurandModel.EAII:           //- 한 동안 (그리드 공급으로부터) 수입된 "활성 전기 에너지"(Wh 또는 kWh)의 절대량                                           - 기간 부분 누적
                    sv.setMeasurand(MeterValueMeasurandModel.EAII);
                    sv.setValue(EAII);
                    sv.setUnit("kWh");
                    break;
                case MeterValueMeasurandModel.CI:                          //-EV에 순간적인 전류 흐름                   - 순간 전류
                    sv.setMeasurand(MeterValueMeasurandModel.CI);
                    sv.setValue(CI);
                    sv.setUnit("A");
                    break;
                case MeterValueMeasurandModel.CO:                        // -EV에 제공되는 최대 전류                   - 최대전류
                    sv.setMeasurand(MeterValueMeasurandModel.CO);
                    sv.setValue(CO);
                    sv.setUnit("A");
                    break;
//                case MeterValueMeasurandModel.PAI:                     // - EV에서 가져온 순간 유효 전력. (W 또는 kW)   - 순간 전력
//                    sv.setMeasurand(MeterValueMeasurandModel.PAI);
//                    sv.setValue(PAI);
//                    sv.setUnit("kW");
//                    break;
                case MeterValueMeasurandModel.PO:                          // -EV에 제공되는 최대 전력                   - 최대전력
                    sv.setMeasurand(MeterValueMeasurandModel.PO);
                    sv.setValue(PO);
                    sv.setUnit("kW");
                    break;
                case MeterValueMeasurandModel.SoC:                                     //-충전 차량의 충전 상태(%)
                    sv.setMeasurand(MeterValueMeasurandModel.SoC);
                    sv.setValue(SoC);
                    sv.setUnit("Percent");
                    break;

                case MeterValueMeasurandModel.TEM:                                     //-충전 차량의 충전 상태(%)
                    sv.setMeasurand(MeterValueMeasurandModel.TEM);
                    sv.setValue(TEM);
                    sv.setUnit("Celsius");
                    break;

            }


            svs[i] = sv;
        }


        MeterValue mv = new MeterValue(zonedDateTime, svs);
        MeterValue[] mvs = new MeterValue[]{mv};
        MeterValuesRequest request = new MeterValuesRequest(connectorId);
        request.setTransactionId(transactionId);
        request.setMeterValue(mvs);



        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {
            Log.i(tag,"MeterValues receive : "+s);
            Logger.i("MeterValues receive : "+s);


            if( ex != null){
                Log.e(tag,"MeterValues receive : "+ex);
                Logger.e("MeterValues receive : "+ex);
            }
            // 에러시에 metervalue 값을 따로 저장해서 다음차례때 보내기..?
        });






        //context - Default = “Sample.Periodic”
                //Interruption.Begin    -중단 시작 시 가져온 값입니다.
                //Interruption.End      -중단 후 재개할 때 사용되는 값입니다
                //Other                 -다른 상황에 대한 가치.
                //Sample.Clock          -시계 정렬 간격에서 가져온 값입니다
                //Sample.Periodic       -거래 시작 시간을 기준으로 주기적 샘플로 사용되는 값입니다.
                //Transaction.Begin     -거래 시작 시 취해진 값입니다
                //Transaction.End       -거래 종료 시 가져오는 값입니다
                //Trigger               -TriggerMessage.req 에 대한 응답으로 취해진 값
        //format - Default =  “Raw”
                //Raw                   -데이터는 정수/십진수 숫자 데이터로 해석됩니다
                //SignedData            -데이터는 16진수 데이터로 인코딩된 부호 있는 이진 데이터 블록으로 표시됩니다
        //measurand - Default = “Energy.Active.Import.Register”     -- (91p)
                //Current.Export                        -EV로부터 순간 전류 흐름
                //Current.Import                        -EV에 순간적인 전류 흐름
                //Current.Offered                        -EV에 제공되는 최대 전류
                //Energy.Active.Export.Register            -(가장 권위 있는) "활성 전기 에너지"(Wh 또는 kWh) 레지스터에서 읽은 수치 값 ,(그리드에) 내보낸 에너지를 측정하는 전기 계량기.
                //Energy.Active.Import.Register            -(가장 권위 있는) "활성 전기 에너지"(Wh 또는 kWh) 레지스터에서 읽은 수치 값 , (그리드 공급으로부터) 수입된 에너지를 측정하는 전기 계량기
                //Energy.Reactive.Export.Register         -(대부분의) "무효 전기 에너지"(VARh 또는 kVARh) 레지스터에서 읽은 숫자 값 ,권위 있는) (그리드에) 수출된 에너지를 측정하는 전기 계량기
                //Energy.Reactive.Import.Register         -(대부분의) "무효 전기 에너지"(VARh 또는 kVARh) 레지스터에서 읽은 숫자 값 ,권위 있는) (그리드 공급으로부터) 수입된 에너지를 측정하는 전기 계량기.
                //Energy.Active.Export.Interval         - 관련 시간 동안 그리드로 내보낸 "활성 전기 에너지"(Wh 또는 kWh)의 절대량 ,Metervalues ReadingContext 및 적용 가능한 간격 기간 구성 값으로 지정된 "간격" , "ClockAlignedDataInterval" 및 "MeterValueSampleInterval"의 경우 (초)입니다.
                //Energy.Active.Import.Interval         - 한 동안 (그리드 공급으로부터) 수입된 "활성 전기 에너지"(Wh 또는 kWh)의 절대량 ,Metervalues ReadingContext로 지정된 관련 시간 "간격" 및 적용 가능한 간격 기간 , "ClockAlignedDataInterval" 및 "MeterValueSampleInterval"에 대한 구성 값(초)입니다
                //Energy.Reactive.Export.Interval         -관련 기간 동안 그리드로 내보낸 "무효 전기 에너지"(VARh 또는 kVARh)의 절대량 ,Metervalues ReadingContext 및 적용 가능한 간격 기간 구성으로 지정된 시간 "간격" , "ClockAlignedDataInterval" 및 "MeterValueSampleInterval"의 값(초)입니다.
                //Energy.Reactive.Import.Interval         -전력 공급 중에 수입된 "무효 전기 에너지"(VARh 또는 kVARh)의 절대량 , Metervalues ReadingContext로 지정된 관련 시간 "간격" 및 적용 가능한 간격 기간 , "ClockAlignedDataInterval" 및 "MeterValueSampleInterval"에 대한 구성 값(초)입니다.
   //             //Frequency                               -전력선 주파수를 즉시 판독합니다. 참고: OCPP 1.6에는 다음에 대한 UnitOfMeasure가 없습니다 ,주파수, 측정량이 있는 SampledValue에 대한 UnitOfMeasure: 주파수는  Hertz입니다.
                //Power.Active.Export                      -EV가 내보내는 순간 유효전력. (W 또는 kW)
                //Power.Active.Import                  -EV에서 가져온 순간 유효 전력. (W 또는 kW
                //Power.Factor                         -전체 에너지 흐름의 순간 역률
                //Power.Offered                        -EV에 제공되는 최대 전력
   //             //Power.Reactive.Export                   -EV가 수출하는 순간 무효전력. (var 또는 kvar)
  //              //Power.Reactive.Import                   -EV에서 수입한 순간 무효전력. (var 또는 kvar)
  //              //RPM                                      -팬 속도(RPM)
                //SoC                                    -충전 차량의 충전 상태(%)
  //              //Temperature                              -충전 지점 내부의 온도 판독
                //Voltage                                -순간 AC RMS 공급 전압

        //phase - 측정된 값을 해석하는 방법을 나타냅니다 Phase specifies how a measured value is to be interpreted. Please note that not all values of Phase are applicable to all Measurands.  (94p)    -- 3상이 아니여서 공백
                //L1                    -L1에서 측정됨
                //L2                    -L2에서 측정됨
                //L3                    -L3에서 측정됨
                //N                     -중립에서 측정됨
                //L1-N                  -중성 도체를 기준으로 L1에서 측정됨
                //L2-N                  -중성 도체를 기준으로 L2에서 측정됨
                //L3-N                  -중성 도체를 기준으로 L3에서 측정됨
                //L1-L2                 -L1과 L2 사이에서 측정됨
                //L2-L3                 -L2와 L3 사이에서 측정됨
                //L3-L1                 -L3과 L1 사이에서 측정됨
        // location - Default=”Outlet”                                                                                      --- Outlet
                //Body                  -충전점 본체 내부 측정(예: 온도)
                //Cable                 -EV와 충전 지점 사이의 케이블에서 측정
                //EV                    -EV로 측정
                //Inlet                 -네트워크("그리드") 입구 연결에서 측정
                //Outlet                -커넥터에서의 측정. 기본값
        // unit -  Default = “Wh”    SampledValue 에 사용되는 값 요소의 선택적 "단위" 필드에 허용되는 값입니다 . (98p)
                //kWh kiloWatt-hours (energy).
                //varh Var-hours (reactive energy).
                //kvarh kilovar-hours (reactive energy).
                //W Watts (power).
                //kW kilowatts (power).
                //VA VoltAmpere (apparent power).
                //VALUE DESCRIPTION
                //kVA kiloVolt Ampere (apparent power).
                //var Vars (reactive power).
                //kvar kilovars (reactive power).
                //A Amperes (current).
                //V Voltage (r.m.s. AC).
                //Celsius Degrees (temperature).
                //Fahrenheit Degrees (temperature).
                //K Degrees Kelvin (temperature).
                //Percent Percentage.



//        bbb.setMeasurand("Frequency");
//        bbb.setUnit("Wh");



    }



    public void sendTriggerMeterValues(int connectorId, int transactionId,String amount,String EAII,String CI,String CO,String PAI,String PO, String SoC, String TEM) throws Exception {

//        MainViewModel viewModel = new MainViewModel();

//        int connectorId = DeviceModel.getConnectId();
//        int transactionId = Integer.parseInt(viewModel.getRight_pay_number().getValue());
//        String amount = String.valueOf(viewModel.getright_charge_amount().getValue());
        ZonedDateTime zonedDateTime = returnZonedDateTime();
//        String EAII = viewModel.getRight_EAII().getValue();
//        String CI = viewModel.getRight_CI().getValue();
//        String CO = viewModel.getRight_CO().getValue();
//        String PAI = viewModel.getRight_PAI().getValue();
//        String PO = viewModel.getRight_PO().getValue();
//        String SoC = String.valueOf(viewModel.getright_charge_persent().getValue());

        Log.i(tag,"sendTriggerMeterValues : "+connectorId+","+transactionId+","+amount+","+zonedDateTime+","+EAII+","+CI+","+CO+","+PAI+","+PO+","+TEM+","+SoC);
        Logger.i("sendTriggerMeterValues : connectorId - "+connectorId+", transactionId - "+transactionId+", amount - "+amount+", zonedDateTime - "+zonedDateTime+", EAII- "+EAII+", CI- "+CI+",CO - "+CO+", PAI - "+PAI+", PO - "+PO+", TEM - "+TEM+", SoC - "+SoC);

        String[] Data;

        Data = ConfigurationControl.getMeterValuesSampledData().split(",");


        SampledValue[] svs = new SampledValue[Data.length];

        // 배열을 미리 쪼개놓고 불러오는식으로 해야할듯..?

        for(int i = 0; i < Data.length ; i++){
            SampledValue sv = new SampledValue("0");
            sv.setContext("Trigger");

            switch (Data[i]){
                case MeterValueMeasurandModel.EAIR:           //-"활성 전기 에너지"(Wh 또는 kWh) 레지스터에서 읽은 수치 값 , (그리드 공급으로부터) 수입된 에너지를 측정하는 전기 계량기        - 총 누적
                    sv.setMeasurand(MeterValueMeasurandModel.EAIR);
                    sv.setValue(amount);
                    sv.setUnit("kWh");
                    break;
                case MeterValueMeasurandModel.EAII:           //- 한 동안 (그리드 공급으로부터) 수입된 "활성 전기 에너지"(Wh 또는 kWh)의 절대량                                           - 기간 부분 누적
                    sv.setMeasurand(MeterValueMeasurandModel.EAII);
                    sv.setValue(EAII);
                    sv.setUnit("kWh");
                    break;
                case MeterValueMeasurandModel.CI:                          //-EV에 순간적인 전류 흐름                   - 순간 전류
                    sv.setMeasurand(MeterValueMeasurandModel.CI);
                    sv.setValue(CI);
                    sv.setUnit("A");
                    break;
                case MeterValueMeasurandModel.CO:                        // -EV에 제공되는 최대 전류                   - 최대전류
                    sv.setMeasurand(MeterValueMeasurandModel.CO);
                    sv.setValue(CO);
                    sv.setUnit("A");
                    break;
//                case MeterValueMeasurandModel.PAI:                     // - EV에서 가져온 순간 유효 전력. (W 또는 kW)   - 순간 전력
//                    sv.setMeasurand(MeterValueMeasurandModel.PAI);
//                    sv.setValue(PAI);
//                    sv.setUnit("kW");
//                    break;
                case MeterValueMeasurandModel.PO:                          // -EV에 제공되는 최대 전력                   - 최대전력
                    sv.setMeasurand(MeterValueMeasurandModel.PO);
                    sv.setValue(PO);
                    sv.setUnit("kW");
                    break;
                case MeterValueMeasurandModel.SoC:                                     //-충전 차량의 충전 상태(%)
                    sv.setMeasurand(MeterValueMeasurandModel.SoC);
                    sv.setValue(SoC);
                    sv.setUnit("Percent");
                    break;

                case MeterValueMeasurandModel.TEM:                                     //-온도 (섭씨)
                    sv.setMeasurand(MeterValueMeasurandModel.TEM);
                    sv.setValue(TEM);
                    sv.setUnit("Celsius");
                    break;


            }


            svs[i] = sv;
        }


        MeterValue mv = new MeterValue(zonedDateTime, svs);
        MeterValue[] mvs = new MeterValue[]{mv};
        MeterValuesRequest request = new MeterValuesRequest(connectorId);
        request.setTransactionId(transactionId);
        request.setMeterValue(mvs);



        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {
            Log.i(tag,"MeterValues receive : "+s);
            Logger.i("MeterValues receive : "+s);

            if(ex!=null){
                Log.e(tag,"MeterValues receive : "+ex);
                Logger.e("MeterValues receive : "+ex);
            }
            // 에러시에 metervalue 값을 따로 저장해서 다음차례때 보내기..?
        });

    }



//    public enum ChargePointErrorCode {
//        ConnectorLockFailure,
//        EVCommunicationError,
//        GroundFailure,
//        HighTemperature,
//        InternalError,
//        LocalListConflict,
//        NoError,
//        OtherError,
//        OverCurrentFailure,
//        OverVoltage,
//        PowerMeterFailure,
//        PowerSwitchFailure,
//        ReaderFailure,
//        ResetFailure,
//        UnderVoltage,
//        WeakSignal
//    }

//    public enum ChargePointStatus {
//        Available,
//        Preparing,
//        Charging,
//        SuspendedEVSE,
//        SuspendedEV,
//        Finishing,
//        Reserved,
//        Unavailable,
//        Faulted
//    }



    // 권한 부여와 충전 시작,끝 사이에 유효성 충돌이 일어날때도 보내 오류를 알려야함(19p)
    //이전 OCPP 버전에 정의된 점유 상태 는 더 이상 관련이 없습니다. Occupied 상태 는 준비, 충전 중, SuspendedEV,SuspendedEVSE 및 완료 라는 5가지 새로운 상태로 나뉩니다 (43p)(82p)
    //전환 횟수를 제한하기 위해 충전 포인트는 선택적 구성 키인 MinimalStatusDuration에 정의된 시간보다 짧은 시간 동안 활성화된 경우 StatusNotification.req 전송을 생략할 수 있습니다 . 이런 방식으로 청구 지점은 특정 StatusNotification.req PDU를 보내지 않도록 선택할 수 있습니다 .(47p)

    public void sendStatusNotification(int connectorId,ChargePointStatus status,ChargePointErrorCode errorCode,String info,String vendorId,String vendorErrorcode) throws Exception {

//        MainViewModel viewModel = new MainViewModel();
//        ChargePointStatus status = viewModel.getChargePoint_Status().getValue();
//        ChargePointErrorCode errorCode = viewModel.getChargePoint_ErrorCode().getValue();
//        String info = viewModel.getStatus_info().getValue();
//        String vendorId = viewModel.getStatus_vendorid().getValue();
//        String vendorErrorcode = viewModel.getStatus_vendorerrorcode().getValue();
//
//        int connectorId = DeviceModel.getConnectId();

        ZonedDateTime time = returnZonedDateTime();

        if(client.isClosed()){
            if(sqlite.SelectNotificationFactoryBool(String.valueOf(connectorId))){      // 기존에 있으면
                sqlite.DeleteNotificationFactory(String.valueOf(connectorId));          // 삭제
            }

            sqlite.InsertUnknownNotificationFactory(String.valueOf(connectorId),String.valueOf(errorCode),String.valueOf(status),info,String.valueOf(time),vendorId,vendorErrorcode); // 삽입

            return;
        }

        Log.i(tag,"sendStatusNotification : "+ connectorId + " " + errorCode+ " "+ status + " "+info+ " "+ time + " "+ vendorId + " "+ vendorErrorcode);
        Logger.i("sendStatusNotification : connectorId-"+ connectorId + " errorCode-" + errorCode+ " status-"+ status + " info-"+info+ " time-"+ time + " vendorId-"+ vendorId + " vendorErrorcode-"+ vendorErrorcode);

        StatusNotificationRequest request = new StatusNotificationRequest(connectorId,errorCode,status);
        request.setTimestamp(time);
        if(info !=null){
            request.setInfo(info);
        }

        if(vendorId != null){
            request.setVendorId(vendorId);
            request.setVendorErrorCode(vendorErrorcode);
        }



        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {
            Log.i(tag,"StatusNotification receive : "+s);
            Logger.i("StatusNotification receive : "+s);

            if(ex != null){         // 에러시
                Log.e(tag,"StatusNotification receive : "+ex);
                Logger.e("StatusNotification receive : "+ex);
                if(sqlite.SelectNotificationFactoryBool(String.valueOf(connectorId))){      // 기존에 있으면
                    sqlite.DeleteNotificationFactory(String.valueOf(connectorId));          // 삭제
                }

                sqlite.InsertUnknownNotificationFactory(String.valueOf(connectorId),String.valueOf(errorCode),String.valueOf(status),info,String.valueOf(time),vendorId,vendorErrorcode); // 삽입
            }


            sqlite.DeleteNotificationFactory(String.valueOf(connectorId));          // 삭제

        });
    }



    public void sendUnknownStatusNotification() throws Exception {

        NotificationFactoryModel factoryModel = sqlite.SelectNotificationFactory();

        if(factoryModel.getStatus() == null){
            return;
        }

        ChargePointStatus status = factoryModel.getStatus();
        ChargePointErrorCode errorCode = factoryModel.getErrorCode();
        String info = factoryModel.getInfo();
        String vendorId = factoryModel.getVendorId();
        String vendorErrorcode = factoryModel.getVendorErrorcode();

        int connectorId = factoryModel.getCId();

        ZonedDateTime time = factoryModel.getTime();

        if(client.isClosed()){
            if(sqlite.SelectNotificationFactoryBool(String.valueOf(connectorId))){      // 기존에 있으면
                sqlite.DeleteNotificationFactory(String.valueOf(connectorId));          // 삭제
            }

            sqlite.InsertUnknownNotificationFactory(String.valueOf(connectorId),String.valueOf(errorCode),String.valueOf(status),info,String.valueOf(time),vendorId,vendorErrorcode); // 삽입
            Log.i(tag,"sendUnknownStatusNotification : "+ connectorId + " " + errorCode+ " "+ status + " "+info+ " "+ time + " "+ vendorId + " "+ vendorErrorcode);
            Logger.i("sendUnknownStatusNotification : connectorId-"+ connectorId + " errorCode-" + errorCode+ " status-"+ status + " info-"+info+ " time-"+ time + " vendorId-"+ vendorId + " vendorErrorcode-"+ vendorErrorcode);

            return;
        }




        StatusNotificationRequest request = new StatusNotificationRequest(connectorId,errorCode,status);
        request.setTimestamp(time);
        if(info !=null){
            request.setInfo(info);
        }

        if(vendorId != null){
            request.setVendorId(vendorId);
            request.setVendorErrorCode(vendorErrorcode);
        }



        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> {
            Log.i(tag,"UnknownStatusNotification receive : "+s);
            Logger.i("UnknownStatusNotification receive : "+s);

            if(ex != null){         // 에러시
                Log.e(tag,"UnknownStatusNotification receive : "+ex);
                Logger.e("UnknownStatusNotification receive : "+ex);
                if(sqlite.SelectNotificationFactoryBool(String.valueOf(connectorId))){      // 기존에 있으면
                    sqlite.DeleteNotificationFactory(String.valueOf(connectorId));          // 삭제
                }

                sqlite.InsertUnknownNotificationFactory(String.valueOf(connectorId),String.valueOf(errorCode),String.valueOf(status),info,String.valueOf(time),vendorId,vendorErrorcode); // 삽입
            }


            sqlite.DeleteNotificationFactory(String.valueOf(connectorId));          // 삭제

        });
    }




    public boolean isClosed(){
        return client.isClosed();
    }


    public void disconnect() {
        if(!client.isClosed()){
            Stop_hb_thread();
            client.disconnect();
        }else{
            Log.e("connect err","ConnectClient is null");
            Logger.e("connect err ConnectClient is null");
        }

    }



    public void sendBootAccept(){       // trigger message
        Intent intent = new Intent(ReceiverModel.BootNotification_Trigger);

        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
    }

    public void Start_trigger_thread(TriggerMessageRequestType type){

        triggerThread = new Thread(() -> triggerHandler.postDelayed(()->{

            Intent intent;

            try {

                switch (type){
                    case BootNotification:
                        sendBootAccept();
                        break;
                    case DiagnosticsStatusNotification:
                        intent = new Intent(ReceiverModel.DiagnosticsStatusNotification_Trigger);
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        break;
                    case FirmwareStatusNotification:
                        intent = new Intent(ReceiverModel.FirmwareStatusNotification_Trigger);
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        break;
                    case Heartbeat:
                        sendHeartBeat();
                        Stop_hb_thread();
                        Start_hb_thread();
                        break;
                    case MeterValues:
//                        sendTriggerMeterValues();
                        intent = new Intent(ReceiverModel.MeterValues_Trigger);
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        break;
                    case StatusNotification:
//                        sendStatusNotification();
                        intent = new Intent(ReceiverModel.StatusNotification_Trigger);
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
                        break;
                    default:
                        break;
                }

                Stop_trigger_thread();
            } catch (Exception e) {
                Logger.e("Start_trigger_thread",e);
                e.printStackTrace();
            }
        },1500));
        triggerThread.start();


    }


    public void Stop_trigger_thread() {
        if (triggerThread != null){
            triggerHandler.removeCallbacksAndMessages(null);
            triggerThread.interrupt();
            triggerThread = null;
        }
    }


//    public void Start_Diagnostics_thread(GetDiagnosticsRequest request){
//
//        DiagnosticsThread = new Thread(() -> DiagnosticsHandler.postDelayed(()->{
//
//
//
//            try {
//
//
//
//                Stop_Diagnostics_thread();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        },1000));
//        DiagnosticsThread.start();
//
//
//    }
//
//
//    public void Stop_Diagnostics_thread() {
//        if (DiagnosticsThread != null){
//            DiagnosticsHandler.removeCallbacksAndMessages(null);
//            DiagnosticsThread.interrupt();
//            DiagnosticsThread = null;
//        }
//    }


    public void Start_hb_thread(){
        int Interval = Integer.parseInt(ConfigurationControl.getHeartBeatInterval());

        if(Interval != 0){
            hb_thread = new Thread(() -> hb_handler.postDelayed(()->{
                try {
//                mClient.connect();
                    sendHeartBeat();
//                mClient.disconnect();

                    Start_hb_thread();
                } catch (Exception e) {
                    Logger.e("Start_hb_thread",e);
                    e.printStackTrace();
                }
            },Interval*1000L));

            hb_thread.start();
        }

    }




    public void Stop_hb_thread() {
        if (hb_thread != null){
            hb_handler.removeCallbacksAndMessages(null);
            hb_thread.interrupt();
            hb_thread = null;
        }
    }



//    public boolean isClosed(){
//        return client.isClosed();
//    }




//    int downlistlenght = 0;
//
//    // 광고 리스트, 광고 다운로드
//    private void AcceptedADList(JSONArray jsonArray){
//
//        String file_path_String = PrefsHelper.ad_path;
//
//        //폴더 없으면 생성
//        if(!new File(file_path_String).exists()){
//            Log.d(tag,"folder make "+ new File(file_path_String).mkdirs());
//        }
//
//        // 해당 경로에 있는 파일 리스트
//        File file_path = new File(file_path_String);
//
//        List<String> f_list = Arrays.asList(file_path.list());
//
//        try{
//            //받아온 광고 리스트 화
//            List<String> ad_list = new ArrayList<>();
//
//            for(int i = 0; i< jsonArray.length(); i++){
//                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                String item = jsonObject1.getString("ADId");
//                Log.d(tag,item);
//                //받아온 리스트 스트링으로 담기
//                ad_list.add(item);
//
//                // 해당 기기에 파일이 있는지 확인
//                boolean r = f_list.contains(item);
//
//                //없으면 다운로드
//                if(!r){
//                    down_AD_file(retrofitadModel.getADid());
//
//                    //다운 리스트 길이
//                    downlistlenght ++;
//
//                }
//            }
//
//            // 그외 다른 파일 삭제
//            extra_file_remove(f_list, ad_list);
//
//            //sharedPreferences 에 저장
//            PrefsHelper.setStringArrayPref(PrefsHelper.ad_url_list_key,ad_list);
//            PrefsHelper.write(PrefsHelper.recent_ad_count_key,0);
//
//
//            //새로운 광고 다운로드 없을때
//            if(downlistlenght == 0){
//                down_setting_com = true;
//
//                // 영상 길이가 이미 끝난 시점에서 다운이 다 됬을때 넘어가기
//                if(video_time_com){
//                    nextActivity();
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//    }

    public boolean create_logfile(String location,ZonedDateTime starttime,ZonedDateTime stoptime) throws Exception {

        Log.d(tag,"starttime : "+starttime +" stoptime : "+ stoptime);

        if(starttime != null){
            starttime = starttime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        }
        if(stoptime != null){
            stoptime = stoptime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        }

        LogFile logFile = new LogFile();
        boolean result = logFile.writeFile(logFile.loadLogFile(starttime,stoptime));

        if(result){
            sendDiagnosticsStatusNotification(DiagnosticsStatus.Uploading);
            upload_logfile(location,logFile,starttime,stoptime);
        }

        return result;
    }

    public void upload_logfile(String location,LogFile logFile,ZonedDateTime starttime,ZonedDateTime stoptime) {

        File file = new File(PrefsHelper.log_path,"Log.txt");

//        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody fileBody = RequestBody.create(file,MediaType.parse("multipart/form-data"));

        String filename = KeyControl.getDeviceId()+"&&"+ DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format( starttime)+"&&"+DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format( stoptime)+".txt";

        Log.i("filename : ", filename);
        // RequestBody로 MultipartBody.Part 객체 생성                            // (서버에서 확인할 파일 키, 파일 이름, RequestBody)
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("upload_log", filename, fileBody);

//        Retrofit_Interface s_retrofitService = Retrofit_Factory.create();

        Log.i(tag,"location : "+ location);
//        Retrofit_Interface_Log s_retrofitService = Retrofit_Factory_Log.create();

//        HashMap<String,String> input = new HashMap<>();
//        input.put("s_time", String.valueOf(starttime));
//        input.put("e_time", String.valueOf(stoptime));

//        RequestBody s_time = RequestBody.create(String.valueOf(starttime),MediaType.parse("text/plain"));
//        RequestBody e_time = RequestBody.create(String.valueOf(stoptime),MediaType.parse("text/plain"));

        s_retrofitService.UploadLogFile(location,filePart).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("logfile", String.valueOf(response.body()));
                Log.i("logfile upload sucess","logfile delete : "+logFile.fileDelete());

                DiagnosticsRetries = 0;
                DiagnosticsRetriesInterval = 0;

                try {
                    sendDiagnosticsStatusNotification(DiagnosticsStatus.Uploaded);
                } catch (Exception e) {
                    Logger.e("upload_logfile",e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("logfile upload file", String.valueOf(t));


                try {
                    sendDiagnosticsStatusNotification(DiagnosticsStatus.UploadFailed);
                } catch (Exception e) {
                    Logger.e("upload_logfile",e);
                    e.printStackTrace();
                }

                if(DiagnosticsRetries != 0){

                    try {
                        Thread.sleep(DiagnosticsRetriesInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    upload_logfile(location,logFile,starttime,stoptime);
                    DiagnosticsRetries --;
                }

            }
        });
    }


//    private void Start_update_Timer(ZonedDateTime retrieveTime,String Location,String downloadedfilename){

    public void setReconnectAbled(boolean bool){
        reconnectAbled = bool;
    }


    private Boolean returnMeterParamsBool(String msg){

        try {
            String[] Data = msg.split(",");
            for (int i = 0; i < Data.length; i++) {
                if (!(Data[i].equals(MeterValueMeasurandModel.CI) || Data[i].equals(MeterValueMeasurandModel.CO) || Data[i].equals(MeterValueMeasurandModel.EAII) || Data[i].equals(MeterValueMeasurandModel.EAIR) || Data[i].equals(MeterValueMeasurandModel.PAI) || Data[i].equals(MeterValueMeasurandModel.PO) || Data[i].equals(MeterValueMeasurandModel.TEM) || Data[i].equals(MeterValueMeasurandModel.SoC))) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }

        return false;
    }

    // 받아온 data 셋값을 db컬럼에 맞추어 반환
    private String[] returnColum(String StopTxnData){
        String[] Colum = StopTxnData.split(",");
//        EAIR text,EAII text, CI text, CO text, PAI text, PO text, SoC text, Idtag text, Type text
        for(int i = 0; i< Colum.length; i++){
            switch (Colum[i]){
                case MeterValueMeasurandModel.EAIR:
                    Colum[i] = "EAIR";
                    break;
                case MeterValueMeasurandModel.EAII:
                    Colum[i] = "EAII";
                    break;
                case MeterValueMeasurandModel.CI:
                    Colum[i] = "CI";
                    break;
                case MeterValueMeasurandModel.CO:
                    Colum[i] = "CO";
                    break;
//                case MeterValueMeasurandModel.PAI:
//                    Colum[i] = "PAI";
//                    break;
                case MeterValueMeasurandModel.PO:
                    Colum[i] = "PO";
                    break;
                case MeterValueMeasurandModel.SoC:
                    Colum[i] = "SoC";
                    break;
                case MeterValueMeasurandModel.TEM:
                    Colum[i] = "TEM";
                    break;
            }
        }

        // Metervalue로 만들때 저장된 time 도 가져와야 하기때문에 요청받은 colum 에 time을 껴줌
        ArrayList<String> list = new ArrayList<>(Arrays.asList(Colum));
        list.add(0,"Time");


        return  list.toArray(new String[list.size()]);
    }

//    private void sendStopTransactionLocalBroadcast(int transactionid, Reason reason){
//        Intent intent = new Intent("StopTransactionReceiver");
//        intent.putExtra("StopIdtag",transactionid);
//        intent.putExtra("StopReason",reason);
//        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
//    }

    private KeyValueType GetConfigurationFuction(String key){
        KeyValueType keyValueType = null;

        switch (key) {
            case ConfigurationModel.AllowOfflineTxForUnknownId:
                keyValueType = new KeyValueType(key,true);
                keyValueType.setValue(String.valueOf(ConfigurationControl.getAllowOfflineTxForUnknownId()));
                break;
            case ConfigurationModel.AuthorizationCacheEnabled:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(String.valueOf(ConfigurationControl.getAuthorizationCacheEnabled()));
                break;
            case ConfigurationModel.AuthorizeRemoteTxRequests:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(String.valueOf(ConfigurationControl.getAuthorizeRemoteTxRequests()));
                break;
            case ConfigurationModel.ClockAlignedDataInterval:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getClockAlignedDataInterval());
                break;
            case ConfigurationModel.ConnectionTimeOut:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getConnectionTimeOut());
                break;
            case ConfigurationModel.ConnectorPhaseRotation:
                keyValueType = new KeyValueType(key,true);
                keyValueType.setValue(ConfigurationControl.getConnectorPhaseRotation());
                break;
            case ConfigurationModel.GetConfigurationMaxKeys:
                keyValueType = new KeyValueType(key,true);
                keyValueType.setValue(ConfigurationControl.getGetConfigurationMaxKeys());
                break;
            case ConfigurationModel.HeartbeatInterval:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getHeartBeatInterval());
                break;
            case ConfigurationModel.LocalAuthorizeOffline:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(String.valueOf(ConfigurationControl.getLocalAuthorizeOffline()));
                break;
            case ConfigurationModel.LocalPreAuthorize:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(String.valueOf(ConfigurationControl.getLocalPreAuthorize()));
                break;
            case ConfigurationModel.MeterValuesAlignedData:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getMeterValuesAlignedData());
                break;
            case ConfigurationModel.MeterValuesSampledData:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getMeterValuesSampledData());
                break;
            case ConfigurationModel.MeterValueSampleInterval:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getMeterValueSampleInterval());
                break;
            case ConfigurationModel.NumberOfConnectors:
                keyValueType = new KeyValueType(key,true);
                keyValueType.setValue(ConfigurationControl.getNumberOfConnectors());
                break;
            case ConfigurationModel.ResetRetries:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getResetRetries());
                break;
            case ConfigurationModel.StopTransactionOnEVSideDisconnect:                  // 충전기 연동되면
                keyValueType = new KeyValueType(key,true);
                keyValueType.setValue(String.valueOf(ConfigurationControl.getStopTransactionOnEVSideDisconnect()));
                break;
            case ConfigurationModel.StopTransactionOnInvalidId:
                keyValueType = new KeyValueType(key,true);
                keyValueType.setValue(String.valueOf(ConfigurationControl.getStopTransactionOnInvalidId()));
                break;
            case ConfigurationModel.StopTxnAlignedData:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getStopTxnAlignedData());
                break;
            case ConfigurationModel.StopTxnSampledData:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getStopTxnSampledData());
                break;
            case ConfigurationModel.SupportedFeatureProfiles:
                keyValueType = new KeyValueType(key,true);
                keyValueType.setValue("Core,FirmwareManagement");
                break;
            case ConfigurationModel.TransactionMessageAttempts:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getTransactionMessageAttempts());
                break;
            case ConfigurationModel.TransactionMessageRetryInterval:    //-- 중앙 시스템이 실패한 거래 관련 메시지를 다시 제출하기 전에 충전 지점이 기다려야 하는 시간
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getTransactionMessageRetryInterval());
                break;
            case ConfigurationModel.UnlockConnectorOnEVSideDisconnect:                // 충전기 연동되면
                keyValueType = new KeyValueType(key,true);
                keyValueType.setValue(String.valueOf(ConfigurationControl.getUnlockConnectorOnEVSideDisconnect()));
                break;
            case ConfigurationModel.WebSocketPingInterval:
                keyValueType = new KeyValueType(key,false);
                keyValueType.setValue(ConfigurationControl.getWebSocketPingInterval());
                break;

            default:
//                valuselist.add(request.getKey()[i]);
                return null;
        }

        return keyValueType;
    }


    private ConfigurationStatus ChangeConfigurationFunction(ChangeConfigurationRequest request){
        Log.i(tag, String.valueOf(request));
        Logger.i("ChangeConfigurationFunction");

        try {
            switch (request.getKey()) {
                case ConfigurationModel.AllowOfflineTxForUnknownId:
                    ConfigurationControl.setAllowOfflineTxForUnknownId(Boolean.parseBoolean(request.getValue()));
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.AuthorizationCacheEnabled:
                    ConfigurationControl.setAuthorizationCacheEnabled(Boolean.parseBoolean(request.getValue()));
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.AuthorizeRemoteTxRequests:
                    ConfigurationControl.setAuthorizeRemoteTxRequests(Boolean.parseBoolean(request.getValue()));
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.ClockAlignedDataInterval:
                    ConfigurationControl.setClockAlignedDataInterval(request.getValue());
//                            Intent ClockAlignedDataIntervalintent = new Intent("ClockAlignedDataIntervalChange");       // 만약 충전중이라면 재시작
//                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(ClockAlignedDataIntervalintent);
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.ConnectionTimeOut:
                    ConfigurationControl.setConnectionTimeOut(request.getValue());
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.HeartbeatInterval:
                    ConfigurationControl.setHeartBeatInterval(request.getValue());
                    Stop_hb_thread();
                    Start_hb_thread();
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.LocalAuthorizeOffline:
                    ConfigurationControl.setLocalAuthorizeOffline(Boolean.parseBoolean(request.getValue()));
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.LocalPreAuthorize:
                    ConfigurationControl.setLocalPreAuthorize(Boolean.parseBoolean(request.getValue()));
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.MeterValuesAlignedData:                     // 항목을 추가할때 기존 항목에 중복성을 검사해서 넣을건지 ...?
                    if (returnMeterParamsBool(request.getValue())) {          // 지원되는 항목에 없을경우
                        return ConfigurationStatus.Rejected;
                    } else {
                        ConfigurationControl.setMeterValuesAlignedData(request.getValue());
                        return ConfigurationStatus.Accepted;
                    }
                case ConfigurationModel.MeterValuesSampledData:                     // 항목을 추가할때 기존 항목에 중복성을 검사해서 넣을건지 ...?
                    if (returnMeterParamsBool(request.getValue())) {          // 지원되는 항목에 없을경우
                        return ConfigurationStatus.Rejected;
                    } else {
                        ConfigurationControl.setMeterValuesSampledData(request.getValue());
                        return ConfigurationStatus.Accepted;
                    }
                case ConfigurationModel.MeterValueSampleInterval:
                    ConfigurationControl.setMeterValueSampleInterval(request.getValue());
//                            Intent MeterValueSampleIntervalintent = new Intent("MeterValueSampleIntervalChange");       // 만약 충전중이라면 재시작
//                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(MeterValueSampleIntervalintent);
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.ResetRetries:
                    ConfigurationControl.setResetRetries(request.getValue());
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.StopTransactionOnEVSideDisconnect:
                    return ConfigurationStatus.NotSupported;
                case ConfigurationModel.StopTransactionOnInvalidId:
                    ConfigurationControl.setStopTransactionOnInvalidId(Boolean.parseBoolean(request.getValue()));
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.StopTxnAlignedData:
                    // 지원하는 유형인지
                    String[] exceptA = request.getValue().split(",");

                    for (String s : exceptA) {
                        if (!Arrays.asList(MeterValueMeasurandModel.List).contains(s)) {
                            return ConfigurationStatus.NotSupported;
                        }
                    }

                    ConfigurationControl.setStopTxnAlignedData(request.getValue());
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.StopTxnSampledData:
                    // 지원하는 유형인지
                    String[] exceptS = request.getValue().split(",");

                    for (String s : exceptS) {
                        if (!Arrays.asList(MeterValueMeasurandModel.List).contains(s)) {
                            return ConfigurationStatus.NotSupported;
                        }
                    }

                    ConfigurationControl.setStopTxnSampledData(request.getValue());
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.TransactionMessageAttempts:
                    ConfigurationControl.setTransactionMessageAttempts(request.getValue());
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.TransactionMessageRetryInterval:
                    ConfigurationControl.setTransactionMessageRetryInterval(request.getValue());
                    return ConfigurationStatus.Accepted;
                case ConfigurationModel.UnlockConnectorOnEVSideDisconnect:
                    return ConfigurationStatus.NotSupported;
                case ConfigurationModel.WebSocketPingInterval:
                    ConfigurationControl.setWebSocketPingInterval(request.getValue());

//                            client.disconnect();
//                            client = null;
//                        connect();
                    //핑설정이후 소프트웨어 끄고 다시 시작하기
                    Intent WebSocketPingIntervalintent = new Intent(ReceiverModel.Receive_Reset);       // 만약 충전중이라면 해제하고 진행
                    WebSocketPingIntervalintent.putExtra("resetType", ResetType.Soft);
                    LocalBroadcastManager.getInstance(mcontext).sendBroadcast(WebSocketPingIntervalintent);

//                            Intent intent = new Intent("mClient_Setting_Receive");
//                            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);

                    return ConfigurationStatus.Accepted;

                default:
                    return ConfigurationStatus.NotSupported;
            }
        }catch (NullPointerException ne){
            return ConfigurationStatus.NotSupported;
        } catch (Exception e){       // 설정에 문제가 있을 경우
            e.printStackTrace();
            return ConfigurationStatus.Rejected;
        }



    }

    private MeterValue returnSampledValues(String[] Data,Reason reason,String amount,String EAII,String CI, String CO, String PAI, String PO, String SoC, String TEM){

        SampledValue[] svs = new SampledValue[Data.length];

        for(int i = 0; i < Data.length ; i++){
            SampledValue sv = new SampledValue("0");

            if(reason.equals(Reason.Local)){        // 멈추는 이유가 로컬이면
                sv.setContext("Transaction.End");       // 충전 완료 멈춤
            }else{                                  // 로컬이 아니면
                sv.setContext("Interruption.Begin");    // 충전 중단
            }

            switch (Data[i]){
                case MeterValueMeasurandModel.EAIR:           //-"활성 전기 에너지"(Wh 또는 kWh) 레지스터에서 읽은 수치 값 , (그리드 공급으로부터) 수입된 에너지를 측정하는 전기 계량기        - 총 누적
                    sv.setMeasurand(MeterValueMeasurandModel.EAIR);
                    sv.setValue(amount);
                    sv.setUnit("kWh");
                    break;
                case MeterValueMeasurandModel.EAII:           //- 한 동안 (그리드 공급으로부터) 수입된 "활성 전기 에너지"(Wh 또는 kWh)의 절대량                                           - 기간 부분 누적
                    sv.setMeasurand(MeterValueMeasurandModel.EAII);
                    sv.setValue(EAII);
                    sv.setUnit("kWh");
                    break;
                case MeterValueMeasurandModel.CI:                          //-EV에 순간적인 전류 흐름                   - 순간 전류
                    sv.setMeasurand(MeterValueMeasurandModel.CI);
                    sv.setValue(CI);
                    sv.setUnit("A");
                    break;
                case MeterValueMeasurandModel.CO:                        // -EV에 제공되는 최대 전류                   - 최대전류
                    sv.setMeasurand(MeterValueMeasurandModel.CO);
                    sv.setValue(CO);
                    sv.setUnit("A");
                    break;
//                case MeterValueMeasurandModel.PAI:                     // - EV에서 가져온 순간 유효 전력. (W 또는 kW)   - 순간 전력
//                    sv.setMeasurand(MeterValueMeasurandModel.PAI);
//                    sv.setValue(PAI);
//                    sv.setUnit("kW");
//                    break;
                case MeterValueMeasurandModel.PO:                          // -EV에 제공되는 최대 전력                   - 최대전력
                    sv.setMeasurand(MeterValueMeasurandModel.PO);
                    sv.setValue(PO);
                    sv.setUnit("kW");
                    break;
                case MeterValueMeasurandModel.SoC:                                     //-충전 차량의 충전 상태(%)
                    sv.setMeasurand(MeterValueMeasurandModel.SoC);
                    sv.setValue(SoC);
                    sv.setUnit("Percent");
                    break;
                case MeterValueMeasurandModel.TEM:                                     //-충전포인트 온도(섭씨)
                    sv.setMeasurand(MeterValueMeasurandModel.TEM);
                    sv.setValue(TEM);
                    sv.setUnit("Celsius");
                    break;

            }


            svs[i] = sv;
        }


        return new MeterValue(returnZonedDateTime(), svs);
    }


    private ZonedDateTime returnZonedDateTime(){
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }








}