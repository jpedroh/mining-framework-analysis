package com.github.dakusui.jcunit8.factorspace;
import com.github.dakusui.jcunit.core.tuples.Tuple;
import com.github.dakusui.jcunit.exceptions.InvalidTestException;
import com.github.dakusui.jcunit.fsm.FiniteStateMachine;
import com.github.dakusui.jcunit.fsm.spec.FsmSpec;
import com.github.dakusui.jcunit.regex.Expr;
import com.github.dakusui.jcunit.regex.Parser;
import com.github.dakusui.jcunit.regex.RegexComposer;
import com.github.dakusui.jcunit8.core.Utils;
import com.github.dakusui.jcunit8.factorspace.fsm.FsmComposer;
import com.github.dakusui.jcunit8.factorspace.fsm.FsmDecomposer;
import com.github.dakusui.jcunit8.factorspace.fsm.Scenario;
import com.github.dakusui.jcunit8.factorspace.regex.RegexDecomposer;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import static com.github.dakusui.jcunit8.exceptions.TestDefinitionException.checkValue;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * A user level factor.
 *
 * @param <T> Type of values held by this class.
 */
public interface Parameter<T extends java.lang.Object> {
  String getName();

  FactorSpace toFactorSpace();

  T composeValue(Tuple tuple);

  Optional<Tuple> decomposeValue(T value);

  List<T> getKnownValues();

  abstract class Base<T extends java.lang.Object> implements Parameter<T> {
    protected final String name;

    private final List<T> knownValues;

    protected Base(String name, List<T> knownValues) {
      this.name = requireNonNull(name);
      this.knownValues = unmodifiableList(requireNonNull(knownValues));
    }

    @Override public String getName() {
      return this.name;
    }

    @Override public FactorSpace toFactorSpace() {
      return FactorSpace.create(decompose(), generateConstraints());
    }

    @Override public List<T> getKnownValues() {
      return this.knownValues;
    }

    protected abstract List<Factor> decompose();

    protected abstract List<Constraint> generateConstraints();

    static <V extends java.lang.Object> Optional<Tuple> _decomposeValue(V value, Stream<Tuple> tuples, Function<Tuple, V> valueComposer, Predicate<Tuple> constraints) {
      AtomicReference<Optional<Tuple>> fallback = new AtomicReference<>(Optional.empty());
      return tuples.filter((Tuple tuple) -> {
        return value.equals(valueComposer.apply(tuple));
      }).filter(constraints).findFirst();
    }
  }

  interface Factory<T extends java.lang.Object> {
    <F extends Factory<T>> F addActualValue(T actualValue);

    <F extends Factory<T>> F addActualValues(List<T> actualValues);

    Parameter<T> create(String name);

    abstract class Base<T extends java.lang.Object> implements Factory<T> {
      protected final List<T> knownValues = new LinkedList<>();

      @SuppressWarnings(value = { "unchecked" }) @Override public <F extends Factory<T>> F addActualValue(T actualValue) {
        knownValues.add(actualValue);
        return (F) this;
      }

      @SuppressWarnings(value = { "unchecked" }) @Override public <F extends Factory<T>> F addActualValues(List<T> actualValues) {
        actualValues.forEach(this::addActualValue);
        return (F) this;
      }
    }
  }

  interface Simple<T extends java.lang.Object> extends Parameter<T> {
    class Impl<T extends java.lang.Object> extends Base<T> implements Simple<T> {
      final Factor factor;

      public Impl(String name, List<T> allLevels) {
        super(name, allLevels);
        this.factor = Factor.create(name, allLevels.toArray());
      }

      @Override protected List<Factor> decompose() {
        return singletonList(factor);
      }

      @Override protected List<Constraint> generateConstraints() {
        return emptyList();
      }

      @SuppressWarnings(value = { "unchecked" }) @Override public T composeValue(Tuple tuple) {
        return (T) tuple.get(getName());
      }


<<<<<<< Unknown file: This is a bug in JDime.
=======
      @SuppressWarnings(value = { "unchecked" }) @Override public T composeValueFrom(Tuple tuple) {
        return (T) tuple.get(getName());
      }
>>>>>>> /usr/src/app/output/dakusui/jcunit/ca5f167b3c86b6b2e90f8b1fe0425f65a6748713/src/main/java/com/github/dakusui/jcunit8/factorspace/Parameter.java/right.java


      @Override public Optional<Tuple> decomposeValue(T value) {
        return Optional.of(Tuple.builder().put(name, value).build());
      }

      @Override public String toString() {
        return String.format("Simple:%s:%s", factor.getName(), factor.getLevels());
      }
    }

    class Factory<T extends java.lang.Object> extends Parameter.Factory.Base<T> {
      private Factory() {
      }

      public static <U extends java.lang.Object> Factory<U> of(List<U> actualValues) {
        return new Factory<U>().addActualValues(actualValues);
      }

      @Override public Parameter<T> create(String name) {
        return new Impl<>(name, this.knownValues);
      }
    }
  }

  interface Regex<T extends java.lang.Object> extends Parameter<List<T>> {
    class Impl<U extends java.lang.Object> extends Parameter.Base<List<U>> implements Regex<U> {
      private final FactorSpace factorSpace;

      private final RegexComposer regexComposer;

      private final Function<String, U> func;

      public Impl(String name, String regex, List<List<U>> knownValues, Function<String, U> func) {
        super(name, knownValues);
        Expr expr = new Parser().parse(regex);
        RegexDecomposer translator = new RegexDecomposer(name, expr);
        this.func = func;
        this.regexComposer = new RegexComposer(name, expr);
        this.factorSpace = translator.decompose();
      }

      public Optional<Tuple> decomposeValue(List<U> value) {
        return _decomposeValue(value, this.factorSpace.stream(), this.regexComposer::compose, Utils.conjunct(this.factorSpace.getConstraints()));
      }

      @Override public List<U> composeValue(Tuple tuple) {
        return composeStringValueFrom(tuple).stream().map(func).collect(toList());
      }

      @Override protected List<Factor> decompose() {
        return factorSpace.getFactors();
      }

      @Override protected List<Constraint> generateConstraints() {
        return factorSpace.getConstraints();
      }

      private List<String> composeStringValueFrom(Tuple tuple) {
        return regexComposer.compose(tuple);
      }
    }

    class Factory<T extends java.lang.Object> extends Parameter.Factory.Base<List<T>> {
      private final String regex;

      private final Function<String, T> func;

      @Override public Regex<T> create(String name) {
        return create(name, regex, knownValues, func);
      }

      public static <T extends java.lang.Object> Factory<T> of(String regex, Function<String, T> func) {
        return new Factory<>(regex, func);
      }

      public static Factory<String> of(String regex) {
        return new Factory<>(regex, (s) -> s);
      }

      private Factory(String regex, Function<String, T> func) {
        this.regex = requireNonNull(regex);
        this.func = requireNonNull(func);
      }

      private static <U extends java.lang.Object> Regex<U> create(String name, String regex, List<List<U>> knownValues, Function<String, U> func) {
        return new Impl<>(name, regex, knownValues, func);
      }
    }
  }

  interface Fsm<SUT extends java.lang.Object> extends Parameter<Scenario<SUT>> {
    class Impl<SUT extends java.lang.Object> extends Parameter.Base<Scenario<SUT>> implements Fsm<SUT> {
      private final FsmComposer<SUT> composer;

      private final FactorSpace factorSpace;

      Impl(String name, FiniteStateMachine<SUT> model, List<Scenario<SUT>> knownValues, int scenarioLength) {
        super(name, knownValues);
        FsmDecomposer<SUT> decomposer = new FsmDecomposer<>(name, model, scenarioLength);
        this.composer = new FsmComposer<>(name, model, scenarioLength);
        this.factorSpace = FactorSpace.create(decomposer.getFactors(), decomposer.getConstraints());
      }

      @Override public Optional<Tuple> decomposeValue(Scenario<SUT> value) {
        return _decomposeValue(value, this.factorSpace.stream(), (tuple) -> {
          try {
            return this.composer.composeValueFrom(tuple);
          } catch (InvalidTestException e) {
            return false;
          }
        }, Utils.conjunct(this.factorSpace.getConstraints()));
      }

      @Override public Scenario<SUT> composeValue(Tuple tuple) {
        return composer.composeValueFrom(tuple);
      }

      @Override protected List<Factor> decompose() {
        return factorSpace.getFactors();
      }

      @Override protected List<Constraint> generateConstraints() {
        return factorSpace.getConstraints();
      }
    }

    class Factory<SUT extends java.lang.Object> extends Parameter.Factory.Base<Scenario<SUT>> {
      private final Class<? extends FsmSpec<SUT>> fsmSpecClass;

      private int scenarioLength;

      @Override public Fsm<SUT> create(String name) {
        return new Impl<>(name, new FiniteStateMachine.Impl<>(name, this.fsmSpecClass), knownValues, scenarioLength);
      }

      public Factory(Class<? extends FsmSpec<SUT>> fsmSpecClass, int scenarioLength) {
        this.fsmSpecClass = requireNonNull(fsmSpecClass);
        this.scenarioLength = checkValue(scenarioLength, (Integer value) -> value > 0);
      }

      public static <SUT_ extends java.lang.Object> Factory<SUT_> of(Class<? extends FsmSpec<SUT_>> fsmSpecClass, int scenarioLength) {
        return new Factory<>(fsmSpecClass, scenarioLength);
      }
    }
  }
}