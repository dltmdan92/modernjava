package com.seungmoo.modernjava.reactive.asyncapp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 할인 서비스 구현
 */
@RequiredArgsConstructor
@Getter
public class Quote {

    private final String shopName;
    private final double price;
    private final Discount.Code discountCode;

    public static Quote parse(String s) {
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Discount.Code discountCode = Discount.Code.valueOf(split[2]);
        return new Quote(shopName, price, discountCode);
    }
    
}
