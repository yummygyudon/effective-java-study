package item_8_ex;

import java.lang.ref.Cleaner;

public class Room implements AutoCloseable{
    // The behavior of cleaners during System.exit is implementation specific. No guarantees are made relating to whether cleaning actions are invoked or not.
    // -> System.exit 동안 클리너의 동작은 구현에 따라 다릅니다. 청소 작업의 호출 여부와 관련하여 보장되지 않습니다.
    private static final Cleaner cleaner = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    private final State state;

    private static class State implements Runnable {
        int numJunkPiles;
        State(int numJunkPiles){
            this.numJunkPiles = numJunkPiles;
        }
        @Override
        public void run() {
            //  Runnable is used to create a thread,
            //  starting the thread causes the object's run method to be called in that separately executing thread.
//            System.out.println("청소!! : close 메서드 혹은 cleaner 호출");
            numJunkPiles = 0;
        }
    }

    public Room(int numJunkPiles){
        state = new State(numJunkPiles);
        // Cleanable register(Object obj, Runnable action)
        // Registers an object and a cleaning action to run
        //this를 통해 item_8_ex.Room 객체 내부에서 Runnable 객체인 state를 가입시킨다.
        cleanable = cleaner.register(this, state);
    }
    @Override
    public void close() { //throws Exception  = if this resource cannot be closed : InterruptedException -> checked Exception은 아니지만 치명적이기 때문에 권장
        //Cleanable represents an object and a cleaning action registered in a Cleaner.
        //Unregisters the cleanable and invokes the cleaning action. The cleanable's cleaning action is invoked at most once regardless of the number of calls to clean.
        // == register()했던 Runnable 객체를 헤제하고 호출 & cleanable의 청소 작업은 청소 호출 횟수에 관계없이 최대 한 번만 실행된다
        cleanable.clean();
        System.out.println("청소!! : close 메서드 혹은 cleaner 호출");
    }


}
