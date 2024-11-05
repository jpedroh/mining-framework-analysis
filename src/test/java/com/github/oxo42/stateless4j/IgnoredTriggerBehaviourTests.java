package com.github.oxo42.stateless4j;
import org.junit.Assert;
import org.junit.Test;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.triggers.IgnoredTriggerBehaviour;
import static org.junit.Assert.assertFalse;

public class IgnoredTriggerBehaviourTests {
  public static FuncBoolean returnTrue = new FuncBoolean() {
    @Override public boolean call() {
      return true;
    }
  };

  public static FuncBoolean returnFalse = new FuncBoolean() {
    @Override public boolean call() {
      return false;
    }
  };

  @Test public void StateRemainsUnchanged() {
    IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<>(Trigger.X, returnTrue);
    assertFalse(ignored.resultsInTransitionFrom(State.B, new Object[0], new OutVar<State>()));
  }

  @Test public void ExposesCorrectUnderlyingTrigger() {
    IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<>(Trigger.X, returnTrue);
    Assert.assertEquals(Trigger.X, ignored.getTrigger());
  }

  @Test public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() {
    IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<>(Trigger.X, returnFalse);
    Assert.assertFalse(ignored.isGuardConditionMet());
  }

  @Test public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() {
    IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<>(Trigger.X, returnTrue);
    Assert.assertTrue(ignored.isGuardConditionMet());
  }
}