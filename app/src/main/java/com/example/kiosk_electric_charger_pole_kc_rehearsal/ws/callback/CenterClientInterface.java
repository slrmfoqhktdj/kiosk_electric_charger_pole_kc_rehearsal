package com.example.kiosk_electric_charger_pole_kc_rehearsal.ws.callback;

import java.time.ZonedDateTime;

import eu.chargetime.ocpp.model.core.AvailabilityType;
import eu.chargetime.ocpp.model.core.Reason;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import eu.chargetime.ocpp.model.firmware.FirmwareStatus;

public interface CenterClientInterface {


    void onGetPay(float timertype_Get_Pay);

    void onGetAuthorize(String itag );

    void onSerialRightChargerStopCommand(String pay_num, Reason reason);

    void onReceive_Reset(ResetType resetType);

    void onGetChageAvailablitity(AvailabilityType T);

    void onServerConn(boolean s_bool);

    void onMeterValues_Trigger();

    void onStatusNotification_Trigger();

    void onSetFirmwareStatus(FirmwareStatus f);

    void onFirmwareStatusNotification_Trigger();

    void onSetDiagnosticsStatus(DiagnosticsStatus d);

    void onDiagnosticsStatusNotification_Trigger();

    void onFirmwareUpdate(int UpdateFirmwareRetries, long UpdateFirmwareRetriesInterval, ZonedDateTime UpdateFirmwareRetrieveDate ,String UpdateFirmwareLocation);


}
