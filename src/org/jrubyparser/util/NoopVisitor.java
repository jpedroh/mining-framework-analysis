/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jrubyparser.util;

import org.jrubyparser.NodeVisitor;
import org.jrubyparser.ast.AliasNode;
import org.jrubyparser.ast.AndNode;
import org.jrubyparser.ast.ArgsCatNode;
import org.jrubyparser.ast.ArgsNode;
import org.jrubyparser.ast.ArgsPushNode;
import org.jrubyparser.ast.ArgumentNode;
import org.jrubyparser.ast.ArrayNode;
import org.jrubyparser.ast.AttrAssignNode;
import org.jrubyparser.ast.BackRefNode;
import org.jrubyparser.ast.BeginNode;
import org.jrubyparser.ast.BignumNode;
import org.jrubyparser.ast.BlockArg18Node;
import org.jrubyparser.ast.BlockArgNode;
import org.jrubyparser.ast.BlockNode;
import org.jrubyparser.ast.BlockPassNode;
import org.jrubyparser.ast.BreakNode;
import org.jrubyparser.ast.CallNode;
import org.jrubyparser.ast.CaseNode;
import org.jrubyparser.ast.ClassNode;
import org.jrubyparser.ast.ClassVarAsgnNode;
import org.jrubyparser.ast.ClassVarDeclNode;
import org.jrubyparser.ast.ClassVarNode;
import org.jrubyparser.ast.Colon2Node;
import org.jrubyparser.ast.Colon3Node;
import org.jrubyparser.ast.CommentNode;
import org.jrubyparser.ast.ComplexNode;
import org.jrubyparser.ast.ConstDeclNode;
import org.jrubyparser.ast.ConstNode;
import org.jrubyparser.ast.DAsgnNode;
import org.jrubyparser.ast.DRegexpNode;
import org.jrubyparser.ast.DStrNode;
import org.jrubyparser.ast.DSymbolNode;
import org.jrubyparser.ast.DVarNode;
import org.jrubyparser.ast.DXStrNode;
import org.jrubyparser.ast.DefinedNode;
import org.jrubyparser.ast.DefnNode;
import org.jrubyparser.ast.DefsNode;
import org.jrubyparser.ast.DotNode;
import org.jrubyparser.ast.EncodingNode;
import org.jrubyparser.ast.EnsureNode;
import org.jrubyparser.ast.EvStrNode;
import org.jrubyparser.ast.FCallNode;
import org.jrubyparser.ast.FalseNode;
import org.jrubyparser.ast.FixnumNode;
import org.jrubyparser.ast.FlipNode;
import org.jrubyparser.ast.FloatNode;
import org.jrubyparser.ast.ForNode;
import org.jrubyparser.ast.GlobalAsgnNode;
import org.jrubyparser.ast.GlobalVarNode;
import org.jrubyparser.ast.HashNode;
import org.jrubyparser.ast.IfNode;
import org.jrubyparser.ast.ImplicitNilNode;
import org.jrubyparser.ast.InstAsgnNode;
import org.jrubyparser.ast.InstVarNode;
import org.jrubyparser.ast.IterNode;
import org.jrubyparser.ast.KeywordArgNode;
import org.jrubyparser.ast.KeywordRestArgNode;
import org.jrubyparser.ast.LambdaNode;
import org.jrubyparser.ast.ListNode;
import org.jrubyparser.ast.LiteralNode;
import org.jrubyparser.ast.LocalAsgnNode;
import org.jrubyparser.ast.LocalVarNode;
import org.jrubyparser.ast.Match2Node;
import org.jrubyparser.ast.Match3Node;
import org.jrubyparser.ast.MatchNode;
import org.jrubyparser.ast.MethodNameNode;
import org.jrubyparser.ast.ModuleNode;
import org.jrubyparser.ast.MultipleAsgnNode;
import org.jrubyparser.ast.NewlineNode;
import org.jrubyparser.ast.NextNode;
import org.jrubyparser.ast.NilNode;
import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.NotNode;
import org.jrubyparser.ast.NthRefNode;
import org.jrubyparser.ast.OpAsgnAndNode;
import org.jrubyparser.ast.OpAsgnNode;
import org.jrubyparser.ast.OpAsgnOrNode;
import org.jrubyparser.ast.OpElementAsgnNode;
import org.jrubyparser.ast.OptArgNode;
import org.jrubyparser.ast.OrNode;
import org.jrubyparser.ast.PostExeNode;
import org.jrubyparser.ast.PreExeNode;
import org.jrubyparser.ast.RationalNode;
import org.jrubyparser.ast.RedoNode;
import org.jrubyparser.ast.RegexpNode;
import org.jrubyparser.ast.RequiredKeywordArgumentValueNode;
import org.jrubyparser.ast.RescueBodyNode;
import org.jrubyparser.ast.RescueNode;
import org.jrubyparser.ast.RestArgNode;
import org.jrubyparser.ast.RetryNode;
import org.jrubyparser.ast.ReturnNode;
import org.jrubyparser.ast.RootNode;
import org.jrubyparser.ast.SClassNode;
import org.jrubyparser.ast.SValueNode;
import org.jrubyparser.ast.SelfNode;
import org.jrubyparser.ast.SplatNode;
import org.jrubyparser.ast.StrNode;
import org.jrubyparser.ast.SuperNode;
import org.jrubyparser.ast.SymbolNode;
import org.jrubyparser.ast.SyntaxNode;
import org.jrubyparser.ast.ToAryNode;
import org.jrubyparser.ast.TrueNode;
import org.jrubyparser.ast.UnaryCallNode;
import org.jrubyparser.ast.UndefNode;
import org.jrubyparser.ast.UntilNode;
import org.jrubyparser.ast.VAliasNode;
import org.jrubyparser.ast.VCallNode;
import org.jrubyparser.ast.WhenNode;
import org.jrubyparser.ast.WhileNode;
import org.jrubyparser.ast.XStrNode;
import org.jrubyparser.ast.YieldNode;
import org.jrubyparser.ast.ZArrayNode;
import org.jrubyparser.ast.ZSuperNode;

/**
 * A base class visitor where visiting nodes will do nothing (no-op) by default.
 */
public class NoopVisitor implements NodeVisitor {

    protected Object visit(Node parent) {
        if (parent == null) return null;

        for (Node node: parent.childNodes()) {
            node.accept(this);
        }

        return null;
    }
    
<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitAliasNode(AliasNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitAliasNode(AliasNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitAliasNode(AliasNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitAndNode(AndNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitAndNode(AndNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitAndNode(AndNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitArgsNode(ArgsNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitArgsNode(ArgsNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitArgsNode(ArgsNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitArgsCatNode(ArgsCatNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitArgsCatNode(ArgsCatNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitArgsCatNode(ArgsCatNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitArgsPushNode(ArgsPushNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitArgsPushNode(ArgsPushNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitArgsPushNode(ArgsPushNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitArrayNode(ArrayNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitArrayNode(ArrayNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitArrayNode(ArrayNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitArgumentNode(ArgumentNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitArgumentNode(ArgumentNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitArgumentNode(ArgumentNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitAttrAssignNode(AttrAssignNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitAttrAssignNode(AttrAssignNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitAttrAssignNode(AttrAssignNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitBackRefNode(BackRefNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitBackRefNode(BackRefNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitBackRefNode(BackRefNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitBeginNode(BeginNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitBeginNode(BeginNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitBeginNode(BeginNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitBignumNode(BignumNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitBignumNode(BignumNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitBignumNode(BignumNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitBlockArgNode(BlockArgNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitBlockArgNode(BlockArgNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitBlockArgNode(BlockArgNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitBlockArg18Node(BlockArg18Node iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitBlockArg18Node(BlockArg18Node iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitBlockArg18Node(BlockArg18Node iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitBlockNode(BlockNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitBlockNode(BlockNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitBlockNode(BlockNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitBlockPassNode(BlockPassNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitBlockPassNode(BlockPassNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitBlockPassNode(BlockPassNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitBreakNode(BreakNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitBreakNode(BreakNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitBreakNode(BreakNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitComplexNode(ComplexNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitComplexNode(ComplexNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitComplexNode(ComplexNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitConstDeclNode(ConstDeclNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitConstDeclNode(ConstDeclNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitConstDeclNode(ConstDeclNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitClassVarDeclNode(ClassVarDeclNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitClassVarDeclNode(ClassVarDeclNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitClassVarDeclNode(ClassVarDeclNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitClassVarNode(ClassVarNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitClassVarNode(ClassVarNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitClassVarNode(ClassVarNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitCallNode(CallNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitCallNode(CallNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitCallNode(CallNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitCaseNode(CaseNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitCaseNode(CaseNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitCaseNode(CaseNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitClassNode(ClassNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitClassNode(ClassNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitClassNode(ClassNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitColon2Node(Colon2Node iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitColon2Node(Colon2Node iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitColon2Node(Colon2Node iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitColon3Node(Colon3Node iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitColon3Node(Colon3Node iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitColon3Node(Colon3Node iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitCommentNode(CommentNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitCommentNode(CommentNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitCommentNode(CommentNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitConstNode(ConstNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitConstNode(ConstNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitConstNode(ConstNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDAsgnNode(DAsgnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDAsgnNode(DAsgnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDAsgnNode(DAsgnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDRegxNode(DRegexpNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDRegxNode(DRegexpNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDRegxNode(DRegexpNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDStrNode(DStrNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDStrNode(DStrNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDStrNode(DStrNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDSymbolNode(DSymbolNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDSymbolNode(DSymbolNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDSymbolNode(DSymbolNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDVarNode(DVarNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDVarNode(DVarNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDVarNode(DVarNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDXStrNode(DXStrNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDXStrNode(DXStrNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDXStrNode(DXStrNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDefinedNode(DefinedNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDefinedNode(DefinedNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDefinedNode(DefinedNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDefnNode(DefnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDefnNode(DefnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDefnNode(DefnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDefsNode(DefsNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDefsNode(DefsNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDefsNode(DefsNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitDotNode(DotNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitDotNode(DotNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitDotNode(DotNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitEncodingNode(EncodingNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitEncodingNode(EncodingNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitEncodingNode(EncodingNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitEnsureNode(EnsureNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitEnsureNode(EnsureNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitEnsureNode(EnsureNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitEvStrNode(EvStrNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitEvStrNode(EvStrNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitEvStrNode(EvStrNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitFCallNode(FCallNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitFCallNode(FCallNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitFCallNode(FCallNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitFalseNode(FalseNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitFalseNode(FalseNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitFalseNode(FalseNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitFixnumNode(FixnumNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitFixnumNode(FixnumNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitFixnumNode(FixnumNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitFlipNode(FlipNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitFlipNode(FlipNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitFlipNode(FlipNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitFloatNode(FloatNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitFloatNode(FloatNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitFloatNode(FloatNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitForNode(ForNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitForNode(ForNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitForNode(ForNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitGlobalVarNode(GlobalVarNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitGlobalVarNode(GlobalVarNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitGlobalVarNode(GlobalVarNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitHashNode(HashNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitHashNode(HashNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitHashNode(HashNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitImplicitNilNode(ImplicitNilNode visited) {
        return visit(visited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitImplicitNilNode(ImplicitNilNode visited) {
        return visit(visited);
    }
=======
    @Override
    public Object visitImplicitNilNode(ImplicitNilNode visited) {
        return visit(visited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitInstAsgnNode(InstAsgnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitInstAsgnNode(InstAsgnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitInstAsgnNode(InstAsgnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitInstVarNode(InstVarNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitInstVarNode(InstVarNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitInstVarNode(InstVarNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitIfNode(IfNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitIfNode(IfNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitIfNode(IfNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitIterNode(IterNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitIterNode(IterNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitIterNode(IterNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitKeywordArgNode(KeywordArgNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitKeywordArgNode(KeywordArgNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitKeywordArgNode(KeywordArgNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitKeywordRestArgNode(KeywordRestArgNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitKeywordRestArgNode(KeywordRestArgNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitKeywordRestArgNode(KeywordRestArgNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitLambdaNode(LambdaNode visited) {
        return visit(visited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitLambdaNode(LambdaNode visited) {
        return visit(visited);
    }
=======
    @Override
    public Object visitLambdaNode(LambdaNode visited) {
        return visit(visited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitListNode(ListNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitListNode(ListNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitListNode(ListNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitLiteralNode(LiteralNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitLiteralNode(LiteralNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitLiteralNode(LiteralNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitLocalAsgnNode(LocalAsgnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitLocalAsgnNode(LocalAsgnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitLocalAsgnNode(LocalAsgnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitLocalVarNode(LocalVarNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitLocalVarNode(LocalVarNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitLocalVarNode(LocalVarNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitMatch2Node(Match2Node iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitMatch2Node(Match2Node iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitMatch2Node(Match2Node iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitMatch3Node(Match3Node iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitMatch3Node(Match3Node iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitMatch3Node(Match3Node iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitMatchNode(MatchNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitMatchNode(MatchNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitMatchNode(MatchNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitModuleNode(ModuleNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitModuleNode(ModuleNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitModuleNode(ModuleNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitMethodNameNode(MethodNameNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitMethodNameNode(MethodNameNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitMethodNameNode(MethodNameNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitNewlineNode(NewlineNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitNewlineNode(NewlineNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitNewlineNode(NewlineNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitNextNode(NextNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitNextNode(NextNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitNextNode(NextNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitNilNode(NilNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitNilNode(NilNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitNilNode(NilNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitNotNode(NotNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitNotNode(NotNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitNotNode(NotNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitNthRefNode(NthRefNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitNthRefNode(NthRefNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitNthRefNode(NthRefNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitOptArgNode(OptArgNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitOptArgNode(OptArgNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitOptArgNode(OptArgNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitOpAsgnNode(OpAsgnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitOpAsgnNode(OpAsgnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitOpAsgnNode(OpAsgnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitOrNode(OrNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitOrNode(OrNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitOrNode(OrNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitPreExeNode(PreExeNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitPreExeNode(PreExeNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitPreExeNode(PreExeNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitPostExeNode(PostExeNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitPostExeNode(PostExeNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitPostExeNode(PostExeNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRationalNode(RationalNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitRationalNode(RationalNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitRationalNode(RationalNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRedoNode(RedoNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitRedoNode(RedoNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitRedoNode(RedoNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRegexpNode(RegexpNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitRegexpNode(RegexpNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitRegexpNode(RegexpNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRequiredKeywordArgumentValueNode(RequiredKeywordArgumentValueNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
=======
    @Override
    public Object visitRequiredKeywordArgumentValueNode(RequiredKeywordArgumentValueNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRescueBodyNode(RescueBodyNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitRescueBodyNode(RescueBodyNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitRescueBodyNode(RescueBodyNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRescueNode(RescueNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitRescueNode(RescueNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitRescueNode(RescueNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRestArgNode(RestArgNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitRestArgNode(RestArgNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitRestArgNode(RestArgNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRetryNode(RetryNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitRetryNode(RetryNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitRetryNode(RetryNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitReturnNode(ReturnNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitReturnNode(ReturnNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitReturnNode(ReturnNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitRootNode(RootNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitRootNode(RootNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitRootNode(RootNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitSClassNode(SClassNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitSClassNode(SClassNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitSClassNode(SClassNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitSelfNode(SelfNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitSelfNode(SelfNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitSelfNode(SelfNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitSplatNode(SplatNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitSplatNode(SplatNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitSplatNode(SplatNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitStrNode(StrNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitStrNode(StrNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitStrNode(StrNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitSuperNode(SuperNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitSuperNode(SuperNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitSuperNode(SuperNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitSValueNode(SValueNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitSValueNode(SValueNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitSValueNode(SValueNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitSymbolNode(SymbolNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitSymbolNode(SymbolNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitSymbolNode(SymbolNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitSyntaxNode(SyntaxNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitSyntaxNode(SyntaxNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitSyntaxNode(SyntaxNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitToAryNode(ToAryNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitToAryNode(ToAryNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitToAryNode(ToAryNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitTrueNode(TrueNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitTrueNode(TrueNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitTrueNode(TrueNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitUnaryCallNode(UnaryCallNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitUnaryCallNode(UnaryCallNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitUnaryCallNode(UnaryCallNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitUndefNode(UndefNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitUndefNode(UndefNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitUndefNode(UndefNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitUntilNode(UntilNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitUntilNode(UntilNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitUntilNode(UntilNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitVAliasNode(VAliasNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitVAliasNode(VAliasNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitVAliasNode(VAliasNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitVCallNode(VCallNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitVCallNode(VCallNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitVCallNode(VCallNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitWhenNode(WhenNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitWhenNode(WhenNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitWhenNode(WhenNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitWhileNode(WhileNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitWhileNode(WhileNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitWhileNode(WhileNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitXStrNode(XStrNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitXStrNode(XStrNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitXStrNode(XStrNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitYieldNode(YieldNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitYieldNode(YieldNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitYieldNode(YieldNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitZArrayNode(ZArrayNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitZArrayNode(ZArrayNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitZArrayNode(ZArrayNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java

<<<<<<< /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/left.java
    public Object visitZSuperNode(ZSuperNode iVisited) {
        return visit(iVisited);
    }
||||||| /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/base.java
    public Object visitZSuperNode(ZSuperNode iVisited) {
        return visit(iVisited);
    }
=======
    @Override
    public Object visitZSuperNode(ZSuperNode iVisited) {
        return visit(iVisited);
    }
>>>>>>> /usr/src/app/output/jruby/jruby-parser/e6dae92cd4c87841431cbdfeffa34a4e05f3a2d9/src/org/jrubyparser/util/NoopVisitor.java/right.java
}
