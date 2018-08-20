package org.jgrapht.alg.util;

import java.util.Objects;

public class Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    public C getThird() {
        return third;
    }

    public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
        return new Triple<>(a, b, c);
    }

    @Override
    public String toString() {
        return "(" + this.first + "," + this.second + "," + this.third + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Triple) {
            @SuppressWarnings("unchecked") Triple<A, B, C> casted = (Triple<A, B, C>) obj;
            return Objects.equals(first, casted.first)
                    && Objects.equals(second, casted.second)
                    && Objects.equals(third, casted.third);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}
