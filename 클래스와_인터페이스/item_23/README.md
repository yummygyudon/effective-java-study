# [Item23] 태그 달린 클래스보다는 클래스 계층 구조를 활용하라
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [태그 달린 클래스의 문제](#-태그-달린-클래스--단점)
- [대안 : 서브타이핑(subtyping)](#대안-서브타이핑--subtyping-)

</div>
</details>

<br/>

---
#### 🌟 핵심 🌟
```text
* 태그 달린 클래스를 써야하는 상황은 거의 없으니 쓰지 마라.

* 클래스 정의 중 태그 필드가 등장한다면
  그대로 이어가지말고 "계층 구조로 대체하는 방법"을 생각해보자.
  (이전에 정의했던 기존 클래스에도 태그가 있다면 리팩토링을 고려하라.)
```

<br/>

---
> 우선 `태그 달린 클래스` 예시

```java
public class Figure {
    enum Shape {RECTANGLE, CIRCLE}

    ;

    final Shape shape;

    // 사각형(RECTANGLE) 일 때만 사용
    double length;
    double width;

    double radius; // 원(CIRCLE) 일 때만 사용

    // 상활별 맞춤형 생성자 필요
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    // 발생 가능 경우의 수에 대해 모두 대응하는 메서드 처리 필요
    double area() {
        switch (shape) {
            case RECTANGLE -> {
                return length * width;
            }
            case CIRCLE -> {
                return Math.PI * (radius * radius);
            }
            default -> throw new AssertionError(shape);
        }
    }
}
```

위에서 보다시피 꽤 불편한 부분이 많이 보인다.<br/>
단점을 알아보자.

## "태그 달린 클래스" 단점
> ⁜ 결론 : 태그 달린 클래스는 **장황**하고 **쉽게 오류가 발생**하며 **비효율적**
- 쓸데 없는 코드 ⬆
  1. _열거 타입 선언, 태그 필드, switch 문_ 등
  2. <u>`final` 필드로 선언</u>하고자 하면 **<u>쓰이지 않는 필드들까지</u> 생성자에서 초기화 필요**<br/><br/>
- **가독성** ⬇
  - 여러 구현이 한 클래스에 혼합<br/><br/>
- **메모리 낭비**
  - 다른 의미 표현을 위한 코드도 언제나 함께 가지고 있어야 함.<br/><br/>
- 오류 발생 위험으로부터 <u>**컴파일러**의 도움을 받기 어려움</u>
  - _"엉뚱한 필드 초기화"_ / _"다양한 의미에 대한 코드 추가 실수"_ → **<u>런타임</u>** 때 문제 발생<br/><br/>
- <u>**인스턴스 타입**</u>만으로 <u>현재 나타내는 의미를 알 방법이 없다</u>

<br/>

---
## [대안] 계층 구조 활용 - 서브타이핑(subtyping)
> <u>클래스 계층 구조를 활용한 **서브타이핑**</u>을 통해 해결

1. 계층 구조의 Root가 될 `추상 클래스` 정의 : " 루트 클래스 "
2. " 태그값에 따라 **동작이 달라지는** 메서드들 "을 <u>**루트 클래스**의 `추상 메서드`</u> 로 선언
3. " 태그값 상관 없이 **동작이 일정한** 메서드들 "을 <u>**루트 클래스**의 `일반 메서드`</u> 로 선언
4. " <u>**모든 하위 클래스**에서 **공통**으로 사용하는 데이터 필드</u> "들도 전부 <u>**루트 클래스**에</u> 선언
5. <u>의미별</u>로 " 루트 클래스 확장 **구체 클래스** " 정의
6. <u>의미별</u>로 " `추상 메서드` " 구현

> 위 예시 코드 개선

```java
abstract class Figure {
    abstract double area();
}

class Circle extends Figure {
    final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }
    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}
class Rectangle extends Figure {
    final double length;
    final double width;

    public Circle(double length, double width) {
        this.length = length;
        this.width = width;
    }
    @Override
    double area() {
        return length * width;
    }
}
``` 

- 각 의미를 독립된 클래스에 담아 **관련없는 데이터 필드 제거**
  - 존재하는 필드들은 `final`
- 추상 메서드 구현 → "컴파일러" 체크
  - [단점](#-태그-달린-클래스--단점) 개선
- 루트(Root) 클래스 수정 없이 **독립적**인 계층 구조 **확장 및 함께 사용** 가능
- 타입이 <u>의미별로</u> 따로 **독립적으로 존재**
  - 변수 의미 **명시 및 제한 가능**
  - "**의미 특정** 매개변수" 입력 가능
- **타입 간 자연스러운 <u>계층 관계</u> 반영**
  - **유연성** 제고
  - `컴파일 타임` - <u>**타입 검사**</u> 능력 향상
