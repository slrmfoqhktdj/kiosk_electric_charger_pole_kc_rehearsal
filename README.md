[개요] 커넥터 하나인 스마트 폴 전용 앱 - KC 인증 받고 운영 테스트할 앱.
       키오스크 앱과 달리 PLC(충전 제어기)와 시리얼로 직접 통신하는 충전 컨트롤러 앱.

[사용기술]
 - Java, Android (minSdk 24, compileSdk/targetSdk 33)
 - OCPP 1.6: Java-OCA-OCPP + OkHttp WebSocket
 - 시리얼(RS-232/485) 통신: android_serialport_api(JNI) + CRC16(CRC-CCITT, 0x1021, 초기 0xFFFF)
 - SQLite(오프라인 트랜잭션/알림 보관), SharedPreferences(OCPP Configuration 키 관리)
 - Retrofit(서버 API, 기상청 초단기실황 API), LiveData/ViewModel, SoundPool(안내음), Logger+파일 로그
 - 부팅 자동 실행, Device Admin 키오스크 모드

[주요기능]
 - PLC 제어 명령: 상태 요청, 충전 시작, 충전 중단, 재부팅, SOC(배터리 %) 요청
   PLC 응답: 상태/시작/중단/재부팅/최초 시작·종료 시 전력량/SOC/기기 측 충전 종료 통지
 - 충전 흐름: 인증(Authorize) → 충전 시작 → 전력량(MeterValues, Sampled/Clock-Aligned 2종
   타이머) → 종료 → 완료 화면. 충전 중 SOC와 경과 시간으로 충전 예상 완료 시간 계산·표시
 - OCPP 서버 원격 명령 처리: RemoteStart/StopTransaction, Reset, ChangeAvailability,
   ChangeConfiguration/GetConfiguration, ClearCache, UnlockConnector, DataTransfer,
   GetDiagnostics(로그 파일 생성·업로드), UpdateFirmware, TriggerMessage
 - 오프라인 대응(KC 인증 시나리오): 서버 단절 시 인증 캐시(AuthorizationCache)·
   LocalAuthorizeOffline·AllowOfflineTxForUnknownId 설정에 따라 로컬 인증, 오프라인 거래는
   SQLite에 저장했다가 복구 시 UnknownStart/StopTransaction으로 보고
 - 안전/예외 처리: PLC 연결 끊김, 긴급 정지 상태 반영, 차량이 SOC를 지원하지 않는 경우 안내,
   타임아웃 시 홈 복귀, 명령 응답 제한시간 타이머
 - 기상 정보(격자 좌표 변환 TransNXY → 기상청 API)를 조회해 서버로 보고

[핵심 시스템 구조와 구현 방식]
 - 3개 통신 계층 분리: ① JsonClient(OCPP/서버) ② SerialPortClient(PLC) ③ CenterActivity
   (두 계층 사이를 중재하는 중앙 제어). 계층 간 이벤트는 Local Broadcast와 콜백 인터페이스
   (CenterClientInterface, SerialClientInterface, SplashClientInterface)로 전달.
 - PLC 프레임: CRC16 검증을 포함한 고정 포맷으로 송수신하고, 수신은 Handler 메시지로 UI/중앙
   제어에 전달. 명령/응답 코드를 SerialModel 상수(0x51 상태, 0x53 시작, 0x55 재부팅, 0x57 중단,
   0x58/0x59 시작·종료 시 응답, 0x61 SOC, 0x62 기기 측 종료)로 정의.
 - OCPP Configuration을 SharedPreferences(ConfigurationControl/KeyControl)에 키별로 저장·검증
   하여 인증 규격(KC 시험 항목)에 맞춰 서버에서 변경 가능한 설정을 지원.
 - 거래/알림 데이터는 SQLiteControl로 영속화해 전원 재시작/서버 단절 이후에도 복구.
 - 다수 Timer(전력량 보고, SOC 요청·비교, 충전 시간, 페이지 타임아웃, 명령 제한시간,
   서버 데이터 전송)가 충전 시퀀스를 구동.

[데이터 흐름]
 사용자/원격 시작 → CenterActivity → SerialPortClient(충전 시작 명령 + CRC) → PLC
   PLC 응답/상태 → SerialPortClient → 브로드캐스트 → CenterActivity
     → ViewModel(UI) / JsonClient(StartTransaction, MeterValues, StopTransaction)
   서버 단절 시: SQLite 적재 → 재연결 후 Unknown Start/Stop 보고
 서버 원격 명령(RemoteStop 등) → JsonClient 핸들러 → CenterActivity → PLC 중단 명령
