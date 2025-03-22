package org.jeasy.rules.core;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineListener;
import org.jeasy.rules.api.RulesEngineParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.Objects;

/**
 * Inference {@link RulesEngine} implementation.
 *
 * Rules are selected based on given facts and fired according to their natural
 * order which is priority by default. This implementation continuously selects
 * and fires rules until no more rules are applicable.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public final class InferenceRulesEngine extends AbstractRulesEngine {
  private static final Logger LOGGER = LoggerFactory.getLogger(InferenceRulesEngine.class);

  private final DefaultRulesEngine delegate;

  /**
     * Create a new inference rules engine with default parameters.
     */
  public InferenceRulesEngine() {
    this(new RulesEngineParameters());
  }

  /**
     * Create a new inference rules engine.
     *
     * @param parameters of the engine
     */
  public InferenceRulesEngine(RulesEngineParameters parameters) {
    super(parameters);
    delegate = new DefaultRulesEngine(parameters);
  }

  @Override public void fire(Rules rules, Facts facts) {

<<<<<<< /usr/src/app/output/j-easy/easy-rules/34f8a99338648331785a5cdb0dd70711c0c328c9/easy-rules-core/src/main/java/org/jeasy/rules/core/InferenceRulesEngine.java/left.java
    rules = Objects.requireNonNull(rules);
=======
    Objects.requireNonNull(rules, "Rules must not be null");
>>>>>>> /usr/src/app/output/j-easy/easy-rules/34f8a99338648331785a5cdb0dd70711c0c328c9/easy-rules-core/src/main/java/org/jeasy/rules/core/InferenceRulesEngine.java/right.java


<<<<<<< /usr/src/app/output/j-easy/easy-rules/34f8a99338648331785a5cdb0dd70711c0c328c9/easy-rules-core/src/main/java/org/jeasy/rules/core/InferenceRulesEngine.java/left.java
    facts = Objects.requireNonNull(facts);
=======
    Objects.requireNonNull(facts, "Facts must not be null");
>>>>>>> /usr/src/app/output/j-easy/easy-rules/34f8a99338648331785a5cdb0dd70711c0c328c9/easy-rules-core/src/main/java/org/jeasy/rules/core/InferenceRulesEngine.java/right.java

    Set<Rule> selectedRules;
    do {
      LOGGER.debug("Selecting candidate rules based on the following facts: {}", facts);
      selectedRules = selectCandidates(rules, facts);
      if (!selectedRules.isEmpty()) {
        delegate.fire(new Rules(selectedRules), facts);
      } else {
        LOGGER.debug("No candidate rules found for facts: {}", facts);
      }
    } while(!selectedRules.isEmpty());
  }

  private Set<Rule> selectCandidates(Rules rules, Facts facts) {
    Set<Rule> candidates = new TreeSet<>();
    for (Rule rule : rules) {
      if (rule.evaluate(facts)) {
        candidates.add(rule);
      }
    }
    return candidates;
  }

  @Override public Map<Rule, Boolean> check(Rules rules, Facts facts) {
    Objects.requireNonNull(rules, "Rules must not be null");
    Objects.requireNonNull(facts, "Facts must not be null");
    return delegate.check(rules, facts);
  }

  /**
     * Register a rule listener.
     * @param ruleListener to register
     */
  public void registerRuleListener(RuleListener ruleListener) {
    super.registerRuleListener(ruleListener);
    delegate.registerRuleListener(ruleListener);
  }

  /**
     * Register a list of rule listener.
     * @param ruleListeners to register
     */
  public void registerRuleListeners(List<RuleListener> ruleListeners) {
    super.registerRuleListeners(ruleListeners);
    delegate.registerRuleListeners(ruleListeners);
  }

  /**
     * Register a rules engine listener.
     * @param rulesEngineListener to register
     */
  public void registerRulesEngineListener(RulesEngineListener rulesEngineListener) {
    super.registerRulesEngineListener(rulesEngineListener);
    delegate.registerRulesEngineListener(rulesEngineListener);
  }

  /**
     * Register a list of rules engine listener.
     * @param rulesEngineListeners to register
     */
  public void registerRulesEngineListeners(List<RulesEngineListener> rulesEngineListeners) {
    super.registerRulesEngineListeners(rulesEngineListeners);
    delegate.registerRulesEngineListeners(rulesEngineListeners);
  }
}