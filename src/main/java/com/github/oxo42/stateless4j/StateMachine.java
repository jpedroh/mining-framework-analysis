package com.github.oxo42.stateless4j;
import com.github.oxo42.stateless4j.delegates.*;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.github.oxo42.stateless4j.triggers.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StateMachine<TState extends java.lang.Object, TTrigger extends java.lang.Object> {
  protected final Map<TState, StateRepresentation<TState, TTrigger>> stateConfiguration = new HashMap<>();

  protected final Map<TTrigger, TriggerWithParameters<TState, TTrigger>> triggerConfiguration = new HashMap<>();

  protected final Func<TState> stateAccessor;

  protected final Action1<TState> stateMutator;

  protected Action2<TState, TTrigger> unhandledTriggerAction = new Action2<TState, TTrigger>() {
    public void doIt(TState state, TTrigger trigger) {
      throw new IllegalStateException(String.format("No valid leaving transitions are permitted from state \'%s\' for trigger \'%s\'. Consider ignoring the trigger.", state, trigger));
    }
  };

  public StateMachine(TState initialState) {
    final StateReference<TState, TTrigger> reference = new StateReference<>();
    reference.setState(initialState);
    stateAccessor = new Func<TState>() {
      public TState call() {
        return reference.getState();
      }
    };
    stateMutator = new Action1<TState>() {
      public void doIt(TState s) {
        reference.setState(s);
      }
    };
  }

  public TState getState() {
    return stateAccessor.call();
  }

  private void setState(TState value) {
    stateMutator.doIt(value);
  }

  public List<TTrigger> getPermittedTriggers() {
    return getCurrentRepresentation().getPermittedTriggers();
  }

  StateRepresentation<TState, TTrigger> getCurrentRepresentation() {
    return getRepresentation(getState());
  }

  protected StateRepresentation<TState, TTrigger> getRepresentation(TState state) {
    StateRepresentation<TState, TTrigger> result = stateConfiguration.get(state);
    if (result == null) {
      result = new StateRepresentation<>(state);
      stateConfiguration.put(state, result);
    }
    return result;
  }

  public StateConfiguration<TState, TTrigger> configure(TState state) {
    return new StateConfiguration<>(getRepresentation(state), new Func2<TState, StateRepresentation<TState, TTrigger>>() {
      public StateRepresentation<TState, TTrigger> call(TState arg0) {
        return getRepresentation(arg0);
      }
    });
  }

  public void fire(TTrigger trigger) {
    publicFire(trigger);
  }

  public <TArg0 extends java.lang.Object> void fire(TriggerWithParameters1<TArg0, TState, TTrigger> trigger, TArg0 arg0) {
    assert trigger != null : "trigger is null";
    publicFire(trigger.getTrigger(), arg0);
  }

  public <TArg0 extends java.lang.Object, TArg1 extends java.lang.Object> void fire(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger, TArg0 arg0, TArg1 arg1) {
    assert trigger != null : "trigger is null";
    publicFire(trigger.getTrigger(), arg0, arg1);
  }

  public <TArg0 extends java.lang.Object, TArg1 extends java.lang.Object, TArg2 extends java.lang.Object> void fire(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger, TArg0 arg0, TArg1 arg1, TArg2 arg2) {
    assert trigger != null : "trigger is null";
    publicFire(trigger.getTrigger(), arg0, arg1, arg2);
  }

  protected void publicFire(TTrigger trigger, Object... args) {
    TriggerWithParameters<TState, TTrigger> configuration = triggerConfiguration.get(trigger);
    if (configuration != null) {
      configuration.validateParameters(args);
    }
    TriggerBehaviour<TState, TTrigger> triggerBehaviour = getCurrentRepresentation().tryFindHandler(trigger);
    if (triggerBehaviour == null) {
      unhandledTriggerAction.doIt(getCurrentRepresentation().getUnderlyingState(), trigger);
      return;
    }
    TState source = getState();
    OutVar<TState> destination = new OutVar<>();
    if (triggerBehaviour.resultsInTransitionFrom(source, args, destination)) {
      Transition<TState, TTrigger> transition = new Transition<>(source, destination.get(), trigger);
      getCurrentRepresentation().exit(transition);
      setState(destination.get());
      getCurrentRepresentation().enter(transition, args);
    }
  }

  public void onUnhandledTrigger(Action2<TState, TTrigger> unhandledTriggerAction) {
    if (unhandledTriggerAction == null) {
      throw new IllegalStateException("unhandledTriggerAction");
    }
    this.unhandledTriggerAction = unhandledTriggerAction;
  }

  public boolean isInState(TState state) {
    return getCurrentRepresentation().isIncludedIn(state);
  }

  public boolean canFire(TTrigger trigger) {
    return getCurrentRepresentation().canHandle(trigger);
  }

  @Override public String toString() {
    List<TTrigger> permittedTriggers = getPermittedTriggers();
    List<String> parameters = new ArrayList<>();
    for (TTrigger tTrigger : permittedTriggers) {
      parameters.add(tTrigger.toString());
    }
    StringBuilder params = new StringBuilder();
    String delim = "";
    for (String param : parameters) {
      params.append(delim);
      params.append(param);
      delim = ", ";
    }
    return String.format("StateMachine {{ State = %s, PermittedTriggers = {{ %s }}}}", getState(), params.toString());
  }

  public <TArg0 extends java.lang.Object> TriggerWithParameters1<TArg0, TState, TTrigger> setTriggerParameters(TTrigger trigger, Class<TArg0> classe0) {
    TriggerWithParameters1<TArg0, TState, TTrigger> configuration = new TriggerWithParameters1<>(trigger, classe0);
    saveTriggerConfiguration(configuration);
    return configuration;
  }

  public <TArg0 extends java.lang.Object, TArg1 extends java.lang.Object> TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> setTriggerParameters(TTrigger trigger, Class<TArg0> classe0, Class<TArg1> classe1) {
    TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> configuration = new TriggerWithParameters2<>(trigger, classe0, classe1);
    saveTriggerConfiguration(configuration);
    return configuration;
  }

  public <TArg0 extends java.lang.Object, TArg1 extends java.lang.Object, TArg2 extends java.lang.Object> TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> setTriggerParameters(TTrigger trigger, Class<TArg0> classe0, Class<TArg1> classe1, Class<TArg2> classe2) {
    TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> configuration = new TriggerWithParameters3<>(trigger, classe0, classe1, classe2);
    saveTriggerConfiguration(configuration);
    return configuration;
  }

  private void saveTriggerConfiguration(TriggerWithParameters<TState, TTrigger> trigger) {
    if (triggerConfiguration.containsKey(trigger.getTrigger())) {
      throw new IllegalStateException("Parameters for the trigger \'" + trigger + "\' have already been configured.");
    }
    triggerConfiguration.put(trigger.getTrigger(), trigger);
  }

  public void generateDotFileInto(final OutputStream dotFile) throws IOException {
    try (OutputStreamWriter w = new OutputStreamWriter(dotFile, "UTF-8")) {
      PrintWriter writer = new PrintWriter(w);
      writer.write("digraph G {\n");
      OutVar<TState> destination = new OutVar<>();
      for (Entry<TState, StateRepresentation<TState, TTrigger>> entry : this.stateConfiguration.entrySet()) {
        Map<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> behaviours = entry.getValue().getTriggerBehaviours();
        for (Entry<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> behaviour : behaviours.entrySet()) {
          for (TriggerBehaviour<TState, TTrigger> triggerBehaviour : behaviour.getValue()) {
            if (triggerBehaviour instanceof TransitioningTriggerBehaviour) {
              destination.set(null);
              triggerBehaviour.resultsInTransitionFrom(null, null, destination);
              writer.write(String.format("\t%s -> %s;\n", entry.getKey(), destination));
            }
          }
        }
      }
      writer.write("}");
    } catch (IOException ex) {
      throw ex;
    }
  }
}