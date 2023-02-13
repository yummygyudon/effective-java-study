# 2장. 객체 생성과 파괴
- 객체를 만들어야 할 때와 만들지 말아야 할 때를 구분하는 법
- 올바른 객체 생성 방법
- 불필요한 생성 피하는 방법
- 제 때 파괴됨을 보장하며 파괴 전 수행할 정리 작업 관리 요령

## Index
1. [Item 1 : 생성자 대신 정적 팩터리 메서드를 고려하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_1)
2. [Item 2 : 생성자에 매개변수가 많다면 빌더를 고려하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_2)
3. [Item 3 : private 생성자나 열거 타입으로 싱글턴임을 보증하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_3)
4. [Item 4 : 인스턴스화를 막으려거든 private 생성자를 사용하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_4)
5. [Item 5 : 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_5)
6. [Item 6 : 불필요한 객체 생성을 피하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_6)
7. [Item 7 : 다 쓴 객체 참조를 해제하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_7)
8. [Item 8 : finalizer와 cleaner 사용을 피하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_8)
9. [Item 9 : try-finally보다는 try-with-resources를 사용하라](https://github.com/yummygyudon/effective-java-study/tree/main/%EA%B0%9D%EC%B2%B4_%EC%83%9D%EC%84%B1%EC%9E%90_%ED%8C%8C%EA%B4%B4/item_9)