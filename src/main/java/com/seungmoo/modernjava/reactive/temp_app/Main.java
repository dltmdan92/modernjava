package com.seungmoo.modernjava.reactive.temp_app;

import io.reactivex.Observable;

import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        // 뉴욕에 새 Publisher를 만들고 TempSubscriber를 구독시킴
        //getTemperatures("New York").subscribe(new TempSubscriber());
        // 화씨를 섭씨로 바꾸는 Processor를 적용
        //getCelsiusTemperatures("New York").subscribe(new TempSubscriber());

        // 매 초마다 뉴욕의 온도 보고를 방출하는 Observable 만들기
        Observable<TempInfo> observable = getTemperature("New York");
        // 단순 Observer로 이 Observable에 가입해서 온도 출력하기
        observable.blockingSubscribe(new TempObserver());
    }

    /**
     * 구독한 Subscriber에게 TempSubscription을 전송하는 Publisher를 반환
     * @param town
     * @return
     */
    public static Flow.Publisher<TempInfo> getTemperatures(String town) {
        return subscriber -> subscriber.onSubscribe(new TempSubscription(subscriber, town));
    }

    public static Flow.Publisher<TempInfo> getCelsiusTemperatures(String town) {
        return subscriber -> {
            TempProcessor processor = new TempProcessor();
            processor.subscribe(subscriber);
            processor.onSubscribe(new TempSubscription(processor, town));
        };
    }

    /**
     * 1초 마다 한 개의 온도를 방출하는 Observable 만들기
     * @param town
     * @return
     */
    public static Observable<TempInfo> getTemperature(String town) {
        // Observable 만들기
        return Observable.create(observableEmitter ->
                // 매 초마다 무한으로 증가하는 일련의 long 값을 방출하는 Observable
                Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(i -> {
                    // 소비된 옵저버가 아직 폐기되지 않았으면 어떤 작업을 수행 (이전 에러)
                    if (!observableEmitter.isDisposed()) {
                        // 온도를 다섯 번 보고했으면 옵저버를 완료하고 스트림을 종료
                        if (i >= 5) {
                            observableEmitter.onComplete();
                        }
                        else {
                            try {
                                // 아니면 온도를 Observer로 보고
                                observableEmitter.onNext(TempInfo.fetch(town));
                            } catch (Exception e) {
                                // 에러가 발생하면 Observer에 알림
                                observableEmitter.onError(e);
                            }
                        }
                    }
                })
        );
    }

}
