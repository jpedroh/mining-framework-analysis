package com.positiverobot.guvna;

public interface Action<S extends java.lang.Object, E extends java.lang.Object> {
  public void apply(
<<<<<<< /usr/src/app/output/olibye/guvna/34cd63519389f9e2fc0f1a8ec79f770a764e5e69/src/main/java/com/positiverobot/guvna/Action.java/left.java
  StateMachine<S, E> stateMachine
=======
  StateMachine<S, E> target
>>>>>>> /usr/src/app/output/olibye/guvna/34cd63519389f9e2fc0f1a8ec79f770a764e5e69/src/main/java/com/positiverobot/guvna/Action.java/right.java
  , E event, S futureState);
}