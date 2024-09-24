package com.example.kiosk_electric_charger_pole_kc_rehearsal.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences.KeyControl;

import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import eu.chargetime.ocpp.model.firmware.FirmwareStatus;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Boolean> ServerConn;    // OCPP 서버와의 연결
    private MutableLiveData<Float> payPrice;  // 가격,단가
    private MutableLiveData<Integer> card_read_state;  // 카드 읽는 상태, 방식

    private MutableLiveData<String> right_pay_number;  //왼쪽 충전기 충전 승인 번호
    private  MutableLiveData<Float> right_pre_charge_amount;   // 오른쪽 예상 충전량
    private  MutableLiveData<Integer> right_payvalue;        // 오른쪽 결제 금액

    private MutableLiveData<Boolean> right_device_charging;  //오른쪽 충전기 충전 진행 여부
    
    private MutableLiveData<Boolean> right_device_charging_enable;  // 서버에서 받아오는 충전기 사용가능 여부

    private MutableLiveData<Integer> right_charge_persent;   //오른쪽 충전 퍼센트
    private MutableLiveData<String> right_charge_time;      //오른쪽 충전 시간

    private MutableLiveData<String> right_charge_pre_time;      //오른쪽 충전 예상 완료 시간

    private MutableLiveData<Integer> right_input_pay;         //오른쪽 실 결제 비용
    private MutableLiveData<Float> right_charge_amount;    //오른쪽 충전량

    private MutableLiveData<String> right_pay_day;    // 오른쪽 결제일
    private MutableLiveData<String> right_pay_token;     // 오른쪽 결제 일련번호

    private MutableLiveData<String> main_title_time_tv;     //시간
    private MutableLiveData<String> main_title_temp_tv ;     //온도
    private MutableLiveData<String> main_title_humidity_tv; //습도
    private MutableLiveData<String> main_title_ws_tv;     //풍속
    private MutableLiveData<String> main_title_wd_tv;     //풍향
    private MutableLiveData<String> main_title_ap_tv;     //대기압
    private MutableLiveData<String> main_title_precipitation_tv ;     //강수량
    private MutableLiveData<String> main_title_voltage_tv;     //발전량

    private MutableLiveData<String> right_EAII;     //한 동안 (그리드 공급으로부터) 수입된 "활성 전기 에너지"(Wh 또는 kWh)의 절대량                                           - 기간 부분 누적
    private MutableLiveData<String> right_CI;     // -EV에 순간적인 전류 흐름                   - 순간 전류
    private MutableLiveData<String> right_CO;     // -EV에 제공되는 최대 전류                   - 최대전류
    private MutableLiveData<String> right_PAI;     // - EV에서 가져온 순간 유효 전력. (W 또는 kW)   - 순간 전력
    private MutableLiveData<String> right_PO;     // -EV에 제공되는 최대 전력                   - 최대전력
    private MutableLiveData<String> right_TEM;     // -충전소 온도


    private MutableLiveData<ChargePointStatus> Status_chargepointstatus;   // 상태
    private MutableLiveData<ChargePointErrorCode>   Status_chargepointerrorcode;   // 에러코드
    private MutableLiveData<String> Status_info;    // 상태 정보
    private MutableLiveData<String> Status_vendorid;    // 공급체 아이디
    private MutableLiveData<String> Status_vendorerrorcode;    // 공급체 에러코드
    
    private MutableLiveData<FirmwareStatus> Status_firmwarestatus;  // 펌웨어 업데이트 현황
    private MutableLiveData<DiagnosticsStatus> Status_diagnosticsstatus;    // 진단 로그 업로드 현황


    public MutableLiveData<Boolean> getServerConn()
    {
        if(ServerConn == null)
            ServerConn = new MutableLiveData<>(false);
        return ServerConn;
    }
    //data binding시 필요
    public MutableLiveData<Float> getPayPrice()
    {
        if(payPrice == null)
            payPrice = new MutableLiveData<>(KeyControl.getpayPrice());
        return payPrice;
    }


    public MutableLiveData<Integer> getCard_read_state()
    {
        if(card_read_state == null)
            card_read_state = new MutableLiveData<>(KeyControl.getcard_read_state());
        return card_read_state;
    }


    public MutableLiveData<Float> getright_pre_charge_amount()
    {
        if(right_pre_charge_amount == null)
            right_pre_charge_amount = new MutableLiveData<>(KeyControl.getright_pre_charge_amount());
        return right_pre_charge_amount;
    }

    public MutableLiveData<Integer> getright_payValue()
    {
        if(right_payvalue == null)
            right_payvalue = new MutableLiveData<>(KeyControl.getright_payvalue());
        return right_payvalue;
    }

    public MutableLiveData<String> getRight_pay_number()
    {
        if(right_pay_number == null)
            right_pay_number = new MutableLiveData<>(KeyControl.getright_pay_number());
        return right_pay_number;
    }

    public MutableLiveData<Boolean> getright_device_charging()
    {
        if(right_device_charging == null)
            right_device_charging = new MutableLiveData<>(KeyControl.getright_device_charging());
        return right_device_charging;
    }

    public MutableLiveData<Boolean> getright_device_charging_enable()
    {
        if(right_device_charging_enable == null)
            right_device_charging_enable = new MutableLiveData<>(KeyControl.getRightChargingEnabled());
        return right_device_charging_enable;
    }

    public MutableLiveData<Integer> getright_charge_persent()
    {
        if(right_charge_persent == null)
            right_charge_persent = new MutableLiveData<>(0);
        return right_charge_persent;
    }

    public MutableLiveData<String> getright_charge_time()
    {
        if(right_charge_time == null)
            right_charge_time = new MutableLiveData<>("00:00:00");
        return right_charge_time;
    }

    public MutableLiveData<String> getright_charge_pre_time()
    {
        if(right_charge_pre_time == null)
            right_charge_pre_time = new MutableLiveData<>("00:00:00");
        return right_charge_pre_time;
    }

    public MutableLiveData<Integer> getright_input_pay()
    {
        if(right_input_pay == null)
            right_input_pay = new MutableLiveData<>(KeyControl.getright_input_pay());
        return right_input_pay;
    }

    public MutableLiveData<Float> getright_charge_amount()
    {
        if(right_charge_amount == null)
            right_charge_amount = new MutableLiveData<>(KeyControl.getright_charge_amount());
        return right_charge_amount;
    }



    public MutableLiveData<String> getRight_pay_day() {
        if(right_pay_day == null)
            right_pay_day = new MutableLiveData<>(KeyControl.getright_pay_day());
        return right_pay_day;
    }

    public MutableLiveData<String> getRight_pay_token() {
        if(right_pay_token == null)
            right_pay_token = new MutableLiveData<>(KeyControl.getright_pay_token());
        return right_pay_token;
    }


    public MutableLiveData<String> getMain_title_time_tv() {
        if(main_title_time_tv == null)
            main_title_time_tv = new MutableLiveData<>("00:00:00");
        return main_title_time_tv;
    }

    public MutableLiveData<String> getMain_title_temp_tv() {
        if(main_title_temp_tv == null)
            main_title_temp_tv = new MutableLiveData<>("측정중");
        return main_title_temp_tv;
    }

    public MutableLiveData<String> getMain_title_humidity_tv() {
        if(main_title_humidity_tv == null)
            main_title_humidity_tv = new MutableLiveData<>("측정중");
        return main_title_humidity_tv;
    }

    public MutableLiveData<String> getMain_title_ws_tv() {
        if(main_title_ws_tv == null)
            main_title_ws_tv = new MutableLiveData<>("측정중");
        return main_title_ws_tv;
    }

    public MutableLiveData<String> getMain_title_wd_tv() {
        if(main_title_wd_tv == null)
            main_title_wd_tv = new MutableLiveData<>("측정중");
        return main_title_wd_tv;
    }

    public MutableLiveData<String> getMain_title_ap_tv() {
        if(main_title_ap_tv == null)
            main_title_ap_tv = new MutableLiveData<>("측정중");
        return main_title_ap_tv;
    }

    public MutableLiveData<String> getMain_title_precipitation_tv() {
        if(main_title_precipitation_tv == null)
            main_title_precipitation_tv = new MutableLiveData<>("측정중");
        return main_title_precipitation_tv;
    }

    public MutableLiveData<String> getMain_title_voltage_tv() {
        if(main_title_voltage_tv == null)
            main_title_voltage_tv = new MutableLiveData<>("측정중");
        return main_title_voltage_tv;
    }

    public MutableLiveData<String> getRight_EAII() {
        if(right_EAII == null)
            right_EAII = new MutableLiveData<>("0");
        return right_EAII;
    }

    public MutableLiveData<String> getRight_CI() {
        if(right_CI == null)
            right_CI = new MutableLiveData<>("0");
        return right_CI;
    }

    public MutableLiveData<String> getRight_CO() {
        if(right_CO == null)
            right_CO = new MutableLiveData<>("0");
        return right_CO;
    }

    public MutableLiveData<String> getRight_PAI() {
        if(right_PAI == null)
            right_PAI = new MutableLiveData<>("0");
        return right_PAI;
    }

    public MutableLiveData<String> getRight_PO() {
        if(right_PO == null)
            right_PO = new MutableLiveData<>("0");
        return right_PO;
    }

    public MutableLiveData<String> getRight_TEM() {
        if(right_TEM == null)
            right_TEM = new MutableLiveData<>("0");
        return right_TEM;
    }

    public MutableLiveData<ChargePointStatus> getChargePoint_Status() {
        if(Status_chargepointstatus == null)
            Status_chargepointstatus = new MutableLiveData<>(ChargePointStatus.Available);
        return Status_chargepointstatus;
    }

    public MutableLiveData<ChargePointErrorCode> getChargePoint_ErrorCode() {
        if(Status_chargepointerrorcode == null)
            Status_chargepointerrorcode = new MutableLiveData<>(ChargePointErrorCode.NoError);
        return Status_chargepointerrorcode;
    }

    public MutableLiveData<String> getStatus_info() {
        if(Status_info == null)
            Status_info = new MutableLiveData<>("");
        return Status_info;
    }

    public MutableLiveData<String> getStatus_vendorid() {
        if(Status_vendorid == null)
            Status_vendorid = new MutableLiveData<>("");
        return Status_vendorid;
    }

    public MutableLiveData<String> getStatus_vendorerrorcode() {
        if(Status_vendorerrorcode == null)
            Status_vendorerrorcode = new MutableLiveData<>("");
        return Status_vendorerrorcode;
    }

    public MutableLiveData<FirmwareStatus> getStatus_firmwarestatus() {
        if(Status_firmwarestatus == null)
            Status_firmwarestatus = new MutableLiveData<>(FirmwareStatus.Idle);
        return Status_firmwarestatus;
    }

    public MutableLiveData<DiagnosticsStatus> getStatus_diagnosticsstatus(){
        if(Status_diagnosticsstatus == null)
            Status_diagnosticsstatus = new MutableLiveData<>(DiagnosticsStatus.Idle);
        return Status_diagnosticsstatus;
    }

    public void setPayPrice(Float payPrice) {
        KeyControl.setpayPrice(payPrice);
        if(this.payPrice == null)
            this.payPrice = new MutableLiveData<>(payPrice);
        this.payPrice.setValue(payPrice);
    }

    public void setCard_read_state(Integer card_read_state) {
        KeyControl.setcard_read_state(card_read_state);
        if(this.card_read_state == null)
            this.card_read_state = new MutableLiveData<>(card_read_state);
        this.card_read_state.setValue(card_read_state);
    }

    public void setRight_pay_number(String right_pay_number) {
        KeyControl.setright_pay_number(right_pay_number);
        if(this.right_pay_number == null)
            this.right_pay_number = new MutableLiveData<>(right_pay_number);

        this.right_pay_number.setValue(right_pay_number);
    }

    public void setRight_pre_charge_amount(Float right_pre_charge_amount) {
        KeyControl.setright_pre_charge_amount(right_pre_charge_amount);
        if(this.right_pre_charge_amount == null)
            this.right_pre_charge_amount = new MutableLiveData<>(right_pre_charge_amount);
        this.right_pre_charge_amount.setValue(right_pre_charge_amount);
    }

    public void setRight_payvalue(Integer right_payvalue) {
        KeyControl.setright_payvalue(right_payvalue);
        if(this.right_payvalue == null)
            this.right_payvalue = new MutableLiveData<>(right_payvalue);
        this.right_payvalue.setValue(right_payvalue);
    }

    public void setRight_device_charging(Boolean right_device_charging) {
        KeyControl.setright_device_charging(right_device_charging);
        if(this.right_device_charging == null)
            this.right_device_charging = new MutableLiveData<>(right_device_charging);
        this.right_device_charging.setValue(right_device_charging);
    }

//    public void setRight_charge_persent(Integer right_charge_persent) {
////        KeyControl.setright_charge_persent(right_charge_persent);
//        if(this.right_charge_persent == null)
//            this.right_charge_persent = new MutableLiveData<>(right_charge_persent);
//        this.right_charge_persent.setValue(right_charge_persent);
//    }

    public void setRight_charge_time(String right_charge_time) {
//        KeyControl.setright_charge_time(right_charge_time);
        if(this.right_charge_time == null)
            this.right_charge_time = new MutableLiveData<>(right_charge_time);
        this.right_charge_time.postValue(right_charge_time);
    }

    public void setRight_input_pay(Integer right_input_pay) {
        KeyControl.setright_input_pay(right_input_pay);
        if(this.right_input_pay == null)
            this.right_input_pay = new MutableLiveData<>(right_input_pay);
        this.right_input_pay.postValue(right_input_pay);
    }

    public void setRight_charge_amount(Float right_charge_amount) {
        KeyControl.setright_charge_amount(right_charge_amount);
        if(this.right_charge_amount == null)
            this.right_charge_amount = new MutableLiveData<>(right_charge_amount);
        this.right_charge_amount.postValue(right_charge_amount);
    }

    public void setRight_pay_day(String right_pay_day) {
        KeyControl.setright_pay_day(right_pay_day);
        if(this.right_pay_day == null)
            this.right_pay_day = new MutableLiveData<>(right_pay_day);
        this.right_pay_day.setValue(right_pay_day);
    }

    public void setRight_pay_token(String right_pay_token) {
        KeyControl.setright_pay_token(right_pay_token);
        if(this.right_pay_token == null)
            this.right_pay_token = new MutableLiveData<>(right_pay_token);
        this.right_pay_token.setValue(right_pay_token);
    }

    public void setResetPay(){
        setRight_pay_token("");       // 결제토큰초기화
        setRight_charge_time("00:00:00");
        setRight_payvalue(0);
        setRight_pay_number("00");
        setRight_pay_day("00");
    }

}
