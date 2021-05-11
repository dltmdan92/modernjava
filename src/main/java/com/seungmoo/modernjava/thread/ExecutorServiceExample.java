package com.seungmoo.modernjava.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ThreadExample.java를 Future 통해서 더 간단하게 코드를 작성가능합니다.
 */
public class ExecutorServiceExample {

    public void run() throws ExecutionException, InterruptedException {
        int x = 1337;

        List<Integer> list = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // Callable 리턴 -> Future
        // 이 코드 마저도 submit메서드 같은 코드로 덕지덕지 붙이게 되었다. --> 비동기 API 기능으로 해결해봅시다.
        Future<Integer> y = executorService.submit(() -> f(x));
        Future<Integer> z = executorService.submit(() -> g(x));
        System.out.println(y.get() + z.get());

        // shutdown 안해주면 Thread가 계속 살아있음
        executorService.shutdown();
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
