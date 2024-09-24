package com.example.kiosk_electric_charger_pole_kc_rehearsal.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.R;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.MainViewModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.ReceiverModel;

import eu.chargetime.ocpp.model.core.Reason;

public class ChargeInfoFragment extends Fragment {

    private String Tag = "ChargeInfoFragment";

    ImageButton charge_pause_btn;
    
    TextView charge_persent_tv,charge_amount_tv;

    ConstraintLayout root_container;

    MainViewModel veiwModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Tag,"onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(Tag,"onCreateView");

        View view = inflater.inflate(R.layout.fragment_charge_info, container, false);

        root_container = view.findViewById(R.id.charge_info_container);
        
        charge_persent_tv = view.findViewById(R.id.charge_persent_tv);      //충전 퍼센트

        
        charge_amount_tv = view.findViewById(R.id.charge_amount_tv);    // 충전량

        charge_pause_btn = view.findViewById(R.id.charge_pause_btn);    //충전 중지
        charge_pause_btn.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiverModel.SerialRightChargerStopCommand);
            intent.putExtra("reason", Reason.Local );
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);

        });



        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        veiwModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        //livedata 바뀔시에 이벤트
        final Observer<Integer> charge_per = b -> {
            if(b==120){
                charge_persent_tv.setText("X");
            }else{
                charge_persent_tv.setText(b+"%");
            }

        };



        final Observer<Float> charge_amount = b -> {
            charge_amount_tv.setText(String.format("%.2f",b));
        };




        veiwModel.getright_charge_persent().observe(getActivity(),charge_per);
        veiwModel.getright_charge_amount().observe(getActivity(),charge_amount);






    }


}