# [Item17] 변경 가능성을 최소화하라
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [고정값은 불변 클래스](#불변-클래스로-고정-값-활용)
  - [불변 클래스 생성 규칙 5가지](#불변-클래스화-규칙-5-선)
- [불변 객체 특징 및 장점](#불변-객체-특징-및-장점)
- [단점 및 대처법](#단점-및-대처법)
- [불변 클래스 설계 방법](#불변-클래스-설계-방법)

</div>
</details>

<br/>

---
#### 🌟 핵심 🌟
```text
* 변경 가능성 최소화
  - Getter가 있다고 해서 무조건 Setter를 만들지는 말자
  - 클래스는 꼭 필요한 경우가 아니라면 "불변"이어야 한다.

* 불변 클래스 활용
  - 불변으로 만들 수 없는 클래스라도 변경할 수 있는 부분을 최소한으로 줄이자
  - 다른 합당한 이유가 없다면 모든 필드는 private final 이어야 한다.

* 생성자는 "불변식 설정이 모두 완료"된 
  & "초기화가 완벽히 끝난" 상태의 객체를 생성해야한다.
  - 확실한 이유 ❌ → 생성자와 정적팩토리 외에는 어떤 "초기화 메서드"도 "public" 금지
```

<br/>

---
## 불변 클래스로 고정 값 활용
불변 클래스는 그 인스턴스의 내부 값을 수정할 수 없는 클래스로<br/>
인스턴스에 간직된 정보는 **고정**되어 객체 파괴 순간까지 절대 달라지지 않는다.<br/>
(ex. `String`, `기본 타입 Boxing`, `BigInteger`, `BigDecimal`, ...)

- 설계 & 구현 & 사용 쉬움
- 오류 발생 가능성 낮음


### 불변 클래스화 규칙 5 선
1. 객체의 **<u>상태를 변경하는 메서드</u>**(`변경자`)를 <u>제공하지 않는다</u>.
2. 클래스를 <u>**확장**할 수 없도록</u> 한다.
3. 모든 **클래스**를 `final`로 선언한다.
   - 시스템 강제 수단을 통해 설계자의 의도를 명확히 표
4. 모든 **필드**를 `private`으로 선언한다.
5. **자신 외**에는 <u>내부의 가변 컴포넌트에 접근할 수 없도록</u> 한다.

> 예시로 **<u>함수형 프로그래밍</u> 방식**으로 구현한 클래스를 사용하자.

```java
// 복소수 처리
public final class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    // 실수부 추출
    public double realPart() {
        return re;
    }

    // 허수부 추출
    public double imaginaryPart() {
        return im;
    }

    /**
     * 피연산자 인스턴스(자신)은 수정하지 않고
     * 새로운 Complex 인스턴스를 만들어 반환
     */
    public Complex plus(Complex complex) {
        return new Complex(re + complex.re, im + complex.im);
    }

    public Complex minus(Complex complex) {
        return new Complex(re - complex.re, im - complex.im);
    }

    public Complex times(Complex complex) {
        return new Complex(re * complex.re - im * complex.im,
                re * complex.im + im * complex.re);
    }

    public Complex dividedBy(Complex complex) {
        double tmp = complex.re * complex.re + complex.im * complex.im;
        return new Complex((re * complex.re + im * complex.im) / tmp,
                (im * complex.re - re * complex.im) / tmp);
    }

    @Override
    public boolean equals(Object o) {
        if (0 == this)
            return true;
        if (!(o instanceof Complex))
            return false;
        Complex c = (Complex) o;
        return Double.compare(re, c.re) == 0 &&
                Double.compare(im, c.im) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(re) + Double.hashCode(im);
    }

    @Override
    public String toString() {
        return "(" + re + " + " + im + "i)";
    }
}
```
⁜ 함수형 프로그래밍 → 코드에서 <u>**불변이 되는 영역**의 비율이 높아짐</u>.
- <u>피연산자에 함수를 적용</u>해 결과를 반환
- 단, **피연산자** 자체는 **그대로**

---
## 불변 객체 특징 및 장점
### 단순
- 생성된 시점의 상태가 파괴될 때까지 그대로 간직
  - 모든 생성자가 클래스 불변식 (Class Invariant)를 보장한다면 <br/>
  프로그래머의 관리 없이도 영원히 불변

### Thread-Safe & 동기화 필요 없음
- 여러 쓰레드가 동시에 사용해도 절대 훼손되지 않는다.
  - _클래스를 Thread-Safe하게 만드는 가장 쉬운 방법_
- 안심하고 "**<u>공유</u>**" 가능
  - " 최대한 **한 번 만든 인스턴스**는 <u>최대한 **재활용**</u> "
    - <u>**자주 쓰이는 값**</u>들을 "**상수**(`public static final`)"로 제공

### 정적 팩토리 제공 가능
자주 사용되는 인스턴스를 Caching 해서<br/>
같은 인스턴스를 중복 생성하지 않게 해주는 정적 팩토리를 제공할 수 있다.<br/>
( ex. `Boxing 기본 타입 클래스`, `BigInteger` 등 )

- 여러 클라이언트 → 인스턴스 공유
  - G.C 비용 감소
  - 메모리 사용량 절감

### 자유로운 공유 & "방어적 복사" 불필요
아무리 복사해봐여 원본과 동일하기 때문에 복사 자체가 의미가 없다.<br/>
그에 따라 "`clone` **메서드**"나 "**복사 생성자**"를 제공하지 않는 것이 좋다.

### 불변 객체끼리의 내부 데이터 공유
원본 인스턴스에서도 새로운 객체로 제공되기 때문에<br/>
인스턴스 <u>내부의 멤버는 가변</u>일지라도<br/>
**원본 인스턴스와 공유**해도 된다.<br/>
( "<u>원본 인스턴스가 가르키는</u> **내부 데이터**"를 **새로 생성된 인스턴스가 가르키더라도** 문제 없다.)


### 객체 생성 시, 멤버를 불변 객체로!
- <u>값이 불변인 구성요소들</u>로 구성된 객체 → 구조가 복잡하더라도 **<u>불변식 유지</u> 용이**

### 그 자체로 "실패 원자성" 제공
> ⁜ **실패 원자성** : _메서드에서 **예외가 발생한 후**에도 그 객체는 <u>여전히 호출 전과 동일한 유효한 상태</u>여야 한다._
- "**상태 절대 불변**" → 잠깐이라도 <u>불일치 상태에 빠질 가능성</u>이 **없다**.

<br/>

## 단점 및 대처법
### 단점 : 값이 다르면 무조건 독립된 객체로 생성
**불변 객체 간의 공유**가 일어났을 때,<br/>
모든 객체 내부가 불변 요소들로만 이루어 진 것이 아니기 때문에<br/>
<u>값의 변화</u>와 더불어 <u>연관된 클래스 내의 **가변 요소**들</u>간의 조합이 번거로워지고

그에 따라 <u>원하는 객체를 완성하기 까지의 **단계</u>가 많고** <br/>
그 <u>중간 단계에서 만들어진 불필요한 객체들</u>이 **모두 버려진다면** **성능 문제**가 발생할 수 밖에 없다.

### 대처법 ① : package-private "가변 동반 클래스" & "다단계 연산" 기본 제공
1. 흔히 쓰일 "`다단계 연산(Multistep Operation)`"들을 **예측** 및 <u>**기본 기능**으로서 제공</u>
2. **내부**에서 <u>다단계 연산 속도 향상</u>을 위한 "`가변 동반 클래스`" 사용


### 대처법 ② : 클래스를 `public`으로 제공 ( [대처법 1](#대처법---package-private--가변-동반-클래스----다단계-연산--기본-제공) 불가능 할 경우)
> ⁜ _대표적인 예 : `String` 클래스_
> - **가변 동반 클래스** : `StringBuilder` (과거 : `StringBuffer`)

<br/>

---
## 불변 클래스 설계 방법 
> 클래스 **불변 보장** : "**<u>자신을 상속하지 못하도록</u>**" 
1. `final` 클래스 선언<br/><br/>
2. 모든 **생성자** `private`/`package-private` & `public` **<u>정적 팩토리</u>** 제공
   - 패키지 바깥의 클라이언트 입장에서 사실상 `final`
   - 다른 패키지에서 해당 클래스 **확장 불가능**
   - 정적 팩토리 → 다수의 구현 클래스를 활용한 "**유연성**" 제공
   - "<u>객체 캐싱</u>(Caching)" 기능 추가 가능 → "**성능 향상**" 제고 가능

사실 [불변 클래스 규칙](#불변-클래스화-규칙-5-선)의<br/>
" <u>_모든 필드가 final이고 어떤 메서드도 그 객체를 수정할 수 없어야 한다._</u> " 규칙은 성능 측면에서 과한 규칙이다.

그에 따라<br/>
" **어떤 메서드**도 객체의 상태 중 <u>외부에 비치는 값</u>을 **변경할 수 없다**. " 정도로 완화시킬 수 있다.

어떤 불변 클래스는 <u>계산 비용이 큰 값을 나중에 계산( **지연 초기화** )</u>해서<br/>
"`final`이 아닌 필드"에 **캐싱**해놓기도 한다.<br/>
→ 똑같은 값 **재요청** : <u>캐싱해둔 값</u> 반환<br/>
→ **계산 비용 절감**<br/>
(_불변 객체이기에 가능한 방법_)
