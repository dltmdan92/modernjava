package com.seungmoo.modernjava.reactive.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

public class SimpleCell implements Flow.Publisher<Integer>, Flow.Subscriber<Integer> {

    private int value = 0;
    private String name;
    private List<Flow.Subscriber<? super Integer>> subscriberList = new ArrayList<>();

    public SimpleCell(String name) {
        this.name = name;
    }

    // Publisher
    // 구독자를 add 한다. (자세한건 Flow 팩토리 클래스 확인 고고)
    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
        subscriberList.add(subscriber);
    }

    // 새로운 값이 있음을 모든 구독자에게 알리는 메서드
    private void notifyAllSubscribers() {
        subscriberList.forEach(subscriber -> subscriber.onNext(this.value));
    }

    // Subscriber
    // 구독한 셀에 새 값이 생겼을때 값을 갱신해서 반응함.
    @Override
    public void onNext(Integer item) {
        this.value = item;
        System.out.println(this.name + ":" + this.value);
        notifyAllSubscribers();
    }

    // Subscriber
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
    }

    // Subscriber
    @Override
    public void onError(Throwable throwable) {

    }

    // Subscriber
    @Override
    public void onComplete() {

    }
}
