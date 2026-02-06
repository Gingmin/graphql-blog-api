# 프로젝트 동작 방식 안내 (Spring Boot + GraphQL + PostgreSQL)

이 문서는 현재 `clean-java-main`(폴더: `blog`) 프로젝트가 **어떤 구성으로 동작하는지**, **어떻게 실행/테스트하는지**, 그리고 **GraphQL 명세(스키마/예제 요청)**까지 한 번에 정리합니다.

## 개요

- **런타임**: Spring Boot (Tomcat 내장, 기본 포트 8080)
- **API 스타일**: GraphQL (HTTP POST `/graphql`)
- **DB**: PostgreSQL + JPA(Hibernate)
- **정적 파일**: `src/main/resources/static/` 아래 파일이 그대로 서빙됨

## 주요 폴더/파일

- **앱 엔트리포인트**
  - `src/main/java/com/example/BlogApplication.java`
- **GraphQL 스키마(여러 파일로 분리)**
  - `src/main/resources/graphql/*.graphqls`
- **GraphQL 컨트롤러(리졸버, User 예시)**
  - `src/main/java/com/example/graphql/user/UserQueryController.java`
  - `src/main/java/com/example/graphql/user/UserMutationController.java`
- **User 도메인 구조(권장 레이어링)**
  - `src/main/java/com/example/user/domain/User.java`
  - `src/main/java/com/example/user/application/UserService.java`
  - `src/main/java/com/example/user/application/UserRepository.java` (port)
  - `src/main/java/com/example/user/infra/UserRepositoryAdapter.java` (adapter)
  - `src/main/java/com/example/user/infra/jpa/UserJpaEntity.java`
  - `src/main/java/com/example/user/infra/jpa/UserJpaRepository.java`
- **PostgreSQL 예제 DDL**
  - `db/postgresql/ddl.sql`
- **로컬 테스트용 HTML**
  - `src/main/resources/static/test.html`
- **실행/빌드 도구**
  - `build.gradle.kts`, `settings.gradle.kts`
  - Gradle Wrapper: `gradlew`, `gradlew.bat`, `gradle/wrapper/*`

## 빌드/의존성(Gradle)

`build.gradle.kts`에서 다음 스타터들을 사용합니다.

- `spring-boot-starter-web`: 내장 톰캣 및 WebMVC
- `spring-boot-starter-graphql`: GraphQL 엔진 + `/graphql` 서블릿 라우팅 + GraphiQL
- `spring-boot-starter-data-jpa`: JPA/Hibernate
- `postgresql`: PostgreSQL JDBC 드라이버 (runtime)

또한 `bootJar` 산출물을 고정 파일명으로 만들도록 설정되어 있습니다.

- 출력: `build/libs/app.jar`

## 실행 방식

### 1) 서버 실행(권장)

```bash
.\gradlew.bat bootRun
```

정상 기동 시 아래가 활성화됩니다.

- **GraphQL Endpoint**: `POST http://localhost:8080/graphql`
- **GraphiQL**: `http://localhost:8080/graphiql`
- **테스트 HTML**: `http://localhost:8080/test.html`

### 2) 실행 JAR로 실행

```bash
.\gradlew.bat bootJar
java -jar build\libs\app.jar
```

### 3) 포트(8080) 충돌이 날 때

이미 다른 프로세스가 8080을 사용 중이면 기동이 실패합니다.

- 해결: 해당 PID를 종료하거나, 포트를 바꿔서 실행

예) 포트 변경:

```bash
.\gradlew.bat bootRun --args="--server.port=8081"
```

## DB(PostgreSQL) 동작 방식

### 설정 위치

DB 접속 정보는 `src/main/resources/application.yml`에 있습니다.

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

현재는 `spring.jpa.hibernate.ddl-auto: validate` 이므로,
**DB에 테이블이 이미 존재해야 앱이 정상 기동**합니다.

### 예제 DDL

`db/postgresql/ddl.sql`은 현재 엔티티에 대응하는 최소 DDL입니다.

```sql
create table if not exists users (
  id bigserial primary key,
  username text not null unique,
  email text not null unique,
  created_at timestamptz not null default now()
);
create index if not exists ix_users_created_at on users (created_at desc);
```

## GraphQL 동작 방식

### 라우팅

Spring Boot 자동 설정이 GraphQL 엔드포인트를 등록합니다.

- **HTTP POST `/graphql`**로 요청
- 요청/응답은 JSON (`{ "query": "...", "variables": {...} }`)

### 스키마(명세)

스키마는 `src/main/resources/graphql/` 아래의 여러 `*.graphqls` 파일을 **전부 합쳐서** 로드합니다.

- 예: `user.graphqls`, `post.graphqls`, `comment.graphqls`, `tag.graphqls`

권장 패턴:

- `type Query`, `type Mutation`은 한 파일에 두고
- 기능이 늘어나면 `extend type Query`, `extend type Mutation`으로 확장
  - (또는 지금처럼, 한 파일에 `type Query/Mutation`을 두고 시작해도 됨)

```graphql
type Query {
  users: [User!]!
  user(id: ID!): User
}

type Mutation {
  createUser(username: String!, email: String!): User!
}

type User {
  id: ID!
  username: String!
  email: String!
}
```

### 리졸버(구현)

User 예시는 아래 리졸버에서 `@QueryMapping`, `@MutationMapping`으로 매핑됩니다.

- `UserQueryController`: `users`, `user(id)`
- `UserMutationController`: `createUser(username, email)`

중요: `@Argument("id")`처럼 **인자 이름을 명시**해 두었습니다.
파라미터 이름 메타데이터가 없을 때 GraphQL 초기화가 실패하는 문제를 피하기 위한 설정입니다.

## GraphQL 예제 요청

### 1) users 조회

```graphql
query {
  users { id username email }
}
```

### 2) user 단건 조회

```graphql
query {
  user(id: "1") { id username email }
}
```

### 3) user 생성

```graphql
mutation {
  createUser(username: "mkk", email: "mkk@example.com") { id username email }
}
```

## 테스트 HTML(`test.html`) 동작 방식

`src/main/resources/static/test.html`은 GraphQL 요청을 브라우저에서 쉽게 보내기 위한 파일입니다.

- 서버로 접근 시: 같은 origin이므로 `fetch("/graphql")` 호출이 정상
- 파일로 직접 열 때(`file://`)는 CORS/보안 정책 때문에 상대경로가 `file:///.../graphql`로 해석되어 실패할 수 있어,
  `file://`일 때는 `http://localhost:8080/graphql`로 보내도록 처리되어 있습니다.

권장 사용법:

- 서버 실행 후 `http://localhost:8080/test.html`로 접속

## VSCode/Cursor 실행 설정

`.vscode/launch.json`에 2개 실행 구성이 있습니다.

- **Run Spring Boot (Gradle classes)**: Gradle로 컴파일 후 메인 클래스를 실행
- **Run Spring Boot (bootJar app.jar)**: `bootJar`로 만든 `build/libs/app.jar`를 classpath로 사용해 실행  
  (IDE가 modulepath/classpath를 자동 구성 못 할 때도 동작하도록 만든 옵션)

### 디버그가 안 될 때(중요)

파일(`BlogApplication.java`)에서 바로 보이는 Run/Debug 버튼(CodeLens)은 환경에 따라 **Gradle classpath를 안 타고**
JDT 임시 classpath로 실행되면서 `SpringApplication cannot be resolved` 같은 오류가 날 수 있습니다.

권장 디버그 방법:

1) `Run and Debug` 패널에서 **`Debug (bootRun --debug-jvm + attach)`** 실행  
2) 또는 터미널에서 `.\gradlew.bat bootRun --debug-jvm` 실행 후 **Attach(5005)** 로 붙기

