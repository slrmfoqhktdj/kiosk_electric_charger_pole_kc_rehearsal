package com.example.kiosk_electric_charger_pole_kc_rehearsal.SharedPreferences;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.model.ConfigurationModel;

public class ConfigurationControl {

    //- AllowOfflineTxForUnknownId	-- 오프라인 일때 알수없는 id가 충전을 할수있는지.  - 선택
    public static void setAllowOfflineTxForUnknownId(boolean allowOfflineTxForUnknownId){PrefsHelper.write(ConfigurationModel.AllowOfflineTxForUnknownId,allowOfflineTxForUnknownId);}
    public static boolean getAllowOfflineTxForUnknownId(){return PrefsHelper.read(ConfigurationModel.AllowOfflineTxForUnknownId,false);}


    //- AuthorizationCacheEnabled  -- (옵션) - 인증캐쉬가 구현되어있는 경우, 인증캐쉬를 사용할건지 아닌지
    public static void setAuthorizationCacheEnabled(boolean authorizationCacheEnabled){PrefsHelper.write(ConfigurationModel.AuthorizationCacheEnabled,authorizationCacheEnabled);}
    public static boolean getAuthorizationCacheEnabled(){return PrefsHelper.read(ConfigurationModel.AuthorizationCacheEnabled,true);}

    //- AuthorizeRemoteTxRequests	-- 원격 충전을 위해 승인을 받아야하는지.
    public static void setAuthorizeRemoteTxRequests(boolean authorizeRemoteTxRequests){PrefsHelper.write(ConfigurationModel.AuthorizeRemoteTxRequests,authorizeRemoteTxRequests);}
    public static boolean getAuthorizeRemoteTxRequests(){return PrefsHelper.read(ConfigurationModel.AuthorizeRemoteTxRequests,true);}

    //- ClockAlignedDataInterval
    public static void setClockAlignedDataInterval(String clockAlignedDataInterval){PrefsHelper.write(ConfigurationModel.ClockAlignedDataInterval,clockAlignedDataInterval);}
    public static String getClockAlignedDataInterval(){return PrefsHelper.read(ConfigurationModel.ClockAlignedDataInterval,"0");}

    //- ConnectionTimeOut	-- 시작부터 충전 전까지의 취소 시간
    public static void setConnectionTimeOut(String connectionTimeOut){PrefsHelper.write(ConfigurationModel.ConnectionTimeOut,connectionTimeOut);}
    public static String getConnectionTimeOut(){return PrefsHelper.read(ConfigurationModel.ConnectionTimeOut,"120");}

    //- ConnectorPhaseRotation	-- 해당기기엔 해당없음 - NotApplicable로 답하면됨
    public static void setConnectorPhaseRotation(String connectorPhaseRotation){PrefsHelper.write(ConfigurationModel.ConnectorPhaseRotation,connectorPhaseRotation);}
    public static String getConnectorPhaseRotation(){return PrefsHelper.read(ConfigurationModel.ConnectorPhaseRotation,"NotApplicable");}

    //- GetConfigurationMaxKeys -- GetConfiguration에서 요청된 구성키의 최대수
    public static String getGetConfigurationMaxKeys(){return PrefsHelper.read(ConfigurationModel.GetConfigurationMaxKeys,String.valueOf(ConfigurationModel.AllKey.length));}

    //- HeartbeatInterval	-- heartbeat간격
    public static void setHeartBeatInterval(String heartBeatInterval){PrefsHelper.write(ConfigurationModel.HeartbeatInterval,heartBeatInterval);}
    public static String getHeartBeatInterval(){return PrefsHelper.read(ConfigurationModel.HeartbeatInterval,"86400");}
//    public static String getHeartBeatInterval(){return PrefsHelper.read(ConfigurationModel.HeartbeatInterval,"10");}

    //- LocalAuthorizeOffline -오프라인 일 때 충전 포인트가 인증 캐시 및/또는 로컬 인증 목록을 사용하여 사용자에게 인증할지 여부를 제어합니다 .
    public static void setLocalAuthorizeOffline(boolean localAuthorizeOffline){PrefsHelper.write(ConfigurationModel.LocalAuthorizeOffline,localAuthorizeOffline);}
    public static boolean getLocalAuthorizeOffline(){return PrefsHelper.read(ConfigurationModel.LocalAuthorizeOffline,false);}

    //- LocalPreAuthorize - 청구 지점이 중앙 시스템의 승인 응답을 기다리지 않고 트랜잭션을 시작하기 위해 승인 캐시 및/또는 로컬 승인 목록을 사용할지 여부를 제어합니다
    public static void setLocalPreAuthorize(boolean localPreAuthorize){PrefsHelper.write(ConfigurationModel.LocalPreAuthorize,localPreAuthorize);}
    public static boolean getLocalPreAuthorize(){return PrefsHelper.read(ConfigurationModel.LocalPreAuthorize,false);}

    //- MeterValuesAlignedData - ClockAlignedDataInterval 초 마다 MeterValues.req PDU 에 포함될 시계 정렬 측정량
    public static void setMeterValuesAlignedData(String meterValuesAlignedData){PrefsHelper.write(ConfigurationModel.MeterValuesAlignedData,meterValuesAlignedData);}
    public static String getMeterValuesAlignedData(){return PrefsHelper.read(ConfigurationModel.MeterValuesAlignedData,"Energy.Active.Import.Register,SoC");}

    //- MeterValuesSampledData
    public static void setMeterValuesSampledData(String meterValuesSampledData){PrefsHelper.write(ConfigurationModel.MeterValuesSampledData,meterValuesSampledData);}
    public static String getMeterValuesSampledData(){return PrefsHelper.read(ConfigurationModel.MeterValuesSampledData,"Energy.Active.Import.Register,SoC");}

    //- MeterValueSampleInterval    --"MeterValues" PDU에 의해 전송되는 계량(또는 기타) 데이터 샘플링 사이의 시간(초)입니다.
    public static void setMeterValueSampleInterval(String meterValueSampleInterval){PrefsHelper.write(ConfigurationModel.MeterValueSampleInterval,meterValueSampleInterval);}
    public static String getMeterValueSampleInterval(){return PrefsHelper.read(ConfigurationModel.MeterValueSampleInterval,"10");}

    //- NumberOfConnectors	-- 충전포인터의 물리적 충전 커넥터 수
    public static String getNumberOfConnectors(){return PrefsHelper.read(ConfigurationModel.NumberOfConnectors,"1");}

    //- ResetRetries	--실패한 충전 포인트 재설정을 재시도하는 횟수입니다
    public static void setResetRetries(String resetRetries){PrefsHelper.write(ConfigurationModel.ResetRetries,resetRetries);}
    public static String getResetRetries(){return PrefsHelper.read(ConfigurationModel.ResetRetries,"2");}

    //- StopTransactionOnEVSideDisconnect --true 로 설정되면 충전 포인트는 케이블이 EV에서 분리될 때 관리상 트랜잭션을 중지해야 합니다. (49p)                                      --- 충전기 연계되고 나서
    public static boolean getStopTransactionOnEVSideDisconnect(){return PrefsHelper.read(ConfigurationModel.StopTransactionOnEVSideDisconnect,true);}

    //- StopTransactionOnInvalidId --승인되지 않은 승인 상태를 수신한 경우 청구 지점이 진행 중인 거래를 중지할지 여부,이 트랜잭션에 대한 StartTransaction.conf
    public static void setStopTransactionOnInvalidId(boolean stopTransactionOnInvalidId){PrefsHelper.write(ConfigurationModel.StopTransactionOnInvalidId,stopTransactionOnInvalidId);}
    public static boolean getStopTransactionOnInvalidId(){return PrefsHelper.read(ConfigurationModel.StopTransactionOnInvalidId,true);}

    //- StopTxnAlignedData	-- StopTransaction.req MeterValues.req 의 TransactionData 요소에 포함될 시계 정렬 주기 측정량, 트랜잭션의 모든 ClockAlignedDataInterval 에 대한 PDU
    public static void setStopTxnAlignedData(String stopTxnAlignedData){PrefsHelper.write(ConfigurationModel.StopTxnAlignedData,stopTxnAlignedData);}
    public static String getStopTxnAlignedData(){return PrefsHelper.read(ConfigurationModel.StopTxnAlignedData,"");}

    //- StopTxnSampledData -- StopTransaction.req PDU 의 TransactionData 요소에 포함될 샘플링된 측정 량,충전 세션 시작부터 MeterValueSampleInterval 초
    public static void setStopTxnSampledData(String stopTxnSampledData){PrefsHelper.write(ConfigurationModel.StopTxnSampledData,stopTxnSampledData);}
    public static String getStopTxnSampledData(){return PrefsHelper.read(ConfigurationModel.StopTxnSampledData,"Energy.Active.Import.Register,SoC");}

    //- SupportedFeatureProfiles --지원되는 기능 프로필 목록입니다 ,Core, FirmwareManagement, LocalAuthListManagement, Reservation, SmartCharging and RemoteTrigger.

    //- TransactionMessageAttempts -- 중앙 시스템이 거래 관련 메시지를 처리하지 못할 때 청구 지점이 거래 관련 메시지 제출을 시도해야 하는 빈도입니다.
    public static void setTransactionMessageAttempts(String transactionMessageAttempts){PrefsHelper.write(ConfigurationModel.TransactionMessageAttempts,transactionMessageAttempts);}
    public static String getTransactionMessageAttempts(){return PrefsHelper.read(ConfigurationModel.TransactionMessageAttempts,"2");}
    //- TransactionMessageRetryInterval -- 중앙 시스템이 실패한 거래 관련 메시지를 다시 제출하기 전에 충전 지점이 기다려야 하는 시간
    public static void setTransactionMessageRetryInterval(String transactionMessageRetryInterval){PrefsHelper.write(ConfigurationModel.TransactionMessageRetryInterval,transactionMessageRetryInterval);}
    public static String getTransactionMessageRetryInterval(){return PrefsHelper.read(ConfigurationModel.TransactionMessageRetryInterval,"30");}

    //- UnlockConnectorOnEVSideDisconnect -- true 로 설정된 경우 충전 포인트는 케이블이 EV에서 분리될 때 충전 포인트 측 케이블의 잠금을 해제해야 합니다.                                     --- 충전기 연계되고 나서
    public static boolean getUnlockConnectorOnEVSideDisconnect(){return PrefsHelper.read(ConfigurationModel.UnlockConnectorOnEVSideDisconnect,false);}

    //- WebSocketPingInterval -- 웹소켓 구현에만 해당됩니다. 0은 클라이언트 측 웹소켓 Ping/Pong을 비활성화합니다
    public static void setWebSocketPingInterval(String webSocketPingInterval){PrefsHelper.write(ConfigurationModel.WebSocketPingInterval,webSocketPingInterval);}
    public static String getWebSocketPingInterval(){return PrefsHelper.read(ConfigurationModel.WebSocketPingInterval,"50");}




}
