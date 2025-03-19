package com.github.oxo42.stateless4j.triggers;

import com.github.oxo42.stateless4j.OutVar;
import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public class DynamicTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {

    private final Func2<Object[], TState> destination;

<<<<<<< /usr/src/app/output/oxo42/stateless4j/26ac62ceca4d74ab85952e8fe26a1dcf201eeda7/src/main/java/com/github/oxo42/stateless4j/triggers/DynamicTriggerBehaviour.java/left.java
    public DynamicTriggerBehaviour(final TTrigger trigger, final Func2<Object[], TState> destination, FuncBoolean guard) {
||||||| /usr/src/app/output/oxo42/stateless4j/26ac62ceca4d74ab85952e8fe26a1dcf201eeda7/src/main/java/com/github/oxo42/stateless4j/triggers/DynamicTriggerBehaviour.java/base.java
    public DynamicTriggerBehaviour(final TTrigger trigger, final Func2<Object[], TState> destination, Func<Boolean> guard) {
=======
    public DynamicTriggerBehaviour(final TTrigger trigger, final Func2<Object[], TState> destination, final Func<Boolean> guard) {
>>>>>>> /usr/src/app/output/oxo42/stateless4j/26ac62ceca4d74ab85952e8fe26a1dcf201eeda7/src/main/java/com/github/oxo42/stateless4j/triggers/DynamicTriggerBehaviour.java/right.java
        super(trigger, guard);
        assert destination != null : "destination is null";
        this.destination = destination;
    }

    public boolean resultsInTransitionFrom(TState source, Object[] args, OutVar<TState> dest) {
        dest.set(destination.call(args));
        return true;
    }
}
