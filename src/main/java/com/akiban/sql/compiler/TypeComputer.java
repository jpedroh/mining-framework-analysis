package com.akiban.sql.compiler;
import com.akiban.sql.parser.*;
import com.akiban.sql.StandardException;
import com.akiban.sql.types.DataTypeDescriptor;
import com.akiban.sql.types.TypeId;
import java.util.*;

/** Calculate types from schema information. */
public class TypeComputer implements Visitor {
  public TypeComputer() {
  }

  public void compute(StatementNode stmt) throws StandardException {
    stmt.accept(this);
  }

  /** Probably need to subclass and handle <code>NodeTypes.COLUMN_REFERENCE</code>
     * to get type propagation started. */
  protected DataTypeDescriptor computeType(ValueNode node) throws StandardException {
    switch (node.getNodeType()) {
      case NodeTypes.RESULT_COLUMN:
      return resultColumn((ResultColumn) node);
      case NodeTypes.AND_NODE:
      case NodeTypes.OR_NODE:
      return binaryLogicalOperatorNode((BinaryLogicalOperatorNode) node);
      case NodeTypes.BINARY_PLUS_OPERATOR_NODE:
      case NodeTypes.BINARY_TIMES_OPERATOR_NODE:
      case NodeTypes.BINARY_DIVIDE_OPERATOR_NODE:
      case NodeTypes.BINARY_MINUS_OPERATOR_NODE:
      return binaryArithmeticOperatorNode((BinaryArithmeticOperatorNode) node);
      case NodeTypes.BINARY_EQUALS_OPERATOR_NODE:
      case NodeTypes.BINARY_NOT_EQUALS_OPERATOR_NODE:
      case NodeTypes.BINARY_GREATER_THAN_OPERATOR_NODE:
      case NodeTypes.BINARY_GREATER_EQUALS_OPERATOR_NODE:
      case NodeTypes.BINARY_LESS_THAN_OPERATOR_NODE:
      case NodeTypes.BINARY_LESS_EQUALS_OPERATOR_NODE:
      return binaryComparisonOperatorNode((BinaryComparisonOperatorNode) node);
      case NodeTypes.SUBQUERY_NODE:
      return subqueryNode((SubqueryNode) node);
      case NodeTypes.CONDITIONAL_NODE:
      return conditionalNode((ConditionalNode) node);
      case NodeTypes.COALESCE_FUNCTION_NODE:
      return coalesceFunctionNode((CoalesceFunctionNode) node);
      default:
      return null;
    }
  }

  protected DataTypeDescriptor resultColumn(ResultColumn node) throws StandardException {
    if (node.getExpression() == null) {
      return null;
    }
    return node.getExpression().getType();
  }

  protected DataTypeDescriptor binaryLogicalOperatorNode(BinaryLogicalOperatorNode node) throws StandardException {
    ValueNode leftOperand = node.getLeftOperand();
    ValueNode rightOperand = node.getRightOperand();
    DataTypeDescriptor leftType = leftOperand.getType();
    DataTypeDescriptor rightType = rightOperand.getType();
    if (leftType == null) {
      return rightType;
    }
    if (!leftType.getTypeId().isBooleanTypeId()) {
      throw new StandardException("Boolean operation on non-boolean: " + leftType.getTypeName());
    }
    if (!rightType.getTypeId().isBooleanTypeId()) {
      throw new StandardException("Boolean operation on non-boolean: " + rightType.getTypeName());
    }
    return leftType.getNullabilityType(leftType.isNullable() || rightType.isNullable());
  }

  protected DataTypeDescriptor binaryArithmeticOperatorNode(BinaryArithmeticOperatorNode node) throws StandardException {
    ValueNode leftOperand = node.getLeftOperand();
    ValueNode rightOperand = node.getRightOperand();
    DataTypeDescriptor leftType = leftOperand.getType();
    DataTypeDescriptor rightType = rightOperand.getType();
    TypeId leftTypeId = leftType.getTypeId();
    TypeId rightTypeId = rightType.getTypeId();
    if (leftTypeId.isStringTypeId() && rightTypeId.isNumericTypeId()) {
      boolean nullableResult;
      nullableResult = leftType.isNullable() || rightType.isNullable();
      int precision = rightType.getPrecision();
      int scale = rightType.getScale();
      int maxWidth = rightType.getMaximumWidth();
      if (rightTypeId.isDecimalTypeId()) {
        int charMaxWidth = leftType.getMaximumWidth();
        precision += (2 * charMaxWidth);
        scale += charMaxWidth;
        maxWidth = precision + 3;
      }
      leftOperand = (ValueNode) node.getNodeFactory().getNode(NodeTypes.CAST_NODE, leftOperand, new DataTypeDescriptor(rightTypeId, precision, scale, nullableResult, maxWidth), node.getParserContext());
      node.setLeftOperand(leftOperand);
    } else {
      if (rightTypeId.isStringTypeId() && leftTypeId.isNumericTypeId()) {
        boolean nullableResult;
        nullableResult = leftType.isNullable() || rightType.isNullable();
        int precision = leftType.getPrecision();
        int scale = leftType.getScale();
        int maxWidth = leftType.getMaximumWidth();
        if (leftTypeId.isDecimalTypeId()) {
          int charMaxWidth = rightType.getMaximumWidth();
          precision += (2 * charMaxWidth);
          scale += charMaxWidth;
          maxWidth = precision + 3;
        }
        rightOperand = (ValueNode) node.getNodeFactory().getNode(NodeTypes.CAST_NODE, rightOperand, new DataTypeDescriptor(leftTypeId, precision, scale, nullableResult, maxWidth), node.getParserContext());
        node.setRightOperand(rightOperand);
      }
    }
    return getTypeCompiler(leftOperand).resolveArithmeticOperation(leftOperand.getType(), rightOperand.getType(), node.getOperator());
  }

  protected DataTypeDescriptor binaryComparisonOperatorNode(BinaryComparisonOperatorNode node) throws StandardException {
    ValueNode leftOperand = node.getLeftOperand();
    ValueNode rightOperand = node.getRightOperand();
    TypeId leftTypeId = leftOperand.getTypeId();
    TypeId rightTypeId = rightOperand.getTypeId();
    if ((leftTypeId == null) || (rightTypeId == null)) {
      return null;
    }
    if (!leftTypeId.isStringTypeId() && rightTypeId.isStringTypeId()) {
      DataTypeDescriptor leftType = leftOperand.getType();
      DataTypeDescriptor rightType = rightOperand.getType();
      rightOperand = (ValueNode) node.getNodeFactory().getNode(NodeTypes.CAST_NODE, rightOperand, leftType.getNullabilityType(rightType.isNullable()), node.getParserContext());
      node.setRightOperand(rightOperand);
    } else {
      if (!rightTypeId.isStringTypeId() && leftTypeId.isStringTypeId()) {
        DataTypeDescriptor leftType = leftOperand.getType();
        DataTypeDescriptor rightType = rightOperand.getType();
        leftOperand = (ValueNode) node.getNodeFactory().getNode(NodeTypes.CAST_NODE, leftOperand, rightType.getNullabilityType(leftType.isNullable()), node.getParserContext());
        node.setLeftOperand(leftOperand);
      }
    }
    if (!node.isForQueryRewrite()) {
      String operator = node.getOperator();
      boolean forEquals = operator.equals("=") || operator.equals("<>");
      boolean cmp = leftOperand.getType().comparable(rightOperand.getType(), forEquals);
      if (!cmp) {
        throw new StandardException("Types not comparable: " + leftOperand.getType().getTypeName() + " and " + rightOperand.getType().getTypeName());
      }
    }
    boolean nullableResult = leftOperand.getType().isNullable() || rightOperand.getType().isNullable();
    return new DataTypeDescriptor(TypeId.BOOLEAN_ID, nullableResult);
  }

  protected DataTypeDescriptor subqueryNode(SubqueryNode node) throws StandardException {
    if (node.getSubqueryType() == SubqueryNode.SubqueryType.EXPRESSION) {
      return node.getResultSet().getResultColumns().get(0).getType().getNullabilityType(true);
    } else {
      return new DataTypeDescriptor(TypeId.BOOLEAN_ID, true);
    }
  }

  protected DataTypeDescriptor conditionalNode(ConditionalNode node) throws StandardException {
    checkBooleanClause(node.getTestCondition(), "WHEN");
    return dominantType(node.getThenElseList());
  }

  protected DataTypeDescriptor coalesceFunctionNode(CoalesceFunctionNode node) throws StandardException {
    return dominantType(node.getArgumentsList());
  }

  protected DataTypeDescriptor dominantType(ValueNodeList nodeList) throws StandardException {
    DataTypeDescriptor result = null;
    for (ValueNode node : nodeList) {
      if (node.getType() == null) {
        continue;
      }
      if (result == null) {
        result = node.getType();
      } else {
        result = result.getDominantType(node.getType());
      }
    }
    return result;
  }

  protected void selectNode(SelectNode node) throws StandardException {
    checkBooleanClause(node.getWhereClause(), "WHERE");
    checkBooleanClause(node.getHavingClause(), "HAVING");
    if (node.getResultColumns() != null) {
      node.getResultColumns().accept(this);
    }
  }

  private void checkBooleanClause(ValueNode clause, String which) throws StandardException {
    if (clause != null) {
      DataTypeDescriptor type = clause.getType();
      if (type == null) {
        assert false : "Type not set yet";
        return;
      }
      if (!type.getTypeId().isBooleanTypeId()) {
        throw new StandardException("Non-boolean " + which + " clause");
      }
    }
  }

  private void fromSubquery(FromSubquery node) throws StandardException {
    if (node.getResultColumns() != null) {
      ResultColumnList rcl1 = node.getResultColumns();
      ResultColumnList rcl2 = node.getSubquery().getResultColumns();
      int size = rcl1.size();
      for (int i = 0; i < size; i++) {
        rcl1.get(i).setType(rcl2.get(i).getType());
      }
    }
  }

  public Visitable visit(Visitable node) throws StandardException {
    if (node instanceof ValueNode) {
      ValueNode valueNode = (ValueNode) node;
      if (valueNode.getType() == null) {
        valueNode.setType(computeType(valueNode));
      }
    } else {
      switch (((QueryTreeNode) node).getNodeType()) {
        case NodeTypes.SELECT_NODE:
        selectNode((SelectNode) node);
        break;
        case NodeTypes.FROM_SUBQUERY:
        fromSubquery((FromSubquery) node);
        break;
      }
    }
    return node;
  }

  public boolean skipChildren(Visitable node) throws StandardException {
    return false;
  }

  public boolean visitChildrenFirst(Visitable node) {
    return true;
  }

  public boolean stopTraversal() {
    return false;
  }

  /**
     * Get the TypeCompiler associated with the given TypeId
     *
     * @param typeId The TypeId to get a TypeCompiler for
     *
     * @return The corresponding TypeCompiler
     *
     */
  protected TypeCompiler getTypeCompiler(TypeId typeId) {
    return TypeCompiler.getTypeCompiler(typeId);
  }

  /**
     * Get the TypeCompiler from this ValueNode, based on its TypeId
     * using getTypeId().
     *
     * @return This ValueNode's TypeCompiler
     *
     */
  protected TypeCompiler getTypeCompiler(ValueNode valueNode) throws StandardException {
    return getTypeCompiler(valueNode.getTypeId());
  }
}