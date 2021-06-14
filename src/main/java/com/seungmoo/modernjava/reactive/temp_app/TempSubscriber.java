package com.seungmoo.modernjava.reactive.temp_app;

import java.util.concurrent.Flow;

/**
 * 받은 온도를 출력하는 Subscriber
 */
public class TempSubscriber implements Flow.Subscriber<TempInfo> {
    private Flow.Subscription subscription;

    /**
     * 구독을 저장하고 첫 번쨰 요청을 전달
     * @param subscription
     */
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
    }

    /**
     * 수신한 온도를 출력하고 다음 정보를 요청
     * @param item
     */
    @Override
    public void onNext(TempInfo item) {
        System.out.println(item);
        this.subscription.request(1);
    }

    /**
     * 에러가 발생하면 에러 메시지 출력
     * @param throwable
     */
    @Override
    public void onError(Throwable throwable) {
        System.err.println(throwable.getMessage());
    }

    /**
     * Complete = 구독 끝내기
     */
    @Override
    public void onComplete() {
        System.out.println("Done!");
    }
}
