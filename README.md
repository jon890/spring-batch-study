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

## Step의 처리 모델

- 테스크릿 지향 처리 (Tasklet Oriented Processing)
    - 비교적 복잡하지 않은 단순한 작업을 실행할 떄 사용
- 청크 지향 처리 (Chunk Oriented Processing)

### 태스크릿 지향 처리

- 매일 새벽 불필요한 로그 파일 삭제
- 특정 디렉토리에서 오래된 파일을 아카이브
- 사용자에게 단순한 알림 메시지 또는 이메일 발송
- 외부 API 호출 후 결과를 단순히 저장하거나 로깅

#### RepeatStatus의 두 얼굴 : FINISHED vs CONTINUABLE

- `RepeatStatus.FINISHED` : "다 끝났다. 이제 Step을 종료해도 된다"
    - Step의 처리가 성공이든 실패든 상관없이 해당 Step이 완료되었음을 의미
    - 더 이상 반복할 필요 없이 다음 스텝으로 넘어가며, 배치 잡은 차근차근 진행
- `RepeatStatus.CONTIUABLE` : "작업 진행 중, 추가 실행이 필요하다"
  - Tasklet의 `execute()` 메서드가 추가로 더 실행되어야 함을 Spring Batch Step에 알리는 신호
  - Step의 종료는 보류되고, 필요한 만큼 `execute()` 메서드가 반복 호출

#### RepeatStatus가 필요한 이유 : 짧은 트랜잭션을 활용한 안전한 배치 처리

"반복 작업 이라면 while문으로 처리하면 되는거 아닌가?"

Spring Batch는 Tasklet의 execute() 호출마다 새로운 트랜잭션을 시작하고 execute()의 실행이 끝나 RepeatStatus가 반환되면 해당 트랜잭션을 커밋한다.

예를 들어, 오래된 주문 데이터를 정리하는 배치 작업을 생각해보자. 한 번에 만 건씩 데이터를 삭제하는데, 총 100만 건의 데이터를 처리해야한다고 하자
- `execute()` 내부에서 while문을 사용한다면 : 80만 건째 처리;중 예외가 발생했을 때, 이미 처리한 79만 건의 데이터도 모두 롤백되어 하나도 정리되지 않은 상태로 돌아감
- `RepeatStatus.CONTINUABLE`로 반복한다면 : 매 만 건 처리마다 트랜잭션이 커밋되므로, 예외가 발생하더라도 79만 건의 데이터는 이미 안전하게 정리된 상태로 남음

결국, RepeatStatus를 반환해 `execute()`를 반복 실행하도록 하는 이유는 거대한 하나의 트랜잭션 대신 **작은 트랜잭션들로 나누어 안전하게 처리**하기 위해서다