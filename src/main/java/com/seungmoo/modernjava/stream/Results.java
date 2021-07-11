package com.seungmoo.modernjava.stream;

/**
 * fork 메서드에서 사용하는 key 객체를 받는 하나의 메서드 정의를 포함
 */
public interface Results {
    /**
     * key에 대응되는 연산 결과를 return 한다.
     * @param key
     * @param <R> : key에 대응되는 연산 결과
     * @return
     */
    public <R> R get(Object key);
}
