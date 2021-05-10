package com.seungmoo.modernjava.behavior_parameter;

public class Apple {
    private Integer weight;
    private AppleColor color;

    public Apple(Integer weight, AppleColor color) {
        this.weight = weight;
        this.color = color;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public AppleColor getColor() {
        return color;
    }

    public void setColor(AppleColor color) {
        this.color = color;
    }
}
