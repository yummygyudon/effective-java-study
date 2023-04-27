# [Item 14] `Comparable`을 구현할지 고려하라
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [compareTo 메서드 규약](#compareto-규약)
- [Comparator](#comparator-활용)

</div>
</details>

<br/>

---
얼핏 보면 Object의 `equals`와 같아보이는 메서드인<br/>
`Comparable`인터페이스의 `compareTo`가 주인공이다.

물론 동치 비교의 기능을 두 메서드 모두 가지고 있으나<br/>
`compareTo` 메서드는 추가적인 기능들을 가지고 있다.

> " _Comparable 인터페이스를 구현했다._ " <br/> 
> → " _클래스의 인스턴스들에는 **자연적인 순서**(natural order)가 있다._ "

- **동치성** 비교
- **순서** 비교
- **제네릭** (타입을 <u>컴파일 타임</u>에 결정)

순서에 대한 비교가 가능한만큼<br/>
**검색**, **극단값 계산**, 자동 정렬되는 **컬렉션 관리**도 가능하다.<br/>
( _ex. String 클래스의 Comparable 구현 → TreeSet 사용 시, 중복 제거 & 알파벳순 정렬_ )

🌟 "알파벳", "숫자", "연대" 등과 같이 <u>**순서가 명확한 값 클래스**를 작성한다면 반드시 **Comparable 인터페이스 구현**하라.</u> 🌟

<br/>

## `compareTo` 규약
> `반사성`, `대칭성`, `추이성` 을 충족해야 한다.
1. 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다.
   - ex. `a > b`가 참이라면 `b < a`가 참이어야 한다.<br/><br/>
2. 첫 번째가 두 번째보다 크고 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다.<br/><br/>
3. 크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야 한다.<br/><br/>
4. `compareTo` 메서드로 수행한 동치성 테스트의 결과가 `equals`와 같아야 한다.
   - 필수는 아니지만 <br/>만약 클래스의 객체를 정렬된 컬렉션에 넣으면 <br/>해당 컬렉션이 구현한 인터페이스(Collection, Set, Map 등)에 정의된 compareTo 동작과 어긋날 수 있다.
     - _정렬된 컬렉션들은 동치성 비교 시, `equals`가 아닌 `compareTo`를 사용한다._


> ⁜ [ **객체 지향적 추상화** ] 기존 클래스를 확장한 <u>구체 클래스에서 새로운 값 컴포넌트를 추가</u>했다면 <br/>위 규약을 지키기 어렵다.
> 
> 만약 < `Comparable`을 구현한 클래스를 확장하여 컴포넌트 >를 추가하고자 한다면<br/>
> 확장 대신 <u>독립된 클래스</u>를 만들고 해당 클래스에 <u>**원래 클래스의 인스턴스**를 가리키는 **필드**</u>를 둔 다음<br/>
> <u>내부 인스턴스를 반환</u>하는 "**뷰**" **메서드**를 제공하면 가능하다.
 
<br/>

---
## `Comparator` 활용
- `Comparable` 인터페이스 <u>**비구현 필드**</u>에 대한 비교 ( _++Class 레벨 비교까진 필요없는 경우_ )
- <u>**표준이 아닌** 방식</u>의 **순서 비교** 

위 2가지 경우에 해당할 땐,<br/>
꼭 클래스에 대해 `Comparable`를 구현하지 않더라도<br/>
비교자를 적용하여 비교작업을 처리해줄 수 있다.

비교자가 바로 "`Comparator`"이다.

기존에 Java에서 만들어준 Comparator 도 있지만<br/>
사용자가 직접 Comparator을 만들어 사용하는 것도 가능하다.

Comparator 를 사용할 때 단 2가지만 기억하면 된다.
- **기존 Java 제공** Comparator 사용 : <u>박싱(Boxing)된 기본 타입 클래스</u> 제공 "**정적 `compare` 메서드**" 사용
  - _ex._<br/>
  ```java
  // String.CASE_INSENSITIVE_ORDER.compare 활용
  
  public  final class CaseInsensitiveString 
                            implements Comparable<CaseInsensitiveString> {
        public String s ;
        public int compareTo(CaseInsensitiveString cis) {
            return String.CASE_INSENSITIVE_ORDER.compare(s,  cis.s);
        }     
  }
  
  // Short.compare() 활용
  public  final class PhoneNumber 
                            implements Comparable<PhoneNumber> {
        public Short areaCode ;
        public Short prefix ;
        public Integer lineNum ;
        public int compareTo(PhoneNumber pn) {
            int result = Short.compare(areaCode, pn.areaCode);
            if(result == 0) {
                result = Short.compare(prefix, pn.prefix);
                if(result == 0) {
                    result = Integer.compare(lineNum, pn.lineNum);
                }
            }
            return result;
        }
    
  }

  ```
  <br/>
- **직접 구현** 사용 : <u>`Comparator` 인터페이스</u> 제공 "**비교자 생성 메서드**" 활용
  - _ex._
  ```java
  
  import static java.util.Comparator.*;
  public  final class PhoneNumber {
        public Short areaCode ;
        public Short prefix ;
        public Integer lineNum ;
        public int compareTo(PhoneNumber pn) {
            return PHONE_NUMBER_COMPARATOR.compare(this, pn);
        }
  }
  public static final Comparator<PhoneNumber> PHONE_NUMBER_COMPARATOR = 
            comparing((PhoneNumber pn) -> pn.areaCode)
                .thenComparingInt(pn -> pn.prefix)
                .thenComparingInt(pn -> pn.lineNum);
  
  ```
