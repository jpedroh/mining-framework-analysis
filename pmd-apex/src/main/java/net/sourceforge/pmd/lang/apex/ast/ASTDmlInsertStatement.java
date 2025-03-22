/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;

<<<<<<< /usr/src/app/output/pmd/pmd/8528a8ed7d2400fccb0f15c366c9843ee706c329/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/ast/ASTDmlInsertStatement.java/left.java
public final class ASTDmlInsertStatement extends AbstractApexNode.Single<Node> {
||||||| /usr/src/app/output/pmd/pmd/8528a8ed7d2400fccb0f15c366c9843ee706c329/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/ast/ASTDmlInsertStatement.java/base.java
public final class ASTDmlInsertStatement extends AbstractApexNode<Node> {
=======
public final class ASTDmlInsertStatement extends AbstractDmlStatement<Node> {
>>>>>>> /usr/src/app/output/pmd/pmd/8528a8ed7d2400fccb0f15c366c9843ee706c329/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/ast/ASTDmlInsertStatement.java/right.java

    ASTDmlInsertStatement(Node dmlInsertStatement) {
        super(dmlInsertStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
