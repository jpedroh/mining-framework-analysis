/*
 * (C) Copyright 2018-2018, by Semen Chudakov and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * Generic triple.
 *
 * @param <A> the first element type
 * @param <B> the second element type
 * @param <C> the third element type
 * @author Semen Chudakov
 */
public class Triple<A, B, C> implements Serializable {
    private static final long serialVersionUID = -6834012384210752417L;

    /**
     * The first element of the triple.
     */
    private final A first;
    /**
     * The second element of the triple.
     */
    private final B second;
    /**
     * The third element of the triple.
     */
    private final C third;

    /**
     * Get the first element of the triple.
     *
     * @return the first element of the triple
     */
    public A getFirst() {
        return first;
    }

    /**
     * Get the second element of the triple.
     *
     * @return the second element of the triple
     */
    public B getSecond() {
        return second;
    }

    /**
     * Get the third element of the triple.
     *
     * @return the third element of the triple
     */
    public C getThird() {
        return third;
    }

    /**
     * Constructs new triple.
     *
     * @param first  first element
     * @param second second element
     * @param third  third element
     */
    Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Creates new pair without the necessity to specify elements types.
     *
     * @param a   first element
     * @param b   second element
     * @param c   third element
     * @param <A> first element type
     * @param <B> second element type
     * @param <C> third element type
     * @return new triple
     */
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
