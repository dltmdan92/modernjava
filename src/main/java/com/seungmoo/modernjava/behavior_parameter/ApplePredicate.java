package com.seungmoo.modernjava.behavior_parameter;

/**
 * 전략 디자인 패턴 중
 * 알고리즘 패밀리 (여러 알고리즘을 캡슐화 하고, 런타임 시에 해당 알고리즘을 선택한다.)
 */
public interface ApplePredicate {

    boolean test(Apple apple);

}
