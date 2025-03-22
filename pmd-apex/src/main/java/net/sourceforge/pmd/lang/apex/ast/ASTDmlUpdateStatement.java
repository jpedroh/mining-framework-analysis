/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;

<<<<<<< /usr/src/app/output/pmd/pmd/8528a8ed7d2400fccb0f15c366c9843ee706c329/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/ast/ASTDmlUpdateStatement.java/left.java
public final class ASTDmlUpdateStatement extends AbstractApexNode.Single<Node> {
||||||| /usr/src/app/output/pmd/pmd/8528a8ed7d2400fccb0f15c366c9843ee706c329/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/ast/ASTDmlUpdateStatement.java/base.java
public final class ASTDmlUpdateStatement extends AbstractApexNode<Node> {
=======
public final class ASTDmlUpdateStatement extends AbstractDmlStatement<Node> {
>>>>>>> /usr/src/app/output/pmd/pmd/8528a8ed7d2400fccb0f15c366c9843ee706c329/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/ast/ASTDmlUpdateStatement.java/right.java

    ASTDmlUpdateStatement(Node dmlUpdateStatement) {
        super(dmlUpdateStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
