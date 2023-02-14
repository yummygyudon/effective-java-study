# [Item 10] `equals`는 일반 규약을 지켜 재정의하라
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [재정의하지 않아도 되는 경우](#재정의하지-않아도-되는-경우)
- [재정의가 필요한 경우](#재정의)
  - [재정의 규약](#재정의-규약)
  - [반사성](#1-반사성)
  - [대칭성](#2-대칭성)
  - [추이성](#3-추이성)
  - [일관성](#4-일관성)
  - [Null이 아니여야 함](#5-must-not-be-null)
- [양질의 equals 재정의 방법](#-양질의-equals-메서드-구현-방법)
- [주의 사항](#-주의-사항-)

</div>
</details>

<br/>

---
> `equals`메서드로 인해 발생하는 문제를 회피하는 가장 쉬운 방법은 " <u>**아예 재정의하지 않는 것**</u> "

### 재정의하지 않아도 되는 경우
1. 각 인스턴스가 본질적으로 **고유**할 경우
   - 애초에 객체 식별성을 위해 구현된`equals` 메서드이기 때문에 재정의할 필요가 없다.
2. " **논리적 동치성(logical equality)** " 검사가 필요 없을 경우
   - 논리적 동치성을 확인하기 위해 재정의해야 하는 것은 맞지만 검사 자체가 필요하지 않을 경우,<br/>
   그냥 재정의를 안하는 것이 안전하다.
3. "상위 클래스에서 재정의한 `equals`"가 **하위 클래스에서도 적절**할 경우
4. <u>`private 클래스`</u>이거나 <u>`package-private`이고 `equals` 메서드를 호출할 필요가 없을</u> 경우
   - _혹시 모를 실행 위험을 방지하기 위해 Override해서 예외 발생시키는 방법을 사용할 수 있다._

<br/>

---
# 재정의
> ⁜ **재정의가 필요한 경우** ⁜<br/>
> <br/>**객체 식별성(Object Identity)** 이 아닌 <u>**논리적 동치성**</u>을 확인해야할 경우에<br/>
<u>상위 클래스의 equals</u>가 **논리적 동치성을 확인하도록 정의되어 있지 않을 때**
 
주로 Integer나 String처럼 값을 표현하는 값 클래스들을 사용할 때,
클라이언트는 두 값 객체의 객체 식별이 아닌 값 비교가 필요하다.

만약 본래 equals를 그대로 사용하게 된다면 같은 값을 갖고 있더라도 서로 다른 객체로 인식하여
같은 값을 가졌음에도 Map의 키나 Set에 공존할 수 있게 된다.

이럴 경우 equals를 논리적 동치성을 확인하도록 재정의 해두면
값을 비교할 수 있을 뿐더러 Map의 키와 Set의 원소로서 사용할 수 있게 된다.
( 값이 같은 인스턴스가 둘 이상 생성되지 않음을 보장하는 인스턴스 통제 클래스라면 재정의하지 않더라도 위 이점들을 누릴 수 있다. )

`Enum` 또한
논리적으로 같은 인스턴스가 만들어지지 않음이 보장되기 때문에 equals를 재정의하지 않더라도
equals를 통해 논리적 동치성까지 확인할 수 있다.

<br/>

## 재정의 규약
> `equals` 메서드는 **동치관계(Equivalence Relation)** 을 구현한다.

대부분의 클래스는 전달받은 클래스가 equals 규약을 지켰다는 가정하에 동작한다.
그렇기에 규약을 어겼을 경우, 문제가 발생할 위험이 높기 때문에

- **반사성**(Reflexivity) : _null 이 아닌 모든 참조 값 x에 대해_ " `x.equals(x)`은 항상 true 이다. " <br/><br/>
- **대칭성**(Symmetry) : _null 이 아닌 모든 참조 값 x, y에 대해_ " `x.equals(y)`가 true 이면 `y.equals(x)` 또한 항상 true 이다. " <br/><br/>
- **추이성**(Transitivity) : _null 이 아닌 모든 참조 값 x, y, z에 대해_ " `x.equals(y)`와 `y.equals(z)`가 true이면 `x.equals(z)` 또한 true 이다. " <br/><br/>
- **일관성**(Consistency) : _null 이 아닌 모든 참조 값 x, y에 대해_ " `x.equals(y)`를 반복적으로 호출하면 항상 true 혹은 항상 false이다. " <br/><br/>
- **Must Not be Null** : _null 이 아닌 모든 참조 값 x에 대해_ " `x.equals(null)`은 항상 false이다. "

<br/>
<br/>

### 1. 반사성
> 객체는 자기 자신과 같아야 한다는 특징

<br/>

### 2. 대칭성
> 두 객체는 서로에 대한 동치 여부에 따라 서로 항상 같은 답을 내놓아야 한다는 특징

```java
public final class CaseInsensitiveString {
   private final String s;

   public CaseInsensitiveString(String s) {
      this.s = Objects.requireNonNull(s) ;
   }
   public String getString(){
       return s ;
   }
   
   @Override
   public boolean equals(Object o) {
       if (o instanceof CaseInsensitiveString) {
           return s.equalsIgnoreCase( ((CaseInsensitiveString) o).getString() ) ;
       }
      if (o instanceof String) {
           return s.equalsIgnoreCase((String) o) ;
       }
       return false ;
   }
}
```

위와 같이 CaseInsensitiveString 이라는 클래스에서는 같은 클래스인 인스턴스와 String 클래스에 대한 equals는 처리가 될 수 있을 것이다.<br/><br/>
하지만 외부에서 String 객체가 CaseInsensitiveString 객체에 대해서 equals 를 실행할 경우,<br/>
String 클래스의 equals에는 CaseInsensitiveString 객체에 대한 처리가 존재하지 않기 때문에 문제가 발생한다.

```
CaseInsentiveString cis = new CaseInsentiveString("abs")
String s = "abs"

cis.equals(s) ; // true
s.equals(cis) ; // false
```

이를 보완하기 위해<br/>
String 클래스의 equals 메서드를 수정하는 것이 아닌<br/>
두 가지 조건을 동시에 만족했을 경우에만 true를 반환할 수 있도록<br/>
논리 연산을 통합시킬 수 있다.
```java
@Override
public boolean equals(Object o) {
   return o instanceof CaseInsensitiveString &&
        ((CaseInsensitiveString) o).getString().equalsIgnoreCase(s) ;
}
```

<br/>

### 3. 추이성
> 첫 번째 객체와 두 번째 객체가 같고, 두 번째 객체와 세 번째 객체가 같다면,<br/> <u>첫 번째 객체와 세 번째 객체도 같아야 한다</u>는 특징

해당 경우는 보통 상속에 의해 문제가 발생할 가능성이 높다.

```
⁜ 좌표를 가진 객체

(x,y) 값을 가지고 있는 Point 객체를 생성하고
이를 상속받아
"색(Color)"라는 추가 필드를 가지며 Point를 상속 받는 ColorPoint 와
"크기(Size)"라는 추가 필드를 가지며 Point를 상속 받는 SizePoint 라는 클래스를 생성한다
```
위와 같은 관계를 가진 3가지 클래스가 있을 때<br/>
같은 면 위에서 각자 좌표를 가진 클래스 인스턴스들은 독립적으로 존재해야 한다는 규칙을 적용한다면<br/>
equals를 통해서 좌표가 같은지를 비교해야 할 것이다.

equals 재정의 방식에 따라 발생하는 문제에 대해서 알아보자.

```java
/**
 * 같은 인스턴스가 아니기 때문에
 * 만약 같은 좌표를 가졌다 하더라도 
 * 
 */
// Point의 equals
@Override
public boolean equals(Object o) {
    if (! (o instanceof Point)) {
        return false ;
    }
    Point p = (Point) o;
    return p.x == x && p.y == y ;
}
// ColorPoint의 equals
@Override
public boolean equals(Object o) {
    if (! (o instanceof ColorPoint)) {
        return false ;
    }
     return super.equals(o) && ((ColorPoint) o).color == color ;
}
```
위처럼 Point를 ColorPoint와 비교했을 땐 서로의 결과가 달라질 수 있다.
Point는 색상을 무시한채 비교하고
ColorPoint는 매개변수 o와 클래스 종류가 다르다고 항상 false만 반환하게 될 것이다.
```
Point p = new Point(1,2);
ColorPoint cp = new ColorPoint(1,2, Color.RED);

p.equals(cp) -> true
cp.equals(p) -> 항상 false
```
<br/>

이를 해결해주기 위해서 아래와 같이 바꿔준다고 해도 문제가 발생한다.

```java
// ColorPoint의 equals
@Override
public boolean equals(Object o) {
    if (!(o instanceof Point)) {
        return false ;
     }
    if (!(o instanceof ColorPoint)){
        // Point 가 들어왔을 때
        return o.equals(this) ; // o.equals(this) 는 true를 반환하게 된다.
     }
    return super.equals(o) &&
        ((ColorPoint) o).color == this.color ;
}

// Point의 equals
@Override
public boolean equals(Object o) {
     if (! (o instanceof Point)) {
        return false ;
     }
     Point p = (Point) o;
     return p.x == x && p.y == y ;
}
```
우선 앞선 방식보단 해결되는 부분이 있긴하다.
```
Point p = new Point(1,2);
ColorPoint cp1 = new ColorPoint(1,2, Color.RED);
ColorPoint cp2 = new ColorPoint(1,2, Color.BLUE);

cp1.equals(p) -> true
p.equals(cp1) -> true

p.equals(cp2) -> true
cp2.equals(p) -> true
```
대칭성은 지켜주게 되었지만<br/>
아래와 같은 예상치 못한 예외가 발생하게 된다.
```
cp1.equals(cp2) -> false
```
추이성이 깨지게 되는 것이다.

사실 객체 지향 언어의 동치 관계에서 나타나는 근복적인 문제지만
구체 클래스를 확장해서 새로운 값을 추가하면서도 equals 규약을 지키는 방법은 없다.

_✵ `if (!(o instanceof ColorPoint))`와 같이 비교 논리 식처럼 하위 클래스가 있으면<br/>
모든 경우에 대한 조건문을 달아줘야하기 때문에<br/>
상속관계에 너무 종속적이게 된다는 문제점이 발생한다._

<br/>

객체 지향의 5원칙인 SOLID 원칙 중 하나인<br/>
리스코프 치환 원칙에 따르면<br/>
해당 타입의 모든 메서드는 하위 타입에서도 똑같이 잘 작동해야 한다.

즉, 위 Point의 하위 클래스도 정의 상 여전히 Point이기 때문에 어디서든 Point로서 활용이 가능해야 된다는 것이다.

<br/>

해당 조건을 충족하기 위해선<br/>
`getClass`를 통한 일치 여부, `instanceof`를 통한 객체 비교 등으로 비교하며<br/>
여러 논리식을 혼합하기 보단<br/>

각 하위 클래스에서는 필드로 Point 객체를 가지게 하며<br/>
Pointer로서 사용될 때를 위한 <u>궁극적으로 비교할 값만을 반환</u>하는 " **뷰 메서드** "를 따로 선언하고<br/>

equals는 해당 클래스에 대한 확인을 철저하게 하면 된다.
```java
public class ColorPoint {
    private final Point point ; // Point 필드로 가지고 좌표 관리
    private final Color color ; // 해당 클래스의 identity를 가지는 unique 필드
   
   public ColorPoint(int x, int y, Color color) {
       this.point = new Point(x,y) ;
       this.color = Objects.requireNonNull(color);
   }
   
   // 뷰 메서드(View Method)
   // 서로 다른 하위 클래스 및 부모 클래스들과 사용될 때
   // 점(좌표)으로서 공통적인 좌표로 다룰 수 있도록 궁극적인 공통 필드 데이터를 가진 좌표만 반환
   public Point asPoint() {
       return point ;
   }
   
   @Override
   public boolean equals(Object o) {
       // 1차 비교 : 일단 같은 클래스 아니면 false
       if (!(o instanceof ColorPoint)) {
           return false ;
       }
      /**
       * 각자 Point를 공통 필드로 가지기 때문에 다운 캐스팅이 가능하다
       */
      ColorPoint cp = (ColorPoint) o ;
      // 2차 비교 : 좌표가 같고 색까지 같아야지만 true 반환
      return cp.point.equals(point) && cp.color.equals(color) ;
   }
}
```

<br/>

### 4. 일관성
> " **불변성** "인 두 객체가 같다면 <u>앞으로도 쭉 같아야하는</u> 특징

가변 객체는 비교 시점에 따라 서로 다를 수도 있고 같을 수도 있기 때문에 배제하지만<br/>
불변 객체는 비교 시점이 언제든 항상 결과가 같아야 한다.

그렇기 때문에 <u>클래스를 작성할 때는 불변 클래스로 만드는게 나을지</u> 고민해봐야 한다.

이처럼 `equals`가 한 번 같다고 한 객체는 영원히 같다고 답하도록 만들어야 하기 때문에 <br/>
`equals`의 판단에는 가변 객체와 같은 **신뢰할 수 없는 자원이 끼어들게 해서는 안되고**<br/>
<u>항시 메모리에 존재하는 객체만을 사용하는 **결정적(deterministic) 계산**만 수행하도록</u> 해야한다.

<br/>

### 5. Must Not be Null
> 모든 객체는 **Null과 같지 않아야** 하는 특징

Null과 같지 않아야 한다고<br/>
`equals` 메서드 내부에 `if (o == null) return false ;` 와 같은 명시적인 조건식은 필요가 없다.<br/>
오히려 묵시적으로 **해당 클래스 타입의 인스턴스인지** 확인하기 위해<br/>
`if (o instanceof (Class)) return false ;` 와 같은 올바른 타입인지 여부를 확인하는 조건식을 사용해야 한다.<br/>

이와 같이 검사하게 되면 자동으로 null이 들어왔을 때 자동으로 false를 반환하게 된다.<br/>
( _equals에서 타입을 확인하지 않아 잘못된 타입이 주어져 ClassCastException이 발생하게 된다._ )

<br/>

---
## ⁜ 양질의 `equals` 메서드 구현 방법
1. `==` 연산자를 사용해 **자기 자신의 참조인지** 확인
   - 자기 자신이면 true 반환
   - **비교 작업이 복잡한 상황**일 때 유용 (_단순 성능 최적화 용도_)<br/><br/>
2. `instanceof` 연산자로 **올바른 타입 입력인지** 확인
   - 원하는 올바른 타입이 아니면 false 반환
   - 단, <u>특정 인터페이스를 구현한 서로 다른 클래스들 간</u>의 비교에서는<br/> 각 클래스에서의 <u>equals에서의 기준</u>을 **해당 클래스들이 구현한 특정 인터페이스**로 사용해야 한다.<br/><br/> 
3. 입력을 **올바른 타입으로 형변환**
   - _2번 방법을 준수하게 되면 자동으로 충족되는 부분_<br/><br/>
4. 입력 객체와 <u>자신에 대응되는 **핵심 필드들</u>이 모두 정확히 일치**하는지 확인
   - 모든 필드가 일치해야만 true를 반환
   - 단, <u>특정 인터페이스를 구현한 서로 다른 클래스들 간</u>의 비교에서는<br/> <u>입력 객체의 필드 값을 가져올 때</u>도 **해당 인터페이스 메서드**를 **사용**해야 한다.

<br/>
<br/>

- ⟪ `float` & `double` ⟫
  - `Float.NaN` 이나 `-0.0f`와 같은 특정 부동 소숫값 등을 유의해야한다.
  - `Float.compare(float, float)` / `Double.compare(double, double)`
    - `Float.equals`나 `Double.equals` 메서드로 가능은하나 **오토박싱**이 수반될 수 있어 성능 저하를 유의해야 한다.
- ⟪ **배열** ⟫
  - 배열 필드의 경우, **각 원소들의 클래스**를 앞선 방법대로 비교하면 된다.
  - 만약 " <u>모든 원소가 핵심</u>일 경우 ", `Arrays.equals` 메서드들 중 하나 사용
- ⟪ **null 정상 취급 필드**를 가진 객체 ⟫
  - **정적 메서드** `Objects.equals(Object, Object)` → _NullPointerException 예방_
- ⟪ 비교 필드가 **복잡**한 객체 ⟫
  - " <u>**필드의 표준**(Canonical Form)</u> "을 저장하여 **표준형끼리 비교** : 불변 클래스에 적합
  - 가변 객체 : **값이 바뀔 때마다** 최신 상태로 필드 표준 **갱신**

<br/>

> ✅ `equals` 구현 체크 : "**대칭적**"인가 / "**추이성**"이 있는가 / "**일관적**"인가<br/>
> 
> ✵ _**단위 테스트**를 작성해 철저히 확인해보는 것이 안전하다.<br/>( `AutoValue`를 이용해 작성했다면 생략해도 무방 )_

<br/> 

### ❗️ 주의 사항 ❗️
- `equals`를 재정의 할 땐 `hashCode`도 반드시 재정의 하라.
- 너무 복잡하게 검사하여 해결하려 들지 마라.
- <u>**Object 외**의 타입</u>을 **매개변수**로 받는 `equals` 메서드는 선언하지 말자
  - " _입력 타입이 Object 가 아니다_ " → **다중 정의** (재정의 ❌)
  - _구체적인 타입을 명시한 equals는 손해가 더 크며 보안 측면에서도 잘못된 정보를 주게 된다._
- 몇 번이고 강조하지만 " <u>**꼭 필요한 경우가 아니라면** `equals`를 재정의하지 말자.</u> "

> Google - `AutoValue` 알아보기 : [GitHub : google/auto](https://github.com/google/auto/tree/main/value)