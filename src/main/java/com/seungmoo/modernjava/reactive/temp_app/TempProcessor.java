package com.seungmoo.modernjava.reactive.temp_app;

import java.util.concurrent.Flow;

/**
 * Processor는 Subscriber이며 동시에 Publisher이다.
 *
 * 사실 Processor의 목적은 Publisher를 구독한 다음 수신한 데이터를 가공해서 다시 제공하는 것
 *
 * 본 소스에서는 화씨로 제공된 데이터를 섭씨로 변환해 다시 방출하는 예제를 Processor를 통해 구현한다.
 * 즉 TempInfo를 다른 TempInfo로 변환하는 Processor를 만들어 볼 것임.
 *
 * 여기서 onNext는 로직을 포함하는 유일한 메서드
 * onSubscribe, onError, onComplete는 단순 전달만을 담당한다.
 */
public class TempProcessor implements Flow.Processor<TempInfo, TempInfo> {

    private Flow.Subscriber<? super TempInfo> subscriber;

    @Override
    public void subscribe(Flow.Subscriber<? super TempInfo> subscriber) {
        this.subscriber = subscriber;
    }

    /**
     * 섭씨로 변환한 다음 TempInfo를 다시 전송
     * @param item
     */
    @Override
    public void onNext(TempInfo item) {
        this.subscriber.onNext(new TempInfo(item.getTown(), (item.getTemp() - 32) * 5 / 9));
    }


    // onSubcribe, onError, onComplete --> 모든 신호는 업스트림의 구독자에 전달
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscriber.onSubscribe(subscription);
    }

    @Override
    public void onError(Throwable throwable) {
        this.subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        this.subscriber.onComplete();
    }
}
