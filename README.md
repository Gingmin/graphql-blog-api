# clean-java-main (blog)

Spring Boot + GraphQL + PostgreSQL(JPA) 예제 프로젝트입니다.  
스키마는 `src/main/resources/graphql/` 아래의 여러 `*.graphqls` 파일로 **기능 단위 분리**해서 관리합니다.

## 요구 사항

- Java 17+
- Gradle 8+ (권장)

## 실행

### 1) 실행 (개발)

```bash
.\gradlew.bat bootRun
```

### 2) 접속

- **GraphiQL**: `http://localhost:8080/graphiql`
- **테스트 HTML**: `http://localhost:8080/test.html`
- **GraphQL Endpoint**: `POST http://localhost:8080/graphql`

## Gradle Wrapper(옵션)

Wrapper를 추가해두면 Gradle 설치 없이도 실행할 수 있습니다.

```bash
gradle wrapper
.\gradlew.bat bootRun
```

## PostgreSQL로 전환(옵션)

1) `db/postgresql/ddl.sql` 를 실행해서 테이블을 만듭니다.

2) `src/main/resources/application.yml`의 접속 정보에 맞게 DB를 준비합니다.

> 참고: 현재 설정은 `spring.jpa.hibernate.ddl-auto: validate` 이므로, 테이블이 **미리 생성**되어 있어야 앱이 기동됩니다.

## Gradle 없이(옵션, 정말 최소)

```bash
javac -encoding UTF-8 -d out src/main/java/com/example/BlogApplication.java
```

## GraphQL 예시(User)

```graphql
query {
  users { id username email }
}
```

```graphql
mutation {
  createUser(username: "mkk", email: "mkk@example.com") { id username email }
}
```
