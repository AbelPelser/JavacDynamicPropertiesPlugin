package org.banana.javacplugin.sample;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import org.banana.javacplugin.Positive;
import org.banana.javacplugin.builder.NewClassBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.banana.javacplugin.util.TreeMakerUtil.javacList;

public class SampleTreeScanner extends TreeScanner<Void, Void> {

    private static final String POSITIVE_ANNOTATION = Positive.class.getSimpleName();
    private static final Set<String> TARGET_TYPES = Stream
            .of(byte.class, short.class, char.class, int.class, long.class, float.class, double.class)
            .map(Class::getName)
            .collect(Collectors.toSet());
    private final TreeMaker factory;
    private final Names symbolsTable;

    public SampleTreeScanner(Context context) {
        factory = TreeMaker.instance(context);
        symbolsTable = Names.instance(context);
    }

    private Name createName(String name) {
        return symbolsTable.fromString(name);
    }

    private JCTree.JCIdent createIdent(String name) {
        return factory.Ident(createName(name));
    }

    private JCTree.JCBinary createIfCondition(VariableTree parameter) {
        Name parameterId = createName(parameter.getName().toString());
        return factory.Binary(
                JCTree.Tag.LE,
                factory.Ident(parameterId),
                factory.Literal(TypeTag.INT, 0)
        );
    }

    private JCBinary createErrorMessageExpr(VariableTree parameter) {
        String parameterName = parameter.getName().toString();
        Name parameterSymbol = createName(parameterName);
        String errorMessagePrefix = String.format(
                "Argument '%s' of type %s is marked by @%s but got '",
                parameterName, parameter.getType(), POSITIVE_ANNOTATION);
        String errorMessageSuffix = "' for it";

        JCBinary errorPlusPrefix = factory.Binary(
                Tag.PLUS,
                factory.Literal(TypeTag.CLASS, errorMessagePrefix),
                factory.Ident(parameterSymbol)
        );
        return factory.Binary(Tag.PLUS, errorPlusPrefix, factory.Literal(TypeTag.CLASS, errorMessageSuffix));
    }

    private JCTree.JCBlock createIfBlock(VariableTree parameter) {
        JCBinary errorMessageExpr = createErrorMessageExpr(parameter);
        JCNewClass exceptionInstance = new NewClassBuilder(factory, symbolsTable)
                .clazz(IllegalArgumentException.class.getSimpleName())
                .args(javacList(errorMessageExpr))
                .build();
        JCThrow exceptionThrow = factory.Throw(exceptionInstance);
        return factory.Block(0, javacList(exceptionThrow));
    }

    private JCTree.JCIf createCheck(VariableTree parameter) {
        JCParens ifCondition = factory.Parens(createIfCondition(parameter));
        JCBlock ifBlock = createIfBlock(parameter);
        return factory.at(((JCTree) parameter).pos).If(ifCondition, ifBlock, null);
    }

    private boolean shouldInstrument(VariableTree parameter) {
        return TARGET_TYPES.contains(parameter.getType().toString())
                && parameter.getModifiers().getAnnotations()
                .stream()
                .map(AnnotationTree::getAnnotationType)
                .map(Object::toString)
                .anyMatch(POSITIVE_ANNOTATION::equals);
    }

    @Override
    public Void visitMethod(MethodTree method, Void v) {
        List<VariableTree> parametersToInstrument = method.getParameters()
                .stream()
                .filter(this::shouldInstrument)
                .collect(Collectors.toList());
        if (!parametersToInstrument.isEmpty()) {
            Collections.reverse(parametersToInstrument);
            parametersToInstrument.forEach(p -> addCheck(method, p));
        }
        return super.visitMethod(method, v);
    }

    private void addCheck(MethodTree method, VariableTree parameter) {
        JCTree.JCIf check = createCheck(parameter);
        JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();
        body.stats = body.stats.prepend(check);
    }
}
