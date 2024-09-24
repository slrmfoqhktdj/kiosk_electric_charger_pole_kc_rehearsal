package com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback;

import eu.chargetime.ocpp.model.core.Reason;

public interface SerialClientInterface {

    void onSerialRightChargerState(boolean ChargingEmergency,int ChargeState,boolean Charging,boolean PLCConnection,String ChargeAmount);

    void onSerialRightChargerStart(boolean StartReceive);

    void onSerialRightChargerReset(boolean ResetReceive);

    void onSerialRightChargerStartCommand();

    void onSerialRightChargerStop(boolean StopReceive);

    void onSerialRightChargerFirstStart(int W);

    void onSerialRightChargerFirstStop(Reason Stopreason, int LW);

    void onSerialRightChargerSOC(int soc);

    void onSerialRightChargerDeviceStop();
}
