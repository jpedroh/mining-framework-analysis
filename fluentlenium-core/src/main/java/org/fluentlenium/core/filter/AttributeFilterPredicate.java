package org.fluentlenium.core.filter;
import org.fluentlenium.core.domain.FluentWebElement;
import java.util.function.Predicate;

/**
 * Filter a FluentWebElement collection to return only the elements with the same text
 */
public class AttributeFilterPredicate implements Predicate<FluentWebElement> {
  private final AttributeFilter filter;

  /**
     * Creates a new Attribute Filter Predicated, from an attribute filter
     *
     * @param filter attribute filter
     */
  public AttributeFilterPredicate(AttributeFilter filter) {
    this.filter = filter;
  }

  @Override public boolean test(FluentWebElement element) {
    String attribute = getAttributeValue(element);
    return filter != null && filter.getMatcher().isSatisfiedBy(attribute);
  }

  private String getAttributeValue(FluentWebElement element) {

<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/adba3e95567b7867a78f938217cd43337df7b426/fluentlenium-core/src/main/java/org/fluentlenium/core/filter/AttributeFilterPredicate.java/left.java
    return "text".equalsIgnoreCase(filter.getAttribute()) ? element.text() : element.attribute(filter.getAttribute());
=======
    if ("text".equalsIgnoreCase(filter.getAttribut())) {
      return element.text();
    } else {
      if ("textContent".equalsIgnoreCase(filter.getAttribut())) {
        return element.textContent();
      } else {
        return element.attribute(filter.getAttribut());
      }
    }
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/adba3e95567b7867a78f938217cd43337df7b426/fluentlenium-core/src/main/java/org/fluentlenium/core/filter/AttributeFilterPredicate.java/right.java
  }
}