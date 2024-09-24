package com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences;

import android.util.Log;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.model.KeyModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.AuthorizationCacheModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.IdTagInfoModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONException;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.IdTagInfo;

public class KeyControl {

    public static void setRightChargingEnabled(boolean rightChargingEnabled){PrefsHelper.write(KeyModel.RightChargingEnabled,rightChargingEnabled);}
    public static Boolean getRightChargingEnabled(){return PrefsHelper.read(KeyModel.RightChargingEnabled,true);}




    public static void setAuthorizationCache(List<String> setRightAuthorizationIdtag){PrefsHelper.setStringArrayPref(KeyModel.RightAuthorizationIdtag,setRightAuthorizationIdtag);}
    public static List<String> getAuthorizationCache(){return PrefsHelper.getStringArrayPref(KeyModel.RightAuthorizationIdtag);}


    public static void clearAuthorizationCache(){   // 캐쉬 초기화
        setAuthorizationCache(new ArrayList<>());
    }


    private static List<String> AuthorizationCacheUpdate(List<String> a, String idtag, IdTagInfoModel idTagInfoModel){


        AuthorizationCacheModel acm = new AuthorizationCacheModel();
//        Gson gson = new Gson();

        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                    @Override
                    public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                        out.value(value.toString());
                    }

                    @Override
                    public ZonedDateTime read(JsonReader in) throws IOException {
                        return ZonedDateTime.parse(in.nextString());
                    }
                })
                .enableComplexMapKeySerialization()
                .create();

        acm.setIdTag(idtag);
        acm.setIdTagInfo(idTagInfoModel);


        if(!a.isEmpty()){
            for(int i =0;i<a.size();i++){

                AuthorizationCacheModel acme = gson.fromJson(a.get(i), AuthorizationCacheModel.class);
                if(acme.getIdTag().equals(idtag)){
                    a.remove(i);
                    break;
                }
            }
        }


        a.add(gson.toJson(acm));

        return a;
    }

    private static List<String> removeUnAcceptedAuthorizationCache(List<String> a){

        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                    @Override
                    public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                        out.value(value.toString());
                    }

                    @Override
                    public ZonedDateTime read(JsonReader in) throws IOException {
                        return ZonedDateTime.parse(in.nextString());
                    }
                })
                .enableComplexMapKeySerialization()
                .create();




        for(int i = a.size() -1; i >= 0; i--){          // remove시에 list형식상 자동으로 index가 변해버리기때문에 오류가 날수있어서 역순으로 for를 돌려서 삭제

            AuthorizationCacheModel acme = gson.fromJson(a.get(i), AuthorizationCacheModel.class);
            if(!acme.getIdTagInfo().getStatus().equals(AuthorizationStatus.Accepted)){
                a.remove(i);
            }
        }

        return a;
    }



    public static void addAuthorizationCache(String idtag,IdTagInfo idTagInfo){   // 기존 캐쉬에 더하기
        List<String> a = getAuthorizationCache();

        if(a.size()>5){             // 저장된 캐시 내역이 6개를 넘을 경우
            Log.d("KeyControl", "addAuthorizationCache remove : "+a.get(0));
            
            a = removeUnAcceptedAuthorizationCache(a);      // 승인이 아닌 캐쉬들 다 삭제
            
            if(a.size()>5) {        //다 삭제해도 자리가 안남으면 가장 오래된 것 삭제
                a.remove(0);
            }
        }

        IdTagInfoModel iti = new IdTagInfoModel();
        iti.setExpiryDate(idTagInfo.getExpiryDate());
        iti.setParentIdTag(idTagInfo.getParentIdTag());
        iti.setStatus(idTagInfo.getStatus());



//        AuthorizationData ad = new AuthorizationData(idtag);
//        ad.setIdTagInfo(idTagInfo);
//
//
//        a.add(ad.toString());

//        Log.d("KeyControl", "AuthorizationCacheUpdate : "+AuthorizationCacheUpdate(a,idtag,iti));

        setAuthorizationCache(AuthorizationCacheUpdate(a,idtag,iti));
    }




    public static boolean getAuthorizationCacheBool(String idtag) throws JSONException {   // 기존 캐쉬에서 해당 idtag 찾기
        List<String> a = getAuthorizationCache();
        boolean result = false;

        if(a.isEmpty()){
            return result;
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                    @Override
                    public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                        out.value(value.toString());
                    }

                    @Override
                    public ZonedDateTime read(JsonReader in) throws IOException {
                        return ZonedDateTime.parse(in.nextString());
                    }
                })
                .enableComplexMapKeySerialization()
                .create();

        for(int i = 0; i< a.size(); i ++){


//            Log.d("KeyControl", "getAuthorizationCacheBool "+ i + "번째 : "+ jsonObject.get("idTag"));

            AuthorizationCacheModel ad = gson.fromJson(a.get(i), AuthorizationCacheModel.class);

            Log.d("KeyControl","idtag : "+ad.getIdTag());



//            AuthorizationData ad = new Gson().fromJson(a.get(i),AuthorizationData.class);

            if(!ad.getIdTag().equals(idtag)){           // 같은 idtag를 가진 중에
                Log.d("KeyControl","idtag : "+ad.getIdTag());
                continue;
            }

            if(!ad.getIdTagInfo().getStatus().equals(AuthorizationStatus.Accepted)){          // status 가 Accepted 이고
                Log.d("KeyControl","Status : "+ad.getIdTagInfo().getStatus());
                continue;
            }

            if(Duration.between(ad.getIdTagInfo().getExpiryDate(),ZonedDateTime.now()).getSeconds() <= 3600){     // 두 시간차이가 20초 이하인 경우
                Log.d("KeyControl","Duration : "+Duration.between(ad.getIdTagInfo().getExpiryDate(),ZonedDateTime.now()).getSeconds());
                result = true;
                break;
            }



        }

        return result;
    }

//    private MutableLiveData<Float> payPrice;  // 가격,단가
    public static void setpayPrice(Float setpayPrice){PrefsHelper.write(KeyModel.payPrice,setpayPrice);}
    public static Float getpayPrice(){return PrefsHelper.read(KeyModel.payPrice,0f);}

//    private MutableLiveData<Integer> card_read_state;  // 카드 읽는 상태, 방식
    public static void setcard_read_state(Integer card_read_state){PrefsHelper.write(KeyModel.card_read_state,card_read_state);}
    public static Integer getcard_read_state(){return PrefsHelper.read(KeyModel.card_read_state,0);}
//
//    private MutableLiveData<String> right_pay_number;  //왼쪽 충전기 충전 승인 번호
    public static void setright_pay_number(String right_pay_number){PrefsHelper.write(KeyModel.right_pay_number,right_pay_number);}
    public static String getright_pay_number(){return PrefsHelper.read(KeyModel.right_pay_number,"00");}
//    private  MutableLiveData<Float> right_pre_charge_amount;   // 오른쪽 예상 충전량
    public static void setright_pre_charge_amount(Float right_pre_charge_amount){PrefsHelper.write(KeyModel.right_pre_charge_amount,right_pre_charge_amount);}
    public static Float getright_pre_charge_amount(){return PrefsHelper.read(KeyModel.right_pre_charge_amount,0f);}
//    private  MutableLiveData<Integer> right_payvalue;        // 오른쪽 결제 금액
    public static void setright_payvalue(Integer right_payvalue){PrefsHelper.write(KeyModel.right_payvalue,right_payvalue);}
    public static Integer getright_payvalue(){return PrefsHelper.read(KeyModel.right_payvalue,0);}
//
//    private MutableLiveData<Boolean> right_device_charging;  //오른쪽 충전기 충전 진행 여부
    public static void setright_device_charging(Boolean right_device_charging){PrefsHelper.write(KeyModel.right_device_charging,right_device_charging);}
    public static Boolean getright_device_charging(){return PrefsHelper.read(KeyModel.right_device_charging,false);}
//
//    private MutableLiveData<Integer> right_charge_persent;   //오른쪽 충전 퍼센트
    public static void setright_charge_persent(Integer right_charge_persent){PrefsHelper.write(KeyModel.right_charge_persent,right_charge_persent);}
    public static Integer getright_charge_persent(){return PrefsHelper.read(KeyModel.right_charge_persent,0);}
//    private MutableLiveData<String> right_charge_time;      //오른쪽 충전 시간
    public static void setright_charge_time(String right_charge_time){PrefsHelper.write(KeyModel.right_charge_time,right_charge_time);}
    public static String getright_charge_time(){return PrefsHelper.read(KeyModel.right_charge_time,"00:00:00");}
//    private MutableLiveData<Integer> right_input_pay;         //오른쪽 실 결제 비용
    public static void setright_input_pay(Integer right_input_pay){PrefsHelper.write(KeyModel.right_input_pay,right_input_pay);}
    public static Integer getright_input_pay(){return PrefsHelper.read(KeyModel.right_input_pay,0);}
//    private MutableLiveData<Float> right_charge_amount;    //오른쪽 충전량
    public static void setright_charge_amount(Float right_charge_amount){PrefsHelper.write(KeyModel.right_charge_amount,right_charge_amount);}
    public static Float getright_charge_amount(){return PrefsHelper.read(KeyModel.right_charge_amount,0f);}
//
//    private MutableLiveData<String> right_pay_day;    // 오른쪽 결제일
    public static void setright_pay_day(String right_pay_day){PrefsHelper.write(KeyModel.right_pay_day,right_pay_day);}
    public static String getright_pay_day(){return PrefsHelper.read(KeyModel.right_pay_day,"");}
//    private MutableLiveData<String> right_pay_token;     // 오른쪽 결제 일련번호
    public static void setright_pay_token(String right_pay_token){PrefsHelper.write(KeyModel.right_pay_token,right_pay_token);}
    public static String getright_pay_token(){return PrefsHelper.read(KeyModel.right_pay_token,"");}





    public static void setDeviceId(String s){PrefsHelper.write(KeyModel.DeviceId,s);}
    public static String getDeviceId(){return PrefsHelper.read(KeyModel.DeviceId,"");}

    public static void setDevicevendor(String s){PrefsHelper.write(KeyModel.Devicevendor,s);}
    public static String getDevicevendor(){return PrefsHelper.read(KeyModel.Devicevendor,"");}


    public static void setDevicemodel(String s){PrefsHelper.write(KeyModel.Devicemodel,s);}
    public static String getDevicemodel(){return PrefsHelper.read(KeyModel.Devicemodel,"");}

    public static void setPAYvendor(String s){PrefsHelper.write(KeyModel.PAYvendor,s);}
    public static String getPAYvendor(){return PrefsHelper.read(KeyModel.PAYvendor,"");}

    public static void setWHEATHERvender(String s){PrefsHelper.write(KeyModel.WHEATHERvender,s);}
    public static String getWHEATHERvender(){return PrefsHelper.read(KeyModel.WHEATHERvender,"");}

    public static void setNx(String s){PrefsHelper.write(KeyModel.Nx,s);}
    public static String getNx(){return PrefsHelper.read(KeyModel.Nx,"");}

    public static void setNy(String s){PrefsHelper.write(KeyModel.Ny,s);}
    public static String getNy(){return PrefsHelper.read(KeyModel.Ny,"");}



}
