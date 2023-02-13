# 3장. 모든 객체의 공통 메서드
- Object 메서드들의 재정의 방법
  - `equals`, `hashCode`, `toString`, `clone`, `finalize`
- 언제 어떻게 재정의 해야하는가
- 재정의 규약 파악
- 규약에 어긋나게 메서드를 Ovveriding 했을 때의 위험
  - 올바른 준수를 가정하에 동작하는 클래스들에서의 발생 문제

## Index
1. [Item 10 : equals는 일반 규약을 지켜 재정의하라](https://github.com/yummygyudon/effective-java-study/tree/main/%eb%aa%a8%eb%93%a0_%ea%b0%9d%ec%b2%b4%ec%9d%98_%ea%b3%b5%ed%86%b5_%eb%a9%94%ec%84%9c%eb%93%9c/item_10)
2. [Item 11 : equals를 재정의하려거든 hashCode도 재정의하라](https://github.com/yummygyudon/effective-java-study/tree/main/%eb%aa%a8%eb%93%a0_%ea%b0%9d%ec%b2%b4%ec%9d%98_%ea%b3%b5%ed%86%b5_%eb%a9%94%ec%84%9c%eb%93%9c/item_11)
3. [Item 12 : toString을 항상 재정의하라](https://github.com/yummygyudon/effective-java-study/tree/main/%eb%aa%a8%eb%93%a0_%ea%b0%9d%ec%b2%b4%ec%9d%98_%ea%b3%b5%ed%86%b5_%eb%a9%94%ec%84%9c%eb%93%9c/item_12)
4. [Item 13 : clone 재정의는 주의해서 진행하라](https://github.com/yummygyudon/effective-java-study/tree/main/%eb%aa%a8%eb%93%a0_%ea%b0%9d%ec%b2%b4%ec%9d%98_%ea%b3%b5%ed%86%b5_%eb%a9%94%ec%84%9c%eb%93%9c/item_13)
5. [Item 14 : Comparable을 구현할지 고려하라](https://github.com/yummygyudon/effective-java-study/tree/main/%eb%aa%a8%eb%93%a0_%ea%b0%9d%ec%b2%b4%ec%9d%98_%ea%b3%b5%ed%86%b5_%eb%a9%94%ec%84%9c%eb%93%9c/item_14)
