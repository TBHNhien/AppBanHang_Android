import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxJavaExample {

    public static void main(String[] args) {
        // Gọi hàm testObservable để thực hiện tác vụ trên luồng khác
        testObservable();

        // Đợi một lúc để đảm bảo luồng bất đồng bộ hoàn thành
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testObservable() {
        // Tạo một Observable để thực hiện tác vụ bất đồng bộ
        Observable<String> observable = Observable
                .fromCallable(() -> {
                    // Simulate a time-consuming task
                    Thread.sleep(1000);
                    return "Task completed on thread: " + Thread.currentThread().getName();
                })
                .subscribeOn(Schedulers.io()); // Đặt luồng chạy ở đây

        // Đăng ký Subscriber để theo dõi kết quả
        observable.subscribe(
                result -> {
                    System.out.println(result);
                },
                error -> {
                    System.err.println("Error: " + error.getMessage());
                }
        );
    }
}

