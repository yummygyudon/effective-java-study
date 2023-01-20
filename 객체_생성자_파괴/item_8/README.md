# [Item 8] `finalizer` & `cleaner` 사용 지양
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [미보장](#프로-미보장러)
- [성능 문제](#2-캐시)
- [보안 문제](#3-리스너콜백)
- [소멸자 대안](#대안)
- [소멸자의 쓰임](#소멸자의-쓰임)
- [개인적 예제](#-개인적-예제)

</div>
</details>

<br/>

---
Java : 2가지 **소멸자** 제공 

- `finalizer`
  - Java 9 : deprecated API
- `cleaner`
  - `finalizer` 대안 : 자신을 수행할 스레드 제어 가능
  - 여전히 즉각 수행에 대한 보장 없음

소멸자는<br/>
<u>**높은 오동작** 확률, **낮은 성능**, **이식성** 문제</u>를 야기하기 때문에<br/>
기본적으로 쓰지 말아야하는 API이다.<br/>
( _여기서의 이식성 : <u>Java 9부터 deprecated 되었기 때문</u>에 하위 버전과의 이식성_ )

비록 `cleaner`가 `finalizer`의 대안이라 하더라도<br/>
단점은 여전하기 때문에<br/>
동일하게 기본적으로 쓰지 말아야하는 기능이다.

> ⁜ `C++`의 파괴자(Destructor)와는 다른 개념
> - 특정 객체와 **관련된 <u>자원을 회수</u>하는 보편적인 방법** <br/>→ ✵ `Java` : `Garbage Collector`
> - 생성자의 꼭 필요한 대척점
> - 비메모리 자원 회수 용도로도 사용 → `Java` : `try~ with~ resources` / `try~ finally`

<br/>

## 프로 미보장러
> <span style="color:grey">_**⟦ Cleaner API 명세 중 ⟧**<br/>
> The behavior of cleaners during System.exit is implementation specific.<br/> No guarantees are made relating to whether cleaning actions are invoked or not.
> <br/> <br/> System.exit 동안 클리너의 동작은 구현에 따라 다릅니다.<br/> 청소 작업의 호출 여부와 관련하여 보장되지 않습니다._

1. **즉시 수행**된다는 보장이 없음 → `제 때 실행되어야 하는 작업`에서 절대 사용 불가
   - GC의 대상은 맞지만 된다는 **보장이 없는 것**
   - 가비지 컬렉터 알고리즘 구현에 따라 동작 천차만별
   - 기본적으로 finalizer 스레드의 우선순위가 다른 애플리케이션 스레드보다 낮음.
   - 해당 인스턴스의 자원 회수가 제멋대로 지연


2. **수행 여부**조차 보장되지 않됨 → 꼭 실행되어야만 하는 `상태를 영구적으로 수정하는 작업`에서 절대 사용 불가
   - 공유 자원(DB)를 다루는 시스템에서 매우 위험
     - 공유 자원의 영구 락(lock)해제를 맡겨놓으면 분산 시스템 전체가 서서히 멈춤
   - _`System.gc`, `System.runFinalization` 메서드는 실행 가능성은 높여주지만 보장은 되지 않음._
   - _`System.runFinalizersOnExit`, `Runtime.runFinalizersOnExit` 메서드는 보장은 해주지만 심각한 결함 야기_


3. 동작 중 **발생한 예외 무시**
   - 경고조차 출력 ❌


4. 처리할 **작업이 남아있더라도 종료**
   - 해당 객체는 자칫 마무리가 안된 상태로 남을 수 있음.
   - 어떻게 동작할지 예측 불가

<br/>

## 성능 문제

> <u>**가비지 컬렉터**가 수거하기 까지의 시간</u>과 `finalizer`를 사용했을 때의 시간의 차이는 천지차이<br/>
> (**안정망 방식**을 활용해서 성능 개선 가능)

<br/>

## 보안 문제
> `finalizer`를 사용한 클래스는
> `finalizer`공격에 노출되어 심각한 보안 문제 야기 가능

### `finalizer` 공격

```
"A 클래스" 와 A를 상속받는 "B 클래스"가 있을 때,
B 클래스가 finalize메서드를 오버라이딩한 상태에서 
인스턴스 생성할 때 예외를 발생시킨다고 가정.

-> 생성하며 예외 던질 때, 원래는 죽어야하지만 "죽을 때 finalize가 실행"됨

-> finalize 로직에서  "메서드나 정적 필드에 접근"할 수 있음

-> GC가 안되도록 만들 수 있음 -> 죽지않고 살아남 -> 메서드를 못쓰게 만들어야 하지만 불가능함
```

정리하자면
- `생성자`/`직렬화` 과정 **예외 발생** → **생성되다 만 객체** 내에서 <u>하위 클래스의 `finalizer`</u> 수행
  - **정적 필드**에 **자신을 참조**시켜버려 <u>GC가 수집하지 못하게</u> 만듦
  - 미완성의 객체의 메서드를 호출해버려 **허용하지 않은 작업 수행**하는 사고 발생

객체 생성을 막으려면 생성자에서 예외를 던지는 것만으로도 해결할 수 있지만<br/>
`finalizer`가 있다면 불가능하다.
(**예외 발생을 무시**하는 특징)

**부모 클래스**에서 `finalize` 메서드를 작성할 때,<br/>
해당 <u>`finalize`메서드를 `final`로</u> 만들면 **<u>상속을 못받게</u> 해서** 안전하게 클래스 방어


<br/>

---
## 대안

**반납이 필요**한 자원이라면
`AutoClosable`을 구현하여<br/>
클라이언트 측에서 **해당 인스턴스를 다 쓰고 나면** <u>Override한 `close` 메서드를 호출</u>하는 방법<br/>
- 기본적으로 close를 오버라이딩 할 때, <u>Throwable을 권장</u>하는데 **예외 발생하게끔** 할 수 있음<br/>
(  <u>예외가 발생하더라도</u> **제대로 종료되도록** <u>`try~ with~ resources`</u> 를 사용)


- JDBC에서 Connection을 사용할 때 `finally 블럭`에 **null 체크**와 더불어 <u>순서를 지키면서 `close` **메서드를 호출**</u>하는 이유

> 각 인스턴스는 " <u>자신이 닫혔는지 추적하는 것</u> "이 좋다.<br/>
> (`close` 메서드에서 <u>본 객체는 더 이상 유효하지 않음</u>을 **필드에 기록**<br/> → _다른 메서드 : 해당 필드를 검사해서 **객체가 닫힌 후에 불렀다면** `IllegalStateException` 던지기_)

> 원래 클라이언트 쪽에서 close 해주는 것이 바람직함.<br/>
> (`try~with~resources`로 암묵적으로 보장하거나 `try~finally`로 명시적으로 보장)



<br/>

## 소멸자의 쓰임

### 1. `close` 미호출에 대한 대비 "안전망"
> **안했을 수도 있다는 가정**에 따라 " 안전망(Safety Net) "으로<br/>
> 보장되진 않지만 소멸자라도 사용해서 대비하는 방법

 : <u>늦게라도</u> 자원 회수를 할 수 있도록 해주는 방법<br/>
 ( 라이브러리 중 일부 클래스는 **안정망 역할의 finalizer 제공** <br/>_ex. 자원의 종료가 중요한 클래스들 `FileInputStream`,`FileOutputStream`,`ThreadPoolExecutor`, ..._)

✵ 실제로 `java.sql.Connection`는 과거 안전망으로 finalizer가 있었다가 현재는 `AutoClosable`을 구현하고 있음.<br/>
`public interface Connection  extends Wrapper, AutoCloseable`

<br/>

### 2. "네이티브 피어(native peer)"와 연결된 객체
> ⁜ **네이티브 피어(native peer)** : 일반 자바 객체가 <u>네이티브 메서드를 통해 기능을 위임</u>한 **네이티브 객체**

네이티브 피어는 일반 자바 객체가 아닌 탓에<br/>
**GC**가 **인식하지 못한다**.

즉, <u>자바 객체(자바 피어)가 회수될 때, **네이티브 페어까지 회수하지 못한다**는 것</u>이다.

이러한 경우가 바로 `cleaner`, `finalizer`가 처리하기에 적당한 작업이다.<br/>
( 단, 네이티브 피어가 <u>심각한 자원을 가지지 않으</u>면서 <u>성능 저하가 감당 가능</u>할 때만 )

- _네이티브 피어가 사용하는 자원을 <u>즉시 회수</u>해야된다면 `close`를 사용해야 함._


---
### ⁜ 개인적 예제
> <span style="color:grey">_**⟦ Cleaner API 명세 중 ⟧**<br/>
> Cleanable represents an object and a cleaning action registered in a Cleaner.<br/>
> Unregisters the cleanable and invokes the cleaning action.<br/>
> The cleanable's cleaning action is invoked at most once regardless of the number of calls to clean.<br/><br/>
> register()을 통해 연결했던 Runnable 객체를 헤제한다.<br/>
> Cleanable의 청소 작업은 청소 호출 횟수에 관계없이 **최대 한 번만** 실행된다._</span>

> <span style="color:grey">_**⟦ Runnable API API 명세 중 ⟧**<br/>
> Runnable is used to create a thread,<br/>
> starting the thread causes the object's run method to be called in that separately executing thread.<br/><br/>
> Runnable은 스레드를 생성하는 데 사용되며,<br/>
> 스레드를 시작하면 객체의 실행 메서드(run())가 별도로 실행되는 스레드에서 호출됩니다._</span>


```java
// 자원 클래스
public class SimpleResource implements AutoCloseable{
    private boolean closed ;
    private static final Cleaner CLEANER = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    private final  ResourceCleaner resourceCleaner;
    public SimpleResource(){
        this.resourceCleaner = new ResourceCleaner();
        /**
         * Cleanable 객체가 자원 클래스에 대한 청소용 인스턴스로서 Runnable 객체 등록
         * 
         * ResourceCleaner 객체와 SimpleResource 객체는 서로 참조 관계가 아니다.
         * ("순환 참조" 가 생겨버려서 GC가 "SimpleResource를 다썼을 때 회수를 못해감"
         */
        // Cleanable register(Object obj, Runnable action) 
        // Registers an object and a cleaning action to run
        // : Cleanable represents an object and a cleaning action registered in a Cleaner.
        this.cleanable = CLEANER.register(this, resourceCleaner);
    }
    public static class ResourceCleaner implements Runnable{
        @Override
        public void run() {
            System.out.println("청소! 청소!");
        }
    }

    public void sayHi() {
        System.out.println("Hi!");
    }

    @Override
    public void close() throws RuntimeException {
        if(this.closed){
            throw new IllegalStateException();
        }
        closed = true;
        System.out.println("Close!");
        cleanable.clean();
    }
}
```
- **실제 청소하는건 `Cleanable` 객체**
    - finalize와는 다르게 <u>청소 작업을 할</u> **별도의 쓰레드**가 필요함 → `Runnable` 객체가 필요함
        - Runnable 객체는 청소 전용 쓰레드 생성을 위해 만들어진 객체기 때문에 <br/>
      Override한 `run()`에는 **청소 작업 로직**을 가져야함

<br/>

- `ResourceCleaner` 클래스가 **정적** 중첩 클래스인 이유
    - 정적이지 않을 경우, 자동으로 **바깥 객체의 참조**를 갖게 됨.
    - <u>람다</u> 역시 바깥 객체의 참조를 갖기 때문에 사용하지 말 것

```java
// 클라이언트
public class SimpleApplication {
    public static void main(String[] args) {
        /**
         * 1. close에 대한 보장이 없음
         */
        SimpleResource simpleResource = new SimpleResource();
        simpleResource.sayHi();
        System.gc();
        
        /**
         * 2. try~ with~ resource~ 로 close 암묵적 보장 : 지역 변수에 대해서만 가능
         * 
         * Hi!
         * Close!
         * 청소! 청소!
         */
        try(SimpleResource simpleResource = new SimpleResource()){
            simpleResource.sayHi();
        }
        
        /**
         * 3. try~ finally~ 로 close 명시적 보장
         *
         * Hi!
         * Close!
         * 청소! 청소!
         */
        SimpleResource simpleResource = new SimpleResource();
        try{
            simpleResource.sayHi();
        }finally {
            simpleResource.close();
        }
    }
}
```