package com.seungmoo.modernjava.stream;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime) // 벤치마크 대상 메서드를 실행하는데 걸린 평균 시간 측정
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 벤치마크 결과를 밀리초 단위로 출력
@Fork(value = 2, jvmArgs = {"-Xms4G, -Xmx4G"}) // 초기 4GB의 힙공간을 부여(Xms) 그리고 4GB 단위로 힙 공간을 늘린다.(Xmx)
public class ParallelStreamBenchMark {
    private static final long N = 10000000L;

    @Benchmark  // 벤치마크 대상 메서드
    public long sequentialSum() {
        return Stream.iterate(1L, i -> i + 1).limit(N)
                .reduce(0L, Long::sum);
    }

    @TearDown(Level.Invocation) // 매 번 벤치마크를 실행한 다음에는 GC 동작 시도
    public void tearDown() {
        System.gc();
    }
}
