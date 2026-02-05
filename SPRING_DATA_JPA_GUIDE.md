# Spring Data JPA 패턴 정리 (Query Method 중심)

이 문서는 Spring Data JPA를 처음/중간에 쓰는 사람이 “왜 인터페이스 메서드만 선언했는데 동작하지?”를 이해하고, 실무에서 자주 쓰는 패턴을 빠르게 적용할 수 있게 정리했습니다.

> 핵심: `JpaRepository`를 상속한 **인터페이스에 메서드를 선언**하면, Spring Data JPA가 **메서드 이름을 파싱해서 쿼리를 자동 생성**합니다.  
> 그래서 `existsByUsername(...)` 같은 메서드는 구현체를 직접 만들지 않아도 실행됩니다.

---

## 1) 기본 구조: “JPA 리포지토리 인터페이스”는 프록시로 구현된다

예시(현재 프로젝트):

- `UserJpaRepository`는 `JpaRepository<UserJpaEntity, Long>`를 상속
- Spring이 런타임에 `UserJpaRepository`의 구현체(프록시)를 만들어 Bean으로 등록

그래서 아래처럼 **인터페이스에 메서드만 선언해도** 동작합니다.

```java
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
```

---

## 2) Query Method(파생 쿼리) 기본 문법

### 2-1. 가장 흔한 접두사(prefix)
- `findBy...`: 조회 (가장 보편)
- `getBy...`, `readBy...`: `findBy`와 유사(팀 컨벤션)
- `existsBy...`: 존재 여부 (boolean)
- `countBy...`: 카운트 (long)
- `deleteBy...`, `removeBy...`: 조건 삭제 (주의: 대량 삭제 시 성능/락 고려)

### 2-2. 조건(프로퍼티) 이름 매칭 규칙
`existsByUsername`의 `Username`은 엔티티의 `username` 필드(프로퍼티)와 매칭됩니다.

- 엔티티 필드가 `userName`이면 메서드는 `existsByUserName` 이어야 함
- “DB 컬럼명”이 아니라 **엔티티 필드명** 기준(예: `@Column(name="user_name")`여도 메서드명은 `UserName` 기준)

---

## 3) 자주 쓰는 키워드(Operators)

아래 키워드를 “필드명 뒤에 붙여서” 조건을 표현합니다.

### 비교/범위
- `LessThan`, `LessThanEqual`
- `GreaterThan`, `GreaterThanEqual`
- `Between`

예)

```java
List<UserJpaEntity> findByIdGreaterThan(Long id);
```

### 문자열/부분 일치
- `Like` (직접 `%` 포함해서 넘기는 편)
- `Containing` (contains)
- `StartingWith`
- `EndingWith`
- `IgnoreCase` (대소문자 무시)

예)

```java
List<UserJpaEntity> findByUsernameContainingIgnoreCase(String q);
```

### 집합/널
- `In` / `NotIn`
- `IsNull` / `IsNotNull`

예)

```java
List<UserJpaEntity> findByIdIn(List<Long> ids);
```

### 논리 조합
- `And`, `Or`

예)

```java
Optional<UserJpaEntity> findByUsernameAndEmail(String username, String email);
```

---

## 4) 반환 타입 패턴(무엇을 리턴하면 좋은가)

### 단건 조회
- `Optional<T>`: “없을 수도 있음”을 타입으로 강제 → 가장 안전
- `T`: 없으면 예외가 날 수 있음(프레임워크 동작/버전에 따라 다를 수 있어 팀 규칙 필요)

권장:

```java
Optional<UserJpaEntity> findByUsername(String username);
```

### 다건 조회
- `List<T>`: 가장 흔함
- `Slice<T>`, `Page<T>`: 페이징 필요할 때

### 존재/개수
- `boolean existsBy...`
- `long countBy...`

---

## 5) 정렬/Top-N/페이징

### 5-1. 메서드 이름에 정렬 넣기

```java
List<UserJpaEntity> findByUsernameContainingOrderByIdDesc(String q);
```

하지만 정렬 조건이 복잡해지면, 보통은 **파라미터로 `Sort`**를 받는 방식을 선호합니다.

```java
List<UserJpaEntity> findByUsernameContaining(String q, Sort sort);
```

### 5-2. Top/First로 상위 N개

```java
List<UserJpaEntity> findTop10ByOrderByIdDesc();
Optional<UserJpaEntity> findFirstByOrderByIdDesc();
```

### 5-3. PageRequest로 페이징

```java
Page<UserJpaEntity> findByUsernameContaining(String q, Pageable pageable);
```

호출:

```java
var page = repo.findByUsernameContaining("mk", PageRequest.of(0, 20, Sort.by("id").descending()));
```

---

## 6) “필드 확장/조인”과 N+1 주의

GraphQL/REST에서 엔티티 연관관계를 그대로 노출하면 **N+1**이 쉽게 발생합니다.

실무에서 흔한 대응:
- 필요한 곳에서만 `@EntityGraph` 또는 `join fetch`(`@Query`) 사용
- 조회용 DTO/Projection 사용
- GraphQL이면 DataLoader로 batch 로딩(필드 리졸버에서 루프 조회 방지)

---

## 7) `@Query`로 직접 JPQL/SQL 작성(파생 쿼리 한계 넘기)

파생 쿼리로 표현하기 어렵거나, 성능상 최적화가 필요하면 `@Query`를 씁니다.

```java
@Query("select u from UserJpaEntity u where u.username = :username")
Optional<UserJpaEntity> findOneByUsername(@Param("username") String username);
```

네이티브 SQL도 가능하지만( `nativeQuery = true` ), 유지보수가 어려워질 수 있어 신중하게 사용합니다.

---

## 8) 실무 체크리스트(자주 틀리는 포인트)

- **메서드 이름은 엔티티 필드명 기준**: DB 컬럼명 기준 아님
- **`Optional.get()` 금지**에 가깝게: `orElseThrow`, `orElse`, `map` 활용
- **`existsBy...`는 인덱스가 중요**: username/email 같은 조회 조건 컬럼에 인덱스/unique를 두면 훨씬 안정적
- **대량 `deleteBy...` 주의**: 트랜잭션/락/성능(필요 시 벌크 쿼리 검토)
- **너무 긴 파생 쿼리 메서드명은 경고 신호**: `@Query`/Specification/Querydsl 고려

---

## 9) (이 프로젝트 컨텍스트) “도메인 분리”에서의 사용법

현재 구조는 JPA 엔티티(`UserJpaEntity`)를 바깥 레이어로 새지 않게, 아래처럼 분리했습니다.

- `user/application/UserRepository` (port): 도메인 타입(`User`) 기준
- `user/infra/jpa/UserJpaRepository`: JPA 엔티티 기준
- `user/infra/UserRepositoryAdapter`: 두 세계를 매핑해서 연결

이 패턴의 장점:
- GraphQL/Service가 JPA에 직접 묶이지 않음
- 테스트/교체(예: 다른 저장소) 용이

