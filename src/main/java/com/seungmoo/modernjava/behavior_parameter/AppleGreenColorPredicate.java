package com.seungmoo.modernjava.behavior_parameter;

import static com.seungmoo.modernjava.behavior_parameter.AppleColor.GREEN;

public class AppleGreenColorPredicate implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return GREEN.equals(apple.getColor());
    }
}
