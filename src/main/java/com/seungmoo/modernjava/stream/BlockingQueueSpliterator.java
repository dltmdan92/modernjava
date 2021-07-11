package com.seungmoo.modernjava.stream;

import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class BlockingQueueSpliterator<T> implements Spliterator<T> {
    private final BlockingQueue<T> q;

    public BlockingQueueSpliterator(BlockingQueue<T> queue) {
        this.q = queue;
    }

    /**
     * ForkingStreamConsumer가 원래의 스트림에서 추가한 요소를 BlockingQueue에서 가져온다.
     *
     *
     *
     * @param action : ForkingStreamConsumer
     * @return
     */
    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        T t;

        while (true) {
            try {
                t = q.take();
                break;
            } catch (InterruptedException e) {}
        }

        if (t != ForkingStreamConsumer.END_OF_STREAM) {
            action.accept(t);
            return true;
        }

        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return 0;
    }

    @Override
    public int characteristics() {
        return 0;
    }
}
