package com.seungmoo.modernjava.reactive.temp_app;

import java.util.concurrent.Flow;

public class Main {

    public static void main(String[] args) {
        // 뉴욕에 새 Publisher를 만들고 TempSubscriber를 구독시킴
        getTemperatures("New York").subscribe(new TempSubscriber());
        // 화씨를 섭씨로 바꾸는 Processor를 적용
        getCelsiusTemperatures("New York").subscribe(new TempSubscriber());
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

}
