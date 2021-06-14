package com.seungmoo.modernjava.reactive.asyncapp;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class NonBlockingApp {
    private static final String SHOP_NAME = "BestPrice";
    private static final List<Shop> shops = List.of(new Shop(SHOP_NAME),
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

    public static void main(String[] args) {

        long start = System.nanoTime();
        findPricesWithFactory("BestPrice");
        long duration = (System.nanoTime() - start) / 1_000_000;
        log.info("Done in " + duration + " msecs");

        // 4번째 map 연산의 thenAccept는 CompletableFuture<Void>를 반환한다.
        CompletableFuture[] futures = findPricesStream(SHOP_NAME)
                .map(f -> f.thenAccept(System.out::println))
                .toArray(size -> new CompletableFuture[size]);
        // allOf는 CompletableFuture Array를 받아서 CompletableFuture<Void>를 반환한다.
        // 그리고 allOf 뒤에 join()을 호출하면 원래 스트림의 모든 CompletableFuture의 실행 완료를 기다릴 수 있음!!
        CompletableFuture.allOf(futures).join();
    }

    // Shop 12개 - 2초 걸림
    // 파이프라인 - 24069 msecs 걸림 (24초)
    // parallelStream --> ForkJoinPool의 객체를 사용 (Runtime.getRuntime().availableProcessors() 만큼만 사용)
    public static List<String> findPrices(String product) {

        // 비 병렬로 돌릴 경우 각 Shop에서 1초씩 걸리므로, 4초 이상이 걸림.
        // BUT parallelStream으로 실행할 경우, 각 Shop에서 비동기 병렬 실행되므로 1초 정도 걸림!

        // 상점 스트림에 파이프라인을 걸어보았다.
        // 성능 최적화는 하나도 안되어있음!!
        return shops.stream()
                .map(shop -> shop.getPrice(product)) // 각 상점을 요청한 제품의 가격과 할인 코드로 return
                .map(Quote::parse) // 이들 문자열을 파싱해서 Quote 객체를 만든다.
                .map(Discount::applyDiscount) // 원격 Discount 서비스에 접근해서 최종 할인가격을 계산
                .collect(toList());
    }

    // Shop 12개 - 1초 걸림
    // 파이프라인 - 2099 msecs 걸림 (2초)
    // 위의 메서드를 supplyAsync Factory 메서드를 활용해보자.
    // CompleletableFuture를 활용하면, parallelStream() 과는 다르게 직접 쓰레드풀을 설정 가능하다!!!
    // 이 방식은 Shop의 갯수가 늘어날 수 록, 유동적으로 쓰레드풀을 설정할 수 있어서 빛을 발한다.
    public static List<String> findPricesWithFactory(String product) {

        ExecutorService executorService = getExecutorService();

        // non blocking 하게 별도의 쓰레드에서 계산 처리 및 future를 반환하도록 한다.
        List<CompletableFuture<String>> priceFutures = shops.stream()
                // supplyAsync 메서드를 통해 비동기 작업을 시킨다.
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPrice(product), executorService
                ))
                // thenApply는 CompletableFuture의 작업을 block하지 않는다.
                // 그러므로 앞의 CompletableFuture가 동작을 완전히 완료한 다음에 thenApply 메서드로 전달된 람다식을 적용한다.
                // 여기서는 CompletableFuture<String>을 CompletableFuture<Quote>로 변환하는 것이다.
                .map(future -> future.thenApply(Quote::parse))
                // thenCompose 메서드를 통해 비동기 작업을 조합할 수 있음 (thenCompose를 통해 첫번째 연산의 결과를 두번째 연산으로 전달한다.)
                // 여기서는 위의 quote로 만드는 작업을 아래 supplyAsync 작업 부분과 조합(전달)했음
                // thenCompose를 통해 두 개의 비동기 연산의 파이프라인(순서)을 만들 수 있음. (A, B -> B 연산은 A에 의존)
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executorService)))
                .collect(toList());

        // thenCombine 은 "독립"적인 CompletableFuture를 합칠 때 쓰인다.
        /*
        CompletableFuture.supplyAsync(() -> shop.getPrice(product)) // 첫번째 Task (price)
                .thenCombine(CompletableFuture.supplyAsync(
                        () -> exchangeService.getRate(Money.EUR, Money.USD), // 두번째 Task (rate)
                        (price, rate) -> price * rate; // 위의 두개의 각각 독립적인 task를 받아서 연산한다.
                ));
         */

        return priceFutures.stream()
                // 해당 map과 위의 map을 하나의 파이프라인에서 돌린다면 --> 루프퓨전으로 인한 loop 당 join()이 수행!!
                // 결구 그러면 동기적, 순차적 구현이 돼버린다. (주의할 것!!)
                .map(CompletableFuture::join) // 모든 비동기 동작이 끝나길 기다림.
                // CompletableFuture의 join은 Future의 get과 같은 의미
                .collect(toList());
    }

    private static ExecutorService getExecutorService() {
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
        return executorService;
    }


    /**
     * 의존관계인 두 개의 Task를 Compose (순서조합)해서 Stream 반환
     * @param product
     * @return
     */
    public static Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), getExecutorService()))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(()->Discount.applyDiscount(quote),getExecutorService())));
    }

}
