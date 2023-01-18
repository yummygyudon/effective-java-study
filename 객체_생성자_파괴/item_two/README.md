# [Item2] 생성자에 매개변수가 많다면 "빌더"를 고려하라.
<details open>
    <summary><b>Index</b></summary>
    <div markdown="1">

- [생성자 방식](#생성자-방식)
    - [점층적 생성자 패턴](#1-점층적-생성자-패턴)
    - [자바 빈즈 패턴](#2-자바-빈즈-패턴)
    - [빌더 패턴](#3-빌더-패턴)

      </div>
</details>

<br/>

---
## 생성자 방식

> " **선택적 매개변수가 많을 때**, 적절한 대응을 하기 어렵다. "
> 
>  _* `정적 팩토리`와 `생성자` 모두가 가진 제약_

상황에 따라 다르게 필요한 특정 인스턴스를 만들기 위해서<br/>
매개변수 또한 상황에 따라 다르게 선택할 수 있어야 한다.


### 1. 점층적 생성자 패턴
> : **Telescoping Constructor** Pattern

- <u>**필수 매개변수만 받는 생성자**</u>를 시작으로 <br/>점차 선택 매개변수 갯수에 따라 **맞춤형 생성자**를 새롭게 정의하는 방식 

#### ⟦ 단점 ⟧
- <u>설정하길 원치 않는 매개변수까지</u> **어쩔 수 없이 값을 지정**해주어 포함시켜야 한다.
- 새로운 <u>매개변수가 늘어날 때마다</u> **새로운 생성자**가 필요해진다.
- 클라이언트 측에서 <u>코드를 **작성**하거나 **읽기**가 어렵다.</u>
  - 생성자 매개변수 **순서, 의미, 갯수**를 파악하기 힘들다.
  - `타입이 같은 매개변수 연속적 배치` → **버그 확인 어려움**
  - `매개변수 순서 실수` → Compiler 파악 불가능 → **런 타임**에 예외 발생

<br/>

### 2. 자바 빈즈 패턴
> : **Java Beans** Pattern
 
1. **매개변수가 없는 생성자** 선언 : **선** <u>인스턴스 생성</u>
2. `setter` 메서드 호출을 통한 값 설정 : **후** <u>값 설정</u>

#### ⟦ 단점 ⟧
- 온전한 객체 하나 만들기 위해 **실행되어야 할 메서드가 많음**
- 온전한 객체 생성 전까지는 " <u>**일관성(Consistency)** 붕괴</u> "
  - 클래스를 " **불변**으로 만들 수 없음 "
  - _**스레드 안전성** → 프로그래머 추가 작업 필요_

_* **생성이 끝난 객체**를 **수동**으로 **얼리고**(`freeze`) <u>얼리기 전까진 사용할 수 없도록</u> 하는 방법도 있지만<br/>
사용성이 좋지 않아 거의 쓰이지 않음.<br/>
( 또한, 컴파일러가 `freeze`가 <u>호출에 대한 보증 방법이 없음</u> → **런 타임 오류** 취약 )_

<br/>

### 3. 빌더 패턴
> : **Builder** Pattern
> 
> _* Telescoping Constructor Pattern과 Java Beans Pattern의 대안_

클라이언트가 필요한 객체를 직접 만들지 않고<br/>
필수 매개변수만으로 생성자/정적 팩토리를 호출함으로서 해당 클래스의 빌더 객체를 얻고<br/>
클래스에 직접적인 setter 메서드가 아닌<br/>
얻은 빌더에서의 setter 메서드로 원하는 선택 매개변수들을 설정하는 방식이다.

마지막으로 `build()` 메서드를 통해 해당 클래스 타입의 불변 객체를 반환한다.

- 생성할 클래스 내부 "**정적 멤버 클래스**" (중첩 클래스) 로서 해당 클래스의 빌더를 선언 


```java
public class TestMainClass {
    private final int a; // 본 클래스의 필수 매개변수
    private final int b; // 본 클래스의 필수 매개변수
    private final String c; // 선택 매개변수
    private final String d; // 선택 매개변수
    private final List<Integer> e; // 선택 매개변수

  
    private TestMainClass(Builder builder){
        this.a = builder.a;
        this.b = builder.b;
        this.c = builder.c;
        this.d = builder.d;
        this.e = builder.e;
    }
    
    /**
    * 정적 팩토리 방식으로 구현
    * TestMainClass.Builder
     * 
     * private으로 선언했기 때문에 다른 클래스에서는 절대 접근할 수 없도록 하면서도
     * 바깥 클래스에서는 접근할 수 있게끔 한다.
     * → TestMainClass의 생성자에서 
     * " Builder의 private 멤버 변수에 접근할 수 있는 이유 "
    * 
    * return this 를 통해서 절차적이지 않더라도 연쇄적인 매개변수 설정이 가능
    */
    public static class Builder {
      // 필수 매개변수는 하단의 Builder 생성자 매개변수로서 대입
      private final int a; 
      private final int b; 
      
      // 선택 매개변수들에 대해 기본값으로 초기화
      private final String c = ""; 
      private final String d = ""; 
      private final List<Integer> e = Collections.emptyList();
      
      public Builder(int a, int b){
          this.a = a;
          this.b = b;
      }
      public Builder c(String value){
          this.c = value;
          return this;
      }
      public Builder d(String value){
        this.d = value;
        return this;
      }
      public Builder e(List<Integer> valueList){
        this.e = valueList;
        return this;
      }
      
      public TestMainClass build() {
          // TestMainClass는 Builder를 매개변수로 하는 생성자 하나만 있으면 됨.
          return new TestMainClass(this);
      }
    }
}
```

위와 같은 구조를 통해<br/>
본래 클래스의 생성자는 **불변 객체를 반환**할 수 있으며<br/>
모든 매개변수에 대한 설정은 **Builder가 책임지고 관리**하다가<br/>
<u>`build()`가 호출되었을 때만</u> 본 **클래스의 생성자**를 호출한다.
> _Called by_ " **플루언트 API** (Fluent API) " / " **메서드 연쇄**(Method Chaining)"

- 빌더에서 객체의 일관성 유지를 위한 매개변수 검사 가능
  - 빌더의 **생성자** : 각각의 **필수 매개변수**에 대한 검사
  - 빌더의 **메서드** : 각각의 **선택 매개변수**에 대한 검사
  - `build()` : **여러 매개변수에 걸친 불변식** 검사

빌더에서 **모든 매개변수에 대해 각각의 검사가 가능**하기 때문에<br/>
어떤 매개변수가 잘못되었는지에 대해 상세한 `IllegalArgumentException` 예외를 발생시켜<br/>
**불변식**(**invariant**)을 보장할 수 있다.

<br/>

#### ⁜ 계층적 설계 클래스 & 빌더 패턴
`추상 클래스 - 추상 빌더`, `구체화 클래스 - 구체화 빌더`와 같은 구조로<br/>
**각 계층**의 클래스에 **관련 빌더**를 **멤버로 정의**할 수 있다.



```java
public abstract class TestParentClass {
    public enum State { WAIT, EXECUTE, STOP, DONE }
    final Set<State> states;

    TestParentClass(Builder<?> builder){
        states = builder.states.clone();
    }
    
    abstract static class Builder<T extends Builder<T>> {
        EnumSet<State> states = EnumSet.noneOf(State.class);
        
        public T addState(State state){
            states.add(Objects.requireNonNull(state));
            return self();
        }
        
        abstract TestParentClass build() ;
        
        // 하위 클래스는 이 메서드를 Overriding 하여
        // this 반환하도록 한다.
        protected abstract T self();
    }
}
```
<br/>

- `TestParentClass.Builder` 클래스는 "**재귀적 타입 한정**"을 이용하는 제너릭 타입으로<br/>
추상 메서드인 `self`를 추가하여<br/> 
**하위 클래스**에서는 <u>형 변환 없이도</u> **메서드 연쇄**가 가능하도록 할 수 있다.

> _**self 타입이 없는** 자바를 위한<br/>
> 위와 같은 방식을 "<u>**시뮬레이트한 셀프 타입** (simulated self-type)</u>" 이라고 한다._

```java
// 상태 지속 시간 매개변수를 필수로 받음
public class TestChildClassA extends TestParentClass {
    public enum Duration { SECOND, MINUTE, HOUR }
    private final Duration duration;
    
    private TestChildClassA(Builder builder){
        super(builder);
        duration = builder.duration;
    }

    public static class Builder extends TestParentClass.Builder<Builder> {
      private final Duration duration;

      public Builder(Duration duration){
          this.duration = Objects.requireNonNull(duration);
      }
      
      @Override
      public TestChildClassA build() {
          return new TestChildClassA(this);
      }

      @Override
      protected Builder self() { return this; }
  }
}

// 상태 우선 순위 매개변수를 필수로 받음
public class TestChildClassB extends TestParentClass {
    private final int priority;

    private TestChildClassA(Builder builder){
        super(builder);
        priority = builder.priority;
    }

    public static class Builder extends TestParentClass.Builder<Builder> {
        private final int priority;

        public Builder(int priority){
            this.priority = Objects.requireNonNull(priority);
        }

        @Override
        public TestChildClassB build() {
            return new TestChildClassB(this);
        }

        @Override
        protected Builder self() { return this; }
    }
}
``` 
위와 같이 계층적으로 상속받아 구현했을 때,<br/>
각 하위 클래스의 빌더가 정의힌 `build()` 메서드에 의헤<br/>
구체 하위 클래스 객체를 반환하도록 할 수 있다.

이와 같이<br/>
<u>상위 클래스의 메서드가 정의한 반환 타입이 아닌</u> 그 **하위 타입**을 **반환**하는 기능을<br/>
" **반환 타이핑** (**Covariant Return Typing**) "이라고 한다.<br/>
( → 클라이언트는 형 변환에 신경쓰지 않고 동일한 이름의 `build()` 메서드 사용 )

- 점층적 생성자보다 "**코드 가독성** 우수" & "**간결**"
- 자바 빈즈보다 "**안전성** 우수"

<br/>

### 주의점
빌더 하나로 여러 객체를 순회하면서 만들 수 있고<br/>
**매개변수에 따라 다른 객체도 만들 수 있기 때문에**<br/>
상당히 유연한 것은 사실이다.

허나<br/>
생성 비용은 물론 크지 않지만<br/>
성능에 민감한 상황에서는 문제가 될 수 있다.

또한, [점층적 생성자 패턴](#1-점층적-생성자-패턴) 보다는 코드가 많아지기 때문에<br/>
매개변수가 4개 이상은 되어야 효율적이라고 볼 수 있다.

_하지만 API는 시간이 지날수록 매개변수가 많아지는 경향이 있기 때문에<br/>
왠만해선 빌더로 시작하는 편이 나을 때가 많다._