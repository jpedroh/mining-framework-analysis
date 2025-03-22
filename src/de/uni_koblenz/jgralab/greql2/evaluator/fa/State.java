package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import java.util.ArrayList;

/**
 * Models a state of the finite automaton. Is baseclass for DFAState, which
 * modells a state in the DFA.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class State {
  /**
	 * the number of the state in the automaton. Beware, this number is not
	 * valid during the nfa-construction or the dfa construction, so use it with
	 * care. It's safe to use it after the dfa-construction has been completed
	 */
  public int number;

  /**
	 * true if the state is final. Beware, this ist not valid during nfa or dfa
	 * construction, so use it with care. It's safe to use it after the
	 * dfa-construction has been completed
	 */
  public boolean isFinal;

  /**
	 * the list of inTransitions
	 */
  public ArrayList<Transition> inTransitions;

  /**
	 * the list of outTransitions
	 */
  public ArrayList<Transition> outTransitions;

  /**
	 * constructs a new state without any transitions connected
	 * 
	 */
  public State() {
    inTransitions = new ArrayList<Transition>();
    outTransitions = new ArrayList<Transition>();
  }

  /**
	 * adds the given transition to the outTransitions
	 */
  public void addOutTransition(Transition t) {
    outTransitions.add(t);
  }

  /**
	 * adds the given Transition to the inTransitions
	 * 
	 * @param t
	 */
  public void addInTransition(Transition t) {
    inTransitions.add(t);
  }

  /**
	 * removes the given transition from the outTransitions
	 * 
	 * @param t
	 */
  public void removeOutTransition(Transition t) {
    outTransitions.remove(t);
  }

  /**
	 * removes the given transition from the inTransitions
	 * 
	 * @param t
	 */
  public void removeInTransition(Transition t) {
    inTransitions.remove(t);
  }
}