package com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.model.Retrofit_PayModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit.model.Retrofit_VersionModel;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Retrofit_Interface {


    /// 결제

    // 전기 시세 받아오기
    @POST("api/pay")
    Call<Retrofit_PayModel> getPay(
//            @Query("Pay") int pay
    );


    /// 업데이트

    //버전 확인
    @POST("api/apk/version")
    Call<Retrofit_VersionModel> getApkVersion();

    //apk 파일 다운
//    @FormUrlEncoded
    @GET("{path}")
//    Call<ResponseBody> downApk(@Path("path") String path,@FieldMap HashMap<String,String> param);
    Call<ResponseBody> downApk(@Path("path") String path);

    //앱 업데이트 확인
    @FormUrlEncoded
    @POST("file/apk/consequnce")
    Call<ResponseBody> updateComplete(
            @FieldMap HashMap<String,String> param
    );

    //결제내역 보내기
    @FormUrlEncoded
    @POST("pay/history")
    Call<ResponseBody> sendPayHistory(
            @FieldMap HashMap<String,String> param
    );

    //충전 시작시간 보내기
    @FormUrlEncoded
    @POST("charging/start")
    Call<ResponseBody> sendCStart(
            @FieldMap HashMap<String,String> param
    );

    //충전 종료시간 보내기
    @FormUrlEncoded
    @POST("charging/end")
    Call<ResponseBody> sendCEnd(
            @FieldMap HashMap<String,String> param
    );


    //GetDiagnosticsRequest 로그파일 업로드

    @Multipart
    @POST("{path}")
    Call<ResponseBody> UploadLogFile(
            @Path("path") String path,
            @Part MultipartBody.Part file
//            @Part("s_time") RequestBody s_time,
//            @Part("e_time") RequestBody e_time

    );


}

