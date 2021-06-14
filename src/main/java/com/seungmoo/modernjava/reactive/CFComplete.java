package com.seungmoo.modernjava.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CFComplete {

    public void run() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;

        CompletableFuture<Integer> cf1 = new CompletableFuture<>();
        CompletableFuture<Integer> cf2 = new CompletableFuture<>();

        // complete 메서드 : 나중에 어떤 값을 이용해 다른 스레드가 이를 완료할 수 있고
        // get()으로 값을 얻을 수 있도록 허용
        executorService.submit(() -> cf1.complete(f(x)));
        executorService.submit(() -> cf2.complete(g(x)));

        int b = g(x);

        // 여기서 cf.get() 을 통해 쓰레드 완료 처리를 후행작업으로 할 수 있다.
        System.out.println(cf1.get() + cf2.get());

        // but 위 코드는 get()을 기다려야 하는 상황이 발생하므로, 프로세싱 자원을 낭비할 수 있음!
        // CFCombine.java에서 이것을 해결해보자!!

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
