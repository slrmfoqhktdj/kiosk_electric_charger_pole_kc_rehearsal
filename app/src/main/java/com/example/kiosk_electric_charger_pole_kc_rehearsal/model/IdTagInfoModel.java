package com.example.kiosk_electric_charger_pole_kc_rehearsal.model;

import java.time.ZonedDateTime;

import eu.chargetime.ocpp.model.core.AuthorizationStatus;

public class IdTagInfoModel {

    private ZonedDateTime expiryDate;
    private String parentIdTag;
    private AuthorizationStatus status;

    public ZonedDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(ZonedDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getParentIdTag() {
        return parentIdTag;
    }

    public void setParentIdTag(String parentIdTag) {
        this.parentIdTag = parentIdTag;
    }

    public AuthorizationStatus getStatus() {
        return status;
    }

    public void setStatus(AuthorizationStatus status) {
        this.status = status;
    }
}
