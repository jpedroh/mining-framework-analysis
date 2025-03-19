package com.akiban.sql.parser;
import com.akiban.sql.types.ValueClassName;

/**
 * This node represents a binary arithmetic operator, like + or *.
 *
 */
public class BinaryArithmeticOperatorNode extends BinaryOperatorNode {
  /**
     * Initializer for a BinaryArithmeticOperatorNode
     *
     * @param leftOperand The left operand
     * @param rightOperand  The right operand
     */
  public void init(Object leftOperand, Object rightOperand) {
    super.init(leftOperand, rightOperand, ValueClassName.NumberDataValue, ValueClassName.NumberDataValue);
  }

  public void setNodeType(int nodeType) {
    String operator = null;
    String methodName = null;
    switch (nodeType) {
      case NodeTypes.BINARY_DIVIDE_OPERATOR_NODE:
      operator = "/";
      methodName = "divide";
      break;
      case NodeTypes.BINARY_MINUS_OPERATOR_NODE:
      operator = "-";
      methodName = "minus";
      break;
      case NodeTypes.BINARY_PLUS_OPERATOR_NODE:
      operator = "+";
      methodName = "plus";
      break;
      case NodeTypes.BINARY_TIMES_OPERATOR_NODE:
      operator = "*";
      methodName = "times";
      break;
      case NodeTypes.MOD_OPERATOR_NODE:
      operator = "mod";
      methodName = "mod";
      break;
      case NodeTypes.BINARY_DIV_OPERATOR_NODE:
      operator = "div";
      methodName = "div";
      break;
      default:
      assert false : "Unexpected nodeType:" + nodeType;
    }
    setOperator(operator);
    setMethodName(methodName);
    super.setNodeType(nodeType);
  }
}