# [Item16] public 클래스에서는 public 필드가 아닌 접근자 메서드 사용
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [public 클래스 "가변 필드"를 노출 금지](#public-클래스-가변-필드-노출-금지)
- [package-private / private 중첩 클래스에서는 가끔 노출해도 좋다](#package-private--private-중첩-클래스에서는-가끔-노출해도-좋다)

</div>
</details>

<br/>

---
#### 🌟 핵심 🌟
```text
* public 클래스는 절대 "가변 필드"를 직접 노출해서는 안 된다.
  - 불변 필드라면 노출해도 덜 위험하지만 바람직하지 않다.
  
* package-private / private 중첩 클래스에서는
  종종 필드를 노출하는게 나을 때도 있다.
```

<br/>

---
## public 클래스 가변 필드 노출 금지
패키지 바깥에서 접근할 수 있는 클래스라면<br/>
"**접근자**"를 제공함으로써<br/>
클래스 내부 표현 방법을 언제든 유연하게 바꿀 수 있는 유연성을 확보할 수 있다.<br/>
( 만약 필드를 공개하면 외부에서 사용하는 클라이언트가 생기기 때문에 표현 방식을 쉽게 바꿀 수 없다. )

### 불변 필드여도 바람직하지 않다
<u>"**불변식**" 보장</u>은 할 수 있게 되지만<br/>
그 외 _**API 변경 없이 표현 방식 변경 불가능**_, _**필드 값을 읽을 때 다른 작업 금지 등**_ 의 단점은 여전하다.

```java
// 생성자에서 검증 및 수행해야하는 로직을 통해 불변식 보장이 가능하지만
// 여전히 비효율적인 코드임을 확인할 수 있따.
public final class Time {
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;

    public final int hour;
    public final int minute;

    public Time(int hour, int minute) {
        if (hour < 0 || hour >= HOURS_PER_DAY) {
            throw new IllegalArgumentException("시간 : " + hour);
        }
        if (minute < 0 || minute >= MINUTES_PER_HOUR) {
            throw new IllegalArgumentException("분 : " + minute);
        }
        this.hour = hour;
        this.minute = minute;
    }
}
```

<br/>

---
## package-private / private 중첩 클래스에서는 가끔 노출해도 좋다
해당 클래스가 표현하려는 추상 개념만 올바르게 표현해주면<br/>
데이터 필드를 노출한다 해도 큰 문제가 없다.

오히려 <u>클래스 선언</u> 면에서나 사용하는 <u>클라이언트 코드</u> 면에서나<br/>
**"접근자" 방식보다 훨씬 깔끔**해진다.

<br/>

물론 클라이언트는 해당 클래스 내부 표현에 종속되기는 하나<br/>
어차피 클라이언트도 <u>해당 **클래스 소속 패키지 안**에서만 동작</u>하는 코드들 뿐이기 때문에<br/>
패키지 바깥 코드에는 <u>전혀 영향 없이</u> **표현 방식을 바꿀 수 있다**.
