package com.seungmoo.modernjava.reactive.asyncapp;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NonBlockingApp {

    public static void main(String[] args) {

        long start = System.nanoTime();
        findPricesWithFactory("myPhone27S");
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " msecs");

    }

    public static List<String> findPrices(String product) {
        List<Shop> shops = List.of(new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"));

        // 비 병렬로 돌릴 경우 각 Shop에서 1초씩 걸리므로, 4초 이상이 걸림.
        // BUT 아래처럼 parallelStream으로 실행할 경우, 각 Shop에서 비동기 병렬 실행되므로 1초 정도 걸림!
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    // 위의 메서드를 supplyAsync Factory 메서드를 활용해보자.
    public static List<String> findPricesWithFactory(String product) {
        List<Shop> shops = List.of(new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"));

        // non blocking 하게 별도의 쓰레드에서 계산 처리 및 future를 반환하도록 한다.
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> String.format("%s price is %.2f",
                                shop.getShopName(), shop.getPrice(product))
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
