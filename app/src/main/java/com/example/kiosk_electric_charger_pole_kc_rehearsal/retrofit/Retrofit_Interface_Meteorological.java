package com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Retrofit_Interface_Meteorological {



    @GET("getUltraSrtNcst")
    @Headers({"Content-Type: application/json"})
    Call<String> GetWhether(
            @Query(value = "serviceKey",encoded = true) String serviceKey,
            @Query("base_date") String base_date,
            @Query("base_time") String base_time,
            @Query("nx") String nx,
            @Query("ny") String ny,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("dataType") String dataType

    );



}

