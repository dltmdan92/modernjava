package com.seungmoo.modernjava.reactive.asyncapp;

import java.util.concurrent.Future;

public class ShopApp {

    public static void main(String[] args) {
        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();

        // 1. 상점에 제품 가격 요청
        // 2. getPriceAsync -> 비동기 API 이므로 즉시 future를 반환함.
        // 3.클라이언트는 future를 이용해서 나중에 결과를 얻을 수 있음.
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
        long invocationTime = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Invocation returned after " + invocationTime + " msecs");

        // 4. 제품을 계산하는 동안 그 사이 클라이언트는 다른 상점에 가격 정보를 요청하는 등
        //      첫 번째 상점의 결과를 기다리면서 대기하지 않고 다른 작업을 처리할 수 있음.
        doSomeThingElse();
        // 다른 상점 검색 등 다른 작업 수행


        try {
            // 5. 그리고 나중에 클라이언트가 딱히 할일이 없으면, 그때 get()으로 요청한다.
            // 이때 Future가 결과값을 가지고 있다면, 값을 리턴 otherwise(아니면) 값이 계산될때까지 block 된다.
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long retrievalTime = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Price returned after " + retrievalTime + " msecs");
    }

    private static void doSomeThingElse() {

    }

}
