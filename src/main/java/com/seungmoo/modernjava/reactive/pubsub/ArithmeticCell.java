package com.seungmoo.modernjava.reactive.pubsub;


public class ArithmeticCell extends SimpleCell {

    private int left;
    private int right;

    public ArithmeticCell(String name) {
        super(name);
    }

    public void setLeft(int left) {
        this.left = left;
        // 셀 값을 갱신하고, 모든 구독자에게 알림
        onNext(left + this.right);
    }

    public void setRight(int right) {
        this.right = right;
        // 셀 값을 갱신하고 모든 구독자에게 알림
        onNext(right + this.right);
    }
}
