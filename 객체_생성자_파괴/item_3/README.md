# [Item3] private 생성자나 열거 타입으로 싱글톤을 보증하라.
<details open>
    <summary><b>Index</b></summary>
    <div markdown="1">

- [싱글톤 방식](#싱글톤-방식)
  - [private 생성자](#1-private-생성자)
  - [정적 팩토리 방식](#2-정적-팩토리-방식)
  - [열거 타입](#3-열거-타입)

    </div>
</details>

<br/>

---
## 싱글톤 방식
> **싱글톤**(**Singleton**) : <u>인스턴스를 **오직 하나**만 생성</u>할 수 있는 클래스
> 
> ⁜ 대표적인 예 : 함수같은 `무상태(State-less) 객체` / 설계상 유일해야 하는 `시스템 컴포넌트`

- 클라이언트를 **테스트**하기 **어려움**
  - <u>**타입**을 **인터페이스**로 정의하고 해당 인터페이스를 구현하여 만든 인스턴스</u>가 아니라면<br/>
  **가짜(`mock`) 구현으로 대체가 불가능**

<br/>

### 1. private 생성자
- `public static final` 인스턴스 멤버 변수
    ```java
    public class Component {
        public static final Component INSTANCE = new Component();
        private Component(){
            // Reflection API에 의한 private 생성자 호출 방지를 위하여 예외 발생
            if (INSTANCE != null){
                throw new RuntimeException("이미 인스턴스가 존재합니다.");
            }
        }
    } 
    ```
    - `private` 생성자는 `Component.INSTANCE`를 <u>**초기화**할 때 딱 한 번만</u> 호출된다.
      - `public`이나 `protected`생성자가 없으므로 해당 클래스의 인스턴스가 **전체 시스템에서 하나뿐**임이 **보장**
      - 단, **리플렉션 API**인 `AccessibleObject.setAccessible`에 의해 `private`생성자가 호출될 위험이 있다.
      <br/>→ 예외 처리 구문

#### ⟦ 장점 ⟧
1. 해당 클래스가 싱글톤임을 API에 명백히 드러난다.
   - `public static` 필드가 `final`이기 때문에 절대 **다른 객체를 참조할 수 없다**.
2. 간결하다.


<br/>
<br/>

---
### 2. 정적 팩토리 방식
- `정적 팩토리` 인스턴스 멤버 변수
    ```java
    public class Component {
        private static final Component INSTANCE = new Component();
        private Component(){
            // Reflection API에 의한 private 생성자 호출 방지를 위하여 예외 발생
            if (INSTANCE != null){
                throw new RuntimeException("이미 인스턴스가 존재합니다.");
            }
        }
        
        public static Component getInstance() {
            return INSTANCE;
        } 
        // 싱글톤임을 보장
        public Object readResolve(){
            // 진짜 Component 반환 & 가짜 Component G.C에 맡김
            return INSTANCE;
        } 
    } 
    ```
    - `Component.getInstance`는 <u>**항상** 같은 객체의 참조를</u> 반환한다.
        - 내부 `private static final` 객체를 참조하여 반환하기 때문에 <br/>
      해당 클래스의 인스턴스가 **전체 시스템에서 하나뿐**임이 **보장**
        - 단, **리플렉션 API**인 `AccessibleObject.setAccessible`에 의해 `private`생성자가 호출될 위험이 있다.
          <br/>→ 예외 처리 구문

#### ⟦ 장점 ⟧
> _아래 장점들이 필요하지 않다면 위 `public 필드` 방법이 더 좋다._
1. API를 바꾸지 않고도 싱글톤이 아니게끔 바꿀 수 있다.
   - 호출되는 스레드별로 다른 인스턴스를 넘겨주게 할 수 있다.
2. 정적 팩토리를 <u>**제너릭 싱글톤</u> 팩토리**로 만들 수 있다.
3. 정적 팩토리의 Instance를 **공급자**(`Supplier<T>` **방식**)로 사용할 수 있다.

<br/>
<br/>

> [`정적 팩토리`와`private 생성자`] 중 하나의 방식으로 만든 싱글톤 클래스를 "**직렬화**"하려면 <br/>
> <u>`Serializable`를 구현한다고 선언</u>하는 것만으로는 **부족**
>    - 모든 인스턴스 필드를 "<u>**일시적**(`transient`)이라 선언"</u> & "<u>`readSolve` 메서드</u>" 제공 
>      - 직렬화 인스턴스에 대해 <u>**역직렬화**할 때마다 **새로운 인스턴스** 발생</u> **방지**



<br/>

---
### 3. 열거 타입   
> _대부분의 상황에서는 <u>원소가 하나뿐인 **열거 타입**</u>이 가장 좋은 방법이다._
 
```java
public enum Component {
    INSTANCE;
    
    public void methodA() {...}
}
```
- (_public 필드 방식과 유사하지만_) 더 **간결** & 간단하게 **직렬화** 가능
- 복잡한 **직렬화** 상황 / **리플렉션 공격**에서도 <u>제 2의 인스턴스 생성</u>을 **완벽히 차단**

_단, 만드려는 싱글톤이 <u>**Enum 외의 클래스**를 **상속**해야 한다</u>면 사용할 수 없다.<br/>
(<u>다른 **인터페이스**를 구현</u>하도록 선언은 가능)_