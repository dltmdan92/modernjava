package com.seungmoo.modernjava.thread;

import java.util.ArrayList;
import java.util.List;

public class ThreadExample {

    public void run() throws InterruptedException {
        int x = 1337;

        List<Integer> list = new ArrayList<>();

        Thread thread1 = new Thread(() -> list.add(f(x)));
        Thread thread2 = new Thread(() -> list.add(g(x)));

        // 쓰레드 태스크 시작
        thread1.start();
        // 쓰레드 태스크 시작
        thread2.start();

        // Wait for this thread to die
        thread1.join();
        // Wait for this thread to die
        thread2.join();

        System.out.println(list.get(0) + list.get(1));
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
