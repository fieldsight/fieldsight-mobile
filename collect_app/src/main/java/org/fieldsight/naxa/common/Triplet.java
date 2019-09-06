package org.fieldsight.naxa.common;

import android.util.Pair;

public class Triplet<T, U, V> {

    private final T first;
    private final U second;
    private final V third;

    public Triplet(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() { return first; }
    public U getSecond() { return second; }
    public V getThird() { return third; }

    public static <A, B> Pair<A, B> create(A a, B b) {
        throw new RuntimeException("Stub!");
    }
}