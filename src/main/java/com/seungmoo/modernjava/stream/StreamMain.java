package com.seungmoo.modernjava.stream;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Service
public class StreamMain {

    public void run() {
        List<Dish> menu = Arrays.asList(
                new Dish("port", false, 800, Type.MEAT),
                new Dish("beef", false, 700, Type.MEAT),
                new Dish("chicken", false, 400, Type.MEAT),
                new Dish("french fries", true, 530, Type.OTHER),
                new Dish("rice", true, 350, Type.OTHER),
                new Dish("season fruit", true, 120, Type.OTHER),
                new Dish("pizza", true, 550, Type.OTHER),
                new Dish("prawns", false, 300, Type.FISH),
                new Dish("salmon", false, 450, Type.FISH)
        );

        List<String> threeHightCaloricDishNames = menu.stream()
                .filter(dish -> dish.getCalories() > 300)
                .map(Dish::getName)
                .limit(3)
                .collect(Collectors.toList());
        System.out.println(threeHightCaloricDishNames);

        // 스트림 슬라이싱
        List<Dish> specialMenu = Arrays.asList(
                new Dish("seasonal fruit", true, 120, Type.OTHER),
                new Dish("prawns", false, 300, Type.FISH),
                new Dish("rice", true, 350, Type.OTHER),
                new Dish("chicken", false, 400, Type.MEAT),
                new Dish("french fries", true, 530, Type.OTHER)
        );

        List<Dish> slicedMenu = specialMenu.stream()
                //.filter(dish -> dish.getCalories() < 320)
                // 이미 정렬된 List에서 320이 넘는 부분에서 연산을 끝내버릴 수 있는 기법
                .takeWhile(dish -> dish.getCalories() < 320)
                .collect(Collectors.toList());

        List<Dish> slicedMenu2 = specialMenu.stream()
                //.filter(dish -> dish.getCalories() < 320)
                // 위의 takeWhile과는 정반대 작업, 프레디케이트가 처음으로 거짓이 되는 지점까지 발견된 요소를 버린다.
                .dropWhile(dish -> dish.getCalories() < 320)
                .collect(Collectors.toList());

        // Helowrd 로 List를 만든다.
        String[] words = {"Hello", "World"};
        List<String> uniqueCharacters = Arrays.stream(words)
                .map(word -> word.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());

        // Stream의 Comparator를 통해서 Max 값을 구한다.
        // 요약 연산 (리듀싱 기능)
        Comparator<Dish> dishCaloryComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> collect = menu.stream()
                .collect(maxBy(dishCaloryComparator));
        // Max Dish를 구하는 리듀싱 메서드
        Optional<Dish> collect2 = menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));

        // 요약 연산
        // 총합을 구하는 연산.
        // 리듀싱 기능
        Integer totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
        // reducing 메소드를 통해 모든 reducing 메서드를 대체할 수 있다.
        // param 1 : 시작값, 인수없을 때는 반환값
        Integer collect1 = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));

        // 두 개 이상의 요약 연산을 한 번에 수행하기
        // summarizingInt 요약 연산 -> count, sum, min, average, max 를 추출한다.
        IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));

        // 문자열 연결하기
        // 문자열을 join해서 하나의 값으로 리듀싱 한다.
        String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));

        // groupingBy : 분류 함수
        Map<Type, List<Dish>> collect3 = menu.stream().collect(groupingBy(Dish::getType));

        // groupingBy
        Map<CaloricLevel, List<Dish>> collect4 = menu.stream().collect(groupingBy(dish -> {
            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        }));


        List<Dish> subDishes1 = menu.subList(0, 3);
        List<Dish> subDishes2 = menu.subList(3, 6);
        List<Dish> subDishes3 = menu.subList(6, 9);

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        threadPool.submit(() -> {
            for (Dish dish : subDishes1) {
                dish.setCalories(dish.getCalories() * 2);
            }
        });

        threadPool.submit(() -> {
            for (Dish dish : subDishes2) {
                dish.setCalories(dish.getCalories() * 2);
            }
        });

        threadPool.submit(() -> {
            for (Dish dish : subDishes3) {
                dish.setCalories(dish.getCalories() * 2);
            }
        });

    }

    // 1부터 n까지 더하는 반복문 기반 메서드
    public long iterativeSum(long n) {
        long result = 0;
        for (long i = 1L; i <= n; i++) {
            result += i;
        }
        return result;
    }


    public long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
                .limit(n) // 스트림 연산을 n개 이하로 제한
                .parallel() // 순차 스트림을 병렬 스트림으로 변황
                .reduce(0L, Long::sum); // 모든 숫자를 sum 하는 리듀싱 연산
    }

}
