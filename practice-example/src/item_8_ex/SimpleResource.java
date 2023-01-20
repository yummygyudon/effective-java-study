package item_8_ex;

import java.lang.ref.Cleaner;

public class SimpleResource implements AutoCloseable{
    private boolean closed ;
    private static final Cleaner CLEANER = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    private final  ResourceCleaner resourceCleaner;
    public SimpleResource(){
        this.resourceCleaner = new ResourceCleaner();
        // Cleanable 객체가 자원 클래스에 대한 청소용 인스턴스로서 Runnable 객체 등록
        // ResourceCleaner 객체와 SimpleResource 객체는 서로 참조 관계가 아니다.(순환 참조 가 생겨버려서 GC가 SimpleResource를 다썼을 때 회수를 못해감
        this.cleanable = CLEANER.register(this, resourceCleaner);
    }
    public static class ResourceCleaner implements Runnable{
        @Override
        public void run() {
            System.out.println("청소! 청소!");
        }
    }

    public void sayHi() {
        System.out.println("Hi!");
    }

    @Override
    public void close() throws RuntimeException {
        if(this.closed){
            throw new IllegalStateException();
        }
        closed = true;
        System.out.println("Close!");
        cleanable.clean();
    }
}
