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

import com.example.kiosk_electric_charger_pole_kc_rehearsal.R;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.MainViewModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.ReceiverModel;

public class ChargeSelectFragment extends Fragment {


    private String Tag = "Charge_Select_Fragment";


    ImageButton charge_02_btn;
    ConstraintLayout rootcontainer_right_noncharging,rootcontainer_right_unablecharging;
    MainViewModel veiwModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Tag,"onCreate");

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charge_select, container, false);


        rootcontainer_right_noncharging = view.findViewById(R.id.charge_select_right_notcharge_container);      //오른쪽 충전중 아닐때
        rootcontainer_right_unablecharging = view.findViewById(R.id.charge_select_right_unable_charge_container);    // 오른쪽 사용 중지



        charge_02_btn = view.findViewById(R.id.charge_02);      // 오른쪽 충전하기 버튼



        charge_02_btn.setOnClickListener(v -> {

            Log.d(Tag,"start click");
//            SerialPortClient serialClient = SerialPortClient.getInstance(getActivity());
//            serialClient.Serialport_SendMessage(SerialModel.RightChargerStart);

                // 시작
            Intent intent = new Intent(ReceiverModel.SerialRightChargerStartCommand);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);

                // 개발중
//                Disabled_Device_Dialog a = new Disabled_Device_Dialog(getActivity());
//                a.showDialog();



        });




        return view;
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(Tag,"onViewCreated");


        veiwModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);



        final Observer<Boolean> right_device_charging_enable = b -> {
            if(!b){

                rootcontainer_right_noncharging.setVisibility(View.GONE);
                rootcontainer_right_unablecharging.setVisibility(View.VISIBLE);
            }else{
                rootcontainer_right_noncharging.setVisibility(View.VISIBLE);
                rootcontainer_right_unablecharging.setVisibility(View.GONE);
            }

        };



        //livedata 등록

        veiwModel.getright_device_charging_enable().observe(getActivity(),right_device_charging_enable);






    }
}