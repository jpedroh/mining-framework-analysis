package com.github.dakusui.jcunit8.testsuite;

import com.github.dakusui.jcunit.core.tuples.Tuple;
import com.github.dakusui.jcunit.core.tuples.TupleUtils;
import com.github.dakusui.jcunit8.factorspace.Constraint;
import com.github.dakusui.jcunit8.factorspace.ParameterSpace;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * As a result of tuple suite generation, tuples that are identical if they are
 * converted back to parameter space can be created.
 * <p>
 * This class eliminates those tuples on its construction.
 */
public interface TestSuite extends List<TestCase> {
  class Builder {
    private final ParameterSpace parameterSpace;
    private final List<TestCase> testCases = new LinkedList<>();

    public Builder(ParameterSpace parameterSpace) {
      this.parameterSpace = requireNonNull(parameterSpace);
    }

<<<<<<< /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/left.java
    public Builder<T> addAllToSeedTuples(Collection<? extends Tuple> collection) {
      collection.stream().map(each -> toTestCase(TestCase.Category.SEED, each)).forEach(testCases::add);
||||||| /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/base.java
    Builder<T> addToRegularTuples(Tuple in) {
      regularTuples.add(new Tuple.Builder().putAll(requireNonNull(in)).build());
=======
    Builder addToRegularTuples(Tuple in) {
      regularTuples.add(new Tuple.Builder().putAll(requireNonNull(in)).build());
>>>>>>> /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/right.java
      return this;
    }

<<<<<<< /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/left.java
    public Builder<T> addAllToRegularTuples(Collection<? extends Tuple> collection) {
      collection.stream().map(each -> toTestCase(TestCase.Category.REGULAR, each)).forEach(testCases::add);
||||||| /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/base.java
    public Builder<T> addAllToRegularTuples(Collection<? extends Tuple> collection) {
      collection.forEach(Builder.this::addToRegularTuples);
=======
    public Builder addAllToRegularTuples(Collection<? extends Tuple> collection) {
      collection.forEach(Builder.this::addToRegularTuples);
>>>>>>> /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/right.java
      return this;
    }
<<<<<<< /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/left.java
  
    public Builder<T> addAllToNegativeTuples(Collection<? extends Tuple> collection) {
      collection.stream().map(each -> toTestCase(TestCase.Category.NEGATIVE, each)).forEach(testCases::add);
||||||| /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/base.java
  
    Builder<T> addToNegativeTuples(Tuple in) {
      negativeTuples.add(new Tuple.Builder().putAll(requireNonNull(in)).build());
      return this;
    }


    public Builder<T> addAllToNegativeTuples(Collection<? extends Tuple> collection) {
      collection.forEach(Builder.this::addToNegativeTuples);
=======
  
    Builder addToNegativeTuples(Tuple in) {
      negativeTuples.add(new Tuple.Builder().putAll(requireNonNull(in)).build());
      return this;
    }

    public Builder addAllToNegativeTuples(Collection<? extends Tuple> collection) {
      collection.forEach(Builder.this::addToNegativeTuples);
>>>>>>> /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/right.java
      return this;
    }
<<<<<<< /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/left.java
  
    private TestCase toTestCase(TestCase.Category category, Tuple testCaseTuple) {
      Tuple tuple = TupleUtils.copy(testCaseTuple);
      return category.createTestCase(
          tuple,
          this.parameterSpace.getConstraints().stream()
              .filter((Constraint constraint) -> !constraint.test(tuple))
              .collect(Collectors.toList()));
    }
||||||| /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/base.java
=======
>>>>>>> /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/right.java

    public TestSuite build() {
      class Impl extends AbstractList<TestCase> implements TestSuite {
        private final List<TestCase> testCases;

        private Impl() {
          this.testCases = new ArrayList<TestCase>(Builder.this.testCases.size()) {{
            Builder.this.testCases.stream(
            ).filter(
                testCase -> stream().noneMatch(
                    registered -> registered.get().equals(testCase.get())
                )
            ).forEach(
                this::add
            );
          }};
        }

        @Override
        public TestCase get(int index) {
          return this.testCases.get(index);
        }

        @Override
        public int size() {
          return this.testCases.size();
        }
      }
      return new Impl();
    }
  }
}
