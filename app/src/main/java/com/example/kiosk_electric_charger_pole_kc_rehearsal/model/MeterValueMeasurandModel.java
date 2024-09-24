package com.example.kiosk_electric_charger_pole_kc_rehearsal.model;

public class MeterValueMeasurandModel {

    //measurand - Default = “Energy.Active.Import.Register”     -- (91p)
    //Current.Export                        -EV로부터 순간 전류 흐름
    //Current.Import                        -EV에 순간적인 전류 흐름
    public final static String CI = "Current.Import";
    //Current.Offered                        -EV에 제공되는 최대 전류
    public final static String CO = "Current.Offered";
    //Energy.Active.Export.Register            -(가장 권위 있는) "활성 전기 에너지"(Wh 또는 kWh) 레지스터에서 읽은 수치 값 ,(그리드에) 내보낸 에너지를 측정하는 전기 계량기.
    //Energy.Active.Import.Register            -(가장 권위 있는) "활성 전기 에너지"(Wh 또는 kWh) 레지스터에서 읽은 수치 값 , (그리드 공급으로부터) 수입된 에너지를 측정하는 전기 계량기
    public final static String EAIR = "Energy.Active.Import.Register";
    //Energy.Reactive.Export.Register         -(대부분의) "무효 전기 에너지"(VARh 또는 kVARh) 레지스터에서 읽은 숫자 값 ,권위 있는) (그리드에) 수출된 에너지를 측정하는 전기 계량기
    //Energy.Reactive.Import.Register         -(대부분의) "무효 전기 에너지"(VARh 또는 kVARh) 레지스터에서 읽은 숫자 값 ,권위 있는) (그리드 공급으로부터) 수입된 에너지를 측정하는 전기 계량기.
    //Energy.Active.Export.Interval         - 관련 시간 동안 그리드로 내보낸 "활성 전기 에너지"(Wh 또는 kWh)의 절대량 ,Metervalues ReadingContext 및 적용 가능한 간격 기간 구성 값으로 지정된 "간격" , "ClockAlignedDataInterval" 및 "MeterValueSampleInterval"의 경우 (초)입니다.
    //Energy.Active.Import.Interval         - 한 동안 (그리드 공급으로부터) 수입된 "활성 전기 에너지"(Wh 또는 kWh)의 절대량 ,Metervalues ReadingContext로 지정된 관련 시간 "간격" 및 적용 가능한 간격 기간 , "ClockAlignedDataInterval" 및 "MeterValueSampleInterval"에 대한 구성 값(초)입니다
    public final static String EAII = "Energy.Active.Import.Interval";
    //Energy.Reactive.Export.Interval         -관련 기간 동안 그리드로 내보낸 "무효 전기 에너지"(VARh 또는 kVARh)의 절대량 ,Metervalues ReadingContext 및 적용 가능한 간격 기간 구성으로 지정된 시간 "간격" , "ClockAlignedDataInterval" 및 "MeterValueSampleInterval"의 값(초)입니다.
    //Energy.Reactive.Import.Interval         -전력 공급 중에 수입된 "무효 전기 에너지"(VARh 또는 kVARh)의 절대량 , Metervalues ReadingContext로 지정된 관련 시간 "간격" 및 적용 가능한 간격 기간 , "ClockAlignedDataInterval" 및 "MeterValueSampleInterval"에 대한 구성 값(초)입니다.
    //             //Frequency                               -전력선 주파수를 즉시 판독합니다. 참고: OCPP 1.6에는 다음에 대한 UnitOfMeasure가 없습니다 ,주파수, 측정량이 있는 SampledValue에 대한 UnitOfMeasure: 주파수는  Hertz입니다.
    //Power.Active.Export                      -EV가 내보내는 순간 유효전력. (W 또는 kW)
    //Power.Active.Import                  -EV에서 가져온 순간 유효 전력. (W 또는 kW
    public final static String PAI = "Power.Active.Import";
    //Power.Factor                         -전체 에너지 흐름의 순간 역률
    //Power.Offered                        -EV에 제공되는 최대 전력
    public final static String PO = "Power.Offered";
    //             //Power.Reactive.Export                   -EV가 수출하는 순간 무효전력. (var 또는 kvar)
    //              //Power.Reactive.Import                   -EV에서 수입한 순간 무효전력. (var 또는 kvar)
    //              //RPM                                      -팬 속도(RPM)
    //SoC                                    -충전 차량의 충전 상태(%)
    public final static String SoC = "SoC";
    public final static String TEM = "Temperature";     //                             -충전 지점 내부의 온도 판독
    //Voltage                                -순간 AC RMS 공급 전압

//    public final static String[] List = {EAII,EAIR,CI,CO,PAI,PO,SoC,TEM};
    public final static String[] List = {EAII,EAIR,CI,CO,PO,SoC,TEM};
}
