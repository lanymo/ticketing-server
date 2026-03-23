# 고성능 티켓팅 시스템 - Project Context

## 프로젝트 개요
대용량 트래픽(동시 5만명, 1000 TPS)을 처리하는 티켓팅 플랫폼.
공부 겸용 프로젝트로, 코드 효율과 학습을 함께 추구합니다.

## 기술 스택
- **Backend**: Spring Boot 3.x, Java 21
- **Database**: MySQL 8.0 (메인 DB)
- **Cache / 분산락 / 대기열**: Redis 7
- **Message Queue**: Apache Kafka (이벤트 스트리밍), RabbitMQ (결제/알림)
- **Build**: Gradle
- **Container**: Docker, Docker Compose

## 패키지 구조
```
ticketing_server
├── domain
│   ├── user
│   │   ├── entity
│   │   ├── service
│   │   ├── dto
│   │   └── repository
│   └── ticket
│       ├── entity
│       ├── service
│       └── ...
└── global
    ├── config
    └── exception
```

## 개발 원칙
- 각 기술 선택의 이유를 이해하며 구현 (왜 Redis인지, 왜 Kafka인지 등)
- 코드에 주석으로 핵심 개념 설명 포함
- 동시성 문제에 특히 주의 (분산락, 원자적 연산)

## 구현 Phase

### Phase 1 - 핵심 예매 기능
- [X]  Docker Compose 환경 구성 (MySQL + Redis)
- [X] application.yml 설정
- [X] User Service (회원가입/로그인/JWT)
- [X] Event Service (공연 CRUD, Redis 캐싱)
- [ ] Ticket Service (재고 관리, 분산 락, 임시 예약)
- [ ] Redis 대기열 구현
- [ ] MySQL 스키마 설계 및 연동

### Phase 2 - 결제 및 알림
- [ ] Payment Service (RabbitMQ consumer, PG 연동)
- [ ] Notification Service (이메일/SMS)
- [ ] Kafka 이벤트 파이프라인 구성
- [ ] 결제 실패 시 재고 롤백 처리

### Phase 3 - 안정성 강화
- [ ] API Gateway Rate Limiting
- [ ] Redis Cluster 구성
- [ ] Kafka Replication 설정
- [ ] RabbitMQ DLQ 설정
- [ ] Circuit Breaker (Resilience4j)

### Phase 4 - 운영 및 모니터링
- [ ] Prometheus + Grafana 대시보드
- [ ] 부하 테스트 (k6 또는 JMeter)
- [ ] 장애 시나리오 테스트


## 핵심 동작 흐름

### 대기열 흐름
1. Redis Sorted Set에 진입 등록 (score = 타임스탬프)
2. Scheduler가 1초마다 N명씩 입장 토큰 발급
3. 입장 토큰 → Redis TTL 설정 (5분)
4. 토큰 검증 통과 시 예매 페이지 접근 허용

### 예매 흐름
1. API Gateway: 입장 토큰 검증 + Rate Limit
2. Ticket Service: Redis 분산락 획득 (Redisson, 좌석 단위)
3. Redis DECR로 재고 원자적 감소
4. 임시 예약 Redis 저장 (TTL 10분)
5. Kafka로 ticket-reserved 이벤트 발행
6. Payment Service: 결제 처리 후 MySQL 최종 저장
7. 결제 실패 시 Redis 재고 INCR 롤백

## 동시성 제어 전략
- **분산락**: Redisson, `lock:seat:{eventId}:{seatId}`, TTL 3초
- **재고 관리**: Redis DECR/INCR 원자적 연산
- **대기열**: Redis Sorted Set, `queue:{eventId}`
- **Rate Limit**: Redis 슬라이딩 윈도우, IP당 10req/s, 유저당 5req/s

## 주요 DB 테이블
```sql
events       (id, name, venue, start_date, total_seats, status)
seats        (id, event_id, seat_number, grade, price, status)
reservations (id, user_id, event_id, seat_id, status, reserved_at, expires_at)
payments     (id, reservation_id, amount, pg_transaction_id, status, paid_at)
queue_logs   (id, user_id, event_id, entered_at, admitted_at)
```

## 성능 목표
| 지표 | 목표 |
|------|------|
| 동시 접속자 | 50,000명 |
| TPS | 1,000 |
| p99 응답시간 | < 2초 |
| 가용성 | 99.9% |

## 개발 환경
- Spring Boot 앱: IntelliJ에서 로컬 실행
- MySQL + Redis: Docker Compose로 실행
- claude.ai: 설계/개념 논의 및 히스토리 관리
- Claude Code (CLI): 파일 직접 생성/수정

## 현재 진행 상황
- 현재 Phase: 1
- 마지막 작업: docker-compuse, application.yml 세팅 완료

## 기술적 결정사항
- JWT 만료시간: 30분
- Redis 대기열 배치 크기: 100명/초
- 임시 예약 TTL: 10분
