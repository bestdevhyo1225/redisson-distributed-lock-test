# Redisson을 활용한 분산 락으로 동시성 문제 해결하기 (테스트 후, 공유하기)

### :exclamation: 테스트 코드는 생략했음.

## 문제

동시에 여러 스레드 및 프로세스가 특정한 자원에 접근할 때, 동시성 이슈가 발생한다. `한 명의 회원이 3회 가량의 100 포인트를 동시에 적립한다고 했을때`, totalPoint는 300 포인트가 아닌 `100 포인트` 로
적립됨.

> 3번 동시 요청

```shell
#!/bin/bash

curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points &
curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points &
curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points
```

> 결과

<img width="643" alt="스크린샷 2022-04-09 오후 11 04 22" src="https://user-images.githubusercontent.com/23515771/162577636-e52e4c65-f335-4ea4-bd71-10128b2c4dd4.png">

## 과정

### Lock 획득 및 해제 로직에 AOP 적용

**Lock을 획득하거나, 해제하는 로직은 부가 기능에 해당하기 때문에 AOP를 적용했음.**

> @DistributedLock

- 핵심 기능에 `Lock 획득 및 해제 부가 기능` 을 적용하는 Annotation Class

> RedissonDistributedLockAspect (AOP)

- 포인트컷 + 어드바이스를 구현하고 있음. (Aspect)
- 적용되는 메서드의 `파라미터` 정보를 얻어온다. (ProceedingJoinPoint의 `args` 프로퍼티를 얻어옴)
- lockName은 `LOCK_NAME_PREFIX + memberId` 값으로 지정했음.
- 다만, `memberId` 는 고정된 값이 아니기 때문에 `리플렉션` 기법을 통해 런타임에 `memberId` 를 획득하여, lockName을 생성했음.
- :exclamation: 주의) 리플렉션을 사용하는 경우, 컴파일에서 에러를 잡을 수 없기 때문에 `런타임 시점에 에러가 발생할 가능성이 높음.` 

> @DistributedLockUniqueKey

- 특정 클래스 `memberId` 프로퍼티에 `@DistributedLockUniqueKey` 선언한 경우만 Lock을 획득할 수 있음.

### 적용 방법

```kotlin
data class CreatePointRequest(

    @field:Positive(message = "memberId는 0보다 큰 값이어야 합니다.")
    @DistributedLockUniqueKey
    val memberId: Long,

    @field:NotBlank(message = "code는 반드시 입력해야 합니다.")
    val code: String,

    @field:NotNull(message = "amounts는 반드시 입력해야 합니다.")
    val amounts: Int,
)
```

`memberId` 프로퍼티에 `@DistributedLockUniqueKey` 애노테이션을 선언한다.

```kotlin
@RestController
@RequestMapping("/points")
class PointController(
    private val pointService: PointService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @DistributedLock(waitTime = 2_500, leaseTime = 3_000, timeUnit = TimeUnit.MILLISECONDS)
    fun createPoint(@RequestBody @Valid request: CreatePointRequest): ResponseEntity<SuccessResponse<CreatePointResultDto>> {
        val serviceDto = CreatePointDto(memberId = request.memberId, code = request.code, amounts = request.amounts)
        val serviceResultDto = pointService.createPoint(serviceDto = serviceDto)
        return ResponseEntity.ok(SuccessResponse(data = serviceResultDto))
    }
}
```

적용할 메서드에 `@DistributedLock(waitTime = 2_500, leaseTime = 3_000, timeUnit = TimeUnit.MILLISECONDS)` 애노테이션을 선언한다.

## 적용 후, 결과

> 적용후, 3번 동시 요청

```shell
#!/bin/bash

curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points &
curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points &
curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points
```

> 결과

<img width="706" alt="스크린샷 2022-04-09 오후 11 07 09" src="https://user-images.githubusercontent.com/23515771/162577743-d8032ffb-8d1d-4387-ba56-fa2201dfdefe.png">

## 남은 테스트

- `차감`, `마일리지 전환`, `소멸(Batch)` 에도 적용해야 함.
- 특히, 대량의 소멸 처리를 하는 도중에, 사용자가 차감한다면? 어떤 전략으로 해결해야 할 지 고민할 것

## 블로그에 정리한 내용들

- [동시성을 위한 여러가지 기법 정리](https://hyos-dev-log.tistory.com/9)
- [[엔터프라이즈 애플리케이션 아키텍처 패턴] 16. 오프라인 동시성 패턴 - 낙관적, 비관적 오프라인 잠금](https://hyos-dev-log.tistory.com/15)
