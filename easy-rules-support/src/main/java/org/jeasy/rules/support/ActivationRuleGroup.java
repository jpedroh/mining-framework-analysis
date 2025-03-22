package org.jeasy.rules.support;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import java.util.TreeSet;

/**
 * An activation rule group is a composite rule that fires the first applicable rule and ignores other rules in
 * the group (XOR logic). Rules are first sorted by their natural order (priority by default) within the group.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class ActivationRuleGroup extends CompositeRule {
  private Rule selectedRule;

  /**
     * Create an activation rule group.
     */
  public ActivationRuleGroup() {
    rules = new TreeSet<>(rules);
  }

  /**
     * Create an activation rule group.
     *
     * @param name of the activation rule group
     */
  public ActivationRuleGroup(String name) {
    super(name);
    rules = new TreeSet<>(rules);
  }

  /**
     * Create a conditional rule group.
     *
     * @param name        of the activation rule group
     * @param description of the activation rule group
     */
  public ActivationRuleGroup(String name, String description) {
    super(name, description);
    rules = new TreeSet<>(rules);
  }

  /**
     * Create an activation rule group.
     *
     * @param name        of the activation rule group
     * @param description of the activation rule group
     * @param priority    of the activation rule group
     */
  public ActivationRuleGroup(String name, String description, int priority) {
    super(name, description, priority);
    rules = new TreeSet<>(rules);
  }

  @Override public boolean evaluate(Facts facts) {
    for (Rule rule : rules) {
      if (rule.evaluate(facts)) {
        selectedRule = rule;
        return true;
      }
    }
    return false;
  }

  @Override public void execute(Facts facts) throws Exception {
    if (selectedRule != null) {
      selectedRule.execute(facts);
    }
  }
}