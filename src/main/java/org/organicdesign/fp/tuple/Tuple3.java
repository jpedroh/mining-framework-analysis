// Copyright 2015 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp.tuple;

import java.util.Objects;

/**
 Holds 3 items of potentially different types.  Designed to let you easily create immutable
 subclasses (to give your data structures meaningful names) with correct equals(), hashCode(), and
 toString() methods.
 */
public class Tuple3<A,B,C> {
    // Fields are protected so that sub-classes can make accessor methods with meaningful names.
    protected final A _1;
    protected final B _2;
    protected final C _3;

    /**
     Constructor is protected (not public) for easy inheritance.  Josh Bloch's "Item 1" says public
     static factory methods are better than constructors because they have names, they can return
     an existing object instead of a new one, and they can return a sub-type.  Therefore, you
     have more flexibility with a static factory as part of your public API then with a public
     constructor.
     */
    protected Tuple3(A a, B b, C c) {
        _1 = a; _2 = b; _3 = c;
    }

    /** Public static factory method */
    public static <A,B,C> Tuple3<A,B,C> of(A a, B b, C c) {
        return new Tuple3<>(a, b, c);
    }

    /** Returns the 1st field of the tuple */
    public A _1() { return _1; }

    /** Returns the 2nd field of the tuple */
    public B _2() { return _2; }

    /** Returns the 3rd field of the tuple */
    public C _3() { return _3; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
               _1 + "," + _2 + "," + _3 +
               ")";
    }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }
        if (!(other instanceof Tuple3)) { return false; }
        // Details...
        @SuppressWarnings("rawtypes") final Tuple3 that = (Tuple3) other;

        return Objects.equals(this._1, that._1()) &&
               Objects.equals(this._2, that._2()) &&
               Objects.equals(this._3, that._3());
    }

    @Override
    public int hashCode() {
        // Has to match Tuple2 which implements java.util.Map.Entry as part of the map contract.
        return  ( (_1 == null ? 0 : _1.hashCode()) ^
                  (_2 == null ? 0 : _2.hashCode()) ) +
                (_3 == null ? 0 : _3.hashCode());
    }
}