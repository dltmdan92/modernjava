package com.seungmoo.modernjava.reactive.temp_app;

import io.reactivex.Observable;

import java.util.Arrays;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        // 뉴욕에 새 Publisher를 만들고 TempSubscriber를 구독시킴
        //getTemperatures("New York").subscribe(new TempSubscriber());
        // 화씨를 섭씨로 바꾸는 Processor를 적용
        //getCelsiusTemperatures("New York").subscribe(new TempSubscriber());

        // 매 초마다 뉴욕의 온도 보고를 방출하는 Observable 만들기
        //Observable<TempInfo> observable = getTemperature("New York");
        // 단순 Observer로 이 Observable에 가입해서 온도 출력하기
        //observable.blockingSubscribe(new TempObserver());

        Observable<TempInfo> observable = getCelsiusTemperatures("New York", "Chicago", "San Francisco");
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

    /**
     * RxJava 응용
     * Observable의 map, merge 메서드를 활용해서 섭씨를 화씨로 바꿔보자
     * @param town
     * @return
     */
    public static Observable<TempInfo> getCelsiusTemperature(String town) {
        // getTemperature가 return 하는 Observable을 받아 화씨를 섭씨로 바꾼 다음
        // 매 초 한 개씩 온도를 다시 방출하는 "또 다른 Observable" 을 반환한다.
        return getTemperature(town)
                .map(temp -> new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9));
    }

    /**
     * 영하 온도만 거르기
     * @param town
     * @return
     */
    public static Observable<TempInfo> getNegativeTemperature(String town) {
        return getTemperature(town)
                .filter(temp -> temp.getTemp() < 0);
    }

    /**
     * 한 개 이상 도시의 온도 보고를 합친다.
     * 이 메서드는 Observable의 Iterable을 인수로 받아
     * 마치 한 개의 Observable처럼 동작하도록 결과를 합친다.
     *
     * Observable은 전달된 Iterable에 포함된 모든 Observable의 이벤트 발행물을 시간 순서대로 방출한다.
     *
     * @param towns
     * @return
     */
    public static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(Arrays.stream(towns)
                .map(Main::getCelsiusTemperature)
                .collect(Collectors.toList()));
    }
}
