package com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.model;

import java.time.ZonedDateTime;

import eu.chargetime.ocpp.model.core.Reason;

public class TransactionFactoryModel {
    private ZonedDateTime Time;
    private int TID;

    private Reason Reason;

    private int Meterstop;

    public ZonedDateTime getTime() {
        return Time;
    }

    public void setTime(ZonedDateTime time) {
        Time = time;
    }

    public int getTID() {
        return TID;
    }

    public void setTID(int TID) {
        this.TID = TID;
    }

    public Reason getReason() {
        return Reason;
    }

    public void setReason(Reason reason) {
        Reason = reason;
    }

    public int getMeterstop() {
        return Meterstop;
    }

    public void setMeterstop(int meterstop) {
        Meterstop = meterstop;
    }

}
