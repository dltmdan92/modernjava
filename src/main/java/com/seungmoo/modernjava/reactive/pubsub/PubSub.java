package com.seungmoo.modernjava.reactive.pubsub;

/**
 * JAVA 9 에서 리액티브 프로그래밍은(Flow interface) 발행 구독(Pub Sub)의 모델을 갖고 있음
 */
public class PubSub {

    public void run() {
        SimpleCell c3 = new SimpleCell("C3");
        SimpleCell c2 = new SimpleCell("C2");
        SimpleCell c1 = new SimpleCell("C1");

        c1.subscribe(c3);

        c1.onNext(10); // C1의 값을 10으로 갱신하고, 구독자에게 알림
        c2.onNext(20); // C2의 값을 20으로 갱신하고, 구독자에게 알림

        // C3는 직접 C1을 구독하므로, C1의 onNext에서 갱신한 값을 구독해간다!!
        // C1:10
        // C3:10
        // C2:20

        ArithmeticCell sc3 = new ArithmeticCell("C3");
        SimpleCell sc2 = new SimpleCell("C2");
        SimpleCell sc1 = new SimpleCell("C1");

        sc1.subscribe(sc3);
        sc2.subscribe(sc1);

        sc1.onNext(10); // C1의 값을 10으로 갱신, 구독자에게 알림
        sc2.onNext(20); // C2의 값을 20으로 갱신, 구독자에게 알림
        sc1.onNext(15); // C1의 값을 15로 갱신, 구독자에게 알림

        // 위의 소스를 통해 C1의 값이 15로 갱신되었을 때, C3이 즉시 반응해서 자신의 값을 갱신한다.
        // 발행자-구독자 상호작용의 멋진 점은 발행자 구독자의 그래프를 설정할 수 있다는 점.
        // 예를 들어 "C5 = SC3 + SC4" 처럼 C3과 C4에 의존하는 새로운 셀 C5를 만들 수 있다.

        ArithmeticCell sc5 = new ArithmeticCell("C5");
        SimpleCell sc4 = new SimpleCell("C4");

        // C5는 C3, C4를 구독
        sc3.subscribe(sc5);
        sc4.subscribe(sc5);

        // upstream : value는 onNext()통해 전달되고 (발행자에게 갱신해줌)
        // downstream : notifyAllSubscribers()를 통해 onNext()로 전달된다. (구독자에게 내려줌)
        // 단점 pub, sub을 남발하면 개판된다.
    }
}
