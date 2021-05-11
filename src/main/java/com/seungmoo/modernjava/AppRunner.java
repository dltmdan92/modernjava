package com.seungmoo.modernjava;

import com.seungmoo.modernjava.thread.ExecutorServiceExample;
import com.seungmoo.modernjava.thread.ScheduledExecutorServiceExample;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppRunner implements ApplicationRunner {

    //private final ParallelStreamBenchMark parallelStreamBenchMark = new ParallelStreamBenchMark();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ScheduledExecutorServiceExample executorServiceExample = new ScheduledExecutorServiceExample();
         executorServiceExample.run();
    }
}
