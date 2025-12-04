# Spring Batch Study

## 용어 정리

### Job

- 하나의 완전한 배치 처리, 우리가 흔히 접하는 배치 작업들은 모두 Job으로 표현됨
    - 매일 심야에 수행되는 **일일 매출 집계**
    - 매주 일요일마다 처리되는 **휴면 회원 정리**
    - 매월 1일에 실행되는 **정기 결제**

### Step

- Job을 구성하는 실행단위. Job은 하나 이상의 Step으로 구성됨
    - 예를 들어 **일일 매출 집계** Job은 다음과 같은 Step들로 이뤄질 수 있음
        - **1. 매출 집계 Step**
            - 전일 주문 데이터를 읽고 (read)
            - 결제 완료된 것만 필터링하여 (process)
            - 상품별/카테고리별로 집계하여 저장 (write)
        - **2. 알림 발송 step**
            - 집계 요약 정보를 생성하여 관리자에게 전달
        - **3. 캐시 갱신 step**
            - 집계된 데이터로 캐시 정보 업데이트

## Spring Batch의 핵심 컴포넌트

- Job과 Step
- JobLauncher
  - Job을 실행하고 실행에 필요한 파라미터를 전달하는 역할
  - 배치 작업 실행의 시작점
- JobRepository
  - 배치 처리의 모든 메타데이터를 저장하고 관리하는 핵심 저장소
  - Job과 Step의 실행 정보(시작/종료 시간, 상태, 결과 등)를 기록
  - 이렇게 저장된 정보들은 배치 작업의 모니터링이나 문제 발생 시 재실행에 활용됨
- ExecutionContext
  - Job과 Step 실행 중의 상태 정보를 key-value 형태로 담는 객체
  - Job과 Step 간의 데이터 공유나 Job 재시작 시 상태 복원에 사용
- 데이터 처리 컴포넌트 구현체 
  - ItemReader 구현체 (다양한 데이터 소스로부터 데이터를 읽어올 수 있음)
    - JdbcCursorItemReader
    - JpaPagingItemReader
    - MongoCursorItemReader
  - ItemWriter 구현체
    - JdbcBatchItemWriter
    - JpaItemWriter
    - MongoItemWriter