package org.banana.javacplugin.debug;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;

public class PrintTreeScanner extends TreeScanner<Void, Void> {

    private final Log log;
    private final TreeMaker factory;
    private final Names symbolsTable;
    private int indent = 0;

    public PrintTreeScanner(Context context) {
        log = Log.instance(context);
        factory = TreeMaker.instance(context);
        symbolsTable = Names.instance(context);
    }

    private void p(Object o) {
        log.printRawLines(Log.WriterKind.NOTICE, getIndent() + o);
    }

    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private void defaultBeginAction(Tree tree) {
        p(tree.getKind());
        indent += 4;
    }

    private void defaultEndAction(Tree tree) {
        p("toString: " + tree.toString().replace("\n", "\\n"));
        indent -= 4;
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree compilationUnitTree, Void unused) {
        this.defaultBeginAction(compilationUnitTree);
        Void v = super.visitCompilationUnit(compilationUnitTree, unused);
        this.defaultEndAction(compilationUnitTree);
        return v;
    }

    @Override
    public Void visitImport(ImportTree importTree, Void unused) {
        this.defaultBeginAction(importTree);
        Void v = super.visitImport(importTree, unused);
        this.defaultEndAction(importTree);
        return v;
    }

    @Override
    public Void visitClass(ClassTree classTree, Void unused) {
        this.defaultBeginAction(classTree);
        Void v = super.visitClass(classTree, unused);
        this.defaultEndAction(classTree);
        return v;
    }

    @Override
    public Void visitMethod(MethodTree methodTree, Void unused) {
        this.defaultBeginAction(methodTree);
        Void v = super.visitMethod(methodTree, unused);
        p("name: " + methodTree.getName());
        this.defaultEndAction(methodTree);
        return v;
    }

    @Override
    public Void visitVariable(VariableTree variableTree, Void unused) {
        this.defaultBeginAction(variableTree);
        Void v = super.visitVariable(variableTree, unused);
        p("name: " + variableTree.getName());
        p("type: " + variableTree.getType());
        this.defaultEndAction(variableTree);
        return v;
    }

    @Override
    public Void visitEmptyStatement(EmptyStatementTree emptyStatementTree, Void unused) {
        this.defaultBeginAction(emptyStatementTree);
        Void v = super.visitEmptyStatement(emptyStatementTree, unused);
        this.defaultEndAction(emptyStatementTree);
        return v;
    }

    @Override
    public Void visitBlock(BlockTree blockTree, Void unused) {
        this.defaultBeginAction(blockTree);
        Void v = super.visitBlock(blockTree, unused);
        p("static: " + blockTree.isStatic());
        this.defaultEndAction(blockTree);
        return v;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree doWhileLoopTree, Void unused) {
        this.defaultBeginAction(doWhileLoopTree);
        Void v = super.visitDoWhileLoop(doWhileLoopTree, unused);
        this.defaultEndAction(doWhileLoopTree);
        return v;
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree whileLoopTree, Void unused) {
        this.defaultBeginAction(whileLoopTree);
        Void v = super.visitWhileLoop(whileLoopTree, unused);
        this.defaultEndAction(whileLoopTree);
        return v;
    }

    @Override
    public Void visitForLoop(ForLoopTree forLoopTree, Void unused) {
        this.defaultBeginAction(forLoopTree);
        Void v = super.visitForLoop(forLoopTree, unused);
        this.defaultEndAction(forLoopTree);
        return v;
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree enhancedForLoopTree, Void unused) {
        this.defaultBeginAction(enhancedForLoopTree);
        Void v = super.visitEnhancedForLoop(enhancedForLoopTree, unused);
        this.defaultEndAction(enhancedForLoopTree);
        return v;
    }

    @Override
    public Void visitLabeledStatement(LabeledStatementTree labeledStatementTree, Void unused) {
        this.defaultBeginAction(labeledStatementTree);
        Void v = super.visitLabeledStatement(labeledStatementTree, unused);
        this.defaultEndAction(labeledStatementTree);
        return v;
    }

    @Override
    public Void visitSwitch(SwitchTree switchTree, Void unused) {
        this.defaultBeginAction(switchTree);
        Void v = super.visitSwitch(switchTree, unused);
        this.defaultEndAction(switchTree);
        return v;
    }

    @Override
    public Void visitCase(CaseTree caseTree, Void unused) {
        this.defaultBeginAction(caseTree);
        Void v = super.visitCase(caseTree, unused);
        this.defaultEndAction(caseTree);
        return v;
    }

    @Override
    public Void visitSynchronized(SynchronizedTree synchronizedTree, Void unused) {
        this.defaultBeginAction(synchronizedTree);
        Void v = super.visitSynchronized(synchronizedTree, unused);
        this.defaultEndAction(synchronizedTree);
        return v;
    }

    @Override
    public Void visitTry(TryTree tryTree, Void unused) {
        this.defaultBeginAction(tryTree);
        Void v = super.visitTry(tryTree, unused);
        this.defaultEndAction(tryTree);
        return v;
    }

    @Override
    public Void visitCatch(CatchTree catchTree, Void unused) {
        this.defaultBeginAction(catchTree);
        Void v = super.visitCatch(catchTree, unused);
        this.defaultEndAction(catchTree);
        return v;
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree conditionalExpressionTree, Void unused) {
        this.defaultBeginAction(conditionalExpressionTree);
        Void v = super.visitConditionalExpression(conditionalExpressionTree, unused);
        this.defaultEndAction(conditionalExpressionTree);
        return v;
    }

    @Override
    public Void visitIf(IfTree ifTree, Void unused) {
        this.defaultBeginAction(ifTree);
        Void v = super.visitIf(ifTree, unused);
        this.defaultEndAction(ifTree);
        return v;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree expressionStatementTree, Void unused) {
        this.defaultBeginAction(expressionStatementTree);
        Void v = super.visitExpressionStatement(expressionStatementTree, unused);
        this.defaultEndAction(expressionStatementTree);
        return v;
    }

    @Override
    public Void visitBreak(BreakTree breakTree, Void unused) {
        this.defaultBeginAction(breakTree);
        Void v = super.visitBreak(breakTree, unused);
        this.defaultEndAction(breakTree);
        return v;
    }

    @Override
    public Void visitContinue(ContinueTree continueTree, Void unused) {
        this.defaultBeginAction(continueTree);
        Void v = super.visitContinue(continueTree, unused);
        this.defaultEndAction(continueTree);
        return v;
    }

    @Override
    public Void visitReturn(ReturnTree returnTree, Void unused) {
        this.defaultBeginAction(returnTree);
        Void v = super.visitReturn(returnTree, unused);
        this.defaultEndAction(returnTree);
        return v;
    }

    @Override
    public Void visitThrow(ThrowTree throwTree, Void unused) {
        this.defaultBeginAction(throwTree);
        Void v = super.visitThrow(throwTree, unused);
        this.defaultEndAction(throwTree);
        return v;
    }

    @Override
    public Void visitAssert(AssertTree assertTree, Void unused) {
        this.defaultBeginAction(assertTree);
        Void v = super.visitAssert(assertTree, unused);
        this.defaultEndAction(assertTree);
        return v;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree methodInvocationTree, Void unused) {
        this.defaultBeginAction(methodInvocationTree);
        Void v = super.visitMethodInvocation(methodInvocationTree, unused);
        this.defaultEndAction(methodInvocationTree);
        return v;
    }

    @Override
    public Void visitNewClass(NewClassTree newClassTree, Void unused) {
        this.defaultBeginAction(newClassTree);
        Void v = super.visitNewClass(newClassTree, unused);
        this.defaultEndAction(newClassTree);
        return v;
    }

    @Override
    public Void visitNewArray(NewArrayTree newArrayTree, Void unused) {
        this.defaultBeginAction(newArrayTree);
        Void v = super.visitNewArray(newArrayTree, unused);
        this.defaultEndAction(newArrayTree);
        return v;
    }

    @Override
    public Void visitLambdaExpression(LambdaExpressionTree lambdaExpressionTree, Void unused) {
        this.defaultBeginAction(lambdaExpressionTree);
        Void v = super.visitLambdaExpression(lambdaExpressionTree, unused);
        this.defaultEndAction(lambdaExpressionTree);
        return v;
    }

    @Override
    public Void visitParenthesized(ParenthesizedTree parenthesizedTree, Void unused) {
        this.defaultBeginAction(parenthesizedTree);
        Void v = super.visitParenthesized(parenthesizedTree, unused);
        this.defaultEndAction(parenthesizedTree);
        return v;
    }

    @Override
    public Void visitAssignment(AssignmentTree assignmentTree, Void unused) {
        this.defaultBeginAction(assignmentTree);
        Void v = super.visitAssignment(assignmentTree, unused);
        this.defaultEndAction(assignmentTree);
        return v;
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree compoundAssignmentTree, Void unused) {
        this.defaultBeginAction(compoundAssignmentTree);
        Void v = super.visitCompoundAssignment(compoundAssignmentTree, unused);
        this.defaultEndAction(compoundAssignmentTree);
        return v;
    }

    @Override
    public Void visitUnary(UnaryTree unaryTree, Void unused) {
        this.defaultBeginAction(unaryTree);
        Void v = super.visitUnary(unaryTree, unused);
        this.defaultEndAction(unaryTree);
        return v;
    }

    @Override
    public Void visitBinary(BinaryTree binaryTree, Void unused) {
        this.defaultBeginAction(binaryTree);
        Void v = super.visitBinary(binaryTree, unused);
        this.defaultEndAction(binaryTree);
        return v;
    }

    @Override
    public Void visitTypeCast(TypeCastTree typeCastTree, Void unused) {
        this.defaultBeginAction(typeCastTree);
        Void v = super.visitTypeCast(typeCastTree, unused);
        p("type: " + typeCastTree.getType());
        this.defaultEndAction(typeCastTree);
        return v;
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree instanceOfTree, Void unused) {
        this.defaultBeginAction(instanceOfTree);
        Void v = super.visitInstanceOf(instanceOfTree, unused);
        this.defaultEndAction(instanceOfTree);
        return v;
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree arrayAccessTree, Void unused) {
        this.defaultBeginAction(arrayAccessTree);
        Void v = super.visitArrayAccess(arrayAccessTree, unused);
        this.defaultEndAction(arrayAccessTree);
        return v;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, Void unused) {
        this.defaultBeginAction(memberSelectTree);
        Void v = super.visitMemberSelect(memberSelectTree, unused);
        p("identifier: " + memberSelectTree.getIdentifier());
        this.defaultEndAction(memberSelectTree);
        return v;
    }

    @Override
    public Void visitMemberReference(MemberReferenceTree memberReferenceTree, Void unused) {
        this.defaultBeginAction(memberReferenceTree);
        Void v = super.visitMemberReference(memberReferenceTree, unused);
        this.defaultEndAction(memberReferenceTree);
        return v;
    }

    @Override
    public Void visitIdentifier(IdentifierTree identifierTree, Void unused) {
        this.defaultBeginAction(identifierTree);
        Void v = super.visitIdentifier(identifierTree, unused);
        p("name: " + identifierTree.getName());
        this.defaultEndAction(identifierTree);
        return v;
    }

    @Override
    public Void visitLiteral(LiteralTree literalTree, Void unused) {
        this.defaultBeginAction(literalTree);
        Void v = super.visitLiteral(literalTree, unused);
        p("value: " + literalTree.getValue());
        this.defaultEndAction(literalTree);
        return v;
    }

    @Override
    public Void visitPrimitiveType(PrimitiveTypeTree primitiveTypeTree, Void unused) {
        this.defaultBeginAction(primitiveTypeTree);
        Void v = super.visitPrimitiveType(primitiveTypeTree, unused);
        p("primitive type kind: " + primitiveTypeTree.getPrimitiveTypeKind());
        this.defaultEndAction(primitiveTypeTree);
        return v;
    }

    @Override
    public Void visitArrayType(ArrayTypeTree arrayTypeTree, Void unused) {
        this.defaultBeginAction(arrayTypeTree);
        Void v = super.visitArrayType(arrayTypeTree, unused);
        p("type: " + arrayTypeTree.getType());
        this.defaultEndAction(arrayTypeTree);
        return v;
    }

    @Override
    public Void visitParameterizedType(ParameterizedTypeTree parameterizedTypeTree, Void unused) {
        this.defaultBeginAction(parameterizedTypeTree);
        Void v = super.visitParameterizedType(parameterizedTypeTree, unused);
        p("type: " + parameterizedTypeTree.getType());
        this.defaultEndAction(parameterizedTypeTree);
        return v;
    }

    @Override
    public Void visitUnionType(UnionTypeTree unionTypeTree, Void unused) {
        this.defaultBeginAction(unionTypeTree);
        Void v = super.visitUnionType(unionTypeTree, unused);
        this.defaultEndAction(unionTypeTree);
        return v;
    }

    @Override
    public Void visitIntersectionType(IntersectionTypeTree intersectionTypeTree, Void unused) {
        this.defaultBeginAction(intersectionTypeTree);
        Void v = super.visitIntersectionType(intersectionTypeTree, unused);
        this.defaultEndAction(intersectionTypeTree);
        return v;
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree typeParameterTree, Void unused) {
        this.defaultBeginAction(typeParameterTree);
        Void v = super.visitTypeParameter(typeParameterTree, unused);
        p("name: " + typeParameterTree.getName());
        this.defaultEndAction(typeParameterTree);
        return v;
    }

    @Override
    public Void visitWildcard(WildcardTree wildcardTree, Void unused) {
        this.defaultBeginAction(wildcardTree);
        Void v = super.visitWildcard(wildcardTree, unused);
        p("bound: " + wildcardTree.getBound());
        this.defaultEndAction(wildcardTree);
        return v;
    }

    @Override
    public Void visitModifiers(ModifiersTree modifiersTree, Void unused) {
        this.defaultBeginAction(modifiersTree);
        Void v = super.visitModifiers(modifiersTree, unused);
        p("flags: " + modifiersTree.getFlags());
        this.defaultEndAction(modifiersTree);
        return v;
    }

    @Override
    public Void visitAnnotation(AnnotationTree annotationTree, Void unused) {
        this.defaultBeginAction(annotationTree);
        Void v = super.visitAnnotation(annotationTree, unused);
        this.defaultEndAction(annotationTree);
        return v;
    }

    @Override
    public Void visitAnnotatedType(AnnotatedTypeTree annotatedTypeTree, Void unused) {
        this.defaultBeginAction(annotatedTypeTree);
        Void v = super.visitAnnotatedType(annotatedTypeTree, unused);
        this.defaultEndAction(annotatedTypeTree);
        return v;
    }

    @Override
    public Void visitOther(Tree tree, Void unused) {
        this.defaultBeginAction(tree);
        Void v = super.visitOther(tree, unused);
        this.defaultEndAction(tree);
        return v;
    }

    @Override
    public Void visitErroneous(ErroneousTree erroneousTree, Void unused) {
        this.defaultBeginAction(erroneousTree);
        Void v = super.visitErroneous(erroneousTree, unused);
        p("ERRONEOUS");
        this.defaultEndAction(erroneousTree);
        return v;
    }
}
