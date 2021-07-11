package com.seungmoo.modernjava.stream;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 스트림에 여러 연산을 수행하는 데 필요한 StreamForker 정의
 *
 * StreamForker를 통해 포크된 스트림에 다른 연산을 할당할 수 있도록 편리하고 유연한 API를 제공한다.
 * 더 Fork할 스트림이 없는 경우, StreamForker에 getResults를 호출해서 정의한 연산을 모두 수행하고 Results를 얻을 수 있다.
 *
 * 내부에서는 연산을 비동기적으로 실행하므로 getResults 메서드를 호출하면 결과는 즉시 반환된다!
 *
 * @param <T>
 */
public class StreamForker<T> {
    private final Stream<T> stream;
    private final Map<Object, Function<Stream<T>, ?>> forks = new HashMap<>();

    public StreamForker(Stream<T> stream) {
        this.stream = stream;
    }

    /**
     *
     * @param key : 연산의 결과를 제공하는 Key
     * @param f : 스트림을 특정 연산의 결과 형식으로 변환하는 Function
     * @return : fork 메서드는 StreamForker 자신을 반환한다. 따라서 여러 연산을 포킹(분기) 해서 파이프라인을 만들 수 있다.
     */
    public StreamForker<T> fork(Object key, Function<Stream<T>, ?> f) {
        forks.put(key, f); // 스트림에 적용할 함수 저장
        return this; // 유연하게 fork 메서드를 여러 번 호출할 수 있도록 this 반환
    }

    /**
     * getResults 메서드를 호출하면 fork 메서드로 추가한 모든 연산이 실행된다.
     * @return
     */
    public Results getResults() {
        ForkingStreamConsumer<T> consumer = build();

        try {
            stream.sequential().forEach(consumer);
        } finally {
            consumer.finish();
        }

        return consumer;
    }

    private ForkingStreamConsumer<T> build() {
        // 각각의 연산을 저장할 큐 리스트를 생성
        List<BlockingQueue<T>> queues = new ArrayList<>();

        // 연산 결과를 포함하는 Future를 연산을 식별할 수 있는 키에 대응시켜 맵에 저장
        Map<Object, Future<?>> actions = forks.entrySet().stream().reduce(
                new HashMap<>(),
                (map, e) -> {
                    map.put(e.getKey(),
                            getOperationResult(queues, e.getValue()));
                    return map;
                },
                (m1, m2) -> {
                    m1.putAll(m2);
                    return m1;
                }
        );

        return new ForkingStreamConsumer<>(queues, actions);
    }

    private Future<?> getOperationResult(List<BlockingQueue<T>> queues, Function<Stream<T>, ?> f) {
        BlockingQueue<T> queue = new LinkedBlockingQueue<>();
        queues.add(queue);
        Spliterator<T> spliterator = new BlockingQueueSpliterator<>(queue);
        Stream<T> source = StreamSupport.stream(spliterator, false);
        return CompletableFuture.supplyAsync(() -> f.apply(source));
    }
}
