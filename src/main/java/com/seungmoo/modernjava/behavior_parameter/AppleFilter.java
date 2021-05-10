package com.seungmoo.modernjava.behavior_parameter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static java.util.Comparator.comparing;

public class AppleFilter {

    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            // Predicate 객체로 사과 검사 조건을 캡슐화 했다.
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static void prettyPrintApple(List<Apple> inventory, AppleFormatter formatter) {
        for (Apple apple : inventory) {
            String output = formatter.accept(apple);
            System.out.println(output);
        }
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        list.forEach(e -> {
            if (p.test(e)) {
                result.add(e);
            }
        });
        return result;
    }

    public static void run() {
        List<Apple> inventory = new ArrayList<>();
        inventory.add(new Apple(100, AppleColor.RED));
        inventory.add(new Apple(150, AppleColor.GREEN));
        inventory.add(new Apple(200, AppleColor.RED));
        inventory.add(new Apple(250, AppleColor.GREEN));

        List<Apple> apples = filterApples(inventory, new AppleGreenColorPredicate());
        prettyPrintApple(inventory, new AppleFancyFormatter());

        // 익명 클래스를 통해 메서드의 동작을 파라미터화 할 수 있다.
        // 그리고 그 익명 클래스를 람다식으로 표현해서 더욱더 간소화 시킬 수 있다.
        //List<Apple> redApples = filterApples(inventory, apple -> AppleColor.RED.equals(apple.getColor()));
        // 이렇게 동작 파라미터화 + 람다식 을 통해 유연성과 간결함을 얻을 수 있다.
        List<Apple> redApples = filter(apples, (apple -> AppleColor.RED.equals(apple.getColor())));

        Runnable runnable = () -> System.out.println("Tricky example!");

        Runnable o = (Runnable) () -> System.out.println("Tricky example!");
        o.run();

        BiFunction<Integer, AppleColor, Apple> appleBiFunction = (weight, AppleColor) -> new Apple(weight, AppleColor);
        BiFunction<Integer, AppleColor, Apple> appleMethodRefer = Apple::new;

        //inventory.sort((a1, a2) -> a1.getWeight().compareTo(a2.getWeight()));
        inventory.sort(comparing(Apple::getWeight));
    }
}
