package net.javacrumbs.jsonunit.assertj;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.ConfigurationWhen;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.core.internal.Diff;
import net.javacrumbs.jsonunit.core.internal.JsonUtils;
import net.javacrumbs.jsonunit.core.internal.Node;
import net.javacrumbs.jsonunit.core.internal.Options;
import net.javacrumbs.jsonunit.core.internal.Path;
import net.javacrumbs.jsonunit.core.internal.matchers.InternalMatcher;
import net.javacrumbs.jsonunit.core.listener.DifferenceListener;
import net.javacrumbs.jsonunit.jsonpath.JsonPathAdapter;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.BigDecimalAssert;
import org.assertj.core.api.BooleanAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.StringAssert;
import org.assertj.core.description.Description;
import org.assertj.core.error.MessageFormatter;
import org.assertj.core.internal.Failures;
import org.hamcrest.Matcher;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static net.javacrumbs.jsonunit.core.internal.Diff.quoteTextValue;
import static net.javacrumbs.jsonunit.core.internal.JsonUtils.getNode;
import static net.javacrumbs.jsonunit.core.internal.JsonUtils.getPathPrefix;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.ARRAY;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.BOOLEAN;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.NULL;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.NUMBER;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.OBJECT;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.STRING;
import static org.assertj.core.util.Strings.isNullOrEmpty;
import static net.javacrumbs.jsonunit.jsonpath.InternalJsonPathUtils.resolveJsonPathsToBeIgnored;

public class JsonAssert extends AbstractAssert<JsonAssert, Object> {
  final Path path;

  final Configuration configuration;

  private final InternalMatcher internalMatcher;

  JsonAssert(Path path, Configuration configuration, Object actual) {
    super(JsonUtils.convertToJson(actual, "actual"), JsonAssert.class);
    this.path = path;
    this.configuration = configuration;
    this.internalMatcher = new InternalMatcher(actual, path.asPrefix(), "", configuration);
    usingComparator(new JsonComparator(configuration, path, false));
  }

  JsonAssert(Object actual, Configuration configuration) {
    this(Path.create("", getPathPrefix(actual)), configuration, actual);
  }

  public JsonAssert node(String node) {
    return new JsonAssert(path.to(node), configuration, getNode(actual, node));
  }

  public JsonAssert and(JsonAssertion... assertions) {
    Arrays.stream(assertions).forEach((a) -> a.doAssert(this));
    return this;
  }

  @Override public JsonAssert isEqualTo(Object expected) {
    Diff diff = Diff.create(expected, actual, "fullJson", path.asPrefix(), configuration);
    String overridingErrorMessage = info.overridingErrorMessage();
    if (!isNullOrEmpty(overridingErrorMessage) && !diff.similar()) {
      failWithMessage(overridingErrorMessage);
    } else {
      diff.failIfDifferent(MessageFormatter.instance().format(info.description(), info.representation(), ""));
    }
    return this;
  }

  private void failWithMessage(String errorMessage) {
    AssertionError assertionError = Failures.instance().failureIfErrorMessageIsOverridden(info);
    if (assertionError == null) {
      String description = MessageFormatter.instance().format(info.description(), info.representation(), "");
      assertionError = new AssertionError(description + errorMessage);
    }
    Failures.instance().removeAssertJRelatedElementsFromStackTraceIfNeeded(assertionError);
    throw assertionError;
  }

  public MapAssert<String, Object> isObject() {
    Node node = assertType(OBJECT);
    return new JsonMapAssert((Map<String, Object>) node.getValue(), path.asPrefix(), configuration).as("Different value found in node \"%s\"", path);
  }

  public BigDecimalAssert isNumber() {
    Node node = assertType(NUMBER);
    return createBigDecimalAssert(node.decimalValue());
  }

  public BigDecimalAssert asNumber() {
    internalMatcher.isPresent(NUMBER.getDescription());
    Node node = getNode(actual, "");
    if (node.getNodeType() == NUMBER) {
      return createBigDecimalAssert(node.decimalValue());
    } else {
      if (node.getNodeType() == STRING) {
        try {
          return createBigDecimalAssert(new BigDecimal(node.asText()));
        } catch (NumberFormatException e) {
          failWithMessage("Node \"" + path + "\" can not be converted to number expected: <a number> but was: <" + quoteTextValue(node.getValue()) + ">.");
        }
      } else {
        internalMatcher.failOnType(node, "number or string");
      }
    }
    return null;
  }

  private BigDecimalAssert createBigDecimalAssert(BigDecimal value) {
    return new BigDecimalAssert(value).as("Different value found in node \"%s\"", path);
  }

  public ListAssert<Object> isArray() {
    Node node = assertType(ARRAY);
    return new JsonListAssert((List<?>) node.getValue(), path.asPrefix(), configuration).as("Different value found in node \"%s\"", path);
  }

  public BooleanAssert isBoolean() {
    Node node = assertType(BOOLEAN);
    return new BooleanAssert((Boolean) node.getValue()).as("Different value found in node \"%s\"", path);
  }

  public StringAssert isString() {
    Node node = assertType(STRING);
    return new StringAssert((String) node.getValue()).as("Different value found in node \"%s\"", path);
  }

  @Override public AbstractStringAssert<?> asString() {
    return isString();
  }

  @Override public void isNull() {
    assertType(NULL);
  }

  public JsonAssert isPresent() {
    internalMatcher.isPresent();
    return this;
  }

  public void isAbsent() {
    internalMatcher.isAbsent();
  }

  @Override public JsonAssert isNotNull() {
    internalMatcher.isNotNull();
    return this;
  }

  private Node assertType(Node.NodeType type) {
    return internalMatcher.assertType(type);
  }

  public static class ConfigurableJsonAssert extends JsonAssert {
    private final Object originalActual;

    ConfigurableJsonAssert(Path path, Configuration configuration, Object actual) {
      super(path, configuration, actual);
      this.originalActual = actual;
    }

    ConfigurableJsonAssert(Object actual, Configuration configuration) {
      this(Path.create("", getPathPrefix(actual)), configuration, actual);
    }

    public ConfigurableJsonAssert when(Option first, Option... other) {
      return withConfiguration((c) -> c.when(first, other));
    }

    @SafeVarargs public final <T extends java.lang.Object> ConfigurableJsonAssert when(ConfigurationWhen.WhenObject<T> object, T... actions) {
      return withConfiguration((c) -> c.when(object, actions));
    }

    public ConfigurableJsonAssert withOptions(Options options) {
      return withConfiguration((c) -> c.withOptions(options));
    }

    public ConfigurableJsonAssert withConfiguration(Function<Configuration, Configuration> configurationFunction) {
      Configuration newConfiguration = configurationFunction.apply(configuration);
      if (configuration.getPathsToBeIgnored() != newConfiguration.getPathsToBeIgnored()) {
        newConfiguration = resolveJsonPathsToBeIgnored(originalActual, newConfiguration);
      }
      return new ConfigurableJsonAssert(path, newConfiguration, actual);
    }

    public ConfigurableJsonAssert withTolerance(BigDecimal tolerance) {
      return withConfiguration((c) -> c.withTolerance(tolerance));
    }

    public ConfigurableJsonAssert withTolerance(double tolerance) {
      return withTolerance(BigDecimal.valueOf(tolerance));
    }

    public ConfigurableJsonAssert whenIgnoringPaths(String... pathsToBeIgnored) {
      return withConfiguration((c) -> c.whenIgnoringPaths(pathsToBeIgnored));
    }

    public ConfigurableJsonAssert withIgnorePlaceholder(String ignorePlaceholder) {
      return withConfiguration((c) -> c.withIgnorePlaceholder(ignorePlaceholder));
    }

    public ConfigurableJsonAssert withMatcher(String matcherName, Matcher<?> matcher) {
      return withConfiguration((c) -> c.withMatcher(matcherName, matcher));
    }

    public ConfigurableJsonAssert withDifferenceListener(DifferenceListener differenceListener) {
      return withConfiguration((c) -> c.withDifferenceListener(differenceListener));
    }

    public JsonAssert inPath(String jsonPath) {
      return new JsonAssert(JsonPathAdapter.inPath(originalActual, jsonPath), configuration);
    }

    @Override public ConfigurableJsonAssert describedAs(Description description) {
      return (ConfigurableJsonAssert) super.describedAs(description);
    }

    @Override public ConfigurableJsonAssert describedAs(String description, Object... args) {
      return (ConfigurableJsonAssert) super.describedAs(description, args);
    }

    @Override public ConfigurableJsonAssert as(Description description) {
      return (ConfigurableJsonAssert) super.as(description);
    }

    @Override public ConfigurableJsonAssert as(String description, Object... args) {
      return (ConfigurableJsonAssert) super.as(description, args);
    }
  }
}