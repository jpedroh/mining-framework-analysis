package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This is the base class of NFA and DFA. Contains attributes and methods both
 * subclasses need.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class FiniteAutomaton {
  private static Logger logger = Logger.getLogger(FiniteAutomaton.class.getPackage().getName());

  public abstract DFA getDFA();

  public State initialState;

  public ArrayList<State> finalStates;

  public ArrayList<State> stateList;

  public ArrayList<Transition> transitionList;

  /**
	 * prints this automaton as ascii-stream
	 */
  public void printAscii() {
    logger.info("|||||||||||||||||||||||  Automaton: |||||||||||||||||||||||||");
    Iterator<State> stateIter = stateList.iterator();
    while (stateIter.hasNext()) {
      State currentState = stateIter.next();
      logger.info("[" + stateList.indexOf(currentState) + "]");
      Iterator<Transition> transitionIter = currentState.outTransitions.iterator();
      while (transitionIter.hasNext()) {
        Transition currentTransition = transitionIter.next();
        int stateNumber = stateList.indexOf(currentTransition.endState);
        if (finalStates.contains(currentTransition.endState)) {
          logger.info("      ----" + currentTransition.edgeString() + "--->    [[" + stateNumber + "]]");
        } else {
          logger.info("      ----" + currentTransition.edgeString() + "--->    [" + stateNumber + "]");
        }
      }
      logger.info("\n--------------------------");
    }
    logger.info("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| ");
  }

  public void printAscii2() {
    for (State currentState : stateList) {
      if (currentState.isFinal) {
        System.out.println("State: [[" + currentState.number + "]]");
      } else {
        System.out.println("State: [" + currentState.number + "]");
      }
      for (Transition currentTransition : currentState.outTransitions) {
        int stateNumber = currentTransition.endState.number;
        if (finalStates.contains(currentTransition.endState)) {
          System.out.println("      ----" + currentTransition.edgeString() + "--->    [[" + stateNumber + "]]");
        } else {
          System.out.println("      ----" + currentTransition.edgeString() + "--->    [" + stateNumber + "]");
        }
      }
      System.out.println("\n--------------------------");
    }
  }

  /**
	 * returns true if the given state if final
	 */
  public boolean isFinal(State s) {
    return finalStates.contains(s);
  }

  /**
	 * creates a new instance
	 */
  public FiniteAutomaton() {
    finalStates = new ArrayList<State>();
    stateList = new ArrayList<State>();
    transitionList = new ArrayList<Transition>();
  }

  /**
	 * sets the attributes "number" and "isFinal" for all states to the right
	 * values, so that each number is unique and only these state are marked as
	 * "final" which are part of the final list. This makes path-search faster
	 */
  protected void updateStateAttributes() {
    Iterator<State> iter = stateList.iterator();
    int i = 0;
    while (iter.hasNext()) {
      State s = iter.next();
      s.isFinal = false;
      s.number = i++;
    }
    iter = finalStates.iterator();
    while (iter.hasNext()) {
      iter.next().isFinal = true;
    }
  }
}