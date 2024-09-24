package com.example.kiosk_electric_charger_pole_kc_rehearsal.model;

public class ConfigurationModel {

    //- AllowOfflineTxForUnknownId	-- (옵션) - 오프라인 일때 알수없는 id가 충전을 할수있는지.
    public final static String AllowOfflineTxForUnknownId = "AllowOfflineTxForUnknownId";

    //- AuthorizationCacheEnabled  -- (옵션) - 인증캐쉬가 구현되어있는 경우, 인증캐쉬를 사용할건지 아닌지
    public final static String AuthorizationCacheEnabled = "AuthorizationCacheEnabled";
    //- AuthorizeRemoteTxRequests	-- 원격 충전을 위해 승인을 받아야하는지.
    public final static String AuthorizeRemoteTxRequests = "AuthorizeRemoteTxRequests";
    //- ClockAlignedDataInterval
    public final static String ClockAlignedDataInterval = "ClockAlignedDataInterval";
    //- ConnectionTimeOut	-- 시작부터 충전 전까지의 취소 시간
    public final static String ConnectionTimeOut = "ConnectionTimeOut";
    //- ConnectorPhaseRotation	-- 해당기기엔 해당없음 - NotApplicable로 답하면됨
    public final static String ConnectorPhaseRotation = "ConnectorPhaseRotation";
    //- GetConfigurationMaxKeys -- GetConfiguration에서 요청된 구성키의 최대수
    public final static String GetConfigurationMaxKeys = "GetConfigurationMaxKeys";
    //- HeartbeatInterval	-- heartbeat간격
    public final static String HeartbeatInterval = "HeartbeatInterval";
    //- LocalAuthorizeOffline-오프라인 일 때 충전 포인트가 인증 캐시 및/또는 로컬 인증 목록을 사용하여 사용자에게 인증할지 여부를 제어합니다 .
    public final static String LocalAuthorizeOffline = "LocalAuthorizeOffline";
    //- LocalPreAuthorize- 청구 지점이 중앙 시스템의 승인 응답을 기다리지 않고 트랜잭션을 시작하기 위해 승인 캐시 및/또는 로컬 승인 목록을 사용할지 여부를 제어합니다
    public final static String LocalPreAuthorize = "LocalPreAuthorize";
    //- MeterValuesAlignedData - ClockAlignedDataInterval 초 마다 MeterValues.req PDU 에 포함될 시계 정렬 측정량
    public final static String MeterValuesAlignedData = "MeterValuesAlignedData";
    //- MeterValuesSampledData
    public final static String MeterValuesSampledData = "MeterValuesSampledData";
    //- MeterValueSampleInterval
    public final static String MeterValueSampleInterval = "MeterValueSampleInterval";
    //- NumberOfConnectors	-- 충전포인터의 물리적 충전 커넥터 수
    public final static String NumberOfConnectors = "NumberOfConnectors";
    //- ResetRetries	--실패한 충전 포인트 재설정을 재시도하는 횟수입니다
    public final static String ResetRetries = "ResetRetries";
    //- StopTransactionOnEVSideDisconnect --true 로 설정되면 충전 포인트는 케이블이 EV에서 분리될 때 관리상 트랜잭션을 중지해야 합니다.
    public final static String StopTransactionOnEVSideDisconnect = "StopTransactionOnEVSideDisconnect";
    //- StopTransactionOnInvalidId --승인되지 않은 승인 상태를 수신한 경우 청구 지점이 진행 중인 거래를 중지할지 여부,이 트랜잭션에 대한 StartTransaction.conf
    public final static String StopTransactionOnInvalidId = "StopTransactionOnInvalidId";
    //- StopTxnAlignedData	-- StopTransaction.req MeterValues.req 의 TransactionData 요소에 포함될 시계 정렬 주기 측정량, 트랜잭션의 모든 ClockAlignedDataInterval 에 대한 PDU
    public final static String StopTxnAlignedData = "StopTxnAlignedData";
    //- StopTxnSampledData -- StopTransaction.req PDU 의 TransactionData 요소에 포함될 샘플링된 측정 량,충전 세션 시작부터 MeterValueSampleInterval 초
    public final static String StopTxnSampledData = "StopTxnSampledData";
    //- SupportedFeatureProfiles --지원되는 기능 프로필 목록입니다 ,Core, FirmwareManagement, LocalAuthListManagement, Reservation, SmartCharging and RemoteTrigger.
    public final static String SupportedFeatureProfiles = "SupportedFeatureProfiles";
    //- TransactionMessageAttempts -- 중앙 시스템이 거래 관련 메시지를 처리하지 못할 때 청구 지점이 거래 관련 메시지 제출을 시도해야 하는 빈도입니다.
    public final static String TransactionMessageAttempts = "TransactionMessageAttempts";
    //- TransactionMessageRetryInterval -- 중앙 시스템이 실패한 거래 관련 메시지를 다시 제출하기 전에 충전 지점이 기다려야 하는 시간
    public final static String TransactionMessageRetryInterval = "TransactionMessageRetryInterval";
    //- UnlockConnectorOnEVSideDisconnect -- true 로 설정된 경우 충전 포인트는 케이블이 EV에서 분리될 때 충전 포인트 측 케이블의 잠금을 해제해야 합니다.
    public final static String UnlockConnectorOnEVSideDisconnect = "UnlockConnectorOnEVSideDisconnect";
    //- WebSocketPingInterval -- 웹소켓 구현에만 해당됩니다. 0은 클라이언트 측 웹소켓 Ping/Pong을 비활성화합니다
    public final static String WebSocketPingInterval = "WebSocketPingInterval";


    public final static String[] AllKey = {AllowOfflineTxForUnknownId,AuthorizationCacheEnabled,AuthorizeRemoteTxRequests,
            ClockAlignedDataInterval,ConnectionTimeOut,ConnectorPhaseRotation,
            GetConfigurationMaxKeys,HeartbeatInterval,LocalAuthorizeOffline,
            LocalPreAuthorize,MeterValuesAlignedData,MeterValuesSampledData,
            MeterValueSampleInterval,NumberOfConnectors,ResetRetries,
            StopTransactionOnEVSideDisconnect,StopTransactionOnInvalidId,StopTxnAlignedData,StopTxnSampledData,
            SupportedFeatureProfiles,TransactionMessageAttempts,TransactionMessageRetryInterval,UnlockConnectorOnEVSideDisconnect,WebSocketPingInterval
    };
}
