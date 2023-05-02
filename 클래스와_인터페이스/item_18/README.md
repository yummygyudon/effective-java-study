# [Item18] 상속보다는 컴포지션 사용
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">


- [캡슐화가 안전한 경우](#상속이-안전한-경우)
- [상속의 캡슐화 깨뜨리기](#상속의-캡슐화-깨뜨리기)
- [컴포지션](#컴포지션--composition-)
- [상속의 위험성과 조건](#상속의-위험성--상속-가능-조건)


</div>
</details>

<br/>

---
#### 🌟 핵심 🌟
```text
* 상속은 "캡슐화"를 해친다.
  - 상위 클래스와 하위 클래스가 순수한 `is-a` 관계일 때만
  - `is-a` 관계일 때여도 
    "하위 클래스와 상위 클래스의 패키지가 서로 다르고" & "상위 클래스 설계가 확장을 고려하지 않았다"면
    문제 발생 가능
    
* "컴포지션" 과 "전달"을 사용하자.
  - 특히 래퍼 클래스(WrapperClass)로 구현할 적절한 인터페이스가 있다면
  - "래퍼 클래스로 구현할 적절한 인터페이스"는 
    하위 클래스보다 견고하고 강하다.
```

<br/>

---
## 캡슐화를 깨뜨리는 상속
> ⁜ 상속 :  "클래스" 가 "타 클래스"를 확장하는 "**구현** 상속"<br/>
> ( _**Class** to **Class**_ )
### 상속이 안전한 경우
1. 상위 클래스 & 하위 클래스가 모두 <u>"**같은 프로그래머**"가 통제하는 "**동일 패키지** 안"</u>일 경우
2. "**확장**" 목적 설계 & **"문서화"**

### 상속의 캡슐화 깨뜨리기
<u>**메서드 호출**과 달리</u> 상속은 캡슐화를 깨뜨린다.

```java
import java.util.Collection;
import java.util.HashSet;

public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    public InstrumentedHashSet() {
    }

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```
위 처럼 `HashSet`을 상속받아 구현했을 때,<br/>
상위 클래스의 내부 구현 방식에 상당히 휘둘리는 상황이 펼쳐질 수 있다.

상위 클래스의 <u>"자기사용(self-use)" 여부</u> 또한 **모르며**<br/>
상위 클래스에서의 "**멤버 추가**" 혹은 "**내부 구현 수정**"이 발생했을 때,<br/>
영문도 모른채 갑자기 <u>잘못된 재정의</u>가 될수도, <u>잘못된 연산 처리</u>가 될수도 있는 것이다.

즉, <u>"**상위 클래스의 메서드**"가 요구하는 규약을 만족하지 못할 가능성이 크다</u>는 것이다.

<br/>

---
## 컴포지션(Composition)
위와 같은 문제를 피해가는 방법이 바로 `컴포지션(Composition)` 이다.

기존 클래스를 **확장하는 대신**,<br/>
<u>새로운 클래스</u>를 만들어 "`private` 필드"로 <u>**기존 클래스**의 인스턴스를 참조</u>하게 만드는 것이다.

- <u>새 클래스</u>의 **인스턴스 메서드**(**Forwarding Method** : 전달 메서드) <br/>→ <u>기존 클래스 **대응</u> 메서드 호출** 및 **결과 반환**(**Forwarding** : 전달)<br/><br/>
  - 기존 클래스의 "<u>내부 구현 방식</u>의 영향력"에서 **벗어남**
  - 기존 클래스에 <u>새로운 메서드가 추가되더라도</u> **전혀 영향받지 않음**<br/>
    (_상속받으려는 클래스에서 사용하고 싶은 기존 클래스에서 대응되는 메서드는 <u>**새로운 클래스**를 통해</u> 전달받음_)

```java
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 집합 클래스 "자신"
 * 전달 클래스인 Set 인스턴스(ForwardingSet)를 감싸고 있는
 * "래퍼 클래스"
 * 
 * @param <E>
 */
public class InstrumentedHashSet<E> extends ForwardingSet<E> {
  private int addCount = 0;

  public InstrumentedHashSet(Set<E> s) {
      // 래퍼 클래스 인스턴스 ForwardingSet에 
      // 자신이 가지고 있을 Set을 넘겨줌 
      super(s);
  }


  @Override
  public boolean add(E e) {
    addCount++;
    return super.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    addCount += c.size();
    return super.addAll(c);
  }

  public int getAddCount() {
    return addCount;
  }
}


/**
 * "전달 클래스"
 * 
 * HashSet의 모든 기능을 정의
 * - 해당 클래스를 사용하는 구현 클래스에 전달해줄 메서드만 잘 전달해주면됨
 * - HashSet에서 메서드를 추가하든 말든 구현 클래스에는 영향 없음
 */
public class ForwardingSet<E> implements Set<E> {
  // 구현 클래스가 사용할 Set 인스턴스를 감싸는 필드   
  private final Set<E> s;
  public ForwardingSet(Set<E> s) {this.s = s;}
  
  public void clear() {s.clear();}
  public boolean contains(Object o) {return s.contains(o);}
  public boolean isEmpty() {return s.isEmpty();}
  public int size() {return s.size();}
  public Iterator<E> iterator() {return s.iterator();}
  public boolean add(E e) {return s.add(e);}
  public boolean remove(Object o) {return s.remove(o);}
  public boolean containsAll(Collection<?> c) {return s.containsAll(c);}
  public boolean addAll(Collection<? extends E> c) {return s.addAll(c);}
  public boolean removeAll(Collection<?> c) {return s.removeAll(c);}
  public boolean retainAll(Collection<?> c) {return s.retainAll(c);}
  
  public <T> T[] toArray(T[] a) {return s.toArray(a);}
  public Object[] toArray() {return s.toArray();}

  @Override
  public int hashCode() {return s.hashCode();}
  @Override
  public boolean equals(Object obj) {return s.equals(obj);}
  @Override
  public String toString() {return s.toString();}
}
```

위와 같이
"임의의 Set에 계측 기능을 덧씌워 새롭게 만들어지는" **전달(Forwarding) 클래스**와<br/>
"HashSet이 아닌 <u>다른 Set 인스턴스를 감싸고(Wrap)있는</u>" **래퍼(Wrapper) 클래스**의 <br/>
"**구조(Composition)**"로 문제를 해결할 수 있다.

> ⁜ _따로 만든 전달용 클래스에 계측 기능을 덧씌운다_ → "<u>**데코레이터 패턴**( Decorator Pattern )</u>" 

> ⁜ `전달 (Forwarding)` - `구조(Composition)` 의 조합 <br/>--(_넓은 의미_)-> `위임(Delegation)`

<br/>

---
## 상속의 위험성 & 상속 가능 조건
### 잘못된 상속 사용의 위험성
컴포지션을 써야할 상황에서 상속을 사용하는 것은<br/>
**<u>내부 구현</u>을 불필요하게 노출**하는 꼴이다.

- **API** 가 <u>내부 구현에 **종속**</u>
- 클래스 **성능** 영구적 **제한**(종속이 되어 <u>확장 및 개선이 불가능</u>)
- 클라이언트의 **노출된 내부 접근**
  - **상위 클래스**를 <u>직접 수정</u> / <u>직접 호출</u> → **하위 클래스의 불변식 파괴**

<br/>

### 상속 가능 조건 및 체크 리스트
```TEXT
1. Class B 가 Class A 와 "is -a" 관계가 확실한가
  - (확신까지 확실히 안된다) → 상속 ❌
  - (NO) → Class A : private 인스턴스 / A와는 다른 API 제공 
           (하지만 이건 필수 구성요소가 아니라 구현하는 방법 중 하나일 뿐_해결된 것 ❌)
           
2. 확장하려는 클래스의 API에 아무런 결함이 없는가

3. (결함이 있다면) 결함이 구현하려는 클래스의 API까지 전파되어도 괜찮은가
```

> ⁜ 상속은 <u>상위 클래스 API의 "**결함**"까지도</u> 그대로 상속받는다