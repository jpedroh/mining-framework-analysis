package org.jgrapht.alg.util;

import org.jgrapht.alg.shortestpath.DeltaSteppingShortestPath;

import java.util.Objects;

public class Quad<A, B, C, D> {
    private final A first;
    private final B second;
    private final C third;
    private final D fourth;

    public Quad(A first, B second, C third, D fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
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

    public D getFourth() {
        return fourth;
    }

    public static <A, B, C, D> Quad<A, B, C, D> of(A a, B b, C c, D d) {
        return new Quad<>(a, b, c, d);
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + "," + third + "," + fourth + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Quad) {
            @SuppressWarnings("unchecked") Quad<A, B, C, D> casted = (Quad<A, B, C, D>) obj;
            return Objects.equals(first, casted.first)
                    && Objects.equals(second, casted.second)
                    && Objects.equals(third, casted.third)
                    && Objects.equals(fourth, casted.fourth);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }
}
