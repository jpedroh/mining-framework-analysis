package com.pholser.junit.quickcheck.internal.generator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.PropertyParameterContext;
import com.pholser.junit.quickcheck.internal.constraint.ConstraintEvaluator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import static java.lang.Math.min;
import static java.util.Collections.*;

public class PropertyParameterGenerationContext extends AbstractGenerationStatus {
  private final PropertyParameterContext parameter;

  private final ConstraintEvaluator evaluator;

  private final Generator<?> generator;

  private final Map<Key<?>, Object> contextValues = new HashMap<>();

  private int successfulEvaluations;

  private int discards;

  public PropertyParameterGenerationContext(PropertyParameterContext parameter, GeneratorRepository repository, GeometricDistribution distro, SourceOfRandomness random) {
    super(distro, initializeRandomness(parameter, random, seedLog));
    this.parameter = parameter;
    this.evaluator = new ConstraintEvaluator(parameter.constraint());

<<<<<<< /usr/src/app/output/pholser/junit-quickcheck/522688761ae36a55f4420e247aeb70ef6a7bec1b/core/src/main/java/com/pholser/junit/quickcheck/internal/generator/PropertyParameterGenerationContext.java/left.java
    this.random = initializeRandomness(parameter, random);
=======
>>>>>>> Unknown file: This is a bug in JDime.

    this.generator = repository.produceGenerator(parameter.typeContext());
  }

  private static SourceOfRandomness initializeRandomness(PropertyParameterContext p, SourceOfRandomness r) {
    if (p.fixedSeed()) {
      r.setSeed(p.seed());
    }
    return r;
  }

  public Object generate() {
    Object nextValue;
    for (nextValue = generator.generate(random, this); !evaluate(nextValue); nextValue = generator.generate(random, this)) {
      ;
    }
    return nextValue;
  }

  public List<Object> shrink(Object larger) {
    return generator.canShrink(larger) ? new ArrayList<>(generator.shrink(random, larger)) : emptyList();
  }

  private boolean evaluate(Object value) {
    evaluator.bind(value);
    boolean result = evaluator.evaluate();
    if (result) {
      ++successfulEvaluations;
    } else {
      ++discards;
    }
    if (tooManyDiscards()) {
      throw new DiscardRatioExceededException(parameter, discards, successfulEvaluations);
    }
    return result;
  }

  private boolean tooManyDiscards() {
    if (parameter.discardRatio() == 0) {
      return discards > parameter.sampleSize();
    }
    return successfulEvaluations == 0 ? discards > parameter.discardRatio() : (discards / successfulEvaluations) >= parameter.discardRatio();
  }

  @Override public int size() {
    int sample = super.size();
    return min(sample, parameter.sampleSize());
  }

  @Override public int attempts() {
    return successfulEvaluations + discards;
  }

  @Override public <T extends java.lang.Object> GenerationStatus setValue(Key<T> key, T value) {
    contextValues.put(key, value);
    return this;
  }

  @Override public <T extends java.lang.Object> Optional<T> valueOf(Key<T> key) {
    return Optional.ofNullable(key.cast(contextValues.get(key)));
  }

  public long effectiveSeed() {
    return random.seed();
  }

  public static class DiscardRatioExceededException extends RuntimeException {
    static final String MESSAGE_TEMPLATE = "For parameter [%s] with discard ratio [%d], %d unsuccessful values and %d successes" + " for a discard ratio of [%f]. Stopping.";

    private static final long serialVersionUID = Long.MIN_VALUE;

    DiscardRatioExceededException(PropertyParameterContext parameter, int discards, int successfulEvaluations) {
      super(String.format(MESSAGE_TEMPLATE, parameter.typeContext().name(), parameter.discardRatio(), discards, successfulEvaluations, (double) discards / successfulEvaluations));
    }
  }
}