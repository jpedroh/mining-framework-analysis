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

<<<<<<< /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/left.java
  class Builder<T extends java.lang.Object> {
    private final ParameterSpace parameterSpace;

    private final List<TestCase> testCases = new LinkedList<>();

    public Builder(ParameterSpace parameterSpace) {
      this.parameterSpace = requireNonNull(parameterSpace);
    }

    public Builder<T> addAllToSeedTuples(Collection<? extends Tuple> collection) {
      collection.stream().map((each) -> toTestCase(TestCase.Category.SEED, each)).forEach(testCases::add);
      return this;
    }

    public Builder<T> addAllToRegularTuples(Collection<? extends Tuple> collection) {
      collection.stream().map((each) -> toTestCase(TestCase.Category.REGULAR, each)).forEach(testCases::add);
      return this;
    }

    public Builder<T> addAllToNegativeTuples(Collection<? extends Tuple> collection) {
      collection.stream().map((each) -> toTestCase(TestCase.Category.NEGATIVE, each)).forEach(testCases::add);
      return this;
    }

    private TestCase toTestCase(TestCase.Category category, Tuple testCaseTuple) {
      Tuple tuple = TupleUtils.copy(testCaseTuple);
      return category.createTestCase(tuple, this.parameterSpace.getConstraints().stream().filter((Constraint constraint) -> !constraint.test(tuple)).collect(Collectors.toList()));
    }

    public TestSuite build() {
      class Impl extends AbstractList<TestCase> implements TestSuite {
        private final List<TestCase> testCases;

        private Impl() {
          this.testCases = new ArrayList<TestCase>(Builder.this.testCases.size()) {
            {
              Builder.this.testCases.stream().filter((testCase) -> stream().noneMatch((registered) -> registered.get().equals(testCase.get()))).forEach(this::add);
            }
          };
        }

        @Override public TestCase get(int index) {
          return this.testCases.get(index);
        }

        @Override public int size() {
          return this.testCases.size();
        }
      }
      return new Impl();
    }
  }
=======
  class Builder {
    private final ParameterSpace parameterSpace;

    private LinkedHashSet<Tuple> regularTuples = new LinkedHashSet<>();

    private LinkedHashSet<Tuple> negativeTuples = new LinkedHashSet<>();

    public Builder(ParameterSpace parameterSpace) {
      this.parameterSpace = requireNonNull(parameterSpace);
    }

    Builder addToRegularTuples(Tuple in) {
      regularTuples.add(new Tuple.Builder().putAll(requireNonNull(in)).build());
      return this;
    }

    public Builder addAllToRegularTuples(Collection<? extends Tuple> collection) {
      collection.forEach(Builder.this::addToRegularTuples);
      return this;
    }

    Builder addToNegativeTuples(Tuple in) {
      negativeTuples.add(new Tuple.Builder().putAll(requireNonNull(in)).build());
      return this;
    }

    public Builder addAllToNegativeTuples(Collection<? extends Tuple> collection) {
      collection.forEach(Builder.this::addToNegativeTuples);
      return this;
    }

    public TestSuite build() {
      List<Tuple> tuples = new ArrayList<Tuple>() {
        {
          this.addAll(regularTuples);
          this.addAll(negativeTuples);
        }
      };
      class Impl extends AbstractList<TestCase> implements TestSuite {
        private Impl() {
        }

        @Override public TestCase get(int index) {
          Tuple object = tuples.get(index);
          if (index < regularTuples.size()) {
            return TestCase.Category.REGULAR.createTestCase(object, Collections.emptyList());
          }
          return TestCase.Category.NEGATIVE.createTestCase(object, parameterSpace.getConstraints().stream().filter((Constraint constraint) -> !constraint.test(tuples.get(index))).collect(Collectors.toList()));
        }

        @Override public int size() {
          return tuples.size();
        }
      }
      return new Impl();
    }
  }
>>>>>>> /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/testsuite/TestSuite.java/right.java
}