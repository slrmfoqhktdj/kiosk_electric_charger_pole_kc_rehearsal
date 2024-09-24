package com.example.kiosk_electric_charger_pole_kc_rehearsal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.R;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.PageAddrModel;

import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;

public class ChargeCompleteFragment extends Fragment {

    private String Tag = "ChargeCompleteFragment";

    ImageButton charge_complete_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Tag,"onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(Tag,"onCreateView");

        View view = inflater.inflate(R.layout.fragment_charge_complete, container, false);


        charge_complete_btn = view.findViewById(R.id.charge_complete_btn);
        charge_complete_btn.setOnClickListener(v -> {
            //페이지이동
            Intent intent = new Intent("Page");
            intent.putExtra("page", PageAddrModel.ChargeSelectFgm);
            intent.putExtra("ChargePointStatus", ChargePointStatus.Available);
            intent.putExtra("ChargePointErrorCode", ChargePointErrorCode.NoError);

            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        });



        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }


}