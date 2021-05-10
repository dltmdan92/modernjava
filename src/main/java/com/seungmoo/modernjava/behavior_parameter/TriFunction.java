package com.seungmoo.modernjava.behavior_parameter;

@FunctionalInterface
public interface TriFunction<R> {
    R apply(int x, int y, int z);
}
