package com.example.kiosk_electric_charger_pole_kc_rehearsal.sqlite.model;

import java.time.ZonedDateTime;

import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;

public class NotificationFactoryModel {
    //(CId text primary key, ErrorCode text, Status text, Info text)
    private int CId;
    private ChargePointErrorCode ErrorCode;

    private ChargePointStatus Status;

    private String Info;

    private ZonedDateTime Time;

    private String VendorId;
    private String VendorErrorcode;


    public int getCId() {
        return CId;
    }

    public void setCId(int CId) {
        this.CId = CId;
    }

    public ChargePointErrorCode getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(ChargePointErrorCode errorCode) {
        ErrorCode = errorCode;
    }

    public ChargePointStatus getStatus() {
        return Status;
    }

    public void setStatus(ChargePointStatus status) {
        Status = status;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public ZonedDateTime getTime() {
        return Time;
    }

    public void setTime(ZonedDateTime time) {
        Time = time;
    }

    public String getVendorId() {
        return VendorId;
    }

    public void setVendorId(String vendorId) {
        VendorId = vendorId;
    }

    public String getVendorErrorcode() {
        return VendorErrorcode;
    }

    public void setVendorErrorcode(String vendorErrorcode) {
        VendorErrorcode = vendorErrorcode;
    }
}
