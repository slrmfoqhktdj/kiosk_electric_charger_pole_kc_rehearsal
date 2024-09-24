package com.example.kiosk_electric_charger_pole_kc_rehearsal.retrofit;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.DeviceModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Retrofit_Factory_Meteorological {
    private static final String Meteorological_BASE_URL = DeviceModel.getMeteorological_BASE_URL();

    private static final OkHttpClient clientBuilder = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();


    public static Retrofit_Interface_Meteorological create() {

//        Retrofit retrofit;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        return new Retrofit.Builder()
                .baseUrl(Meteorological_BASE_URL)
                .client(clientBuilder)
                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

                .create(Retrofit_Interface_Meteorological.class);

    }
}
