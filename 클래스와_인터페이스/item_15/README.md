# [Item 15] 클래스와 멤버의 접근 권한을 최소화하라
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [프로그램 요소의 접근성은 최소한으로](#프로그램-요소의-접근성은--최소한--으로)
- [public API 설계](#꼭-필요한-것만-골라-최소한의-public-api-설계)
- [상수외에는 public 금지](#-상수--만-public)

</div>
</details>

<br/>

---
> 🌟 **핵심** 🌟
```text
- 프로그램 요소의 접근성은 가능한 한 최소한으로 설계

- 꼭 필요한 것만 골라 최소한의 public API 설계
  - 그 외 클래스/인터페이스/멤버가 의도치않게 API로 공개되지 않도록
  
- (public 클래스) 상수용 public static final 필드 외에는 어떠한 public 필드 없도록

- public static final 필드가 참조하는 객체가 불변인지 확인 
```

<br/>

---
## 프로그램 요소의 접근성은 "최소한"으로

잘 설계된 컴포넌트의 특징은<br/>
" _클래스 **내부 데이터**와 **내부 구현 정보**를 <u>외부 컴포넌트로부터 잘 숨기는 것</u>_ "이다.<br/>
( `정보 은닉(Information Hiding)`/`캡슐화(Encapsulation)` )

즉,<br/>
모든 내부 구현을 숨겨, 구현과 API를 깔끔하게 분리하고<br/>
서로의 내부 동작 방식에는 개의치 않은 채로 오직 해당 API를 통해서만 다른 컴포넌트와 소통하도록 해야 한다.

### 정보 은닉의 장점
- 시스템 개발 속도 향상
  - 여러 컴포넌트 병렬적 개발
- 시스템 관리 비용 절감
  - 각 컴포넌트에 대한 빠른 파악을 통한 재빠른 디버깅
  - 컴포넌트 교체에 대한 적은 부담감
- 성능 최적화 기여
  - 타 컴포넌트에 끼치는 영향 없이 해당 컴포넌트만 독립적인 최적화 가능
- 소프트웨어 재사용성 증진
  - 외부에 대한 의존성(결합도)이 낮아 다른 환경에서도 재사용될 수 있는 가능성 높음
- 대규모 시스템 제작 난이도 절감
  - 미완성 단계에서도 개별 컴포넌트 동작 검증 가능

<br/>

---

## 꼭 필요한 것만 골라 최소한의 public API 설계

### 접근 제어자 활용
접근 제어자를 제대로 활용하는 것이 정보 은닉의 핵심이다.
```text
✶ private : 멤버를 선언한 톱레벨 클래스에서만 접근 가능 

✶ package-private(default) : 멤버 소속 패키지 내의 모든 클래스에서 접근 가능

✶ protected : package-private 접근 범위 포함 & 멤버 선언 클래스의 하위 클래스의 접근도 가능
( public 클래스의 protected 멤버 → 공개 API )

✶ public : 모든 곳에서의 접근 가능 
```

- " 모든 클래스와 멤버의 **접근성**을 <u>**가능한 한 좁혀야** 한다</u>. "
  - 가능한 한 항상 가장 낮은 접근 수준을 부여

> ⁜ _**톱 레벨** 클래스 : **가장 바깥**의 클래스_

<br/>

"<u>톱 레벨 클래스</u>"와 "<u>인터페이스</u>"에 부여할 수 있는 접근 수준은 `package-private`과 `public` 두 가지다.
- `package-private` 선언 : 내부 구현
  - 언제든 수정 가능 (외부 클라이언트에 영향 ❌)
- `public` 선언 : API
  - 지속적인 관리 필요

<br/>

#### 1. private static 중첩
한 클래스에서만 사용하는 `package-private` 범위의 톱 레벨 클래스/인터페이스를 <br/>
해당 클래스 안에 `private static`으로 중첩시키는 것

➡ 바깥 클래스 하나에서만 접근 가능

<br/>

#### 2. package-private 으로 좁히기
public일 필요가 없는 클래스의 접근 수준을 
`package-private` 범위의 톱 레벨 클래스로 좁히는 것
- 내부 구현으로써 바꾸는 것

➡ 해당 패키지의 API가 아니라면 내부 구현으로서 [장점](#정보-은닉의-장점) 활용

```text
1. 클래스의 공개 API 신중한 설계
2. 우선 모든 멤버 private 설정
3. 동일 패키지 내 타 클래스의 접근이 필수인 멤버만
   선택적으로 package-private 전환

* 만약 3번과 같은 권한 해제가 자주 발생하면
  설계 측면에서 추가적인 컴포넌트 분리를 고민해야 한다.
```

_단, `Serializable`을 구현한 클래스의 경우,<br/>
`private`/`package-private` 필드이더라도 의도치 않게 **공개 API**가 될 수도 있다._

<br/>

#### 3. protected 주의
- public 클래스의 **protected 멤버** → 공개 API
  - `protected` 멤버의 수는 적을수록 좋다.

<br/>

#### 4. public 클래스의 "인스턴스 필드"는 되도록 public 금지

- **가변 객체** 참조
- `final`이 아닌 인스턴스 필드 `public` 선언

위 두 방식으로 필드를 설계하면<br/>
그 필드에 담을 수 있는 <u>값에 대한 제한력이 사라지며</u><br/>
해당 필드와 관련된 모든 것은 <u>불변식을 보장할 수 없게 된다</u>.

즉, 클래스가 `public 가변 필드`를 갖게 되면 일반적으로 <u>Thread-Safe 하지 않고</u><br/>
리팩토링에도 어려움을 겪게 된다.

<br/>

---
## "상수"만 public 
추상 개념을 완성하는 데에 꼭 필요한 구성요소로써의 "**상수**"라면<br/>
`public static final` 필드로 공개해도 문제 없다.

단, 이러한 필드들은 다음 조건을 지켜야 한다.
- **기본 타입** 값 참조
- **불변** 객체 참조

> ⁜ <u>길이가 0인 아닌</u> **배열**은 모두 변경 가능하니 주의 
- 클래스에서 public static final 배열 **필드 선언** 금지
- 해당 배열 필드 직접 반환 **접근자 메서드** 제공 금지
```java
public class Thing{}

// 보안 허점
public class Constant {
  public static final Thing[] VALUES = {};
}
```

해결방법은 2가지이다.
- 배열 값은 `private`으로 만들고 <u>`public` **불변 리스트**를 추가</u>
- 배열 값을 `private`으로 만들고 <u>**복사본**</u>을 반환

```java
import java.util.* ;

public class Thing {
}

public class SolutionA {
  private static final Thing[] PRI_VALUES = {};
  public static final List<Thing> VALUES =
          Collections.unmodifiableList(Arrays.asList(PRI_VALUES));
}

public class SolutionB {
  private static final Thing[] PRI_VALUES = {};
  public static final Thing[] values() {
        return PRI_VALUES.clone();
  }
}
```