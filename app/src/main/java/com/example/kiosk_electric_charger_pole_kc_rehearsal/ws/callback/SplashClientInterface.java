package com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback;

import eu.chargetime.ocpp.model.core.ResetType;

public interface SplashClientInterface {
    void onBootNotification(int interval,int bootState);

    void onReset(ResetType resetType);

    void onBootNotification_Trigger();

}
