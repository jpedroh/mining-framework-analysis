package org.dynjs.codegen;
import static me.qmx.jitescript.util.CodegenUtils.*;
import java.util.ArrayList;
import java.util.List;
import me.qmx.jitescript.CodeBlock;
import org.dynjs.compiler.bytecode.Chunker;
import org.dynjs.exception.ThrowException;
import org.dynjs.parser.Statement;
import org.dynjs.parser.ast.AbstractForStatement;
import org.dynjs.parser.ast.AdditiveExpression;
import org.dynjs.parser.ast.ArrayLiteralExpression;
import org.dynjs.parser.ast.AssignmentExpression;
import org.dynjs.parser.ast.BitwiseExpression;
import org.dynjs.parser.ast.BitwiseInversionOperatorExpression;
import org.dynjs.parser.ast.BlockStatement;
import org.dynjs.parser.ast.BooleanLiteralExpression;
import org.dynjs.parser.ast.BracketExpression;
import org.dynjs.parser.ast.BreakStatement;
import org.dynjs.parser.ast.CaseClause;
import org.dynjs.parser.ast.CatchClause;
import org.dynjs.parser.ast.CommaOperator;
import org.dynjs.parser.ast.CompoundAssignmentExpression;
import org.dynjs.parser.ast.ContinueStatement;
import org.dynjs.parser.ast.DefaultCaseClause;
import org.dynjs.parser.ast.DeleteOpExpression;
import org.dynjs.parser.ast.DoWhileStatement;
import org.dynjs.parser.ast.DotExpression;
import org.dynjs.parser.ast.EmptyStatement;
import org.dynjs.parser.ast.EqualityOperatorExpression;
import org.dynjs.parser.ast.Expression;
import org.dynjs.parser.ast.ExpressionStatement;
import org.dynjs.parser.ast.FloatingNumberExpression;
import org.dynjs.parser.ast.ForExprInStatement;
import org.dynjs.parser.ast.ForExprOfStatement;
import org.dynjs.parser.ast.ForExprStatement;
import org.dynjs.parser.ast.ForVarDeclInStatement;
import org.dynjs.parser.ast.ForVarDeclOfStatement;
import org.dynjs.parser.ast.ForVarDeclStatement;
import org.dynjs.parser.ast.FunctionCallExpression;
import org.dynjs.parser.ast.FunctionDeclaration;
import org.dynjs.parser.ast.FunctionExpression;
import org.dynjs.parser.ast.IdentifierReferenceExpression;
import org.dynjs.parser.ast.IfStatement;
import org.dynjs.parser.ast.InOperatorExpression;
import org.dynjs.parser.ast.OfOperatorExpression;
import org.dynjs.parser.ast.InstanceofExpression;
import org.dynjs.parser.ast.IntegerNumberExpression;
import org.dynjs.parser.ast.LogicalExpression;
import org.dynjs.parser.ast.LogicalNotOperatorExpression;
import org.dynjs.parser.ast.MultiplicativeExpression;
import org.dynjs.parser.ast.NamedValue;
import org.dynjs.parser.ast.NewOperatorExpression;
import org.dynjs.parser.ast.NullLiteralExpression;
import org.dynjs.parser.ast.NumberLiteralExpression;
import org.dynjs.parser.ast.ObjectLiteralExpression;
import org.dynjs.parser.ast.PostOpExpression;
import org.dynjs.parser.ast.PreOpExpression;
import org.dynjs.parser.ast.PropertyAssignment;
import org.dynjs.parser.ast.PropertyGet;
import org.dynjs.parser.ast.PropertySet;
import org.dynjs.parser.ast.RegexpLiteralExpression;
import org.dynjs.parser.ast.RelationalExpression;
import org.dynjs.parser.ast.ReturnStatement;
import org.dynjs.parser.ast.StrictEqualityOperatorExpression;
import org.dynjs.parser.ast.StringLiteralExpression;
import org.dynjs.parser.ast.SwitchStatement;
import org.dynjs.parser.ast.TernaryExpression;
import org.dynjs.parser.ast.ThisExpression;
import org.dynjs.parser.ast.ThrowStatement;
import org.dynjs.parser.ast.TryStatement;
import org.dynjs.parser.ast.TypeOfOpExpression;
import org.dynjs.parser.ast.UnaryMinusExpression;
import org.dynjs.parser.ast.UnaryPlusExpression;
import org.dynjs.parser.ast.VariableDeclaration;
import org.dynjs.parser.ast.VariableStatement;
import org.dynjs.parser.ast.VoidOperatorExpression;
import org.dynjs.parser.ast.WhileStatement;
import org.dynjs.parser.ast.WithStatement;
import org.dynjs.runtime.BasicBlock;
import org.dynjs.runtime.BlockManager;
import org.dynjs.runtime.Completion;
import org.dynjs.runtime.DynArray;
import org.dynjs.runtime.DynObject;
import org.dynjs.runtime.EnvironmentRecord;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;
import org.dynjs.runtime.JSObject;
import org.dynjs.runtime.NameEnumerator;
import org.dynjs.runtime.PropertyDescriptor;
import org.dynjs.runtime.Reference;
import org.dynjs.runtime.Types;
import org.dynjs.runtime.builtins.types.BuiltinArray;
import org.dynjs.runtime.builtins.types.BuiltinNumber;
import org.dynjs.runtime.builtins.types.BuiltinObject;
import org.dynjs.runtime.builtins.types.BuiltinRegExp;
import org.dynjs.runtime.builtins.types.regexp.DynRegExp;
import org.dynjs.runtime.interp.InterpretingVisitorFactory;
import org.objectweb.asm.tree.LabelNode;

public class BasicBytecodeGeneratingVisitor extends CodeGeneratingVisitor {
  private static final String[] EMPTY_STRING_ARRAY = {  };

  public BasicBytecodeGeneratingVisitor(InterpretingVisitorFactory interpFactory, BlockManager blockManager) {
    super(interpFactory, blockManager);
  }

  @Override public CodeBlock jsGetValue(final Class<?> throwIfNot) {
    CodeBlock codeBlock = new CodeBlock().aload(Arities.EXECUTION_CONTEXT).swap().invokestatic(p(Types.class), "getValue", sig(Object.class, ExecutionContext.class, Object.class));
    if (throwIfNot != null) {
      LabelNode end = new LabelNode();
      codeBlock.dup().instance_of(p(throwIfNot)).iftrue(end).pop().append(jsThrowTypeError("expected " + throwIfNot.getName())).label(end).nop();
    }
    return codeBlock;
  }

  public void visitPlus(ExecutionContext context, AdditiveExpression expr, boolean strict) {
    LabelNode doubleNums = new LabelNode();
    LabelNode stringConcatByLeft = new LabelNode();
    LabelNode stringConcat = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    aconst_null();
    append(jsToPrimitive());
    dup();
    instance_of(p(String.class));
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    swap();
    iftrue(stringConcatByLeft);
    aconst_null();
    append(jsToPrimitive());
    dup();
    instance_of(p(String.class));
    iftrue(stringConcat);
    append(jsToNumber());
    swap();
    append(jsToNumber());
    swap();
    append(ifEitherIsDouble(doubleNums));
    append(convertTopTwoToPrimitiveLongs());
    ladd();
    append(convertTopToLong());
    go_to(end);
    label(doubleNums);
    append(convertTopTwoToPrimitiveDoubles());
    dadd();
    append(convertTopToDouble());
    go_to(end);
    label(stringConcatByLeft);
    aconst_null();
    append(jsToPrimitive());
    label(stringConcat);
    append(jsToString());
    swap();
    append(jsToString());
    swap();
    invokevirtual(p(String.class), "concat", sig(String.class, String.class));
    label(end);
    nop();
  }

  public void visitMinus(ExecutionContext context, AdditiveExpression expr, boolean strict) {
    LabelNode doubleNums = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    append(jsToNumber());
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    append(jsToNumber());
    append(ifEitherIsDouble(doubleNums));
    append(convertTopTwoToPrimitiveLongs());
    lsub();
    append(convertTopToLong());
    go_to(end);
    label(doubleNums);
    append(convertTopTwoToPrimitiveDoubles());
    dsub();
    append(convertTopToDouble());
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, BitwiseExpression expr, boolean strict) {
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    if (expr.getOp().equals(">>>")) {
      append(jsToUint32());
    } else {
      append(jsToInt32());
    }
    invokevirtual(p(Number.class), "longValue", sig(long.class));
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    switch (expr.getOp()) {
      case "<<":
      case ">>":
      case ">>>":
      append(jsToUint32());
      invokevirtual(p(Number.class), "longValue", sig(long.class));
      l2i();
      ldc(0x1F);
      iand();
      break;
      case "&":
      case "|":
      case "^":
      append(jsToInt32());
      invokevirtual(p(Number.class), "longValue", sig(long.class));
      break;
    }
    if (expr.getOp().equals("<<")) {
      lshl();
      l2i();
      append(convertTopToInteger());
    } else {
      if (expr.getOp().equals(">>")) {
        lshr();
        l2i();
        append(convertTopToInteger());
      } else {
        if (expr.getOp().equals(">>>")) {
          lushr();
          append(convertTopToLong());
        } else {
          if (expr.getOp().equals("&")) {
            land();
            append(convertTopToLong());
          } else {
            if (expr.getOp().equals("|")) {
              lor();
              append(convertTopToLong());
            } else {
              if (expr.getOp().equals("^")) {
                lxor();
                append(convertTopToLong());
              }
            }
          }
        }
      }
    }
  }

  @Override public void visit(ExecutionContext context, ArrayLiteralExpression expr, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    invokestatic(p(BuiltinArray.class), "newArray", sig(DynArray.class, ExecutionContext.class));
    int index = 0;
    for (Expression each : expr.getExprs()) {
      if (each != null) {
        dup();
        aload(Arities.EXECUTION_CONTEXT);
        ldc(index + "");
        each.accept(context, this, strict);
        append(jsGetValue());
        invokestatic(p(PropertyDescriptor.class), "newPropertyDescriptorForObjectInitializer", sig(PropertyDescriptor.class, Object.class));
        iconst_0();
        i2b();
        invokevirtual(p(DynArray.class), "defineOwnProperty", sig(boolean.class, ExecutionContext.class, String.class, PropertyDescriptor.class, boolean.class));
        pop();
      }
      ++index;
    }
    dup();
    aload(Arities.EXECUTION_CONTEXT);
    ldc("length");
    ldc((long) expr.getExprs().size());
    invokestatic(p(Long.class), "valueOf", sig(Long.class, long.class));
    iconst_0();
    i2b();
    invokeinterface(p(JSObject.class), "put", sig(void.class, ExecutionContext.class, String.class, Object.class, boolean.class));
  }

  @Override public void visit(ExecutionContext context, AssignmentExpression expr, boolean strict) {
    LabelNode throwRefError = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getLhs().accept(context, this, strict);
    dup();
    instance_of(p(Reference.class));
    iffalse(throwRefError);
    checkcast(p(Reference.class));
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    dup_x1();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
    go_to(end);
    label(throwRefError);
    pop();
    newobj(p(ThrowException.class));
    dup();
    aload(Arities.EXECUTION_CONTEXT);
    ldc(expr.getLhs().toString() + " is not a reference");
    invokevirtual(p(ExecutionContext.class), "createReferenceError", sig(JSObject.class, String.class));
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokespecial(p(ThrowException.class), "<init>", sig(void.class, ExecutionContext.class, Object.class));
    athrow();
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, BitwiseInversionOperatorExpression expr, boolean strict) {
    expr.getExpr().accept(context, this, strict);
    append(jsGetValue());
    append(jsToInt32());
    invokevirtual(p(Long.class), "longValue", sig(long.class));
    ldc(-1L);
    lxor();
    invokestatic(p(Long.class), "valueOf", sig(Long.class, long.class));
  }

  @Override public void visit(ExecutionContext context, BlockStatement statement, boolean strict) {
    LabelNode abrupt = new LabelNode();
    LabelNode end = new LabelNode();
    normalCompletion();
    astore(Arities.COMPLETION);
    for (Statement each : statement.getBlockContent()) {
      if (each == null) {
        continue;
      }
      LabelNode nonAbrupt = new LabelNode();
      LabelNode bringForwardValue = new LabelNode();
      LabelNode nextStatement = new LabelNode();
      if (each.getPosition() != null) {
        line(each.getPosition().getLine());
        aload(Arities.EXECUTION_CONTEXT);
        ldc(each.getPosition().getLine());
        invokevirtual(p(ExecutionContext.class), "setLineNumber", sig(void.class, int.class));
      }
      if (each.getSizeMetric() > Chunker.STATEMENT_THRESHOLD) {
        interpretedStatement(each, strict);
      } else {
        each.accept(context, this, strict);
      }
      dup();
      append(handleCompletion(nonAbrupt, abrupt, abrupt, abrupt));
      label(nonAbrupt);
      dup();
      append(jsCompletionValue());
      ifnull(bringForwardValue);
      astore(Arities.COMPLETION);
      go_to(nextStatement);
      label(bringForwardValue);
      dup();
      aload(Arities.COMPLETION);
      append(jsCompletionValue());
      putfield(p(Completion.class), "value", ci(Object.class));
      astore(Arities.COMPLETION);
      label(nextStatement);
    }
    go_to(end);
    label(abrupt);
    astore(Arities.COMPLETION);
    label(end);
    aload(Arities.COMPLETION);
  }

  @Override public void visit(ExecutionContext context, BooleanLiteralExpression expr, boolean strict) {
    if (expr.getValue()) {
      getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
    } else {
      getstatic(p(Boolean.class), "FALSE", ci(Boolean.class));
    }
  }

  @Override public void visit(ExecutionContext context, BreakStatement statement, boolean strict) {
    breakCompletion(statement.getTarget());
  }

  @Override public void visit(ExecutionContext context, CaseClause clause, boolean strict) {
    clause.getBlock().accept(context, this, strict);
  }

  @Override public void visit(ExecutionContext context, DefaultCaseClause clause, boolean strict) {
    clause.getBlock().accept(context, this, strict);
  }

  @Override public void visit(ExecutionContext context, CatchClause clause, boolean strict) {
    clause.getBlock().accept(context, this, strict);
  }

  @Override public void visit(ExecutionContext context, CompoundAssignmentExpression expr, boolean strict) {
    expr.getRootExpr().accept(context, this, strict);
    dup();
    expr.getRootExpr().getLhs().accept(context, this, strict);
    swap();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
  }

  @Override public void visit(ExecutionContext context, ContinueStatement statement, boolean strict) {
    continueCompletion(statement.getTarget());
  }

  @Override public void visit(ExecutionContext context, DeleteOpExpression expr, boolean strict) {
    LabelNode checkAsProperty = new LabelNode();
    LabelNode handleEnvRec = new LabelNode();
    LabelNode returnTrue = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getExpr().accept(context, this, strict);
    dup();
    instance_of(p(Reference.class));
    iffalse(returnTrue);
    checkcast(p(Reference.class));
    dup();
    invokevirtual(p(Reference.class), "isUnresolvableReference", sig(boolean.class));
    iffalse(checkAsProperty);
    dup();
    invokevirtual(p(Reference.class), "isStrictReference", sig(boolean.class));
    iffalse(returnTrue);
    append(jsThrowSyntaxError("unable to delete " + expr.getExpr()));
    go_to(returnTrue);
    label(checkAsProperty);
    dup();
    invokevirtual(p(Reference.class), "isPropertyReference", sig(boolean.class));
    iffalse(handleEnvRec);
    dup();
    append(jsGetBase());
    append(jsToObject());
    swap();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    dup();
    invokevirtual(p(Reference.class), "getReferencedName", sig(String.class));
    swap();
    invokevirtual(p(Reference.class), "isStrictReference", sig(boolean.class));
    invokeinterface(p(JSObject.class), "delete", sig(boolean.class, ExecutionContext.class, String.class, boolean.class));
    invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
    go_to(end);
    LabelNode throwSyntax = new LabelNode();
    label(handleEnvRec);
    dup();
    invokevirtual(p(Reference.class), "isStrictReference", sig(boolean.class));
    iftrue(throwSyntax);
    dup();
    append(jsGetBase());
    checkcast(p(EnvironmentRecord.class));
    swap();
    invokevirtual(p(Reference.class), "getReferencedName", sig(String.class));
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokeinterface(p(EnvironmentRecord.class), "deleteBinding", sig(boolean.class, ExecutionContext.class, String.class));
    invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
    go_to(end);
    label(throwSyntax);
    append(jsThrowSyntaxError("unable to delete"));
    go_to(end);
    label(returnTrue);
    pop();
    getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, DoWhileStatement statement, boolean strict) {
    LabelNode begin = new LabelNode();
    LabelNode normalTarget = new LabelNode();
    LabelNode breakTarget = new LabelNode();
    LabelNode continueTarget = new LabelNode();
    LabelNode end = new LabelNode();
    label(begin);
    invokeCompiledStatementBlock("Do", statement.getBlock(), strict);
    dup();
    append(handleCompletion(normalTarget, breakTarget, continueTarget, end));
    label(normalTarget);
    statement.getTest().accept(context, this, strict);
    append(jsGetValue());
    append(jsToBoolean());
    invokevirtual(p(Boolean.class), "booleanValue", sig(boolean.class));
    iffalse(end);
    pop();
    go_to(begin);
    label(breakTarget);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    convertToNormalCompletion();
    go_to(end);
    label(continueTarget);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    go_to(normalTarget);
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, EmptyStatement statement, boolean strict) {
    normalCompletion();
  }

  @Override public void visit(ExecutionContext context, EqualityOperatorExpression expr, boolean strict) {
    LabelNode returnTrue = new LabelNode();
    LabelNode returnFalse = new LabelNode();
    LabelNode end = new LabelNode();
    aload(Arities.EXECUTION_CONTEXT);
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    invokestatic(p(Types.class), "compareEquality", sig(boolean.class, ExecutionContext.class, Object.class, Object.class));
    if (expr.getOp().equals("==")) {
      iftrue(returnTrue);
      go_to(returnFalse);
    } else {
      iffalse(returnTrue);
      go_to(returnFalse);
    }
    label(returnTrue);
    getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
    go_to(end);
    label(returnFalse);
    getstatic(p(Boolean.class), "FALSE", ci(Boolean.class));
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, CommaOperator expr, boolean strict) {
    expr.getLhs().accept(context, this, strict);
    jsGetValue();
    pop();
    expr.getRhs().accept(context, this, strict);
    jsGetValue();
  }

  @Override public void visit(ExecutionContext context, ExpressionStatement statement, boolean strict) {
    Expression expr = statement.getExpr();
    if (expr instanceof FunctionDeclaration) {
      normalCompletion();
    } else {
      expr.accept(context, this, strict);
      append(jsGetValue());
      normalCompletionWithValue();
    }
  }

  @Override public void visit(ExecutionContext context, 
<<<<<<< /usr/src/app/output/dynjs/dynjs/a98c081265982db02dec1543679620aae7783b47/src/main/java/org/dynjs/codegen/BasicBytecodeGeneratingVisitor.java/left.java
  FloatingNumberExpression expr
=======
  ForVarDeclOfStatement statement
>>>>>>> /usr/src/app/output/dynjs/dynjs/a98c081265982db02dec1543679620aae7783b47/src/main/java/org/dynjs/codegen/BasicBytecodeGeneratingVisitor.java/right.java
  , boolean strict) {
    LabelNode nextName = new LabelNode();
    LabelNode checkCompletion = new LabelNode();
    LabelNode bringForward = new LabelNode();
    LabelNode doBreak = new LabelNode();
    LabelNode doContinue = new LabelNode();
    LabelNode undefEnd = new LabelNode();
    LabelNode end = new LabelNode();
    normalCompletion();
    statement.getRhs().accept(context, this, strict);
    append(jsGetValue());
    dup();
    append(jsPushUndefined());
    if_acmpeq(undefEnd);
    dup();
    append(jsPushNull());
    if_acmpeq(undefEnd);
    append(jsToObject());
    invokeinterface(p(JSObject.class), "getAllEnumerablePropertyNames", sig(NameEnumerator.class));
    astore(4);
    label(nextName);
    aload(4);
    invokevirtual(p(NameEnumerator.class), "hasNext", sig(boolean.class));
    iffalse(end);
    aload(4);
    invokevirtual(p(NameEnumerator.class), "next", sig(String.class));
    append(jsToObject());
    swap();
    invokevirtual(p(ExecutionContext.class), "createPropertyReference", sig(Reference.class, JSObject.class, String.class));
    statement.getDeclaration().accept(context, this, strict);
    pop();
    aload(Arities.EXECUTION_CONTEXT);
    ldc(statement.getDeclaration().getIdentifier());
    invokevirtual(p(ExecutionContext.class), "resolve", sig(Reference.class, String.class));
    swap();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
    invokeCompiledStatementBlock("For", statement.getBlock(), strict);
    dup();
    append(jsCompletionValue());
    ifnull(bringForward);
    swap();
    pop();
    go_to(checkCompletion);
    label(bringForward);
    dup_x1();
    swap();
    append(jsGetValue());
    putfield(p(Completion.class), "value", ci(Object.class));
    label(checkCompletion);
    dup();
    append(handleCompletion(nextName, doBreak, doContinue, end));
    label(doBreak);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    convertToNormalCompletion();
    go_to(end);
    label(doContinue);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    go_to(nextName);
    label(undefEnd);
    pop();
    label(end);

<<<<<<< /usr/src/app/output/dynjs/dynjs/a98c081265982db02dec1543679620aae7783b47/src/main/java/org/dynjs/codegen/BasicBytecodeGeneratingVisitor.java/left.java
    visit(context, (NumberLiteralExpression) expr, strict)
=======
    nop()
>>>>>>> /usr/src/app/output/dynjs/dynjs/a98c081265982db02dec1543679620aae7783b47/src/main/java/org/dynjs/codegen/BasicBytecodeGeneratingVisitor.java/right.java
    ;
  }

  @Override public void visit(ExecutionContext context, ForExprInStatement statement, boolean strict) {
    LabelNode nextName = new LabelNode();
    LabelNode checkCompletion = new LabelNode();
    LabelNode bringForward = new LabelNode();
    LabelNode doBreak = new LabelNode();
    LabelNode doContinue = new LabelNode();
    LabelNode undefEnd = new LabelNode();
    LabelNode end = new LabelNode();
    normalCompletion();
    statement.getRhs().accept(context, this, strict);
    append(jsGetValue());
    dup();
    append(jsPushUndefined());
    if_acmpeq(undefEnd);
    dup();
    append(jsPushNull());
    if_acmpeq(undefEnd);
    append(jsToObject());
    invokeinterface(p(JSObject.class), "getAllEnumerablePropertyNames", sig(NameEnumerator.class));
    astore(4);
    label(nextName);
    aload(4);
    invokevirtual(p(NameEnumerator.class), "hasNext", sig(boolean.class));
    iffalse(end);
    aload(4);
    invokevirtual(p(NameEnumerator.class), "next", sig(String.class));
    statement.getExpr().accept(context, this, strict);
    swap();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
    invokeCompiledStatementBlock("For", statement.getBlock(), strict);
    dup();
    append(jsCompletionValue());
    ifnull(bringForward);
    swap();
    pop();
    go_to(checkCompletion);
    label(bringForward);
    dup_x1();
    swap();
    append(jsGetValue());
    putfield(p(Completion.class), "value", ci(Object.class));
    label(checkCompletion);
    dup();
    append(handleCompletion(nextName, doBreak, doContinue, end));
    label(doBreak);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    convertToNormalCompletion();
    go_to(end);
    label(doContinue);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    go_to(nextName);
    label(undefEnd);
    pop();
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, ForExprOfStatement statement, boolean strict) {
    LabelNode nextName = new LabelNode();
    LabelNode checkCompletion = new LabelNode();
    LabelNode bringForward = new LabelNode();
    LabelNode doBreak = new LabelNode();
    LabelNode doContinue = new LabelNode();
    LabelNode undefEnd = new LabelNode();
    LabelNode end = new LabelNode();
    normalCompletion();
    statement.getRhs().accept(context, this, strict);
    append(jsGetValue());
    dup();
    append(jsPushUndefined());
    if_acmpeq(undefEnd);
    dup();
    append(jsPushNull());
    if_acmpeq(undefEnd);
    append(jsToObject());
    invokeinterface(p(JSObject.class), "getAllEnumerablePropertyNames", sig(NameEnumerator.class));
    astore(4);
    label(nextName);
    aload(4);
    invokevirtual(p(NameEnumerator.class), "hasNext", sig(boolean.class));
    iffalse(end);
    aload(4);
    invokevirtual(p(NameEnumerator.class), "next", sig(String.class));
    append(jsToObject());
    swap();
    invokevirtual(p(ExecutionContext.class), "createPropertyReference", sig(Reference.class, JSObject.class, String.class));
    statement.getExpr().accept(context, this, strict);
    swap();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
    invokeCompiledStatementBlock("For", statement.getBlock(), strict);
    dup();
    append(jsCompletionValue());
    ifnull(bringForward);
    swap();
    pop();
    go_to(checkCompletion);
    label(bringForward);
    dup_x1();
    swap();
    append(jsGetValue());
    putfield(p(Completion.class), "value", ci(Object.class));
    label(checkCompletion);
    dup();
    append(handleCompletion(nextName, doBreak, doContinue, end));
    label(doBreak);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    convertToNormalCompletion();
    go_to(end);
    label(doContinue);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    go_to(nextName);
    label(undefEnd);
    pop();
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, ForVarDeclInStatement statement, boolean strict) {
    LabelNode nextName = new LabelNode();
    LabelNode checkCompletion = new LabelNode();
    LabelNode bringForward = new LabelNode();
    LabelNode doBreak = new LabelNode();
    LabelNode doContinue = new LabelNode();
    LabelNode undefEnd = new LabelNode();
    LabelNode end = new LabelNode();
    normalCompletion();
    statement.getRhs().accept(context, this, strict);
    append(jsGetValue());
    dup();
    append(jsPushUndefined());
    if_acmpeq(undefEnd);
    dup();
    append(jsPushNull());
    if_acmpeq(undefEnd);
    append(jsToObject());
    invokeinterface(p(JSObject.class), "getAllEnumerablePropertyNames", sig(NameEnumerator.class));
    astore(4);
    label(nextName);
    aload(4);
    invokevirtual(p(NameEnumerator.class), "hasNext", sig(boolean.class));
    iffalse(end);
    aload(4);
    invokevirtual(p(NameEnumerator.class), "next", sig(String.class));
    statement.getDeclaration().accept(context, this, strict);
    pop();
    aload(Arities.EXECUTION_CONTEXT);
    ldc(statement.getDeclaration().getIdentifier());
    invokevirtual(p(ExecutionContext.class), "resolve", sig(Reference.class, String.class));
    swap();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
    invokeCompiledStatementBlock("For", statement.getBlock(), strict);
    dup();
    append(jsCompletionValue());
    ifnull(bringForward);
    swap();
    pop();
    go_to(checkCompletion);
    label(bringForward);
    dup_x1();
    swap();
    append(jsGetValue());
    putfield(p(Completion.class), "value", ci(Object.class));
    label(checkCompletion);
    dup();
    append(handleCompletion(nextName, doBreak, doContinue, end));
    label(doBreak);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    convertToNormalCompletion();
    go_to(end);
    label(doContinue);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    go_to(nextName);
    label(undefEnd);
    pop();
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, ForExprStatement statement, boolean strict) {
    if (statement.getExpr() != null) {
      statement.getExpr().accept(context, this, strict);
      pop();
    }
    visitFor(context, statement, strict);
  }

  @Override public void visit(ExecutionContext context, ForVarDeclStatement statement, boolean strict) {
    List<VariableDeclaration> decls = statement.getDeclarationList();
    for (VariableDeclaration each : decls) {
      each.accept(context, this, strict);
      pop();
    }
    visitFor(context, statement, strict);
  }

  public void visitFor(ExecutionContext context, AbstractForStatement statement, boolean strict) {
    LabelNode begin = new LabelNode();
    LabelNode bringForward = new LabelNode();
    LabelNode hasValue = new LabelNode();
    LabelNode checkCompletion = new LabelNode();
    LabelNode doIncrement = new LabelNode();
    LabelNode doBreak = new LabelNode();
    LabelNode doContinue = new LabelNode();
    LabelNode end = new LabelNode();
    normalCompletion();
    label(begin);
    if (statement.getTest() != null) {
      statement.getTest().accept(context, this, strict);
      append(jsGetValue());
      append(jsToBoolean());
      invokevirtual(p(Boolean.class), "booleanValue", sig(boolean.class));
      iffalse(end);
    }
    invokeCompiledStatementBlock("For", statement.getBlock(), strict);
    dup();
    append(jsCompletionValue());
    ifnull(bringForward);
    go_to(hasValue);
    label(bringForward);
    dup_x1();
    swap();
    append(jsCompletionValue());
    putfield(p(Completion.class), "value", ci(Object.class));
    go_to(checkCompletion);
    label(hasValue);
    swap();
    pop();
    label(checkCompletion);
    dup();
    append(handleCompletion(doIncrement, doBreak, doContinue, end));
    label(doIncrement);
    if (statement.getIncrement() != null) {
      statement.getIncrement().accept(context, this, strict);
      append(jsGetValue());
      pop();
    }
    go_to(begin);
    label(doBreak);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    convertToNormalCompletion();
    go_to(end);
    label(doContinue);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    convertToNormalCompletion();
    go_to(doIncrement);
    label(end);
  }

  @Override public void visit(ExecutionContext context, FunctionCallExpression expr, boolean strict) {
    LabelNode propertyRef = new LabelNode();
    LabelNode noSelf = new LabelNode();
    LabelNode doCall = new LabelNode();
    LabelNode isCallable = new LabelNode();
    aload(Arities.EXECUTION_CONTEXT);
    expr.getMemberExpression().accept(context, this, strict);
    dup();
    append(jsGetValue());
    swap();
    dup();
    dup_x2();
    instance_of(p(Reference.class));
    iffalse(noSelf);
    checkcast(p(Reference.class));
    dup();
    invokevirtual(p(Reference.class), "isPropertyReference", sig(boolean.class));
    iftrue(propertyRef);
    append(jsGetBase());
    checkcast(p(EnvironmentRecord.class));
    invokeinterface(p(EnvironmentRecord.class), "implicitThisValue", sig(Object.class));
    go_to(doCall);
    label(propertyRef);
    append(jsGetBase());
    go_to(doCall);
    label(noSelf);
    pop();
    append(jsPushUndefined());
    label(doCall);
    swap();
    List<Expression> argExprs = expr.getArgumentExpressions();
    int numArgs = argExprs.size();
    bipush(numArgs);
    anewarray(p(Object.class));
    for (int i = 0; i < numArgs; ++i) {
      dup();
      bipush(i);
      argExprs.get(i).accept(context, this, strict);
      append(jsGetValue());
      aastore();
    }
    swap();
    dup_x2();
    invokestatic(p(Types.class), "isCallable", sig(boolean.class, Object.class));
    iftrue(isCallable);
    append(jsThrowTypeError(expr.getMemberExpression() + " is not a function"));
    label(isCallable);
    invokevirtual(p(ExecutionContext.class), "call", sig(Object.class, Object.class, JSFunction.class, Object.class, Object[].class));
  }

  @Override public void visit(ExecutionContext context, FunctionDeclaration statement, boolean strict) {
    normalCompletion();
  }

  @Override public void visit(ExecutionContext context, FunctionExpression expr, boolean strict) {
    compiledFunction(expr.getDescriptor().getIdentifier(), expr.getDescriptor().getFormalParameterNames(), expr.getDescriptor().getBlock(), expr.getDescriptor().isStrict());
  }

  @Override public void visit(ExecutionContext context, IdentifierReferenceExpression expr, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    ldc(expr.getIdentifier());
    invokevirtual(p(ExecutionContext.class), "resolve", sig(Reference.class, String.class));
  }

  @Override public void visit(ExecutionContext context, IfStatement statement, boolean strict) {
    LabelNode elseBranch = new LabelNode();
    LabelNode noElseBranch = new LabelNode();
    LabelNode end = new LabelNode();
    statement.getTest().accept(context, this, strict);
    append(jsGetValue());
    append(jsToBoolean());
    invokevirtual(p(Boolean.class), "booleanValue", sig(boolean.class));
    if (statement.getElseBlock() == null) {
      iffalse(noElseBranch);
    } else {
      iffalse(elseBranch);
    }
    if (statement.getThenBlock() != null) {
      invokeCompiledStatementBlock("Then", statement.getThenBlock(), strict);
    } else {
      normalCompletion();
    }
    go_to(end);
    if (statement.getElseBlock() == null) {
      label(noElseBranch);
      normalCompletion();
    } else {
      label(elseBranch);
      invokeCompiledStatementBlock("Else", statement.getElseBlock(), strict);
    }
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, InOperatorExpression expr, boolean strict) {
    LabelNode typeError = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    dup();
    instance_of(p(JSObject.class));
    iffalse(typeError);
    checkcast(p(JSObject.class));
    swap();
    append(jsToString());
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokeinterface(p(JSObject.class), "hasProperty", sig(boolean.class, ExecutionContext.class, String.class));
    go_to(end);
    label(typeError);
    pop();
    pop();
    iconst_0();
    i2b();
    append(jsThrowTypeError("not an object"));
    label(end);
    invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
  }

  @Override public void visit(ExecutionContext context, InstanceofExpression expr, boolean strict) {
    LabelNode typeError = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    dup();
    instance_of(p(JSFunction.class));
    iffalse(typeError);
    checkcast(p(JSFunction.class));
    swap();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokeinterface(p(JSFunction.class), "hasInstance", sig(boolean.class, ExecutionContext.class, Object.class));
    go_to(end);
    label(typeError);
    pop();
    pop();
    iconst_0();
    i2b();
    append(jsThrowTypeError("not an object"));
    label(end);
    invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
  }

  @Override public void visit(ExecutionContext context, 
<<<<<<< /usr/src/app/output/dynjs/dynjs/a98c081265982db02dec1543679620aae7783b47/src/main/java/org/dynjs/codegen/BasicBytecodeGeneratingVisitor.java/left.java
  IntegerNumberExpression
=======
  OfOperatorExpression
>>>>>>> /usr/src/app/output/dynjs/dynjs/a98c081265982db02dec1543679620aae7783b47/src/main/java/org/dynjs/codegen/BasicBytecodeGeneratingVisitor.java/right.java
   expr, boolean strict) {
    LabelNode typeError = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    dup();
    instance_of(p(JSObject.class));
    iffalse(typeError);
    checkcast(p(JSObject.class));
    swap();
    append(jsToString());
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokeinterface(p(JSObject.class), "hasProperty", sig(boolean.class, ExecutionContext.class, String.class));
    go_to(end);
    label(typeError);
    pop();
    pop();
    iconst_0();
    i2b();
    append(jsThrowTypeError("not an object"));
    label(end);

<<<<<<< /usr/src/app/output/dynjs/dynjs/a98c081265982db02dec1543679620aae7783b47/src/main/java/org/dynjs/codegen/BasicBytecodeGeneratingVisitor.java/left.java
    visit(context, (NumberLiteralExpression) expr, strict)
=======
    invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class))
>>>>>>> /usr/src/app/output/dynjs/dynjs/a98c081265982db02dec1543679620aae7783b47/src/main/java/org/dynjs/codegen/BasicBytecodeGeneratingVisitor.java/right.java
    ;
  }

  @Override public void visit(ExecutionContext context, LogicalExpression expr, boolean strict) {
    LabelNode end = new LabelNode();
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    dup();
    append(jsToBoolean());
    invokevirtual(p(Boolean.class), "booleanValue", sig(boolean.class));
    if (expr.getOp().equals("&&")) {
      iffalse(end);
    } else {
      if (expr.getOp().equals("||")) {
        iftrue(end);
      }
    }
    pop();
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    go_to(end);
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, LogicalNotOperatorExpression expr, boolean strict) {
    LabelNode returnFalse = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getExpr().accept(context, this, strict);
    append(jsGetValue());
    append(jsToBoolean());
    invokevirtual(p(Boolean.class), "booleanValue", sig(boolean.class));
    iftrue(returnFalse);
    iconst_1();
    go_to(end);
    label(returnFalse);
    iconst_0();
    label(end);
    invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
    nop();
  }

  @Override public void visit(ExecutionContext context, DotExpression expr, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    ldc(expr.getIdentifier());
    swap();
    append(jsCheckObjectCoercible(null));
    swap();
    append(jsCreatePropertyReference());
  }

  @Override public void visit(ExecutionContext context, BracketExpression expr, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    expr.getRhs().accept(context, this, strict);
    swap();
    append(jsCheckObjectCoercible(null));
    swap();
    append(jsGetValue());
    append(jsToString());
    append(jsCreatePropertyReference());
  }

  @Override public void visit(ExecutionContext context, MultiplicativeExpression expr, boolean strict) {
    LabelNode doubleNums = new LabelNode();
    LabelNode returnNaN = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    append(jsToNumber());
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    append(jsToNumber());
    append(ifEitherIsNaN(returnNaN));
    if (expr.getOp().equals("%")) {
      append(ifTopIsZero(returnNaN));
    }
    if (!expr.getOp().equals("/")) {
      append(ifEitherIsDouble(doubleNums));
      if (expr.getOp().equals("*")) {
        append(convertTopTwoToPrimitiveLongs());
        lmul();
        append(convertTopToLong());
      } else {
        if (expr.getOp().equals("/")) {
          append(convertTopTwoToPrimitiveLongs());
          ldiv();
          append(convertTopToLong());
        } else {
          if (expr.getOp().equals("%")) {
            invokestatic(p(BuiltinNumber.class), "modulo", sig(Number.class, Number.class, Number.class));
          }
        }
      }
      go_to(end);
      label(doubleNums);
    }
    append(convertTopTwoToPrimitiveDoubles());
    if (expr.getOp().equals("*")) {
      dmul();
    } else {
      if (expr.getOp().equals("/")) {
        ddiv();
      } else {
        if (expr.getOp().equals("%")) {
          drem();
        }
      }
    }
    append(convertTopToDouble());
    go_to(end);
    label(returnNaN);
    pop();
    pop();
    getstatic(p(Double.class), "NaN", ci(double.class));
    invokestatic(p(Double.class), "valueOf", sig(Double.class, double.class));
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, NewOperatorExpression expr, boolean strict) {
    LabelNode end = new LabelNode();
    expr.getExpr().accept(context, this, strict);
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    append(jsGetValue(JSFunction.class));
    bipush(0);
    anewarray(p(Object.class));
    invokevirtual(p(ExecutionContext.class), "construct", sig(Object.class, JSFunction.class, Object[].class));
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, NullLiteralExpression expr, boolean strict) {
    getstatic(p(Types.class), "NULL", ci(Types.Null.class));
  }

  public void visit(ExecutionContext context, NumberLiteralExpression expr, boolean strict) {
    String text = expr.getText();
    if (text.indexOf('.') == 0) {
      ldc("0" + text);
      invokestatic(p(Double.class), "valueOf", sig(Double.class, String.class));
    } else {
      if (text.indexOf(".") > 0) {
        ldc(text);
        ldc(10);
        invokestatic(p(Types.class), "parseLongOrDouble", sig(Number.class, String.class, int.class));
      } else {
        if (text.startsWith("0x") || text.startsWith("0X")) {
          String realText = text.substring(2);
          ldc(realText);
          bipush(expr.getRadix());
          invokestatic(p(Long.class), "valueOf", sig(Long.class, String.class, int.class));
        } else {
          final int index = text.toLowerCase().indexOf('e');
          if (index > 0) {
            String base = text.substring(0, index);
            String exponent = text.substring(index);
            String javafied = base + ".0" + exponent;
            ldc(javafied);
            invokestatic(p(Double.class), "valueOf", sig(Double.class, String.class));
          } else {
            ldc(text);
            bipush(expr.getRadix());
            invokestatic(p(Types.class), "parseLongOrDouble", sig(Number.class, String.class, int.class));
          }
        }
      }
    }
  }

  @Override public void visit(ExecutionContext context, ObjectLiteralExpression expr, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    invokestatic(p(BuiltinObject.class), "newObject", sig(DynObject.class, ExecutionContext.class));
    for (PropertyAssignment each : expr.getPropertyAssignments()) {
      dup();
      each.accept(context, this, strict);
    }
  }

  @Override public void visit(ExecutionContext context, PostOpExpression expr, boolean strict) {
    LabelNode doubleNum = new LabelNode();
    LabelNode invalid = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getExpr().accept(context, this, strict);
    dup();
    instance_of(p(Reference.class));
    iffalse(invalid);
    dup();
    invokevirtual(p(Reference.class), "isValidForPrePostIncrementDecrement", sig(boolean.class));
    iffalse(invalid);
    dup();
    append(jsGetValue());
    append(jsToNumber());
    dup();
    instance_of(p(Double.class));
    iftrue(doubleNum);
    dup2();
    invokevirtual(p(Number.class), "longValue", sig(long.class));
    ldc(1L);
    if (expr.getOp().equals("++")) {
      ladd();
    } else {
      lsub();
    }
    invokestatic(p(Long.class), "valueOf", sig(Long.class, long.class));
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
    swap();
    pop();
    go_to(end);
    label(doubleNum);
    dup2();
    invokevirtual(p(Number.class), "doubleValue", sig(double.class));
    iconst_1();
    i2d();
    if (expr.getOp().equals("++")) {
      dadd();
    } else {
      dsub();
    }
    invokestatic(p(Double.class), "valueOf", sig(Double.class, double.class));
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
    swap();
    pop();
    go_to(end);
    label(invalid);
    append(jsThrowSyntaxError("invalid operation"));
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, PreOpExpression expr, boolean strict) {
    LabelNode storeNewValue = new LabelNode();
    LabelNode doubleNum = new LabelNode();
    LabelNode invalid = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getExpr().accept(context, this, strict);
    dup();
    instance_of(p(Reference.class));
    iffalse(invalid);
    dup();
    invokevirtual(p(Reference.class), "isValidForPrePostIncrementDecrement", sig(boolean.class));
    iffalse(invalid);
    dup();
    dup();
    append(jsGetValue());
    append(jsToNumber());
    dup();
    instance_of(p(Double.class));
    iftrue(doubleNum);
    invokevirtual(p(Number.class), "longValue", sig(long.class));
    ldc(1L);
    if (expr.getOp().equals("++")) {
      ladd();
    } else {
      lsub();
    }
    invokestatic(p(Long.class), "valueOf", sig(Long.class, long.class));
    go_to(storeNewValue);
    label(doubleNum);
    invokevirtual(p(Number.class), "doubleValue", sig(double.class));
    iconst_1();
    i2d();
    if (expr.getOp().equals("++")) {
      dadd();
    } else {
      dsub();
    }
    invokestatic(p(Double.class), "valueOf", sig(Double.class, double.class));
    label(storeNewValue);
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
    append(jsGetValue());
    go_to(end);
    label(invalid);
    append(jsThrowSyntaxError("invalid operation"));
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, PropertyGet propertyGet, boolean strict) {
    dup();
    aload(Arities.EXECUTION_CONTEXT);
    ldc(propertyGet.getName());
    invokeinterface(p(JSObject.class), "getOwnProperty", sig(Object.class, ExecutionContext.class, String.class));
    compiledFunction(null, EMPTY_STRING_ARRAY, propertyGet.getBlock(), false);
    ldc(propertyGet.getName());
    swap();
    invokestatic(p(PropertyDescriptor.class), "newPropertyDescriptorForObjectInitializerGet", sig(PropertyDescriptor.class, Object.class, String.class, JSFunction.class));
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    ldc(propertyGet.getName());
    swap();
    iconst_0();
    i2b();
    invokeinterface(p(JSObject.class), "defineOwnProperty", sig(boolean.class, ExecutionContext.class, String.class, PropertyDescriptor.class, boolean.class));
    pop();
  }

  @Override public void visit(ExecutionContext context, PropertySet propertySet, boolean strict) {
    dup();
    aload(Arities.EXECUTION_CONTEXT);
    ldc(propertySet.getName());
    invokeinterface(p(JSObject.class), "getOwnProperty", sig(Object.class, ExecutionContext.class, String.class));
    compiledFunction(null, new String[] { propertySet.getIdentifier() }, propertySet.getBlock(), false);
    ldc(propertySet.getName());
    swap();
    invokestatic(p(PropertyDescriptor.class), "newPropertyDescriptorForObjectInitializerSet", sig(PropertyDescriptor.class, Object.class, String.class, JSFunction.class));
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    ldc(propertySet.getName());
    swap();
    iconst_0();
    i2b();
    invokeinterface(p(JSObject.class), "defineOwnProperty", sig(boolean.class, ExecutionContext.class, String.class, PropertyDescriptor.class, boolean.class));
    pop();
  }

  @Override public void visit(ExecutionContext context, NamedValue namedValue, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    ldc(namedValue.getName());
    namedValue.getExpr().accept(context, this, strict);
    append(jsGetValue());
    if (namedValue.getExpr() instanceof FunctionExpression) {
      ldc(namedValue.getName());
      swap();
      invokestatic(p(PropertyDescriptor.class), "newPropertyDescriptorForObjectInitializer", sig(PropertyDescriptor.class, String.class, Object.class));
    } else {
      invokestatic(p(PropertyDescriptor.class), "newPropertyDescriptorForObjectInitializer", sig(PropertyDescriptor.class, Object.class));
    }
    iconst_0();
    i2b();
    invokeinterface(p(JSObject.class), "defineOwnProperty", sig(boolean.class, ExecutionContext.class, String.class, PropertyDescriptor.class, boolean.class));
    pop();
  }

  @Override public void visit(ExecutionContext context, RegexpLiteralExpression expr, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    ldc(expr.getPattern());
    ldc(expr.getFlags());
    invokestatic(p(BuiltinRegExp.class), "newRegExp", sig(DynRegExp.class, ExecutionContext.class, Object.class, String.class));
  }

  @Override public void visit(ExecutionContext context, RelationalExpression expr, boolean strict) {
    LabelNode returnFalse = new LabelNode();
    LabelNode end = new LabelNode();
    aload(Arities.EXECUTION_CONTEXT);
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    if (expr.getOp().equals(">") || expr.getOp().equals("<=")) {
      swap();
      iconst_0();
      i2b();
    } else {
      iconst_1();
      i2b();
    }
    invokestatic(p(Types.class), "compareRelational", sig(Object.class, ExecutionContext.class, Object.class, Object.class, boolean.class));
    dup();
    if (expr.getOp().equals("<") || expr.getOp().equals(">")) {
      append(jsPushUndefined());
      if_acmpeq(returnFalse);
      go_to(end);
    } else {
      if (expr.getOp().equals("<=") || expr.getOp().equals(">=")) {
        append(jsPushUndefined());
        if_acmpeq(returnFalse);
        dup();
        getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
        if_acmpeq(returnFalse);
        pop();
        getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
        go_to(end);
      }
    }
    label(returnFalse);
    pop();
    getstatic(p(Boolean.class), "FALSE", ci(Boolean.class));
    go_to(end);
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, ReturnStatement statement, boolean strict) {
    if (statement.getExpr() == null) {
      append(jsPushUndefined());
    } else {
      statement.getExpr().accept(context, this, strict);
      append(jsGetValue());
    }
    returnCompletion();
  }

  @Override public void visit(ExecutionContext context, StrictEqualityOperatorExpression expr, boolean strict) {
    LabelNode returnTrue = new LabelNode();
    LabelNode returnFalse = new LabelNode();
    LabelNode end = new LabelNode();
    aload(Arities.EXECUTION_CONTEXT);
    expr.getLhs().accept(context, this, strict);
    append(jsGetValue());
    expr.getRhs().accept(context, this, strict);
    append(jsGetValue());
    invokestatic(p(Types.class), "compareStrictEquality", sig(boolean.class, ExecutionContext.class, Object.class, Object.class));
    if (expr.getOp().equals("===")) {
      iftrue(returnTrue);
      go_to(returnFalse);
    } else {
      iffalse(returnTrue);
      go_to(returnFalse);
    }
    label(returnTrue);
    getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
    go_to(end);
    label(returnFalse);
    getstatic(p(Boolean.class), "FALSE", ci(Boolean.class));
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, StringLiteralExpression expr, boolean strict) {
    ldc(expr.getLiteral());
  }

  @Override public void visit(ExecutionContext context, SwitchStatement statement, boolean strict) {
    LabelNode end = new LabelNode();
    normalCompletion();
    astore(Arities.COMPLETION);
    statement.getExpr().accept(context, this, strict);
    append(jsGetValue());
    List<CaseClause> caseClauses = statement.getCaseClauses();
    List<LabelNode> labels = new ArrayList<>();
    int numClauses = caseClauses.size();
    int defaultIndex = -1;
    for (int i = 0; i < numClauses; ++i) {
      CaseClause eachCase = caseClauses.get(i);
      LabelNode caseLabel = new LabelNode();
      labels.add(caseLabel);
      if (eachCase instanceof DefaultCaseClause) {
        defaultIndex = i;
        continue;
      }
      LabelNode notMatched = new LabelNode();
      dup();
      aload(Arities.EXECUTION_CONTEXT);
      swap();
      eachCase.getExpression().accept(context, this, strict);
      append(jsGetValue());
      invokestatic(p(Types.class), "compareStrictEquality", sig(boolean.class, ExecutionContext.class, Object.class, Object.class));
      iffalse(notMatched);
      pop();
      go_to(caseLabel);
      label(notMatched);
    }
    pop();
    if (defaultIndex >= 0) {
      go_to(labels.get(defaultIndex));
    } else {
      go_to(end);
    }
    for (int i = 0; i < numClauses; ++i) {
      LabelNode eachLabel = labels.get(i);
      label(eachLabel);
      CaseClause eachCase = caseClauses.get(i);
      invokeCompiledStatementBlock("Case", eachCase.getBlock(), strict);
      LabelNode normal = new LabelNode();
      LabelNode broke = new LabelNode();
      LabelNode abrupt = new LabelNode();
      LabelNode caseEnd = new LabelNode();
      dup();
      append(handleCompletion(normal, broke, abrupt, abrupt));
      label(normal);
      dup();
      append(jsCompletionValue());
      ifnonnull(caseEnd);
      dup();
      aload(Arities.COMPLETION);
      append(jsCompletionValue());
      putfield(p(Completion.class), "value", ci(Object.class));
      go_to(caseEnd);
      label(broke);
      convertToNormalCompletion();
      label(abrupt);
      astore(Arities.COMPLETION);
      go_to(end);
      label(caseEnd);
      astore(Arities.COMPLETION);
    }
    label(end);
    aload(Arities.COMPLETION);
  }

  @Override public void visit(ExecutionContext context, TernaryExpression expr, boolean strict) {
    LabelNode elseBranch = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getTest().accept(context, this, strict);
    append(jsGetValue());
    append(jsToBoolean());
    invokevirtual(p(Boolean.class), "booleanValue", sig(boolean.class));
    iffalse(elseBranch);
    expr.getThenExpr().accept(context, this, strict);
    go_to(end);
    label(elseBranch);
    expr.getElseExpr().accept(context, this, strict);
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, ThisExpression expr, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    invokevirtual(p(ExecutionContext.class), "getThisBinding", sig(Object.class));
  }

  @Override public void visit(ExecutionContext context, ThrowStatement statement, boolean strict) {
    statement.getExpr().accept(context, this, strict);
    append(jsGetValue());
    newobj(p(ThrowException.class));
    dup_x1();
    swap();
    aload(Arities.EXECUTION_CONTEXT);
    swap();
    invokespecial(p(ThrowException.class), "<init>", sig(void.class, ExecutionContext.class, Object.class));
    athrow();
  }

  @Override public void visit(ExecutionContext context, TryStatement statement, boolean strict) {
    LabelNode end = new LabelNode();
    LabelNode tryStart = new LabelNode();
    LabelNode tryEnd = new LabelNode();
    LabelNode outerCatchHandler = new LabelNode();
    label(tryStart);
    invokeCompiledStatementBlock("Try", statement.getTryBlock(), strict);
    label(tryEnd);
    if (statement.getFinallyBlock() != null) {
      invokeCompiledStatementBlock("Finally", statement.getFinallyBlock(), strict);
      dup();
      getfield(p(Completion.class), "type", ci(Completion.Type.class));
      getstatic(p(Completion.Type.class), "NORMAL", ci(Completion.Type.class));
      LabelNode normalFinally = new LabelNode();
      if_acmpeq(normalFinally);
      swap();
      pop();
      go_to(end);
      label(normalFinally);
      pop();
    }
    go_to(end);
    trycatch(tryStart, tryEnd, outerCatchHandler, p(ThrowException.class));
    if (statement.getCatchClause() != null) {
      LabelNode catchCatchHandler = new LabelNode();
      LabelNode catchStart = new LabelNode();
      LabelNode catchEnd = new LabelNode();
      label(outerCatchHandler);
      invokevirtual(p(ThrowException.class), "getValue", sig(Object.class));
      aload(Arities.EXECUTION_CONTEXT);
      swap();
      compiledStatementBlock("Catch", statement.getCatchClause().getBlock(), strict);
      swap();
      ldc(statement.getCatchClause().getIdentifier());
      swap();
      label(catchStart);
      invokevirtual(p(ExecutionContext.class), "executeCatch", sig(Completion.class, BasicBlock.class, String.class, Object.class));
      label(catchEnd);
      if (statement.getFinallyBlock() != null) {
        invokeCompiledStatementBlock("Finally", statement.getFinallyBlock(), strict);
        dup();
        getfield(p(Completion.class), "type", ci(Completion.Type.class));
        getstatic(p(Completion.Type.class), "NORMAL", ci(Completion.Type.class));
        LabelNode normalFinally = new LabelNode();
        if_acmpeq(normalFinally);
        swap();
        pop();
        go_to(end);
        label(normalFinally);
        pop();
        go_to(end);
        LabelNode normalFinallyAfterThrow = new LabelNode();
        trycatch(catchStart, catchEnd, catchCatchHandler, null);
        label(catchCatchHandler);
        invokeCompiledStatementBlock("Finally", statement.getFinallyBlock(), strict);
        dup();
        getfield(p(Completion.class), "type", ci(Completion.Type.class));
        getstatic(p(Completion.Type.class), "NORMAL", ci(Completion.Type.class));
        if_acmpeq(normalFinallyAfterThrow);
        swap();
        pop();
        go_to(end);
        label(normalFinallyAfterThrow);
        pop();
        athrow();
      }
    } else {
      label(outerCatchHandler);
      if (statement.getFinallyBlock() != null) {
        invokeCompiledStatementBlock("Finally", statement.getFinallyBlock(), strict);
        dup();
        getfield(p(Completion.class), "type", ci(Completion.Type.class));
        getstatic(p(Completion.Type.class), "NORMAL", ci(Completion.Type.class));
        LabelNode normalFinally = new LabelNode();
        if_acmpeq(normalFinally);
        swap();
        pop();
        go_to(end);
        label(normalFinally);
        pop();
        athrow();
      }
    }
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, TypeOfOpExpression expr, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    expr.getExpr().accept(context, this, strict);
    invokestatic(p(Types.class), "typeof", sig(String.class, ExecutionContext.class, Object.class));
  }

  @Override public void visit(ExecutionContext context, UnaryMinusExpression expr, boolean strict) {
    LabelNode doubleNum = new LabelNode();
    LabelNode zero = new LabelNode();
    LabelNode end = new LabelNode();
    expr.getExpr().accept(context, this, strict);
    append(jsGetValue());
    append(jsToNumber());
    dup();
    instance_of(p(Double.class));
    iftrue(doubleNum);
    dup();
    invokevirtual(p(Number.class), "longValue", sig(long.class));
    ldc(0L);
    lcmp();
    iffalse(zero);
    invokevirtual(p(Number.class), "longValue", sig(long.class));
    ldc(-1L);
    lmul();
    invokestatic(p(Long.class), "valueOf", sig(Long.class, long.class));
    go_to(end);
    label(doubleNum);
    invokevirtual(p(Number.class), "doubleValue", sig(double.class));
    iconst_m1();
    i2d();
    dmul();
    invokestatic(p(Double.class), "valueOf", sig(Double.class, double.class));
    go_to(end);
    label(zero);
    pop();
    ldc(-0.0);
    invokestatic(p(Double.class), "valueOf", sig(Double.class, double.class));
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, UnaryPlusExpression expr, boolean strict) {
    expr.getExpr().accept(context, this, strict);
    append(jsGetValue());
    append(jsToNumber());
  }

  @Override public void visit(ExecutionContext context, VariableDeclaration expr, boolean strict) {
    if (expr.getExpr() == null) {
      ldc(expr.getIdentifier());
    } else {
      append(jsResolve(expr.getIdentifier()));
      aload(Arities.EXECUTION_CONTEXT);
      expr.getExpr().accept(context, this, strict);
      append(jsGetValue());
      invokevirtual(p(Reference.class), "putValue", sig(void.class, ExecutionContext.class, Object.class));
      ldc(expr.getIdentifier());
    }
  }

  @Override public void visit(ExecutionContext context, VariableStatement statement, boolean strict) {
    for (VariableDeclaration each : statement.getVariableDeclarations()) {
      each.accept(context, this, strict);
      pop();
    }
    normalCompletion();
  }

  @Override public void visit(ExecutionContext context, VoidOperatorExpression expr, boolean strict) {
    expr.getExpr().accept(context, this, strict);
    append(jsGetValue());
    pop();
    append(jsPushUndefined());
  }

  @Override public void visit(ExecutionContext context, WhileStatement statement, boolean strict) {
    LabelNode end = new LabelNode();
    LabelNode breakTarget = new LabelNode();
    LabelNode continueTarget = new LabelNode();
    LabelNode begin = new LabelNode();
    normalCompletion();
    label(begin);
    statement.getTest().accept(context, this, strict);
    append(jsGetValue());
    append(jsToBoolean());
    invokevirtual(p(Boolean.class), "booleanValue", sig(boolean.class));
    iffalse(end);
    invokeCompiledStatementBlock("Do", statement.getBlock(), strict);
    swap();
    pop();
    dup();
    append(handleCompletion(begin, breakTarget, continueTarget, end));
    label(breakTarget);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    convertToNormalCompletion();
    go_to(end);
    label(continueTarget);
    dup();
    append(jsCompletionTarget());
    append(statement.isInLabelSet());
    iffalse(end);
    go_to(begin);
    label(end);
    nop();
  }

  @Override public void visit(ExecutionContext context, WithStatement statement, boolean strict) {
    aload(Arities.EXECUTION_CONTEXT);
    statement.getExpr().accept(context, this, strict);
    append(jsGetValue());
    append(jsToObject());
    compiledStatementBlock("With", statement.getBlock(), strict);
    invokevirtual(p(ExecutionContext.class), "executeWith", sig(Completion.class, JSObject.class, BasicBlock.class));
  }
}