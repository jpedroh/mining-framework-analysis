package net.sourceforge.pmd.lang.apex.ast;
import com.google.summit.ast.Node;

public final class ASTDmlInsertStatement extends 
<<<<<<< /usr/src/app/output/pmd/pmd/8528a8ed7d2400fccb0f15c366c9843ee706c329/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/ast/ASTDmlInsertStatement.java/left.java
AbstractApexNode.Single
=======
AbstractDmlStatement
>>>>>>> /usr/src/app/output/pmd/pmd/8528a8ed7d2400fccb0f15c366c9843ee706c329/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/ast/ASTDmlInsertStatement.java/right.java
<Node> {
  ASTDmlInsertStatement(Node dmlInsertStatement) {
    super(dmlInsertStatement);
  }

  @Override protected <P extends java.lang.Object, R extends java.lang.Object> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
    return visitor.visit(this, data);
  }
}