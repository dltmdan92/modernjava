package com.seungmoo.modernjava.stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class ForkingStreamConsumer<T> implements Consumer<T>, Results {
    static final Object END_OF_STREAM = new Object();

    private final List<BlockingQueue<T>> queues;
    private final Map<Object, Future<?>> actions;

    public ForkingStreamConsumer(List<BlockingQueue<T>> queues, Map<Object, Future<?>> actions) {
        this.queues = queues;
        this.actions = actions;
    }

    /**
     * 키에 대응하는 동작의 결과를 반환, Future의 계산완료 대기 (get)
     * 맵에서 Future를 가져온 다음에 값을 Unwrap or 결과 대기
     *
     * @param key
     * @param <R>
     * @return
     */
    @Override
    public <R> R get(Object key) {
        try {
            return ((Future<R>) actions.get(key)).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 스트림에서 탐색한 요소를 모든 큐로 전달
     * ForkingStreamConsumer가 스트림 요소를 받을 때마다 요소를 BlockingQueue로 추가한다.
     * @param t
     */
    @Override
    public void accept(T t) {
        queues.forEach(q -> q.add(t));
    }

    /**
     * 스트림의 끝을 알리는 마지막 요소를 큐에 삽입
     */
    void finish() {
        accept((T) END_OF_STREAM);
    }
}
