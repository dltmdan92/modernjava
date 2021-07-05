package com.seungmoo.modernjava.reactive.temp_app;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 수신한 온도를 출력하는 Observer
 *
 * Observer는 TempSubscriber와 비슷하지만 더 단순한다.
 * RxJava의 Observable은 역압력을 지원하지 않으므로, 전달된 요소를 처리한 다음 추가 요소를 요청하는 request() 메서드가 필요 없음!!
 */
public class TempObserver implements Observer<TempInfo> {
    @Override
    public void onSubscribe(@NonNull Disposable disposable) {

    }

    @Override
    public void onNext(@NonNull TempInfo tempInfo) {
        System.out.println(tempInfo);
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        System.out.println("Got problem: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Done!");
    }
}
