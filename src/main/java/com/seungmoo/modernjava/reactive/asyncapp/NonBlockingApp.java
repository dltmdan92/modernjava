package com.seungmoo.modernjava.reactive.asyncapp;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

@Slf4j
public class NonBlockingApp {
    private static final String SHOP_NAME = "BestPrice";

    public static void main(String[] args) {

        long start = System.nanoTime();
        findPricesWithFactory("myPhone27S");
        long duration = (System.nanoTime() - start) / 1_000_000;
        log.info("Done in " + duration + " msecs");
    }

    // Shop 12개 - 2초 걸림
    // parallelStream --> ForkJoinPool의 객체를 사용 (Runtime.getRuntime().availableProcessors() 만큼만 사용)
    public static List<String> findPrices(String product) {

        List<Shop> shops = List.of(new Shop(SHOP_NAME),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"),
                new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"),
                new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"));

        // 비 병렬로 돌릴 경우 각 Shop에서 1초씩 걸리므로, 4초 이상이 걸림.
        // BUT 아래처럼 parallelStream으로 실행할 경우, 각 Shop에서 비동기 병렬 실행되므로 1초 정도 걸림!
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    // Shop 12개 - 1초 걸림
    // 위의 메서드를 supplyAsync Factory 메서드를 활용해보자.
    // CompleletableFuture를 활용하면, parallelStream() 과는 다르게 직접 쓰레드풀을 설정 가능하다!!!
    // 이 방식은 Shop의 갯수가 늘어날 수 록, 유동적으로 쓰레드풀을 설정할 수 있어서 빛을 발한다.
    public static List<String> findPricesWithFactory(String product) {

        List<Shop> shops = List.of(new Shop(SHOP_NAME),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"),
                new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"),
                new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"));

        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(shops.size(), 100), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                // 자바에서 일반 스레드가 실행 중이면 자바 프로그램은 종료되지 않는다.
                // 따라서 어떤 이벤트를 한없이 기다리면서 종료되지 않는 일반 스레드가 있으면 문제가 될 수 있음!!
                // 반면 데몬스레드는 자바 프로그램이 종료될 때 강제로 실행이 종료될 수 있음. (두 쓰레드의 성능은 같음)
                thread.setDaemon(true); // 프로그램 종료를 방해하지 않는 데몬 스레드를 사용
                return thread;
            }
        });

        // non blocking 하게 별도의 쓰레드에서 계산 처리 및 future를 반환하도록 한다.
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> String.format("%s price is %.2f",
                                shop.getShopName(), shop.getPrice(product)), executorService
                ))
                .collect(Collectors.toList());

        return priceFutures.stream()
                // 해당 map과 위의 map을 하나의 파이프라인에서 돌린다면 --> 루프퓨전으로 인한 loop 당 join()이 수행!!
                // 결구 그러면 동기적, 순차적 구현이 돼버린다. (주의할 것!!)
                .map(CompletableFuture::join) // 모든 비동기 동작이 끝나길 기다림.
                // CompletableFuture의 join은 Future의 get과 같은 의미
                .collect(Collectors.toList());
    }

}
