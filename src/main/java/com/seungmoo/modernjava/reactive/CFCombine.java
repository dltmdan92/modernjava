package com.seungmoo.modernjava.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CFComplete.java 예제에서 발생하는 get() 으로 인한 블록킹 현상을 해결하는 예제!
 */
public class CFCombine {

    public void run() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        CompletableFuture<Integer> b = new CompletableFuture<>();

        // thenCombine을 써서 두 함수를 조합한다.
        // STEP 1. a, b 함수 조합 선언을 먼저하고
        CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> y + z);

        // STEP 2. a, b 함수를 각각 정의 (선 조합, 후 정의)
        executorService.submit(() -> a.complete(f(x)));
        executorService.submit(() -> b.complete(g(x)));

        // 조합한 다음에 나중에 쓸 때 한번 get() 해준다.
        System.out.println(c.get());
        executorService.shutdown();

        // thenCombine을 통해 각각 2개의 쓰레드에서 f(x), g(x)를 실행하고
        // 나중에 main쓰레드에서 조합 및 덧셈 수행한다.

        // 많은 수의 Future를 사용하는 Case에서
        // CompletableFuture와 콤비네이터를 이용해, get() 블록을 최소화하고
        // 병렬 실행의 효율성 + 데드락 회피 기법을 구현할 수 있다.
    }

    private int g(int x) {
        System.out.println(Thread.currentThread());
        return x * -1000;
    }

    private int f(int x) {
        System.out.println(Thread.currentThread());
        return x * 1000;
    }

}
