package org.banana.javacplugin.builder;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;

import static org.banana.javacplugin.util.ListUtil.javacList;

@Data
@Accessors(chain = true, fluent = true)
public abstract class AbstractTreeBuilder<T extends JCTree> {
    protected final TreeMaker factory;
    protected final Names symbolsTable;

    protected Name createName(String name) {
        return symbolsTable.fromString(name);
    }

    protected JCTree.JCIdent createIdent(String name) {
        return factory.Ident(createName(name));
    }

    protected JCTree.JCTypeApply createParametrizedType(String varTypeName, String... paramArgTypeNames) {
        JCTree.JCIdent[] paramArgs = Arrays.stream(paramArgTypeNames)
                .map(this::createIdent)
                .toArray(JCTree.JCIdent[]::new);
        return createParametrizedType(varTypeName, paramArgs);
    }

    protected JCTree.JCTypeApply createParametrizedType(String varType, JCTree.JCExpression... paramArgTypes) {
        return factory.TypeApply(
                createIdent(varType),
                javacList(paramArgTypes)
        );
    }

    protected JCTree.JCFieldAccess createMemberSelect(JCTree.JCExpression varName, String memberName) {
        return factory.Select(varName, createName(memberName));
    }

    protected JCTree.JCFieldAccess createMemberSelect(String varName, String memberName) {
        return createMemberSelect(createIdent(varName), memberName);
    }

    public abstract T build();
}
