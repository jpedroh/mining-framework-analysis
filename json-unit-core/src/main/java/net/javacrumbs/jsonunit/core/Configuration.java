package net.javacrumbs.jsonunit.core;
import net.javacrumbs.jsonunit.core.internal.Options;
import net.javacrumbs.jsonunit.core.internal.PathOption;
import net.javacrumbs.jsonunit.core.listener.DifferenceListener;
import org.hamcrest.Matcher;
import java.math.BigDecimal;
import java.util.*;
import java.util.Arrays;

public class Configuration {
  private static final DifferenceListener DUMMY_LISTENER = (difference, context) -> {
  };

  private static final String DEFAULT_IGNORE_PLACEHOLDER = "${json-unit.ignore}";

  private static final String ALTERNATIVE_IGNORE_PLACEHOLDER = "#{json-unit.ignore}";

  private static final Configuration EMPTY_CONFIGURATION = new Configuration(null, Options.empty(), DEFAULT_IGNORE_PLACEHOLDER, Matchers.empty(), Collections.emptySet(), DUMMY_LISTENER, Collections.emptyList());

  private final BigDecimal tolerance;

  private final Options options;

  private final String ignorePlaceholder;

  private final Matchers matchers;

  private final List<PathOption> pathOptions;

  private final Set<String> pathsToBeIgnored;

  private final DifferenceListener differenceListener;

  @Deprecated public Configuration(BigDecimal tolerance, Options options, String ignorePlaceholder) {
    this(tolerance, options, ignorePlaceholder, Matchers.empty(), Collections.emptySet(), DUMMY_LISTENER, Collections.emptyList());
  }

  private Configuration(BigDecimal tolerance, Options options, String ignorePlaceholder, Matchers matchers, Collection<String> pathsToBeIgnored, DifferenceListener differenceListener, List<PathOption> pathOptions) {
    this.tolerance = tolerance;
    this.options = options;
    this.ignorePlaceholder = ignorePlaceholder;
    this.matchers = matchers;
    this.pathsToBeIgnored = Collections.unmodifiableSet(new HashSet<>(pathsToBeIgnored));
    this.pathOptions = pathOptions;
    this.differenceListener = differenceListener;
  }

  public static Configuration empty() {
    return EMPTY_CONFIGURATION;
  }

  public Configuration withTolerance(BigDecimal tolerance) {
    return new Configuration(tolerance, options, ignorePlaceholder, matchers, pathsToBeIgnored, differenceListener, pathOptions);
  }

  public Configuration withTolerance(double tolerance) {
    return withTolerance(BigDecimal.valueOf(tolerance));
  }

  public Configuration when(Option first, Option... next) {
    return withOptions(first, next);
  }

  public Configuration withOptions(Option first, Option... next) {
    return new Configuration(tolerance, options.with(first, next), ignorePlaceholder, matchers, pathsToBeIgnored, differenceListener, pathOptions);
  }

  public Configuration withOptions(Options options) {
    return new Configuration(tolerance, options, ignorePlaceholder, matchers, pathsToBeIgnored, differenceListener, pathOptions);
  }

  @SafeVarargs public final <T extends java.lang.Object> Configuration when(ConfigurationWhen.WhenObject<T> object, T... actions) {
    Configuration configuration = this;
    for (T action : actions) {
      configuration = object.apply(configuration, action);
    }
    return configuration;
  }

  Configuration withPathOptions(PathOption pathOption) {
    List<PathOption> newOptions = new ArrayList<>(this.pathOptions);
    newOptions.add(pathOption);
    return new Configuration(tolerance, options, ignorePlaceholder, matchers, pathsToBeIgnored, differenceListener, Collections.unmodifiableList(newOptions));
  }

  Configuration whenIgnoringPaths(List<String> pathsToBeIgnored) {
    List<String> newPaths = new ArrayList<>(this.pathsToBeIgnored);
    newPaths.addAll(pathsToBeIgnored);
    return new Configuration(tolerance, options, ignorePlaceholder, matchers, Collections.unmodifiableList(newPaths), differenceListener, pathOptions);
  }

  public Configuration whenIgnoringPaths(String... pathsToBeIgnored) {
    return whenIgnoringPaths(Arrays.asList(pathsToBeIgnored));
  }

  public Configuration withIgnorePlaceholder(String ignorePlaceholder) {
    return new Configuration(tolerance, options, ignorePlaceholder, matchers, pathsToBeIgnored, differenceListener, pathOptions);
  }

  public Configuration withMatcher(String matcherName, Matcher<?> matcher) {
    return new Configuration(tolerance, options, ignorePlaceholder, matchers.with(matcherName, matcher), pathsToBeIgnored, differenceListener, pathOptions);
  }

  public Configuration withDifferenceListener(DifferenceListener differenceListener) {
    return new Configuration(tolerance, options, ignorePlaceholder, matchers, pathsToBeIgnored, differenceListener, pathOptions);
  }

  public static DifferenceListener dummyDifferenceListener() {
    return DUMMY_LISTENER;
  }

  public Matcher<?> getMatcher(String matcherName) {
    return matchers.getMatcher(matcherName);
  }

  public BigDecimal getTolerance() {
    return tolerance;
  }

  public Options getOptions() {
    return options;
  }

  public String getIgnorePlaceholder() {
    return ignorePlaceholder;
  }

  public List<PathOption> getPathOptions() {
    return pathOptions;
  }

  public Set<String> getPathsToBeIgnored() {
    return pathsToBeIgnored;
  }

  public DifferenceListener getDifferenceListener() {
    return differenceListener;
  }

  public boolean shouldIgnore(String expectedValue) {
    if (DEFAULT_IGNORE_PLACEHOLDER.equals(ignorePlaceholder)) {
      return DEFAULT_IGNORE_PLACEHOLDER.equals(expectedValue) || ALTERNATIVE_IGNORE_PLACEHOLDER.equals(expectedValue);
    } else {
      return ignorePlaceholder.equals(expectedValue);
    }
  }

  public Configuration whenIgnoringPaths(Collection<String> pathsToBeIgnored) {
    return new Configuration(tolerance, options, ignorePlaceholder, matchers, pathsToBeIgnored, differenceListener);
  }
}