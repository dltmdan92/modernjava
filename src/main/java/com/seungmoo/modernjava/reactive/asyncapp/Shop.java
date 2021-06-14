package com.seungmoo.modernjava.reactive.asyncapp;

import lombok.Getter;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Getter
public class Shop {

    private final String shopName;
    private static final Random random = new Random();

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public String getPrice(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];

        return String.format("%s:%.2f:%s", product, price, code);
    }

    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();

        new Thread(() -> {

            // 가격을 계산하던 중 Error 발생하게 되고, 쓰레드가 정상적으로 종료되지 않는다면,
            // 메인 쓰레드(클라이언트)에서 future의 get()이 끝날때 까지 blocking되는 문제가 발생!!
            // Exception 발생 시, future를 종료시키고 client에게 Exception을 전달한다. --> completeExceptionally

            try {
                // 계산이 정상적으로 종료되면, Future에 가격 정보를 저장한 채로 Future를 종료한다.
                double price = calculatePrice(product); // 다른 쓰레드에서 비동기적으로 계산 수행
                futurePrice.complete(price); // 계산이 완료된 것은 Future에 값 설정
            } catch (Exception ex) {
                // 계산 도중에 문제가 발생하면 발생한 에러를 포함시켜 Future를 종료한다.
                futurePrice.completeExceptionally(ex);
            }
        }).start();

        return futurePrice; // 계산 결과가 완료되길 기다리지 않고 Future를 반환한다.
    }

    // 위의 메서드를 이렇게 간단하게 구현할 수 있음 (Factory 메서드 사용)
    // 에러 관리하는 것도 위와 똑같다.
    public Future<Double> getPriceAsyncWithFactory(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

    private double calculatePrice(String product) {
        delay();
        return Math.random() * product.charAt(0) + product.charAt(1);
    }

    public static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void randomDelay() {
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
