package de.uni_koblenz.jgralab.greql2.evaluator.fa;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class models a deterministic finite automaton. The automaton is not
 * really deterministic, because there may exist more than one transition at a
 * single state, which may fire for a given edge oder vertex. For instance,
 * there may exist a tranistion which accepts all edges that are of type
 * "isExprOf" and one transition, which accepts the edge "e", which is a
 * variable and gets several values during evaluation. Now, if e is an edge of
 * type "isExprOf", both transitions may fire. So, the automaton is not
 * deterministic. But there may exists no two transitions at a state that
 * accepts the same edges, for instance, there will be never two edges that
 * accept all "isExprOf" edges.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class DFA extends FiniteAutomaton {
  @Override public DFA getDFA() {
    return this;
  }

  /**
	 * creates a new DFA from the given NFA. Removes all epsilon transitions and
	 * uses Myhill-Construction to create the DFA out of the NFA.
	 */
  public DFA(NFA nfa) {
    finalStates = new ArrayList<State>();
    transitionList = new ArrayList<Transition>();
    stateList = new ArrayList<State>();
    eleminateEpsilonTransitions(nfa);
    myhillConstruction(nfa);
    removeDuplicateTransitions();
    updateStateAttributes();
  }

  private void removeDuplicateTransitions() {
    Set<Transition> duplicateTransitions = new HashSet<Transition>();
    for (State s : this.stateList) {
      for (int i = 0; i < s.outTransitions.size() - 1; i++) {
        Transition t1 = s.outTransitions.get(i);
        for (int j = i + 1; j < s.outTransitions.size(); j++) {
          Transition t2 = s.outTransitions.get(j);
          if ((t1.endState == t2.endState) && (t1.startState == t2.startState) && (t1.equalSymbol(t2))) {
            duplicateTransitions.add(t2);
          }
        }
      }
    }
    for (Transition t : duplicateTransitions) {
      transitionList.remove(t);
      t.delete();
    }
  }

  /**
	 * removes the given epsilon transition and replace it
	 */
  private void removeEpsilonTransition(NFA nfa, Transition epsilonTransition) {
    State X = epsilonTransition.startState;
    State Y = epsilonTransition.endState;
    if (X != Y) {
      if ((Y.inTransitions.size() == 1) && (nfa.initialState != Y)) {
        Iterator<Transition> iter = Y.outTransitions.iterator();
        while (iter.hasNext()) {
          Transition t = iter.next();
          t.startState = X;
          X.outTransitions.add(t);
          iter.remove();
        }
        nfa.stateList.remove(Y);
        nfa.finalStates.remove(Y);
      } else {
        for (Transition currentTransition : Y.outTransitions) {
          if (!(currentTransition.isEpsilon() && currentTransition.endState == X)) {
            Transition newTransition = currentTransition.copy(false);
            nfa.transitionList.add(newTransition);
            newTransition.setStartState(X);
            newTransition.setEndState(newTransition.endState);
          }
        }
      }
    }
    nfa.transitionList.remove(epsilonTransition);
    epsilonTransition.delete();
    if (Y.isFinal) {
      if (!X.isFinal) {
        X.isFinal = true;
        nfa.finalStates.add(X);
      }
    }
  }

  /**
	 * eleminates all epsilon-transitions in the given NFA and replaces them
	 * with normal transitions
	 */
  private void eleminateEpsilonTransitions(NFA nfa) {
    boolean containsEpsilonTransitions = true;
    while (containsEpsilonTransitions) {
      containsEpsilonTransitions = false;
      int curTransNr = 0;
      while ((curTransNr < nfa.transitionList.size())) {
        Transition currentTransition = nfa.transitionList.get(curTransNr);
        if (currentTransition.isEpsilon()) {
          removeEpsilonTransition(nfa, currentTransition);
          containsEpsilonTransitions = true;
        } else {
          curTransNr++;
        }
      }
    }
  }

  /**
	 * constructs the DEA via powerset-construction (Myhill-Construction)
	 */
  private void myhillConstruction(NFA nfa) {
    initialState = new DFAState(nfa.initialState);
    if (nfa.initialState.isFinal) {
      initialState.isFinal = true;
      finalStates.add(initialState);
    }
    stateList.add(initialState);
    int i = 0;
    while (i < stateList.size()) {
      State currentState = stateList.get(i);
      for (int j = 0; j < currentState.outTransitions.size(); j++) {
        Transition firstTransition = currentState.outTransitions.get(j);
        DFAState newDFAState = new DFAState(firstTransition.endState);
        transitionList.addAll(newDFAState.addRepresentedState(firstTransition.endState));
        for (int k = j + 1; k < currentState.outTransitions.size(); k++) {
          Transition secondTransition = currentState.outTransitions.get(k);
          if (firstTransition.equalSymbol(secondTransition)) {
            if (firstTransition.endState != secondTransition.endState) {
              transitionList.addAll(newDFAState.addRepresentedState(secondTransition.endState));
            }
            secondTransition.delete();
            k--;
          }
        }
        firstTransition.setEndState(newDFAState);
        boolean foundSameState = false;
        for (int k = 0; k < stateList.size(); k++) {
          DFAState stateToCheck = (DFAState) stateList.get(k);
          if (stateToCheck.representSameNFAStates(newDFAState)) {
            foundSameState = true;
            ArrayList<Transition> inTransList = new ArrayList<Transition>(newDFAState.inTransitions);
            Iterator<Transition> iter = inTransList.iterator();
            while (iter.hasNext()) {
              iter.next().setEndState(stateToCheck);
            }
          }
        }
        if (!foundSameState) {
          stateList.add(newDFAState);
          if (newDFAState.containsFinalStateOfNFA(nfa)) {
            newDFAState.isFinal = true;
            finalStates.add(newDFAState);
          }
        }
      }
      i++;
    }
  }
}