package item_8_ex;

public class SimpleApplication {
    public static void main(String[] args) {
        /*
        // 1. close에 대한 보장이 없음
        SimpleResource simpleResource = new SimpleResource();
        simpleResource.sayHi();
        System.gc();
         */

        /*
        // 2. try~ with~ resource~ 로 close 암묵적 보장 : 지역 변수에 대해서만 가능
        //Hi!
        //Close!
        //청소! 청소!
        try(SimpleResource simpleResource = new SimpleResource()){
            simpleResource.sayHi();
        }
         */


        // 3. try~ finally~ 로 close 명시적 보장
        //Hi!
        //Close!
        //청소! 청소!
        SimpleResource simpleResource = new SimpleResource();
        try{
            simpleResource.sayHi();
        }finally {
            simpleResource.close();
        }
    }
}
