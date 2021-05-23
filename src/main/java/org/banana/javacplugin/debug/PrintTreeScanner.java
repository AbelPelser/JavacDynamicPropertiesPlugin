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
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;

import java.util.List;

public class PrintTreeScanner extends TreeScanner<Void, List<JCTree>> {

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
        Type type = ((JCTree) tree).type;
        p("Type: " + type);
        if (type != null) {
            p("Type class: " + type.getClass().getName());
        }
        indent += 4;
    }

    private void defaultEndAction(Tree tree) {
        p("toString: " + tree.toString().replace("\n", "\\n"));
        indent -= 4;
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree compilationUnitTree, List<JCTree> unused) {
        this.defaultBeginAction(compilationUnitTree);
        p("visitCompilationUnit(CompilationUnitTree compilationUnitTree, List<JCTree> unused)");
        JCTree.JCCompilationUnit jcTree = (JCTree.JCCompilationUnit) compilationUnitTree;
        Void v = super.visitCompilationUnit(compilationUnitTree, unused);
        this.defaultEndAction(compilationUnitTree);
        return v;
    }

    @Override
    public Void visitImport(ImportTree importTree, List<JCTree> unused) {
        this.defaultBeginAction(importTree);
        p("visitImport(ImportTree importTree, List<JCTree> unused)");
        Void v = super.visitImport(importTree, unused);
        this.defaultEndAction(importTree);
        return v;
    }

    @Override
    public Void visitClass(ClassTree classTree, List<JCTree> unused) {
        this.defaultBeginAction(classTree);
        p("visitClass(ClassTree classTree, List<JCTree> unused)");
        Void v = super.visitClass(classTree, unused);
        this.defaultEndAction(classTree);
        return v;
    }

    @Override
    public Void visitMethod(MethodTree methodTree, List<JCTree> unused) {
        this.defaultBeginAction(methodTree);
        p("visitMethod(MethodTree methodTree, List<JCTree> unused)");
        Void v = super.visitMethod(methodTree, unused);
        p("name: " + methodTree.getName());
        this.defaultEndAction(methodTree);
        return v;
    }

    @Override
    public Void visitVariable(VariableTree variableTree, List<JCTree> unused) {
        this.defaultBeginAction(variableTree);
        p("visitVariable(VariableTree variableTree, List<JCTree> unused)");
        Void v = super.visitVariable(variableTree, unused);
        p("name: " + variableTree.getName());
        p("type: " + variableTree.getType());
        this.defaultEndAction(variableTree);
        return v;
    }

    @Override
    public Void visitEmptyStatement(EmptyStatementTree emptyStatementTree, List<JCTree> unused) {
        this.defaultBeginAction(emptyStatementTree);
        p("visitEmptyStatement(EmptyStatementTree emptyStatementTree, List<JCTree> unused)");
        Void v = super.visitEmptyStatement(emptyStatementTree, unused);
        this.defaultEndAction(emptyStatementTree);
        return v;
    }

    @Override
    public Void visitBlock(BlockTree blockTree, List<JCTree> unused) {
        this.defaultBeginAction(blockTree);
        p("visitBlock(BlockTree blockTree, List<JCTree> unused)");
        Void v = super.visitBlock(blockTree, unused);
        p("static: " + blockTree.isStatic());
        this.defaultEndAction(blockTree);
        return v;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree doWhileLoopTree, List<JCTree> unused) {
        this.defaultBeginAction(doWhileLoopTree);
        p("visitDoWhileLoop(DoWhileLoopTree doWhileLoopTree, List<JCTree> unused)");
        Void v = super.visitDoWhileLoop(doWhileLoopTree, unused);
        this.defaultEndAction(doWhileLoopTree);
        return v;
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree whileLoopTree, List<JCTree> unused) {
        this.defaultBeginAction(whileLoopTree);
        p("visitWhileLoop(WhileLoopTree whileLoopTree, List<JCTree> unused)");
        Void v = super.visitWhileLoop(whileLoopTree, unused);
        this.defaultEndAction(whileLoopTree);
        return v;
    }

    @Override
    public Void visitForLoop(ForLoopTree forLoopTree, List<JCTree> unused) {
        this.defaultBeginAction(forLoopTree);
        p("visitForLoop(ForLoopTree forLoopTree, List<JCTree> unused)");
        Void v = super.visitForLoop(forLoopTree, unused);
        this.defaultEndAction(forLoopTree);
        return v;
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree enhancedForLoopTree, List<JCTree> unused) {
        this.defaultBeginAction(enhancedForLoopTree);
        p("visitEnhancedForLoop(EnhancedForLoopTree enhancedForLoopTree, List<JCTree> unused)");
        Void v = super.visitEnhancedForLoop(enhancedForLoopTree, unused);
        this.defaultEndAction(enhancedForLoopTree);
        return v;
    }

    @Override
    public Void visitLabeledStatement(LabeledStatementTree labeledStatementTree, List<JCTree> unused) {
        this.defaultBeginAction(labeledStatementTree);
        p("visitLabeledStatement(LabeledStatementTree labeledStatementTree, List<JCTree> unused)");
        Void v = super.visitLabeledStatement(labeledStatementTree, unused);
        this.defaultEndAction(labeledStatementTree);
        return v;
    }

    @Override
    public Void visitSwitch(SwitchTree switchTree, List<JCTree> unused) {
        this.defaultBeginAction(switchTree);
        p("visitSwitch(SwitchTree switchTree, List<JCTree> unused)");
        Void v = super.visitSwitch(switchTree, unused);
        this.defaultEndAction(switchTree);
        return v;
    }

    @Override
    public Void visitCase(CaseTree caseTree, List<JCTree> unused) {
        this.defaultBeginAction(caseTree);
        p("visitCase(CaseTree caseTree, List<JCTree> unused)");
        Void v = super.visitCase(caseTree, unused);
        this.defaultEndAction(caseTree);
        return v;
    }

    @Override
    public Void visitSynchronized(SynchronizedTree synchronizedTree, List<JCTree> unused) {
        this.defaultBeginAction(synchronizedTree);
        p("visitSynchronized(SynchronizedTree synchronizedTree, List<JCTree> unused)");
        Void v = super.visitSynchronized(synchronizedTree, unused);
        this.defaultEndAction(synchronizedTree);
        return v;
    }

    @Override
    public Void visitTry(TryTree tryTree, List<JCTree> unused) {
        this.defaultBeginAction(tryTree);
        p("visitTry(TryTree tryTree, List<JCTree> unused)");
        Void v = super.visitTry(tryTree, unused);
        this.defaultEndAction(tryTree);
        return v;
    }

    @Override
    public Void visitCatch(CatchTree catchTree, List<JCTree> unused) {
        this.defaultBeginAction(catchTree);
        p("visitCatch(CatchTree catchTree, List<JCTree> unused)");
        Void v = super.visitCatch(catchTree, unused);
        this.defaultEndAction(catchTree);
        return v;
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree conditionalExpressionTree, List<JCTree> unused) {
        this.defaultBeginAction(conditionalExpressionTree);
        p("visitConditionalExpression(ConditionalExpressionTree conditionalExpressionTree, List<JCTree> unused)");
        Void v = super.visitConditionalExpression(conditionalExpressionTree, unused);
        this.defaultEndAction(conditionalExpressionTree);
        return v;
    }

    @Override
    public Void visitIf(IfTree ifTree, List<JCTree> unused) {
        this.defaultBeginAction(ifTree);
        p("visitIf(IfTree ifTree, List<JCTree> unused)");
        Void v = super.visitIf(ifTree, unused);
        this.defaultEndAction(ifTree);
        return v;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree expressionStatementTree, List<JCTree> unused) {
        this.defaultBeginAction(expressionStatementTree);
        p("visitExpressionStatement(ExpressionStatementTree expressionStatementTree, List<JCTree> unused)");
        Void v = super.visitExpressionStatement(expressionStatementTree, unused);
        this.defaultEndAction(expressionStatementTree);
        return v;
    }

    @Override
    public Void visitBreak(BreakTree breakTree, List<JCTree> unused) {
        this.defaultBeginAction(breakTree);
        p("visitBreak(BreakTree breakTree, List<JCTree> unused)");
        Void v = super.visitBreak(breakTree, unused);
        this.defaultEndAction(breakTree);
        return v;
    }

    @Override
    public Void visitContinue(ContinueTree continueTree, List<JCTree> unused) {
        this.defaultBeginAction(continueTree);
        p("visitContinue(ContinueTree continueTree, List<JCTree> unused)");
        Void v = super.visitContinue(continueTree, unused);
        this.defaultEndAction(continueTree);
        return v;
    }

    @Override
    public Void visitReturn(ReturnTree returnTree, List<JCTree> unused) {
        this.defaultBeginAction(returnTree);
        p("visitReturn(ReturnTree returnTree, List<JCTree> unused)");
        Void v = super.visitReturn(returnTree, unused);
        this.defaultEndAction(returnTree);
        return v;
    }

    @Override
    public Void visitThrow(ThrowTree throwTree, List<JCTree> unused) {
        this.defaultBeginAction(throwTree);
        p("visitThrow(ThrowTree throwTree, List<JCTree> unused)");
        Void v = super.visitThrow(throwTree, unused);
        this.defaultEndAction(throwTree);
        return v;
    }

    @Override
    public Void visitAssert(AssertTree assertTree, List<JCTree> unused) {
        this.defaultBeginAction(assertTree);
        p("visitAssert(AssertTree assertTree, List<JCTree> unused)");
        Void v = super.visitAssert(assertTree, unused);
        this.defaultEndAction(assertTree);
        return v;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree methodInvocationTree, List<JCTree> unused) {
        this.defaultBeginAction(methodInvocationTree);
        p("visitMethodInvocation(MethodInvocationTree methodInvocationTree, List<JCTree> unused)");
        Void v = super.visitMethodInvocation(methodInvocationTree, unused);
        this.defaultEndAction(methodInvocationTree);
        return v;
    }

    @Override
    public Void visitNewClass(NewClassTree newClassTree, List<JCTree> unused) {
        this.defaultBeginAction(newClassTree);
        p("visitNewClass(NewClassTree newClassTree, List<JCTree> unused)");
        Void v = super.visitNewClass(newClassTree, unused);
        this.defaultEndAction(newClassTree);
        return v;
    }

    @Override
    public Void visitNewArray(NewArrayTree newArrayTree, List<JCTree> unused) {
        this.defaultBeginAction(newArrayTree);
        p("visitNewArray(NewArrayTree newArrayTree, List<JCTree> unused)");
        Void v = super.visitNewArray(newArrayTree, unused);
        this.defaultEndAction(newArrayTree);
        return v;
    }

    @Override
    public Void visitLambdaExpression(LambdaExpressionTree lambdaExpressionTree, List<JCTree> unused) {
        this.defaultBeginAction(lambdaExpressionTree);
        p("visitLambdaExpression(LambdaExpressionTree lambdaExpressionTree, List<JCTree> unused)");
        Void v = super.visitLambdaExpression(lambdaExpressionTree, unused);
        this.defaultEndAction(lambdaExpressionTree);
        return v;
    }

    @Override
    public Void visitParenthesized(ParenthesizedTree parenthesizedTree, List<JCTree> unused) {
        this.defaultBeginAction(parenthesizedTree);
        p("visitParenthesized(ParenthesizedTree parenthesizedTree, List<JCTree> unused)");
        Void v = super.visitParenthesized(parenthesizedTree, unused);
        this.defaultEndAction(parenthesizedTree);
        return v;
    }

    @Override
    public Void visitAssignment(AssignmentTree assignmentTree, List<JCTree> unused) {
        this.defaultBeginAction(assignmentTree);
        p("visitAssignment(AssignmentTree assignmentTree, List<JCTree> unused)");
        Void v = super.visitAssignment(assignmentTree, unused);
        this.defaultEndAction(assignmentTree);
        return v;
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree compoundAssignmentTree, List<JCTree> unused) {
        this.defaultBeginAction(compoundAssignmentTree);
        p("visitCompoundAssignment(CompoundAssignmentTree compoundAssignmentTree, List<JCTree> unused)");
        Void v = super.visitCompoundAssignment(compoundAssignmentTree, unused);
        this.defaultEndAction(compoundAssignmentTree);
        return v;
    }

    @Override
    public Void visitUnary(UnaryTree unaryTree, List<JCTree> unused) {
        this.defaultBeginAction(unaryTree);
        p("visitUnary(UnaryTree unaryTree, List<JCTree> unused)");
        Void v = super.visitUnary(unaryTree, unused);
        this.defaultEndAction(unaryTree);
        return v;
    }

    @Override
    public Void visitBinary(BinaryTree binaryTree, List<JCTree> unused) {
        this.defaultBeginAction(binaryTree);
        p("visitBinary(BinaryTree binaryTree, List<JCTree> unused)");
        Void v = super.visitBinary(binaryTree, unused);
        this.defaultEndAction(binaryTree);
        return v;
    }

    @Override
    public Void visitTypeCast(TypeCastTree typeCastTree, List<JCTree> unused) {
        this.defaultBeginAction(typeCastTree);
        p("visitTypeCast(TypeCastTree typeCastTree, List<JCTree> unused)");
        Void v = super.visitTypeCast(typeCastTree, unused);
        p("type: " + typeCastTree.getType());
        this.defaultEndAction(typeCastTree);
        return v;
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree instanceOfTree, List<JCTree> unused) {
        this.defaultBeginAction(instanceOfTree);
        p("visitInstanceOf(InstanceOfTree instanceOfTree, List<JCTree> unused)");
        Void v = super.visitInstanceOf(instanceOfTree, unused);
        this.defaultEndAction(instanceOfTree);
        return v;
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree arrayAccessTree, List<JCTree> unused) {
        this.defaultBeginAction(arrayAccessTree);
        p("visitArrayAccess(ArrayAccessTree arrayAccessTree, List<JCTree> unused)");
        Void v = super.visitArrayAccess(arrayAccessTree, unused);
        this.defaultEndAction(arrayAccessTree);
        return v;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, List<JCTree> unused) {
        this.defaultBeginAction(memberSelectTree);
        p("visitMemberSelect(MemberSelectTree memberSelectTree, List<JCTree> unused)");
        Void v = super.visitMemberSelect(memberSelectTree, unused);
        p("identifier: " + memberSelectTree.getIdentifier());
        this.defaultEndAction(memberSelectTree);
        return v;
    }

    @Override
    public Void visitMemberReference(MemberReferenceTree memberReferenceTree, List<JCTree> unused) {
        this.defaultBeginAction(memberReferenceTree);
        p("visitMemberReference(MemberReferenceTree memberReferenceTree, List<JCTree> unused)");
        Void v = super.visitMemberReference(memberReferenceTree, unused);
        this.defaultEndAction(memberReferenceTree);
        return v;
    }

    @Override
    public Void visitIdentifier(IdentifierTree identifierTree, List<JCTree> unused) {
        this.defaultBeginAction(identifierTree);
        p("visitIdentifier(IdentifierTree identifierTree, List<JCTree> unused)");
        Void v = super.visitIdentifier(identifierTree, unused);
        p("name: " + identifierTree.getName());
        this.defaultEndAction(identifierTree);
        return v;
    }

    @Override
    public Void visitLiteral(LiteralTree literalTree, List<JCTree> unused) {
        this.defaultBeginAction(literalTree);
        p("visitLiteral(LiteralTree literalTree, List<JCTree> unused)");
        Void v = super.visitLiteral(literalTree, unused);
        p("value: " + literalTree.getValue());
        this.defaultEndAction(literalTree);
        return v;
    }

    @Override
    public Void visitPrimitiveType(PrimitiveTypeTree primitiveTypeTree, List<JCTree> unused) {
        this.defaultBeginAction(primitiveTypeTree);
        p("visitPrimitiveType(PrimitiveTypeTree primitiveTypeTree, List<JCTree> unused)");
        Void v = super.visitPrimitiveType(primitiveTypeTree, unused);
        p("primitive type kind: " + primitiveTypeTree.getPrimitiveTypeKind());
        this.defaultEndAction(primitiveTypeTree);
        return v;
    }

    @Override
    public Void visitArrayType(ArrayTypeTree arrayTypeTree, List<JCTree> unused) {
        this.defaultBeginAction(arrayTypeTree);
        p("visitArrayType(ArrayTypeTree arrayTypeTree, List<JCTree> unused)");
        Void v = super.visitArrayType(arrayTypeTree, unused);
        p("type: " + arrayTypeTree.getType());
        this.defaultEndAction(arrayTypeTree);
        return v;
    }

    @Override
    public Void visitParameterizedType(ParameterizedTypeTree parameterizedTypeTree, List<JCTree> unused) {
        this.defaultBeginAction(parameterizedTypeTree);
        p("visitParameterizedType(ParameterizedTypeTree parameterizedTypeTree, List<JCTree> unused)");
        Void v = super.visitParameterizedType(parameterizedTypeTree, unused);
        p("type: " + parameterizedTypeTree.getType());
        this.defaultEndAction(parameterizedTypeTree);
        return v;
    }

    @Override
    public Void visitUnionType(UnionTypeTree unionTypeTree, List<JCTree> unused) {
        this.defaultBeginAction(unionTypeTree);
        p("visitUnionType(UnionTypeTree unionTypeTree, List<JCTree> unused)");
        Void v = super.visitUnionType(unionTypeTree, unused);
        this.defaultEndAction(unionTypeTree);
        return v;
    }

    @Override
    public Void visitIntersectionType(IntersectionTypeTree intersectionTypeTree, List<JCTree> unused) {
        this.defaultBeginAction(intersectionTypeTree);
        p("visitIntersectionType(IntersectionTypeTree intersectionTypeTree, List<JCTree> unused)");
        Void v = super.visitIntersectionType(intersectionTypeTree, unused);
        this.defaultEndAction(intersectionTypeTree);
        return v;
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree typeParameterTree, List<JCTree> unused) {
        this.defaultBeginAction(typeParameterTree);
        p("visitTypeParameter(TypeParameterTree typeParameterTree, List<JCTree> unused)");
        Void v = super.visitTypeParameter(typeParameterTree, unused);
        p("name: " + typeParameterTree.getName());
        this.defaultEndAction(typeParameterTree);
        return v;
    }

    @Override
    public Void visitWildcard(WildcardTree wildcardTree, List<JCTree> unused) {
        this.defaultBeginAction(wildcardTree);
        p("visitWildcard(WildcardTree wildcardTree, List<JCTree> unused)");
        Void v = super.visitWildcard(wildcardTree, unused);
        p("bound: " + wildcardTree.getBound());
        this.defaultEndAction(wildcardTree);
        return v;
    }

    @Override
    public Void visitModifiers(ModifiersTree modifiersTree, List<JCTree> unused) {
        this.defaultBeginAction(modifiersTree);
        p("visitModifiers(ModifiersTree modifiersTree, List<JCTree> unused)");
        Void v = super.visitModifiers(modifiersTree, unused);
        p("flags: " + modifiersTree.getFlags());
        this.defaultEndAction(modifiersTree);
        return v;
    }

    @Override
    public Void visitAnnotation(AnnotationTree annotationTree, List<JCTree> unused) {
        this.defaultBeginAction(annotationTree);
        p("visitAnnotation(AnnotationTree annotationTree, List<JCTree> unused)");
        Void v = super.visitAnnotation(annotationTree, unused);
        this.defaultEndAction(annotationTree);
        return v;
    }

    @Override
    public Void visitAnnotatedType(AnnotatedTypeTree annotatedTypeTree, List<JCTree> unused) {
        this.defaultBeginAction(annotatedTypeTree);
        p("visitAnnotatedType(AnnotatedTypeTree annotatedTypeTree, List<JCTree> unused)");
        Void v = super.visitAnnotatedType(annotatedTypeTree, unused);
        this.defaultEndAction(annotatedTypeTree);
        return v;
    }

    @Override
    public Void visitOther(Tree tree, List<JCTree> unused) {
        this.defaultBeginAction(tree);
        p("visitOther(Tree tree, List<JCTree> unused)");
        Void v = super.visitOther(tree, unused);
        this.defaultEndAction(tree);
        return v;
    }

    @Override
    public Void visitErroneous(ErroneousTree erroneousTree, List<JCTree> unused) {
        this.defaultBeginAction(erroneousTree);
        p("visitErroneous(ErroneousTree erroneousTree, List<JCTree> unused)");
        Void v = super.visitErroneous(erroneousTree, unused);
        p("ERRONEOUS");
        this.defaultEndAction(erroneousTree);
        return v;
    }
}
