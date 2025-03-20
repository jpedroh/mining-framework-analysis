package com.pengyifan.util.regex;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * A compiled representation of a regular expression.
 * <p>
 * A regular expression, specified as a string, must first be compiled into an instance of this
 * class. The resulting pattern can then be used to create a {@link com.pengyifan.util.regex.RegExpMatcher}
 * object that can match arbitrary {@link java.lang.CharSequence} against the regular expression.
 * All of the state involved in performing a match resides in the matcher, so many matchers can
 * share the same pattern. A typical invocation sequence is thus
 * <pre>
 * RegExpPattern p = RegExpPattern.compile("a*b");
 * RegExpMatcher m = p.matcher("aaaaab");
 * while (m.find()) {
 *   ...
 * }
 * </pre>
 * Only three regular-expression constructs are implemented
 * <ul>
 * <li>Characters: x (the character x)</li>
 * <li>Logical operators: XY (X followed by Y), X|Y (either X or Y)</li>
 * <li>Greedy quantifiers: X* (X, zero or more times)</li>
 * </ul>
 */
public class RegExpPattern {
  private final static char epsilon = 0;

  private final static RegInfixToPostfix converter = new RegInfixToPostfix();

  /**
   * NFA Table is stored in a deque of CAG_States. Each RegExpState object has a multimap of
   * transitions where the key is the input character and values are the references to states to
   * which it transfers.
   */
  private final FsaTable nfaTable;

  private FsaTable dfaTable;

  private final Stack<FsaTable> operandStack;

  private final Stack<Character> operatorStack;

  private int nextStateID;

  private final Set<Character> inputSet;

  /**
   * The original regular-expression pattern string.
   */
  private final String pattern;

  /**
   * This private constructor is used to create all Patterns. The pattern string is all that is
   * needed to completely describe a Pattern.
   *
   * @param pattern pattern
   */
  private RegExpPattern(String pattern) {
    nextStateID = 0;
    nfaTable = new FsaTable();
    dfaTable = new FsaTable();
    operandStack = new Stack<>();
    operatorStack = new Stack<>();
    inputSet = Sets.newHashSet();
    this.pattern = pattern;
    if (!compile()) {
      throw new PatternSyntaxException("Illegal pattern", pattern, 0);
    }
  }

  /**
   * Compiles the given regular expression into a pattern.
   *
   * @param regex The expression to be compiled
   * @return The compiled pattern object
   * @throws PatternSyntaxException If the expression's syntax is invalid
   */
  public static RegExpPattern compile(String regex) {
    return new RegExpPattern(regex);
  }

  /**
   * Creates a matcher that will match the given input against this pattern.
   *
   * @param input The character sequence to be matched
   * @return A new matcher for this pattern
   */
  public RegExpMatcher matcher(CharSequence input) {
    return new RegExpMatcher(this, input);
  }

  protected FsaTable getDfaTable() {
    return dfaTable;
  }

  /**
   * Constructs basic NFA for single character and pushes it onto the stack.
   *
   * @param ch The input character
   */
  private void push(char ch) {
    RegExpState s0 = new RegExpState(++nextStateID);
    RegExpState s1 = new RegExpState(++nextStateID);
    s0.addTransition(ch, s1);
    FsaTable NFATable = new FsaTable();
    NFATable.add(s0);
    NFATable.add(s1);
    operandStack.push(NFATable);
    inputSet.add(ch);
  }

  /**
   * Pops an element from the operand stack
   *
   * @return an element was poped successfully, otherwise empty (syntax error)
   */
  private FsaTable pop() {
    FsaTable table = new FsaTable();
    if (!operandStack.isEmpty()) {
      table.addAll(operandStack.pop());
    }
    return table;
  }

  /**
   * Evaluates the concatenation operator. This function pops two operands from the stack and
   * evaluates the concatenation on them, pushing the result back on the stack.
   *
   * @return true if successful
   */
  private boolean concat() {
    FsaTable B = pop();
    FsaTable A = pop();
    if (B.isEmpty() || A.isEmpty()) {
      return false;
    }
    A.getLast().addTransition(epsilon, B.getFirst());
    A.addAll(B);
    operandStack.push(A);
    return true;
  }

  /**
   * Evaluates the Kleen's closure - star operator. Pops one operator from the stack and evaluates
   * the star operator on it. It pushes the result on the operand stack again.
   *
   * @return true if successful
   */
  private boolean star() {
    FsaTable A = pop();
    if (A.isEmpty()) {
      return false;
    }
    RegExpState pStartState = new RegExpState(++nextStateID);
    RegExpState pEndState = new RegExpState(++nextStateID);
    pStartState.addTransition(epsilon, pEndState);
    pStartState.addTransition(epsilon, A.getFirst());
    A.getLast().addTransition(epsilon, pEndState);
    A.getLast().addTransition(epsilon, A.getFirst());
    A.add(pEndState);
    A.addFirst(pStartState);
    operandStack.push(A);
    System.out.printf("STAR\n");
    return true;
  }

  /**
   * Evaluates the union operator. Pops 2 operands from the stack and evaluates the union operator
   * pushing the result on the operand stack.
   *
   * @return true if successful
   */
  private boolean union() {
    FsaTable B = pop();
    FsaTable A = pop();
    if (B.isEmpty() || A.isEmpty()) {
      return false;
    }
    RegExpState pStartState = new RegExpState(++nextStateID);
    RegExpState pEndState = new RegExpState(++nextStateID);
    pStartState.addTransition(epsilon, A.getFirst());
    pStartState.addTransition(epsilon, B.getFirst());
    A.getLast().addTransition(epsilon, pEndState);
    B.getLast().addTransition(epsilon, pEndState);
    B.add(pEndState);
    A.addFirst(pStartState);
    A.addAll(B);
    operandStack.push(A);
    return true;
  }

  /**
   * Creates Nondeterministic Finite Automata from a Regular Expression
   *
   * @return true if successful
   */
  private boolean createNfa() {
    List<Character> postfix = converter.convertToPostfix(pattern);
    for (char c : postfix) {
      if (!RegInfixToPostfix.isOperator(c)) {
        push(c);
      } else {
        switch (c) {
          case '*':
          star();
          break;
          case '|':
          union();
          break;
          case RegInfixToPostfix.CONTACT_CHAR:
          concat();
          break;
          default:
          return false;
        }
      }
    }
    FsaTable table = pop();
    if (table.isEmpty()) {
      return false;
    }
    nfaTable.addAll(table);
    nfaTable.getLast().setIsAcceptingState(true);
    return true;
  }

  /**
   * Calculates the Epsilon Closure
   *
   * @param T input state set
   * @return epsilon closure of all states given with the parameter.
   */
  private Set<RegExpState> epsilonClosure(Set<RegExpState> T) {
    Set<RegExpState> Res = Sets.newHashSet(T);
    Stack<RegExpState> unprocessedStack = new Stack<>();
    for (RegExpState state : T) {
      unprocessedStack.push(state);
    }
    while (!unprocessedStack.isEmpty()) {
      RegExpState t = unprocessedStack.pop();
      for (RegExpState u : t.getTransition(epsilon)) {
        if (!Res.contains(u)) {
          Res.add(u);
          unprocessedStack.push(u);
        }
      }
    }
    return Res;
  }

  /**
   * Calculates all transitions on specific input char.
   *
   * @param ch input char
   * @param T  input state set
   * @return all states reachable from this Set of states on an input character.
   */
  private Set<RegExpState> move(char ch, Set<RegExpState> T) {
    return T.stream().map((s) -> s.getTransition(ch)).flatMap(List::stream).collect(Collectors.toSet());
  }

  /**
   * Converts NFA to DFA using the SubSet Construction Algorithm
   */
  private void convertNfaToDfa() {
    dfaTable.clear();
    if (nfaTable.isEmpty()) {
      return;
    }
    nextStateID = 0;
    LinkedList<RegExpState> unmarkedStates = Lists.newLinkedList();
    Set<RegExpState> NFAStartStateSet = Sets.newHashSet(nfaTable.getFirst());
    Set<RegExpState> DFAStartStateSet = epsilonClosure(NFAStartStateSet);
    RegExpState DFAStartState = new RegExpState(DFAStartStateSet, ++nextStateID);
    dfaTable.add(DFAStartState);
    unmarkedStates.add(DFAStartState);
    while (!unmarkedStates.isEmpty()) {
      RegExpState processingDFAState = unmarkedStates.removeLast();
      for (char c : inputSet) {
        Set<RegExpState> EpsilonClosureRes = epsilonClosure(move(c, processingDFAState.getNfaStates()));
        if (EpsilonClosureRes.isEmpty()) {
          continue;
        }
        Optional<RegExpState> opt = dfaTable.stream().filter((s) -> s.getNfaStates().equals(EpsilonClosureRes)).findFirst();
        if (opt.isPresent()) {
          processingDFAState.addTransition(c, opt.get());
        } else {
          RegExpState U = new RegExpState(EpsilonClosureRes, ++nextStateID);
          unmarkedStates.add(U);
          dfaTable.add(U);
          processingDFAState.addTransition(c, U);
        }
      }
    }
  }

  /**
   * Optimizes the DFA. This function scanns DFA and checks for states that are not accepting
   * states
   * and there is no transition from that state to any other state. Then after deleting this state
   * we need to go through the DFA and delete all transitions from other states to this one.
   */
  private void reduceDfa() {
    Set<RegExpState> DeadEndSet = dfaTable.stream().filter(RegExpState::isDeadEnd).collect(Collectors.toSet());
    if (DeadEndSet.isEmpty()) {
      return;
    }
    for (RegExpState state : DeadEndSet) {
      dfaTable.forEach((s) -> s.removeTransition(state));
      Iterator<RegExpState> itr = dfaTable.iterator();
      while (itr.hasNext()) {
        RegExpState s = itr.next();
        if (s.equals(state)) {
          itr.remove();
        }
      }
    }
  }

  /**
   * Cleans up the memory
   */
  private void reset() {
    nfaTable.clear();
    dfaTable.clear();
    nextStateID = 0;
    operandStack.clear();
    operatorStack.clear();
    inputSet.clear();
  }

  /**
   * @return true if success othewise it returns false.
   */
  private boolean compile() {
    reset();
    if (!createNfa()) {
      return false;
    }
    convertNfaToDfa();
    reduceDfa();
    return true;
  }

  public void writeNfaTable(Path file) throws IOException {
    StringBuilder strNFATable = new StringBuilder();
    for (char c : inputSet) {
      strNFATable.append("\t\t").append(c);
    }
    strNFATable.append("\t\tepsilon").append('\n');
    for (RegExpState pState : nfaTable) {
      strNFATable.append(pState.getStateID());
      for (char c : inputSet) {
        strNFATable.append("\t\t").append(getStateString(pState.getTransition(c)));
      }
      strNFATable.append("\t\t").append(getStateString(pState.getTransition(epsilon))).append('\n');
    }
    FileUtils.writeStringToFile(file.toFile(), strNFATable.toString());
  }

  private String getStateString(List<RegExpState> states) {
    StringJoiner sj = new StringJoiner(",");
    states.forEach((s) -> sj.add(String.valueOf(s.getStateID())));
    return sj.toString();
  }

  public void writeDfaTable(Path file) throws IOException {
    StringBuilder strDFATable = new StringBuilder();
    strDFATable.append("\t\t").append(Joiner.on("\t\t").join(inputSet)).append('\n');
    for (RegExpState state : dfaTable) {
      strDFATable.append(state.getStateID());
      for (char c : inputSet) {
        strDFATable.append("\t\t").append(getStateString(state.getTransition(c)));
      }
      strDFATable.append('\n');
    }
    FileUtils.writeStringToFile(file.toFile(), strDFATable.toString());
  }

  public void writeNfaGraph(Path file) throws IOException {
    FileUtils.writeStringToFile(file.toFile(), toDOT(nfaTable));
  }

  public void writeDfaGraph(Path file) throws IOException {
    FileUtils.writeStringToFile(file.toFile(), toDOT(dfaTable));
  }

  /**
   * Return string in graph description language
   *
   * @return string in graph description language
   */
  private String toDOT(FsaTable table) {
    StringBuilder strDFAGraph = new StringBuilder("digraph{\n");
    table.stream().filter(RegExpState::isAcceptingState).forEach((s) -> strDFAGraph.append('\t').append(s.getStateID()).append("\t[shape=doublecircle];\n"));
    strDFAGraph.append('\n');
    for (RegExpState s1 : table) {
      for (RegExpState s2 : s1.getTransition(epsilon)) {
        strDFAGraph.append('\t').append(s1.getStateID()).append(" -> ").append(s2.getStateID()).append("\t[label=\"epsilon\"];\n");
      }
      for (char c : inputSet) {
        for (RegExpState s2 : s1.getTransition(c)) {
          strDFAGraph.append('\t').append(s1.getStateID()).append(" -> ").append(s2.getStateID()).append("\t[label=\"").append(c).append("\"];\n");
        }
      }
    }
    strDFAGraph.append('}');
    return strDFAGraph.toString();
  }

  static class FsaTable extends LinkedList<RegExpState> {
  }
}