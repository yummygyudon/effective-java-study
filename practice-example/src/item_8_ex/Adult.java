package item_8_ex;

public class Adult {
    public static void main(String[] args) {
        try (Room myRoom = new Room(7)){
            System.out.println("안녕");
        }
    }
}