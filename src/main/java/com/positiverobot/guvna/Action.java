package com.positiverobot.guvna;

public interface Action<S,E> {
<<<<<<< /usr/src/app/output/olibye/guvna/34cd63519389f9e2fc0f1a8ec79f770a764e5e69/src/main/java/com/positiverobot/guvna/Action.java/left.java
	public void apply(StateMachine<S,E> stateMachine, E event, S futureState);
||||||| /usr/src/app/output/olibye/guvna/34cd63519389f9e2fc0f1a8ec79f770a764e5e69/src/main/java/com/positiverobot/guvna/Action.java/base.java
	public void apply(T target, E event, S futureState);
=======
	public void apply(StateMachine<S,E> target, E event, S futureState);
>>>>>>> /usr/src/app/output/olibye/guvna/34cd63519389f9e2fc0f1a8ec79f770a764e5e69/src/main/java/com/positiverobot/guvna/Action.java/right.java
}
