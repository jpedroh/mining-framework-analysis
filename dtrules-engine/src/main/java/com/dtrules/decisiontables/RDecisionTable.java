package com.dtrules.decisiontables;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.dtrules.decisiontables.DTNode.Coordinate;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.ARObject;
import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RName;
import com.dtrules.interpreter.RNull;
import com.dtrules.interpreter.RString;
import com.dtrules.interpreter.RType;
import com.dtrules.session.DTState;
import com.dtrules.session.EntityFactory;
import com.dtrules.session.IDecisionTableError;
import com.dtrules.session.IRSession;
import com.dtrules.session.RuleSet;
import com.dtrules.xmlparser.GenericXMLParser;

/**
 * Decision Tables are the classes that hold the Rules for a set of Policy 
 * implemented using DTRules.  There are three types: <br><br>
 * 
 * BALANCED -- These decision tables expect all branches to be defined in the condition table <br>
 * ALL      -- Evaluates all the columns, then executes all the actions, in the order they
 *             are specified, for all columns whose conditions are met.<br>
 * FIRST    -- Effectively evaluates each column, and executes only the first Column whose
 *             conditions are met.<br>
 * @author paul snow
 * Mar 1, 2007
 *
 */
public class RDecisionTable extends ARObject {
  public static RType dttype = RType.newType("decisiontable");

  public static final String DASH = "-";

  private final RName dtname;

  private String filename = null;

  enum UnbalancedType {
    FIRST,
    ALL
  }



  public static enum Type {
    BALANCED() {
      void build(DTState state, RDecisionTable dt) {
        dt.compile();
        dt.buildBalanced();
        dt.check(null);
      }
    },
    FIRST() {
      void build(DTState state, RDecisionTable dt) {
        dt.compile();
        dt.buildUnbalanced(state, UnbalancedType.FIRST);
        dt.check(null);
      }
    },
    ALL() {
      void build(DTState state, RDecisionTable dt) {
        dt.compile();
        dt.buildUnbalanced(state, UnbalancedType.ALL);
        dt.check(null);
      }
    }
    ;

    abstract void build(DTState state, RDecisionTable dt);
  }

  public Type type = Type.BALANCED;

  public static final int MAXCOL = 16;

  int maxcol = 1;

  int maxbalcol = 0;

  private final IRSession session;

  private final RuleSet ruleset;

  public final Map<RName, String> fields = new HashMap<RName, String>();

  private boolean compiled = false;

  String[] contexts;

  String[] contextComments;

  String[] contextsPostfix;

  String contextsrc;

  IRObject rcontext;

  String[] initialActions;

  String[] initialActionsPostfix;

  String[] initialActionsComment;

  IRObject[] rinitialActions;

  String[][] conditiontable;

  String[][] conditiontablebalanced;

  String[] conditions;

  String[] conditionsPostfix;

  String[] conditionsComment;

  IRObject[] rconditions;

  String[][] actiontable;

  String[][] actiontablebalanced;

  String[] actions;

  String[] actionsComment;

  String[] actionsPostfix;

  IRObject[] ractions;

  String[] policystatements;

  String[] policyStatementsBalanced;

  String[] policystatementsPostfix;

  IRObject[] rpolicystatements;

  boolean[] columnsSpecified = null;

  boolean[] columnsUsed = null;

  boolean[] conditionsUsed = null;

  boolean[] actionsUsed = null;

  boolean[] columnUnreachable = null;

  boolean hasNullColumn = false;

  int starColumn = -1;

  int otherwiseColumn = -1;

  int alwaysColumn = -1;

  public static RName table_name = RName.getRName("Name");

  public static RName file_name = RName.getRName("File_Name");

  public static RName type_name = RName.getRName("type");

  List<IDecisionTableError> errorlist = new ArrayList<IDecisionTableError>();

  DTNode decisiontree = null;

  private int numberOfRealColumns = 0;

  boolean optimize = true;

  public boolean getHasNullColumn() {
    return hasNullColumn;
  }

  /**
     * @return the type
     */
  public Type getType() {
    return type;
  }

  /**
     * @return the mAXCOL
     */
  public static int getMAXCOL() {
    return MAXCOL;
  }

  /**
     * @return the maxcol
     */
  public int getMaxcol() {
    return maxcol;
  }

  /**
     * @return the ruleset
     */
  public RuleSet getRuleset() {
    return ruleset;
  }

  /**
     * @return the fields
     */
  public Map<RName, String> getFields() {
    return fields;
  }

  /**
     * @return the initialActions
     */
  public String[] getInitialActions() {
    return initialActions;
  }

  /**
     * @return the rinitialActions
     */
  public IRObject[] getRinitialActions() {
    return rinitialActions;
  }

  /**
     * @return the initialActionsPostfix
     */
  public String[] getInitialActionsPostfix() {
    return initialActionsPostfix;
  }

  /**
     * @return the initialActionsComment
     */
  public String[] getInitialActionsComment() {
    return initialActionsComment;
  }

  /**
     * 
     * @return comments on each context statement.
     */
  public String[] getContextsComment() {
    return contextComments;
  }

  /**
     * @return the contexts
     */
  public String[] getContexts() {
    return contexts;
  }

  /**
     * @return the contextsPostfix
     */
  public String[] getContextsPostfix() {
    return contextsPostfix;
  }

  /**
     * @return the contextsrc
     */
  public String getContextsrc() {
    return contextsrc;
  }

  /**
     * @return the rcontext
     */
  public IRObject getRcontext() {
    return rcontext;
  }

  /**
     * @return the columnsSpecified
     */
  public boolean[] getColumnsSpecified() {
    return columnsSpecified;
  }

  /**
     * @return the columnsUsed
     */
  public boolean[] getColumnsUsed() {
    return columnsUsed;
  }

  /**
     * @return the conditionsUsed
     */
  public boolean[] getConditionsUsed() {
    return conditionsUsed;
  }

  /**
     * @return the actionsUsed
     */
  public boolean[] getActionsUsed() {
    return actionsUsed;
  }

  /**
     * @return the columnUnreachable
     */
  public boolean[] getColumnUnreachable() {
    return columnUnreachable;
  }

  /**
     * @return the errorlist
     */
  public List<IDecisionTableError> getErrorlist() {
    return errorlist;
  }

  public String[][] getActionTableBalanced(IRSession session) {
    if (actiontablebalanced == null) {
      try {
        RDecisionTable dt = getBalancedTable(session);
        actiontablebalanced = dt.actiontable;
        conditiontablebalanced = dt.conditiontable;
        policyStatementsBalanced = dt.policystatements;
      } catch (RulesException e) {
      }
    }
    return actiontablebalanced;
  }

  public String[][] getConditionTableBalanced(IRSession session) {
    if (conditiontablebalanced == null) {
      try {
        RDecisionTable dt = getBalancedTable(session);
        actiontablebalanced = dt.actiontable;
        conditiontablebalanced = dt.conditiontable;
        policyStatementsBalanced = dt.policystatements;
      } catch (RulesException e) {
      }
    }
    return conditiontablebalanced;
  }

  private void whatsUsed() {
    int conditionCnt = conditiontable.length > 0 ? conditiontable[0].length : 0;
    columnsSpecified = new boolean[conditionCnt];
    columnsUsed = new boolean[conditionCnt];
    columnUnreachable = new boolean[conditionCnt];
    conditionsUsed = new boolean[conditions.length];
    actionsUsed = new boolean[actions.length];
    for (int col = 0; col < conditionCnt; col++) {
      for (int row = 0; row < conditiontable.length; row++) {
        if (conditiontable[row][col].equalsIgnoreCase("y") || conditiontable[row][col].equalsIgnoreCase("n") || conditiontable[row][col].equalsIgnoreCase("*")) {
          columnsSpecified[col] = true;
        }
      }
      for (int row = 0; row < actions.length; row++) {
        if (columnsSpecified[col] && actiontable[row][col].equalsIgnoreCase("x")) {
          columnsUsed[col] = true;
          actionsUsed[row] = true;
        }
      }
    }
  }

  private void setUnreachable() {
    for (int i = 0; i < columnsUsed.length; i++) {
      columnUnreachable[i] = columnsUsed[i];
    }
    setUnreachable(decisiontree);
  }

  private void setUnreachable(DTNode node) {
    if (node == null) {
      return;
    }
    if (node instanceof CNode) {
      setUnreachable(((CNode) node).iftrue);
      setUnreachable(((CNode) node).iffalse);
      conditionsUsed[((CNode) node).conditionNumber] = true;
    }
    if (node instanceof ANode) {
      for (int col : ((ANode) node).columns) {
        if (col <= columnUnreachable.length) {
          columnUnreachable[col - 1] = false;
        }
      }
      if (((ANode) node).columns.size() == 0) {
        hasNullColumn = true;
      }
      for (int action : ((ANode) node).anumbers) {
        actionsUsed[action] = true;
      }
    }
  }

  public int getNumberOfRealColumns() {
    if (decisiontree == null) {
      return 0;
    }
    return decisiontree.countColumns();
  }

  /**
     * Check for errors in the decision table.  Returns the column
     * and row of a problem if one is found.  If nothing is wrong,
     * a null is returned.
     * @return
     */
  public Coordinate validate() {
    if (decisiontree == null) {
      if (actions != null && actions.length == 0) {
        return null;
      }
      return new Coordinate(0, 0);
    }
    return decisiontree.validate();
  }

  BalanceTable balanceTable = null;

  public boolean isCompiled() {
    return compiled;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
    fields.put(file_name, filename);
  }

  @Override public IRObject clone(IRSession s) throws RulesException {
    RDecisionTable dt = new RDecisionTable(s, dtname.stringValue());
    dt.numberOfRealColumns = numberOfRealColumns;
    dt.contexts = contexts.clone();
    dt.contextsPostfix = contextsPostfix.clone();
    dt.contextsrc = contextsrc;
    dt.rcontext = rcontext != null ? rcontext.clone(s) : null;
    dt.rinitialActions = rinitialActions.clone();
    dt.initialActions = initialActions.clone();
    dt.initialActionsComment = initialActionsComment.clone();
    dt.initialActionsPostfix = initialActionsPostfix.clone();
    dt.conditiontable = conditiontable.clone();
    dt.conditions = conditions.clone();
    dt.conditionsPostfix = conditionsPostfix.clone();
    dt.conditionsComment = conditionsComment.clone();
    dt.rconditions = rconditions.clone();
    dt.actiontable = actiontable.clone();
    dt.actions = actions.clone();
    dt.actionsComment = actionsComment.clone();
    dt.actionsPostfix = actionsPostfix.clone();
    dt.ractions = ractions.clone();
    dt.policystatements = policystatements.clone();
    dt.policystatementsPostfix = policystatementsPostfix.clone();
    dt.rpolicystatements = rpolicystatements.clone();
    return dt;
  }

  /**
     * Changes the type of the given decision table.  The table is rebuilt. 
     * @param type
     * @return Returns a list of errors which occurred when the type was changed.
	 */
  public void setType(Type type) {
    this.type = type;
  }

  /**
	 * This routine compiles the Context statements for the 
	 * decision table into a single executable array.  
	 * It must embed into this array a call to executeTable 
	 * (which avoids this context building for the table).
	 */
  private void buildContexts() {
    if (contextsPostfix == null || contextsPostfix.length == 0) {
      return;
    }
    contextsrc = "/" + getName().stringValue() + " executeTable ";
    boolean keep = false;
    for (int i = contextsPostfix.length - 1; i >= 0; i--) {
      if (contextsPostfix[i] != null) {
        contextsrc = "{ " + contextsrc + " } " + contextsPostfix[i];
        keep = true;
      }
    }
    if (keep == true) {
      try {
        rcontext = RString.compile(session, contextsrc, true);
      } catch (RulesException e) {
        errorlist.add(new CompilerError(IDecisionTableError.Type.CONTEXT, "Formal Compiler Error: " + e, contextsrc, 0));
      }
    }
  }

  /**
     * Build this decision table according to its type.
     *
     */
  public void build(DTState state) {
    errorlist.clear();
    decisiontree = null;
    buildContexts();
    type.build(state, this);
  }

  /**
     * Return the name of this decision table.
     * @return
     */
  public RName getName() {
    return dtname;
  }

  /**
     * Renames this decision table.
     * @param session
     * @param newname
     * @throws RulesException
     */
  public void rename(IRSession session, RName newname) throws RulesException {
    session.getEntityFactory().deleteDecisionTable(dtname);
    session.getEntityFactory().newDecisionTable(newname, session);
  }

  /**
     * Create a Decision Table 
     * @param tables
     * @param name
     * @throws RulesException
     */
  public RDecisionTable(IRSession session, String name) throws RulesException {
    this.session = session;
    ruleset = session.getRuleSet();
    dtname = RName.getRName(name, true);
    EntityFactory ef = session.getEntityFactory();
    RDecisionTable dttable = ef.findDecisionTable(RName.getRName(name));
    if (dttable != null) {
      new CompilerError(CompilerError.Type.TABLE, "Duplicate Decision Tables Found", 0, 0);
    }
  }

  /**
	 * Compile each condition and action.  We mark the decision table as
	 * uncompiled if any error is detected.  However, we still attempt to 
	 * compile all conditions and all actions.
	 */
  public List<IDecisionTableError> compile() {
    try {
      compiled = true;
      rconditions = new IRObject[conditionsPostfix.length];
      ractions = new IRObject[actionsPostfix.length];
      rinitialActions = new IRObject[initialActionsPostfix.length];
      rpolicystatements = new IRObject[policystatementsPostfix.length];
      actiontablebalanced = null;
      conditiontablebalanced = null;
      for (int i = 0; i < initialActions.length; i++) {
        try {
          rinitialActions[i] = RString.compile(session, initialActionsPostfix[i], true);
        } catch (Exception e) {
          errorlist.add(new CompilerError(IDecisionTableError.Type.INITIALACTION, "Postfix Interpretation Error: " + e, initialActionsPostfix[i], i));
          compiled = false;
          rinitialActions[i] = RNull.getRNull();
        }
      }
      for (int i = 0; i < rconditions.length; i++) {
        try {
          rconditions[i] = RString.compile(session, conditionsPostfix[i], true);
        } catch (RulesException e) {
          errorlist.add(new CompilerError(IDecisionTableError.Type.CONDITION, "Postfix Interpretation Error: " + e, conditionsPostfix[i], i));
          compiled = false;
          rconditions[i] = RNull.getRNull();
        }
      }
      for (int i = 0; i < ractions.length; i++) {
        try {
          ractions[i] = RString.compile(session, actionsPostfix[i], true);
        } catch (RulesException e) {
          errorlist.add(new CompilerError(IDecisionTableError.Type.ACTION, "Postfix Interpretation Error: " + e, actionsPostfix[i], i));
          compiled = false;
          ractions[i] = RNull.getRNull();
        }
      }
      for (int i = 0; i < policystatementsPostfix.length; i++) {
        try {
          rpolicystatements[i] = RString.compile(session, policystatementsPostfix[i], true);
        } catch (RulesException e) {
          errorlist.add(new CompilerError(IDecisionTableError.Type.POLICYSTATEMENT, "Postfix Interpretation Error: " + e, policystatementsPostfix[i], i));
          compiled = false;
          rpolicystatements[i] = RNull.getRNull();
        }
      }
    } catch (Exception e) {
      errorlist.add(new CompilerError(IDecisionTableError.Type.TABLE, "Unexpected Exception Thrown: " + e, 0, 0));
    }
    return errorlist;
  }

  /**
	 * Checks the compile of this decision table, setting the columns used and
	 * looks for unreachable columns.
	 */
  public void check(PrintStream out) {
    whatsUsed();
    setUnreachable();
    boolean header = false;
    for (int i = 0; i < columnUnreachable.length; i++) {
      if (columnUnreachable[i]) {
        if (out != null && header == false) {
          out.println(getName().stringValue());
          header = true;
        }
        if (out != null) {
          out.println("  *** Column " + (i + 1) + " cannot be reached.");
        }
      }
    }
    for (int i = 0; i < conditionsUsed.length; i++) {
      if (rconditions[i] != null && conditionsUsed[i] == false) {
        if (out != null && header == false) {
          out.println(getName().stringValue());
          header = true;
        }
        if (out != null) {
          out.println("      condition " + (i + 1) + " is not used");
        }
      }
    }
    for (int i = 0; i < actionsUsed.length; i++) {
      if (ractions[i] != null && actionsUsed[i] == false) {
        if (out != null && header == false) {
          out.println(getName().stringValue());
          header = true;
        }
        if (out != null) {
          out.println("      action " + (i + 1) + " is not used");
        }
      }
    }
  }

  public void execute(DTState state) throws RulesException {
    arrayExecute(state);
  }

  public void arrayExecute(DTState state) throws RulesException {
    RDecisionTable last = state.getCurrentTable();
    state.setCurrentTable(this);
    state.traceTagBegin("decisiontable", "name", dtname.stringValue());
    try {
      int estk = state.edepth();
      int dstk = state.ddepth();
      int cstk = state.cdepth();
      state.pushframe();
      if (rcontext == null) {
        if (state.testState(DTState.TRACE)) {
          try {
            state.traceTagBegin("execute_table");
            executeTable(state);
            state.traceTagEnd();
          } catch (RulesException e) {
            state.traceTagEnd();
            throw e;
          }
        } else {
          executeTable(state);
        }
      } else {
        if (state.testState(DTState.TRACE)) {
          state.traceTagBegin("context", "execute", contextsrc);
          if (state.testState(DTState.VERBOSE)) {
            for (String context : this.contexts) {
              if (context != null && context.trim().length() > 0) {
                state.traceInfo("formal", context);
              }
            }
          }
          state.traceTagBegin("execute_table");
          try {
            rcontext.execute(state);
          } catch (RulesException e) {
            state.traceTagEnd();
            state.traceTagEnd();
            e.setSection("Context", 0);
            throw e;
          }
          state.traceTagEnd();
          state.traceTagEnd();
        } else {
          rcontext.execute(state);
        }
      }
      state.popframe();
      if (estk != state.edepth() || dstk != state.ddepth() || cstk != state.cdepth()) {
        throw new RulesException("Stacks Not balanced", "DecisionTables", "Error while executing table: " + getName().stringValue() + "\n" + (estk != state.edepth() ? "Entity Stack size before: " + estk + " after: " + state.edepth() + "\n" : "") + (dstk != state.ddepth() ? "Data Stack size before: " + dstk + " after: " + state.ddepth() + "\n" : "") + (cstk != state.cdepth() ? "Control Stack size before: " + cstk + " after: " + state.cdepth() + "\n" : ""));
      }
    } catch (RulesException e) {
      try {
        state.traceTagEnd();
      } catch (RuntimeException e2) {
      }
      e.addDecisionTable(this.getName().stringValue(), this.getFilename());
      state.setCurrentTable(last);
      throw e;
    }
    state.traceTagEnd();
    state.setCurrentTable(last);
  }

  /**
	 * A decision table is executed by simply executing the
	 * binary tree underneath the table.
	 */
  public void executeTable(DTState state) throws RulesException {
    boolean trace = state.testState(DTState.TRACE);
    if (compiled == false) {
      throw new RulesException("UncompiledDecisionTable", "RDecisionTable.execute", "Attempt to execute an uncompiled decision table: " + dtname.stringValue());
    }
    int edepth = state.edepth();
    if (trace) {
      if (state.testState(DTState.VERBOSE)) {
        state.traceTagBegin("entity_stack");
        for (int i = 0; i < state.edepth(); i++) {
          state.traceInfo("entity", "id", state.getes(i).getID() + "", state.getes(i).stringValue());
        }
        state.traceTagEnd();
      }
      state.traceTagBegin("initialActions");
      for (int i = 0; rinitialActions != null && i < rinitialActions.length; i++) {
        try {
          state.traceTagBegin("initialAction");
          state.traceInfo("formal", initialActions[i]);
          int dstk = state.ddepth();
          rinitialActions[i].execute(state);
          if (dstk != state.ddepth()) {
            throw new RulesException("datastackunbalanced", "initialActions", "Initial Action: " + (i + 1) + " failed!");
          }
          state.traceTagEnd();
        } catch (RulesException e) {
          e.setSection("Initial Actions", i + 1);
          throw e;
        }
      }
      state.traceTagEnd();
      if (decisiontree != null) {
        decisiontree.execute(state);
      }
      state.traceTagEnd();
      state.traceTagBegin("execute_table");
    } else {
      for (int i = 0; rinitialActions != null && i < rinitialActions.length; i++) {
        state.setCurrentTableSection("InitialActions", i);
        try {
          rinitialActions[i].execute(state);
        } catch (RulesException e) {
          e.setSection("Initial Actions", i + 1);
          throw e;
        }
      }
      if (decisiontree != null) {
        decisiontree.execute(state);
      }
    }
    while (state.edepth() > edepth) {
      state.entitypop();
    }
  }

  /**
	 * Builds (if necessary) the internal representation of the decision table,
	 * then validates that structure.
	 * @return true if the structure builds and is valid; false otherwise.
	 */
  public List<IDecisionTableError> getErrorList(DTState state) {
    if (decisiontree == null) {
      errorlist.clear();
      build(state);
    }
    return errorlist;
  }

  /**
	 * Builds the decision tree, which is a binary tree of "DTNode"'s which can be executed
     * directly.  This defines the execution of a Decision Table.
     * <br><br>
     * The way we build this binary tree is we walk down each column, tracing
	 * that column's path through the decision tree.  Once we are at the end of the column,
	 * we add on the actions.  This algorithm assumes that a decision table describes
	 * a complete decision tree, i.e. there is no set of possible condition states which 
     * are not explicitly handled by the decision table.
	 *
	 */
  void buildBalanced() {
    if (conditiontable[0].length == 0 || conditiontable[0][0].equals("*")) {
      decisiontree = ANode.newANode(this, 0);
      return;
    }
    decisiontree = new CNode(this, 0, 0, rconditions[0]);
    for (int col = 0; col < maxcol; col++) {
      boolean laststep = conditiontable[0][col].equalsIgnoreCase("y");
      CNode last = (CNode) decisiontree;
      boolean star = false;
      for (int i = 1; i < conditiontable.length; i++) {
        String t = conditiontable[i][col];
        boolean yes = t.equalsIgnoreCase("y");
        boolean no = t.equalsIgnoreCase("n");
        if (star) {
          new CompilerError(IDecisionTableError.Type.TABLE, "You can\'t follow a \'*\' with a \'" + t + "\' ", i, col);
        }
        star = t.equalsIgnoreCase("*");
        boolean invalid = false;
        if (yes || no) {
          CNode here = null;
          try {
            if (laststep) {
              here = (CNode) last.iftrue;
            } else {
              here = (CNode) last.iffalse;
            }
            if (here == null) {
              here = new CNode(this, col, i, rconditions[i]);
              if (laststep) {
                last.iftrue = here;
              } else {
                last.iffalse = here;
              }
            }
          } catch (RuntimeException e) {
            invalid = true;
          }
          if (invalid || here.conditionNumber != i) {
            errorlist.add(new CompilerError(IDecisionTableError.Type.TABLE, "Condition Table Compile Error ", i, col));
            return;
          }
          last = here;
          laststep = yes;
        }
      }
      if (laststep) {
        last.iftrue = ANode.newANode(this, col);
      } else {
        last.iffalse = ANode.newANode(this, col);
      }
    }
    DTNode.Coordinate rowCol = decisiontree.validate();
    if (rowCol != null) {
      errorlist.add(new CompilerError(IDecisionTableError.Type.TABLE, "Condition Table isn\'t balanced.", rowCol.row, rowCol.col));
      compiled = false;
    }
  }

  boolean newline = true;

  private void printattrib(PrintStream p, String tag, String body) {
    if (!newline) {
      p.println();
    }
    p.print("<");
    p.print(tag);
    p.print(">");
    p.print(body);
    p.print("</");
    p.print(tag);
    p.print(">");
    newline = false;
  }

  private void openTag(PrintStream p, String tag) {
    if (!newline) {
      p.println();
    }
    p.print("<");
    p.print(tag);
    p.print(">");
    newline = false;
  }

  /**
     * Write the XML representation of this decision table to the given outputstream.
     * @param o Output stream where the XML for this decision table will be written.
     */
  public void writeXML(PrintStream p) {
    p.println("<decision_table>");
    newline = true;
    printattrib(p, "table_name", dtname.stringValue());
    Iterator<RName> ifields = fields.keySet().iterator();
    while (ifields.hasNext()) {
      RName name = ifields.next();
      printattrib(p, name.stringValue(), fields.get(name));
    }
    openTag(p, "conditions");
    for (int i = 0; i < conditions.length; i++) {
      openTag(p, "condition_details");
      printattrib(p, "condition_number", (i + 1) + "");
      printattrib(p, "condition_description", GenericXMLParser.encode(conditions[i]));
      printattrib(p, "condition_postfix", GenericXMLParser.encode(conditionsPostfix[i]));
      printattrib(p, "condition_comment", GenericXMLParser.encode(conditionsComment[i]));
      p.println();
      newline = true;
      for (int j = 0; j < maxcol; j++) {
        p.println("<condition_column column_number=\"" + (j + 1) + "\" column_value=\"" + conditiontable[i][j] + "\" />");
      }
      p.println("</condition_details>");
    }
    p.println("</conditions>");
    openTag(p, "actions");
    for (int i = 0; i < actions.length; i++) {
      openTag(p, "action_details");
      printattrib(p, "action_number", (i + 1) + "");
      printattrib(p, "action_description", GenericXMLParser.encode(actions[i]));
      printattrib(p, "action_postfix", GenericXMLParser.encode(actionsPostfix[i]));
      printattrib(p, "action_comment", GenericXMLParser.encode(actionsComment[i]));
      p.println();
      newline = true;
      for (int j = 0; j < maxcol; j++) {
        if (actiontable[i][j].length() > 0) {
          p.println("<action_column column_number=\"" + (j + 1) + "\" column_value=\"" + actiontable[i][j] + "\" />");
        }
      }
      p.println("</action_details>");
    }
    p.println("</actions>");
    p.println("</decision_table>");
  }

  /**
	 * All Decision Tables are executable.
	 */
  public boolean isExecutable() {
    return true;
  }

  /**
	 * The string value of the decision table is simply its name.
	 */
  public String stringValue() {
    String number = fields.get("ipad_id");
    if (number == null) {
      number = "";
    }
    return number + " " + dtname.stringValue();
  }

  /**
	 * The string value of the decision table is simply its name.
	 */
  public String toString() {
    return stringValue();
  }

  /**
     * Return the postFix value 
	 */
  public String postFix() {
    return dtname.stringValue();
  }

  /**
	 * The type is Decision Table.
	 */
  public RType type() {
    return dttype;
  }

  /**
	 * @return the actions
	 */
  public String[] getActions() {
    return actions;
  }

  /**
	 * @return the actiontable
	 */
  public String[][] getActiontable() {
    return actiontable;
  }

  /**
	 * @return the conditions
	 */
  public String[] getConditions() {
    return conditions;
  }

  /**
	 * @return the conditiontable
	 */
  public String[][] getConditiontable() {
    return conditiontable;
  }

  public String getDecisionTableId() {
    return fields.get(RName.getRName("table_number"));
  }

  public void setDecisionTableId(String decisionTableId) {
    fields.put(RName.getRName("table_number"), decisionTableId);
  }

  public String getPurpose() {
    return fields.get(RName.getRName("purpose"));
  }

  public void setPurpose(String purpose) {
    fields.put(RName.getRName("purpose"), purpose);
  }

  public String getComments() {
    return fields.get(RName.getRName("comments"));
  }

  public void setComments(String comments) {
    fields.put(RName.getRName("comments"), comments);
  }

  public String getReference() {
    return fields.get(RName.getRName("policy_reference"));
  }

  public void setReference(String reference) {
    fields.put(RName.getRName("policy_reference"), reference);
  }

  /**
	 * @return the dtname
	 */
  public String getDtname() {
    return dtname.stringValue();
  }

  /**
	 * @return the ractions
	 */
  public IRObject[] getRactions() {
    return ractions;
  }

  /**
	 * @param ractions the ractions to set
	 */
  public void setRactions(IRObject[] ractions) {
    this.ractions = ractions;
  }

  /**
	 * @return the rconditions
	 */
  public IRObject[] getRconditions() {
    return rconditions;
  }

  /**
	 * @param rconditions the rconditions to set
	 */
  public void setRconditions(IRObject[] rconditions) {
    this.rconditions = rconditions;
  }

  /**
	 * @param actions the actions to set
	 */
  public void setActions(String[] actions) {
    this.actions = actions;
  }

  /**
	 * @param actiontable the actiontable to set
	 */
  public void setActiontable(String[][] actiontable) {
    this.actiontable = actiontable;
  }

  /**
	 * @param conditions the conditions to set
	 */
  public void setConditions(String[] conditions) {
    this.conditions = conditions;
  }

  /**
	 * @param conditiontable the conditiontable to set
	 */
  public void setConditiontable(String[][] conditiontable) {
    this.conditiontable = conditiontable;
  }

  /**
	 * @return the actionsComment
	 */
  public final String[] getActionsComment() {
    return actionsComment;
  }

  /**
	 * @param actionsComment the actionsComment to set
	 */
  public final void setActionsComment(String[] actionsComment) {
    this.actionsComment = actionsComment;
  }

  /**
	 * @return the actionsPostfix
	 */
  public final String[] getActionsPostfix() {
    return actionsPostfix;
  }

  /**
	 * @param actionsPostfix the actionsPostfix to set
	 */
  public final void setActionsPostfix(String[] actionsPostfix) {
    this.actionsPostfix = actionsPostfix;
  }

  /**
	 * @return the conditionsComment
	 */
  public final String[] getConditionsComment() {
    return conditionsComment;
  }

  /**
	 * @param conditionsComment the conditionsComment to set
	 */
  public final void setConditionsComment(String[] conditionsComment) {
    this.conditionsComment = conditionsComment;
  }

  /**
	 * @return the conditionsPostfix
	 */
  public final String[] getConditionsPostfix() {
    return conditionsPostfix;
  }

  /**
	 * @param conditionsPostfix the conditionsPostfix to set
	 */
  public final void setConditionsPostfix(String[] conditionsPostfix) {
    this.conditionsPostfix = conditionsPostfix;
  }

  /**
     * A little helpper function that inserts a new column in a table
     * of strings organized as String table [row][column];  Inserts blanks
     * in all new entries, so this works for both conditions and actions.
     * @param table     
     * @param col
     */
  private static void insert(String[][] table, int maxcol, final int col) {
    for (int i = 0; i < maxcol; i++) {
      for (int j = 15; j > col; j--) {
        table[i][j] = table[i][j - 1];
      }
      table[i][col] = " ";
    }
  }

  /**
     * Insert a new column at the given column number (Zero based) 
     * @param col The zero based column number for the new column
     * @throws RulesException
	 */
  public void insert(int col) throws RulesException {
    if (maxcol >= 16) {
      throw new RulesException("TableTooBig", "insert", "Attempt to insert more than 16 columns in a Decision Table");
    }
    insert(conditiontable, maxcol, col);
    insert(actiontable, maxcol, col);
  }

  /**
     * Balances an unbalanced decision table.  The additional columns have
     * no actions added.  There are two approaches to balancing tables.  One
     * is to have executed all columns whose conditions are met.  The other is
     * to execute only the first column whose conditions are met.  This 
     * routine executes all columns whose conditions are met.
     */
  public void buildUnbalanced(DTState state, UnbalancedType type) {
    if (conditiontable.length == 0 || conditiontable[0].length == 0 || conditiontable[0][0] == null) {
      return;
    }
    if (conditiontable.length == 0 || conditiontable[0].length == 0 || conditiontable[0][0] == null || conditiontable[0][0].equals("*")) {
      decisiontree = ANode.newANode(this, 0);
      return;
    }
    if (conditions.length < 1) {
      errorlist.add(new CompilerError(IDecisionTableError.Type.CONDITION, "You have to have at least one condition in a decision table", 0, 0));
    }
    CNode top = new CNode(this, 1, 0, this.rconditions[0]);
    for (int col = 0; col < maxcol; col++) {
      boolean nonemptycolumn = false;
      for (int row = 0; row < conditions.length; row++) {
        String v = conditiontable[row][col];
        if (v.equals(DASH) || v.equals(" ")) {
          v = DASH;
          conditiontable[row][col] = DASH;
        } else {
          nonemptycolumn = true;
        }
      }
      if (nonemptycolumn) {
        try {
          int numerrs = errorlist.size() + 1;
          processCol(type, top, 0, col, -1);
          while (errorlist.size() > numerrs) {
            errorlist.remove(errorlist.size() - 1);
          }
        } catch (Exception e) {
        }
      }
    }
    ANode defaults;
    defaults = new ANode(this);
    addDefaults(top, defaults);
    decisiontree = optimize(state, top);
  }

  /**
     * Replace any untouched branches in the tree with a pointer
     * to the defaults for this table.  We only replace nulls.
     * @param node
     * @param defaults
     * @return
     */
  private DTNode addDefaults(DTNode node, ANode defaults) {
    if (node == null) {
      return defaults;
    }
    if (node instanceof ANode) {
      return node;
    }
    CNode cnode = (CNode) node;
    cnode.iffalse = addDefaults(cnode.iffalse, defaults);
    cnode.iftrue = addDefaults(cnode.iftrue, defaults);
    return node;
  }

  /**
     * At this level, we just check to make sure the table is okay
     * to optimize.  optimize2 does the real work of optimization, if
     * it is okay to do so.
     * 
     * @param node
     * @return
     */
  private DTNode optimize(DTState state, DTNode node) {
    if (!state.getSession().getRulesDirectory().isOptimize()) {
      return node;
    }
    if (type == Type.ALL) {
      boolean opt = true;
      for (int i = 0; opt && i < actionsPostfix.length; i++) {
        String tokens[] = actionsPostfix[i].split("[ \t\r\n]");
        for (int j = 0; opt && j < tokens.length; j++) {
          if (tokens[j].equalsIgnoreCase("policystatements")) {
            opt = false;
          }
        }
      }
      if (!opt) {
        return node;
      }
    }
    return optimize2(state, node);
  }

  /**
     * Replaces the given DTNode with the optimized DTNode.
     * @param node
     * @return
     */
  private DTNode optimize2(DTState state, DTNode node) {
    ANode opt = node.getCommonANode(state);
    if (opt != null) {
      return opt;
    }
    CNode cnode = (CNode) node;
    cnode.iftrue = optimize2(state, cnode.iftrue);
    cnode.iffalse = optimize2(state, cnode.iffalse);
    if (cnode.iftrue.equalsNode(state, cnode.iffalse)) {
      cnode.iftrue.addNode(cnode.iffalse);
      return cnode.iftrue;
    }
    return cnode;
  }

  /**
     * Build a path through the decision tables for a particular column.
     * This routine throws an exception, but the calling routine just ignores it.
     * That way we don't flood the error list with lots of duplicate errors.
     * @param here
     * @param row
     * @param col
     * @param star -- Have we encountered a star as a column value yet.
     * @return
     */
  private DTNode processCol(UnbalancedType code, DTNode here, int row, int col, int istar) throws Exception {
    if (starColumn >= 0 && col > starColumn) {
      int rowX = 0;
      for ( ; conditiontable[rowX][col] == DASH; rowX++) {
        ;
      }
      errorlist.add(new CompilerError(IDecisionTableError.Type.TABLE, "Only one \'star\' column is allowed, and it must be the last column.", "*", rowX));
    }
    if (row >= conditions.length) {
      ANode thisCol = ANode.newANode(this, col);
      thisCol.setStar(istar >= 0);
      if (istar >= 0) {
        starColumn = col;
        boolean otherwise = conditions[istar].trim().equalsIgnoreCase("otherwise") || conditions[istar].trim().equalsIgnoreCase("default");
        boolean always = conditions[istar].trim().equalsIgnoreCase("always");
        if (!always && !otherwise) {
          errorlist.add(new CompilerError(IDecisionTableError.Type.CONDITION, "A condition row with a star (\'*\') must have a condition with a value" + " of \'default\', \'otherwise\', or \'always\'", "*", 0));
        }
        if (here == null) {
          return thisCol;
        }
        if (otherwise) {
          otherwiseColumn = col;
          return here;
        }
        if (always) {
          alwaysColumn = col;
          thisCol.addNode((ANode) here);
          return thisCol;
        }
      }
      if (here != null && code == UnbalancedType.FIRST) {
        return here;
      }
      if (here != null && code == UnbalancedType.ALL) {
        thisCol.addNode((ANode) here);
        return thisCol;
      }
      return thisCol;
    }
    String v = conditiontable[row][col];
    boolean dcare = v == DASH;
    boolean yes = v.equalsIgnoreCase("y");
    boolean no = v.equalsIgnoreCase("n");
    if (istar >= 0 && (yes | no)) {
      errorlist.add(new CompilerError(IDecisionTableError.Type.CONDITION, "Cannot follow a \'*\' with a \'" + v + "\' at row " + (row + 1) + " column " + (col + 1), v, 0));
    }
    if (v.equalsIgnoreCase("*")) {
      istar = row;
    }
    if (istar < 0 && !yes && !no && !dcare) {
      errorlist.add(new CompilerError(IDecisionTableError.Type.CONDITION, "Bad value in Condition Table \'" + v + "\' at row " + (row + 1) + " column " + (col + 1), v, 0));
    }
    if ((here == null || here.getRow() != row) && dcare) {
      return processCol(code, here, row + 1, col, istar);
    }
    if (istar >= 0) {
      DTNode t = processCol(code, here, row + 1, col, istar);
      t.setStar(true);
      return t;
    }
    if (here == null) {
      here = new CNode(this, col, row, rconditions[row]);
    } else {
      if (here.getRow() != row) {
        CNode t = new CNode(this, col, row, rconditions[row]);
        t.iffalse = here;
        t.iftrue = here.cloneDTNode();
        here = t;
      }
    }
    if (yes || dcare) {
      DTNode next = ((CNode) here).iftrue;
      DTNode t = processCol(code, next, row + 1, col, -1);
      ((CNode) here).iftrue = t;
      if (yes && t.getStar()) {
        errorlist.add(new CompilerError(IDecisionTableError.Type.CONDITION, "Cannot follow a \'Y\' with a \'*\' at row " + (row + 1) + " column " + (col + 1), v, 0));
      }
    }
    if (no || dcare) {
      DTNode next = ((CNode) here).iffalse;
      DTNode t = processCol(code, next, row + 1, col, -1);
      ((CNode) here).iffalse = t;
      if (no && t.getStar()) {
        errorlist.add(new CompilerError(IDecisionTableError.Type.CONDITION, "Cannot follow a \'N\' with a \'*\' at row " + (row + 1) + " column " + (col + 1), v, 0));
      }
    }
    return here;
  }

  /**
     * In the case of an unbalanced decision table, this method returns a balanced
     * decision table using one of the two unbalanced rules:  FIRST (which executes only
     * the first column whose conditions are matched) and ALL (which executes all columns
     * whose conditions are matched).  If the decision table is balanced, this method returns
     * an "optimized" decision table where all possible additional "don't cares" are inserted.
     * 
     * @return
     */
  RDecisionTable getBalancedTable(IRSession session) throws RulesException {
    if (balanceTable == null) {
      balanceTable = new BalanceTable(this);
    }
    return balanceTable.balancedTable(session);
  }

  public BalanceTable getBalancedTable() throws RulesException {
    return new BalanceTable(this);
  }

  public Iterator<RDecisionTable> DecisionTablesCalled() {
    ArrayList<RDecisionTable> tables = new ArrayList<RDecisionTable>();
    ArrayList<RArray> stack = new ArrayList<RArray>();
    for (int i = 0; i < ractions.length; i++) {
      addTables(ractions[i], stack, tables);
    }
    return tables.iterator();
  }

  private void addTables(IRObject action, List<RArray> stack, List<RDecisionTable> tables) {
    if (action == null) {
      return;
    }
    if (action.type().getId() == iArray) {
      RArray array = (RArray) action;
      if (stack.contains(array)) {
        return;
      }
      stack.add(array);
      try {
        Iterator<?> objects = array.arrayValue().iterator();
        while (objects.hasNext()) {
          addTables((IRObject) objects.next(), stack, tables);
        }
      } catch (RulesException e) {
      }
    }
    if (action.type().getId() == iDecisiontable && !tables.contains(action)) {
      tables.add((RDecisionTable) action);
    }
  }

  /**
     * Returns the list of Decision Tables called by this Decision Table
     * @return
     */
  ArrayList<RDecisionTable> decisionTablesCalled() {
    ArrayList<RDecisionTable> calledTables = new ArrayList<RDecisionTable>();
    addlist(calledTables, rinitialActions);
    addlist(calledTables, rconditions);
    addlist(calledTables, ractions);
    return calledTables;
  }

  /**
     * We do a recursive search down each IRObject in these lists, looking for
     * references to Decision Tables.  We only add references to Decision Tables
     * to the list of called tables if the list of called tables doesn't yet have
     * that reference.
     * 
     * @param calledTables
     * @param list
     */
  private void addlist(ArrayList<RDecisionTable> calledTables, IRObject[] list) {
    for (int i = 0; i < list.length; i++) {
      ArrayList<RDecisionTable> tables = new ArrayList<RDecisionTable>();
      ArrayList<RArray> stack = new ArrayList<RArray>();
      getTables(stack, tables, list[i]);
      for (RDecisionTable table : tables) {
        if (!calledTables.contains(table)) {
          calledTables.add(table);
        }
      }
    }
  }

  /**
     * Here we do a recursive search of all the constructs in an IROBject.  This
     * is because some IRObjects are arrays, so we search them as well.
     * @param obj
     * @return
     */
  private ArrayList<RDecisionTable> getTables(ArrayList<RArray> stack, ArrayList<RDecisionTable> tables, IRObject obj) {
    if (obj instanceof RDecisionTable) {
      tables.add((RDecisionTable) obj);
    }
    if (obj instanceof RArray && !stack.contains(obj)) {
      stack.add((RArray) obj);
      for (IRObject obj2 : (RArray) obj) {
        getTables(stack, tables, obj2);
      }
    }
    return tables;
  }

  public String[] getPolicystatements() {
    return policystatements;
  }

  public String[] getPolicyStatementsBalanced(IRSession session) {
    if (policystatements == null) {
      try {
        RDecisionTable dt = getBalancedTable(session);
        actiontablebalanced = dt.actiontable;
        conditiontablebalanced = dt.conditiontable;
        policyStatementsBalanced = dt.policystatements;
      } catch (RulesException e) {
      }
    }
    return policyStatementsBalanced;
  }

  public String[] getPolicystatementsPostfix() {
    return policystatementsPostfix;
  }

  public IRObject[] getRpolicystatements() {
    return rpolicystatements;
  }

  public DTNode getDecisiontree() {
    return decisiontree;
  }

  /**
     * This method provides field values given a field name.  A few "virtual" field names are
     * supported so as to avoid having to have special code to access these values.  They include
     * the Table_Name, File_Name, 
     * @param fieldname
     * @return
     */
  public String getField(String fieldname) {
    RName fn = RName.getRName(fieldname);
    if (fn.equals(table_name)) {
      return dtname.stringValue();
    } else {
      if (fn.equals(type_name)) {
        switch (type) {
          case ALL:
          return "ALL";
          case FIRST:
          return "FIRST";
          case BALANCED:
          return "BALANCED";
          default:
          return "UNDEFINED";
        }
      }
    }
    return fields.get(fn);
  }
}