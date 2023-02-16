# [Item 13] `clone` 재정의는 주의해서 진행하라
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [Cloneable 인터페이스의 위험성](#cloneable-인터페이스의-위험성)
- [제대로 된 Cloneable 구현](#제대로-된-cloneable-구현)
  - [clone 재귀](#1-clone-재귀-호출)
  - [상태 재생성 고수준 메서드 호출](#2-상태-재생성-고수준-메서드-호출)
- [유의점](#유의점)
- [Cloneable 안써버리기](#복사-생성자--복사-팩토리)

</div>
</details>

<br/>

---
우선 `Cloneable`은 "**Mix-in Interface(믹스인 인터페이스)**"로 <u>복제해도 되는 클래스임을 명시</u>하는 용도의 인터페이스이다.<br/>
하지만 `clone`이라는 복제 메서드는 `Cloneable`이 아닌 `Object`에 있고 심지어 `protected` 이다.

그렇기 때문에 Cloneable을 구현했다고 해서 외부 객체에서 clone을 호출할 수 있는게 아니다.<br/>
(_물론 Reflection을 사용하면 가능하겠지만 해당 객체에 접근이 허용된 `clone`을 제공하지 않을 수도 있기 때문에 100% 보장된 것이 아니다._)</br>

✵ _비록 단점은 많지만 Cloneable 방식은 많이 쓰이기 때문에 몰라서는 안된다._

<br/>

## `Cloneable` 인터페이스의 위험성
"`Object`"에 있는 protected 메서드인 `clone`의 동작 방식을 결정한다.

만약 아무것도 건드리지 않고 Cloneable을 사용할 땐, 다음과 같이 동작된다.
- `Cloneable` 구현 객체에서 `clone` : 해당 객체 **필드들을 하나하나 복사**한 객체 반환
- `Cloneable` <u>구현하지 않은</u> 객체에서 `clone` : `CloneNotSupportedException`

본래 보통의 인터페이스를 구현한다는 것은<br/>
해당 클래스가 인터페이스 내에 정의된 기능을 제공한다는 의미와 같지만<br/>
`Cloneable`의 경우는 `clone`메서드를 **public으로 제공**하면서 <br/><u>상위 클래스에 정의된 protected 메서드의 동작 방식을 변경한 것이 된다.</u>

(_위 같이 위험하게 쓰지 말자._)

clone이라는 단어의 개념과 같이 복제가 제대로 이뤄지도록 하기 위해서는<br/>
**해당 클래스**와 **모든 상위 클래스**는 <u>"복잡 + 비강제적 + 허술한 프로토콜"</u>을 지켜야 한다.

하지만 프로토콜을 지킨다하더라도
쉽게 깨지고 위험한 모순적인 상황이 발생한다.
바로 " <u>**외부**에서 **생성자 호출 없이 객체를 생성**하게 되는 것</u> "이다.

```plain
이상하면서 강제적이지도 않은 규약이다.

⟪ clone 메서드 일반 규약 ⟫

x.clone() != x  : True
x.clone().getClass() == x.getClass() : True
x.clone().equals(x) : 일반적으로 True

clone 메서드가 반환하는 객체는 super.clone을 호출해 얻어야만 한다.(관례상)

해당 클래스와 Object를 제외한 모든 상위 클래스에 대해
x.clone().getClass() == x.getClass() : True

반환 객체와 원본 객체는 독립적이어야 한다. (관례상)
: super.clone으로 얻은 객체 필드 중 하나 이상을 반환 전에 수정해야 할 수도 있다.
```
문제는 **강제성이 없다**는 것이다.<br/>
(강제성만 있다면 "**생성자 연쇄(Constructor Chaining)**"과 유사한 매커니즘이다.)

즉, " <u>super.clone이 아닌 **생성자**로 clone 메서드의 반환값을 생성해 반환</u> "할 수 있다는 것이고<br/>
이는 <u>**하위 클래스**에서 super.clone을 호출했을 때</u> **잘못된 동작**을 야기한다.

만약 <u>하위 클래스를 만들 수 없는</u> **final 클래스**라면 걱정 없지만<br/>
애초에 final 클래스에선 _Cloneable을 구현할 이유도 없기 때문에 어쩌면 논외의 케이스인 것이다._

<br/>

## 제대로 된 `Cloneable` 구현 
> 우선 **제대로 동작하는 clone 메서드를 가진 <u>상위 클래스**</u>를 상속받아야 한다.

- `super.clone` 호출
   - 원본의 **완벽한 복제본 반환** : 모든 필드가 원본 필드와 동일한 값을 가짐
   - 기본 타입 / 불변 객체 참조 필드일 경우 1번에서 끝내면 된다.
     - Java에서 지원해주는 **공변 반환 타이핑 방식**을 활용하여 <u>클라이언트 측에서 **형변환 작업이 필요하지 않도록**</u> 해준다.
   ```java
   /**
    * Object를 반환하게 되겠지만 PhoneNumber 클래스 타입(본 타입)으로 casting 해주는 것을 권장한다.
    * : Java - " 공변 반환 타입 (Covariant Return Typing) " 지원
    * 
    *  -> ⟦ 재정의한 메서드의 반환 타입 ⟧ 은 
    *     " 상위 클래스의 메서드가 반환하는 타입의 ⟦하위 타입⟧ "일 수 있다.
    */
   public class PhoneNumber implements Cloneable {
        @Override
        public PhoneNumber clone() {
            try {
                return (PhoneNumber) super.clone();
            } catch (CloneNotSupportedException e) {
                /**
                * 사실 애초에 발생할 일이 없는 예외이지만
                * Object의 clone은 과거부터 checkedException 인 해당 예외를 던지도록 되어 있기 때문에
                * 불필요한 try-catch 문이 필요하다.
                */
                throw new AssertionError(); 
            }
        }    
   }
    ```
   <br/>

❗️ 단, **가변 클래스 참조** 필드를 가진 경우 위 방법에서 끝내면 안된다.

복제할 클래스 내부 필드 중 기본 타입이나 불변 클래스 타입이 아닌<br/>
**가변 객체**를 참조할 때, <br/><u>원본에서 참조하고 있던 객체와 **똑같은 객체를 참조**</u>하게 되는 것이다.

이 말은 즉슨, 원본이나 복제본 중 **하나만 수정하더라도** <br/>
<u>다른 하나도 **같이 수정**</u>되어 불변식을 해치게 되고<br/>
그에 따라 프로그램이 이상하게 동작하면서 `NullPointerException`을 발생시킨다는 것이다.

본래 이러한 동일 객체 참조 문제는 생성자 호출을 함으로서 방지할 수 있지만<br/>
그대로 사용해도 문제 없던 기본 타입 필드나 불변 클래스 타입도 일일이 신경써줘야 하기도 하고<br/>
~~사실 이럴거면 차라리 clone을 안쓰는 것이 바람직하다...~~

`clone`에서 <u>**원본 객체에 영향을 끼치지 않는**</u> 동시에 <u>**복제 객체의 불변식을 보장**</u>하기 위해선<br/>
아래와 같은 방식을 사용할 수 있다.

<br/>

### 1. clone 재귀 호출
- **배열**

우선 예외적일 수 있는 "복사 객체가 <u>**배열**</u>일 경우"를 살펴보자.<br/>
사실 배열일 경우는 단순 `clone` 호출을 통해 복사하는 것이 가장 적절하다.

배열의 `clone`은 런타인 타입과 컴파일 타임 모두가 원본 배열과 똑같은 배열을 반환하기 때문에<br/>
오히려 단순 `clone` 호출을 권장한다.
> ✅ " <u>**배열**</u> "만이 clone 메서드가 복사를 위한 가장 깔끔하고 합당한 방법이다

<br/>

- **Entry** 

반면, 재귀 호출만으로 부족한 경우도 존재한다.<br/>
**해시테이블(Hash Table**)로 예를 들면 단순 배열이 아닌 **배열 내부에 또다른 구조의 객체**가 존재한다.<br/><br/>
물론 동일 객체 참조를 하더라도 안전하다면 문제가 없겠지만<br/>
아닐 경우엔 원본과 같은 객체를 참조하기 때문에 문제가 생길 가능성이 높다.

실제로 HashTable에는 key, value, next로 이루어진 <u>**연결 리스트가 담긴 Entry**로 이루어진 배열</u>이 필드로 존재한다.<br/>
위에 언급했던 문제를 회피하기 위해선 **연결리스트를 재귀적으로 복사**하는 `deepCopy`가 지원된다.

길지만 않다면 잘 작동하겠지만<br/>
만약 각각의 연결리스트가 **길다면** <u>재귀 호출로 인한 "**스택 오버플로**"</u>가 발생할 수 있다.

이러한 경우는<br/>
차라리 재귀적으로 들어가기보단<br/>
<u>각 필드의 값을 가져와서</u> **새로운 객체를 생성하는 반복자**를 활용하여 순회하는 것이 바람직하다.

<br/>

> _본래 **가변 객체 필드**는 **final로 선언**하는 것이 일반적인 활용 방법이다.<br/><br/>
> 허나 <u>Cloneable 아키텍쳐는 해당 용법과 충돌</u>하기 때문에<br/>
> Cloneable을 활용하여 복제할 수 있는 클래스를 만들기 위해서는 <br/>
> **일부 필드에서 final을 제거해야 할 수도 있다.**<br/>
> (단,**가변 객체 공유**가 **안전**하다면 괜찮다.)_


<br/>

### 2. 상태 재생성 고수준 메서드 호출
해당 방법의 절차는 다음과 같다.
1. `super.clone` 호출 : 모든 필드 **초기 상태 설정**
2. <u>원본 객체 **상태를 다시 생성**</u>하는 **고수준 메서드** 호출

훨씬 편리하고 간결하지만<br/>
저수준에서 바로 처리하는 것에 비해 **처리가 느리다**.<br/>
또한 " <u>**필드 단위** 객체 복사</u> "를 기초로 하는 *Cloneable 아키텍쳐 정책과는 어울리지 않는 방법*이다.

<br/>

---
## 유의점
- <u>**재정의 가능성이 있는 메서드**</u> 호출 금지
  - 만약 `clone`이 <u>하위 클래스에서 재정의한 메서드를 호출</u>하게 되면<br/> 더 하위에 있는 클래스는 **복제 과정**에서 **상태 교정의 기회**를 잃게 되버린다.
  - **private** 이거나 **final인 메서드**를 활용하자<br/><br/>


- **" public인 `clone` 메서드** "에서는 `throws 절` 금지
  - _Object의 clone 메서드는 CloneNotSupportedException 예외를 발생시키지만 재정의한 메서드에서는 그렇지 않다._
  - checked Exception을 굳이 발생시키지 않아야 사용하기 편리하다.<br/></br>


- **상속용 클래스**는 <u>Cloneable 구현 금지</u>
  - (그럼에도 구현하겠다면 2가지 방법이 있다.)
    1. Object 방식 모방 : ⟪ 제대로 작동되는 **clone 메서드** + 접근제어자 **protected** + **CloneNotSupportedException** 발생 ⟫
       - **하위 클래스**에게 **Cloneable 구현 여부 선택권** 부여<br/><br/>
    2. **clone을 동작하지 않게 구현** : 하위 클래스 재정의 강제 금지
        ```java
        @Override
        protected final Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException() ;
       }
        ```
       - `super.clone` 강제 금지<br/><br/>


- <u>Cloneable 구현 "**스레드 안전 클래스**"</u> 작성 : clone 메서드의 **동기화 작업** 필요
  - 기존 Object의 clone에서는 동기화에 대한 처리가 없기 때문에<br/>
  <u>super.clone 외에 특별한 연산로직이 없을지라도</u> clone을 **재정의하고 동기화**해주어야 한다.


> **⟦ 정리 ⟧**
> <br/>
> 
> - **Cloneable을 구현**하는 모든 클래스 : clone 재정의
> - **접근 제어자** : public
> - **반환 타입** : <u>클래스 자신</u>
> - 무조건 `super.clone` **호출 이후** 필요한 필드 수정<br/>
> ( 기본 타입 필드 / 불변 객체 참조 클래스 : **필드 수정 필요 ❌** _ _단, '일련 번호'나 '고유 ID'는 수정 필요_ )
 
<br/>

## 복사 생성자 & 복사 팩토리
> ⁜ 복사 생성자 == **변형 생성자**(Conversion Constructor)<br/>
> ⁜ 복사 팩토리 == **변형 팩토리**(Conversion Factory)

사실 Cloneable을 구현하지 않은 클래스를 확장할 때 <br/>
억지로 위 필요 사항을 준수하면서 Cloneable을 구현하는 것은 바보같은 짓이다.

위 내용들은 이미 Cloneable이 구현된 클래스에 대해서 확장할 때만 유의하면 되는 것이고
일반적으로는 "**복사 생성자**", "**복사 팩토리**"를 활용하는 것이 훨씬 유용하다.

- **복사 생성자**
    ```java
    public MyClass(MyClass myClass) { ... }
    ```     
- **복사 팩토리**
    ```java
    public static MyClass newInstance(MyClass myClass) { ... }
    ```     

Cloneable와 비교해보자면

- **생성자 사용** : _언어 모순적이고 위험하지 않은 객체 생성 매커니즘_


- final **필드 충돌 위험** ❌


- 불필요한 **Unchecked Exception 발생 필요** ❌ : CloneNotSupportedException 발생 throws 절 필요 없음.

더 나아가
해당 클래스가 " 구현한 **인터페이스 타입의 인스턴스**를 <u>인수로 받는 것</u> "도 가능하다.<br/>
( _ex. **Collection** / **Map** 타입을 받는 생성자가 <u>모든 범용 컬렉션 구현체</u>에 존재_ )

덕분에 클라이언트는 **원본 구현 타입에 얽매이지 않고**<br/>
복제본의 타입을 직접 선택할 수 있다.<br/>
( _ex. **HashSet** → **TreeSet** 복제 : `new TreeSet<>(HashSet s)`_ )
