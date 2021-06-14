package com.seungmoo.modernjava.reactive.temp_app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

/**
 * Subscriber에게 TempInfo 스트림을 전송하는 Subscription
 */
public class TempSubscription implements Flow.Subscription {

    private final Flow.Subscriber<? super TempInfo> subscriber;
    private final String town;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TempSubscription(Flow.Subscriber<? super TempInfo> subscriber, String town) {
        this.subscriber = subscriber;
        this.town = town;
    }

    @Override
    public void request(long n) {
        // Subscriber가 만든 요청을 한 개씩 반복
        /*for (long i = 0L; i < n; i++) {
            try {
                // 현재 온도를 Subscriber로 전달
                subscriber.onNext(TempInfo.fetch(town));
            } catch (Exception e) {
                // 온도 가져오기를 실패하면 Subscriber로 에러를 전달
                subscriber.onError(e);
                break;
            }
        }*/

        // 위의 코드에서 TempInfo가 Error를 발생시키지 않는 사항이 발생하면
        // 동일 쓰레드에서 onNext(Subscription) -> request() -> 또 onNext(Subscription) 무한으로 돌면서 StackOverflow가 발생!!
        // 이것을 해결하기 위해 Excutor로 별도 쓰레드에서 처리하도록 한다.
        // 무한 호출이 되지만, StackOverflow는 발생하지 않는다. (executor 덕분에 별도 쓰레드에서 돌림)
        executor.submit(() -> {
            for (long i = 0L; i < n; i++) {
                try {
                    subscriber.onNext(TempInfo.fetch(town));
                } catch (Exception e) {
                    subscriber.onError(e);
                    break;
                }
            }
        });
    }

    /**
     * 구독이 취소되면 완료(onComplete) 신호를 Subscriber로 전달.
     */
    @Override
    public void cancel() {
        subscriber.onComplete();
    }

}
