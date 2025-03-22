package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Modells a state in the DFA. A DFAState may represent more than one NFAstate,
 * because the DFA is created via powerset-construction (Myhill-construction)
 * from the NFA.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * 
 */
public class DFAState extends State {
  /**
	 * the list of states this state is constructed from if it is constructed
	 * via the myhill-algorithm
	 */
  private ArrayList<State> neaStates;

  /**
	 * constructs a new state from the given nea state
	 */
  public DFAState(State s) {
    neaStates = new ArrayList<State>();
    ArrayList<Transition> newTransitions = new ArrayList<Transition>();
    Iterator<Transition> transitionIter = s.outTransitions.iterator();
    while (transitionIter.hasNext()) {
      Transition t = transitionIter.next();
      Transition t2 = t.copy(false);
      newTransitions.add(t2);
    }
    Iterator<Transition> newTransitionIter = newTransitions.iterator();
    while (newTransitionIter.hasNext()) {
      Transition newTransition = newTransitionIter.next();
      newTransition.setStartState(this);
      newTransition.setEndState(newTransition.endState);
    }
  }

  /**
	 * Adds the given NEA State to the list of States this DEA state represents
	 * 
	 * @param s
	 *            the state to add to the list of represented states
	 * @return a list of new created transitions or null if this state already
	 *         represents the given state
	 */
  public ArrayList<Transition> addRepresentedState(State s) {
    if (neaStates.contains(s)) {
      return new ArrayList<Transition>();
    }
    neaStates.add(s);
    ArrayList<Transition> oldTransList = new ArrayList<Transition>();
    ArrayList<Transition> newTransList = new ArrayList<Transition>();
    oldTransList.addAll(s.outTransitions);
    Iterator<Transition> transitionIter = oldTransList.iterator();
    while (transitionIter.hasNext()) {
      Transition t = transitionIter.next().copy(true);
      t.setStartState(this);
      newTransList.add(t);
    }
    return newTransList;
  }

  /**
	 * returns true if this state and the given state s represent the same NFA
	 * states
	 */
  public boolean representSameNFAStates(DFAState s) {
    if (neaStates.size() != s.neaStates.size()) {
      return false;
    }
    Iterator<State> iter = s.neaStates.iterator();
    while (iter.hasNext()) {
      if (!neaStates.contains(iter.next())) {
        return false;
      }
    }
    return true;
  }

  /**
	 * returns true if this state contains a final state of the given nfa
	 */
  public boolean containsFinalStateOfNFA(NFA nfa) {
    Iterator<State> stateIter = neaStates.iterator();
    while (stateIter.hasNext()) {
      if (nfa.finalStates.contains(stateIter.next())) {
        return true;
      }
    }
    return false;
  }

  /**
	 * returns true if this state contains the given state
	 */
  public boolean containsState(State s) {
    return neaStates.contains(s);
  }
}