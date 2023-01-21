# [Item 9] `try-finally`보다 `try- with- resources`  지향
<details open>
    <summary><b>Index</b></summary>
<div markdown="1">

- [전통방식 : try-finally](#전통-방식--try--finally)
- [try- with- resources](#try--with--resources)

</div>
</details>

<br/>

---
> ✵ _좋은 예시 : `InputStream`, `OutputStream`, `java.sql.Connection`_

자바 라이브러리에는 `close` 메서드 호출로 직접 닫아줘야 하는 리소스들이 많다.
- 놓치기 쉽기 때문에 휴먼 에러로 인한 **예측 불가 성능 문제** 발생 확률이 높다.

## 전통 방식 : `try- finally`
```java
static String firstLineOfFileLegacy(String path) throws IOException{
    BufferedReader br = new BufferedReader(new FileReader(path));
    try{
        return br.readLine();
    }finally {
        br.close();
    }
}
```
위 코드에서는 `try`블럭과 안타깝게도 `finally` 블럭 모두에서 발생할 수 있는데
- `public void close() throws IOException`
- `public String readLine() throws IOException { return readLine(false); }`

`close`에서의 예외와 `readLine`에서 발생한 예외는 `IOException`으로 동일하다.

안타깝게도 첫 번째 예외인 `readLine`에서 발생한 `IOException` 예외 정보는 남지 않고<br/> 
가장 **마지막에 발생**한 `close`에서의 `IOException` 예외 **기록만 남겨지게 되어** 디버깅이 어려워진다.<br/>
(예외 기록 순서는 바꿀 수 있지만 지저분한 코드가 많아진다.)

<br/>

## `try- with- resources`
`try- with- resources` 구조를 사용하기 위해선<br/>
반드시 해당 자원 클래스는 <u>`AutoClosable` **인터페이스</u>가 구현**되어야 한다.

한 눈에 봐도<br/>
기존 Legacy한 방식인 `try~ finally~`를<br/>
`try~ with~ resources~`로 변환했을 때 " **코드 간결성** "이 개선된 것을 확인할 수 있다.<br/>
(사용하는 자원이 많아질 수록 빛을 발한다.)
```java
static String firstLineOfFileLegacy(String path) throws IOException{
    BufferedReader br = new BufferedReader(new FileReader(path));
    try{
        return br.readLine();
    }finally {
        br.close();
    }
}

static String firstLineOfFile(String path) throws IOException{
    try(BufferedReader br = new BufferedReader(new FileReader(path))){
        return br.readLine();
    }
}
```
```java
static void copyLegacy(String src, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try{
     OutputStream out = new FileOutputStream(dst);
     try{
         byte[] buf = new byte[1024];
         int n ;
         while ((n = in.read(buf)) >= 0)
             out.write(buf,0,n);
     } finally {
         out.close();
     }
    }finally {
        in.close();
    }
}

static void copy(String src, String dst) throws IOException {

    try(InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst)) {
        byte[] buf = new byte[1024];
        int n;
        while ((n = in.read(buf)) >= 0)
            out.write(buf, 0, n);
    }
}
```


더 나아가
" <u>핵심적인 예외에 대한 **추적**</u> "이 가능해진다.<br/>
위 코드에서 볼 수 있듯<br/>
`close`에서의 예외보다 `readLine`에서 발생한 예외 확인이 더 중요한 상황에서<br/>
`readLine`**에서 발생한 예외를 기록**하고 <u>`close`에서의 예외는 숨겨진다</u>.

심지어 `close`에서의 **예외가 숨겨졌더라도**<br/>
**스택 추적 내역**에 <u>`suppressed`</u>라는 라벨을 달고 출력된다.<br/>
(ex.`Suppressed: java.io.IOException:...` → `getSuppress)

또한 <u>`catch`블럭도 활용</u> 가능하기 때문에<br/>
" **예외 발생 사후 처리**에 대한 로직 "도 작성할 수 있다.