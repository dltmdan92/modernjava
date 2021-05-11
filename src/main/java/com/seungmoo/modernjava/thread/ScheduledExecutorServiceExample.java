package com.seungmoo.modernjava.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 본 코드는 work1이 자원을 독점하는 것이 아님
 * work1은 메인스레드에서 실행되고, work2는 별도의 스레드에서 실행된다는 것!
 *
 * work1이 자원을 block하고, 심지어 잠을 자는 상태가 되는 경우에
 * 아래 처럼 work2가 block이 끝나길 기다리지 않고, 독립적으로 비동기적으로 실행되도록 하는 것!
 */
public class ScheduledExecutorServiceExample {

    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        // work1이 끝난 다음, 10초 뒤에 work2()를 개별 태스크로 스케줄함.
        work1();
        scheduledExecutorService.schedule(ScheduledExecutorServiceExample::work2, 10, TimeUnit.SECONDS);

        scheduledExecutorService.shutdown();
    }

    public static void work1() {
        System.out.println("thread-["+Thread.currentThread().getName() + "] Hello from Work1!");
    }

    public static void work2() {
        System.out.println("thread-["+Thread.currentThread().getName() + "] Hello from Work2!");
    }

}
