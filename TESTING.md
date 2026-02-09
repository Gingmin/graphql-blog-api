# 테스트 실행 가이드

이 프로젝트는 테스트를 2종류로 나눠서 실행할 수 있습니다.

- **단위/슬라이스 테스트**: 빠르고 자주 실행 (DB 불필요)
- **통합 테스트**: 실제 Spring Context + 로컬 PostgreSQL을 사용 (느리지만 신뢰도 높음)

---

## 1) 단위/슬라이스 테스트 실행 (기본)

`@Tag("integration")`이 **붙지 않은 테스트만** 실행됩니다.

```bash
.\gradlew.bat test
```

---

## 2) 통합 테스트 실행

`@Tag("integration")`이 **붙은 테스트만** 실행됩니다.

```bash
.\gradlew.bat integrationTest
```

### 통합 테스트가 사용하는 DB

통합 테스트는 Docker 없이 **로컬 PostgreSQL**에 붙습니다.

- 설정 파일: `src/test/resources/application-test.yml`
- 스키마 부트스트랩: `src/test/resources/schema.sql`
- DB 접속 기본값(필요시 환경변수로 override):
  - `TEST_DB_URL` (기본: `jdbc:postgresql://localhost:5434/clean`)
  - `TEST_DB_USERNAME` (기본: `peuser`)
  - `TEST_DB_PASSWORD` (기본: `rhtmxm`)

예) 다른 DB로 바꿔서 실행:

```bash
set TEST_DB_URL=jdbc:postgresql://localhost:5434/clean_test
set TEST_DB_USERNAME=peuser
set TEST_DB_PASSWORD=rhtmxm
.\gradlew.bat integrationTest
```

> 주의: 통합 테스트는 매 테스트마다 테이블 데이터를 정리합니다(`delete from ...`).  
> 반드시 “테스트용 DB”를 사용하세요.

---

## 3) 특정 테스트만 실행(빠르게)

### 클래스 1개만

```bash
.\gradlew.bat test --tests "com.example.graphql.UserGraphqlTest"
```

### 이름 패턴으로

```bash
.\gradlew.bat test --tests "*Post*"
```

통합 테스트도 동일:

```bash
.\gradlew.bat integrationTest --tests "*Auth*"
```

---

## 4) 자주 쓰는 추천 루틴

- 개발 중: `.\gradlew.bat test`
- DB/매핑/조인 수정 후: `.\gradlew.bat integrationTest`
- 최종 확인: `.\gradlew.bat test integrationTest`

