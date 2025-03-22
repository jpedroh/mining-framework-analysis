package org.jeasy.rules.api;
import org.jeasy.rules.core.RuleProxy;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class encapsulates a set of rules and represents a rules namespace.
 * Rules must have a unique name within a rules namespace.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class Rules implements Iterable<Rule> {
  private Set<Rule> rules = new TreeSet<>();

  /**
     * Create a new {@link Rules} object.
     *
     * @param rules to register
     */
  public Rules(Set<Rule> rules) {
    this.rules = new TreeSet<>(rules);
  }

  /**
     * Create a new {@link Rules} object.
     *
     * @param rules to register
     */
  public Rules(Rule... rules) {
    Collections.addAll(this.rules, rules);
  }

  /**
     * Create a new {@link Rules} object.
     *
     * @param rules to register
     */
  public Rules(Object... rules) {
    for (Object rule : rules) {
      this.register(rule);
    }
  }

  /**
     * Register a new rule.
     *
     * @param rule to register, must not be null
     */
  public void register(Object rule) {
    Objects.requireNonNull(rule);
    rules.add(RuleProxy.asRule(rule));
  }

  /**
     * Register a new set of rule.
     *
     * @param rulesSet to register, must not be null
     */
  public void registerAll(Set<?> rulesSet) {
    Objects.requireNonNull(rulesSet);
    rulesSet.forEach(this::register);
  }

  /**
     * Unregister a rule.
     *
     * @param rule to unregister, must not be null
     */
  public void unregister(Object rule) {
    Objects.requireNonNull(rule);
    rules.remove(RuleProxy.asRule(rule));
  }

  /**
     * Unregister a set of rules.
     *
     * @param rulesSet set to unregister, must not be null
     */
  public void unregisterAll(Set<?> rulesSet) {
    Objects.requireNonNull(rulesSet);
    rulesSet.forEach(this::unregister);
  }

  /**
     * Unregister a rule by name.
     *
     * @param ruleName name of the rule to unregister, must not be null
     */
  public void unregister(final String ruleName) {
    Objects.requireNonNull(ruleName);
    Rule rule = findRuleByName(ruleName);
    if (rule != null) {
      unregister(rule);
    }
  }

  /**
     * Check if the rule set is empty.
     *
     * @return true if the rule set is empty, false otherwise
     */
  public boolean isEmpty() {
    return rules.isEmpty();
  }

  /**
     * Clear rules.
     */
  public void clear() {
    rules.clear();
  }

  /**
     * Return how many rules are currently registered.
     *
     * @return the number of rules currently registered
     */
  public int size() {
    return rules.size();
  }

  /**
     * Return an iterator on the rules set. It is not intended to remove rules
     * using this iterator.
     * @return an iterator on the rules set
     */
  @Override public Iterator<Rule> iterator() {
    return rules.iterator();
  }

  private Rule findRuleByName(String ruleName) {
    return rules.stream().filter((rule) -> rule.getName().equalsIgnoreCase(ruleName)).findFirst().orElse(null);
  }
}