package org.banana.javacplugin.builder;

import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true, fluent = true)
public class VarDefBuilder extends AbstractTreeBuilder<JCTree.JCVariableDecl> {
    private long modifiers = 0;
    private String name;
    private JCTree.JCExpression type;
    private JCTree.JCExpression init = null;

    public VarDefBuilder(TreeMaker factory, Names symbolsTable) {
        super(factory, symbolsTable);
    }

    public VarDefBuilder type(JCTree.JCExpression type) {
        this.type = type;
        return this;
    }

    public VarDefBuilder type(TypeTag typeTag) {
        this.type = factory.TypeIdent(typeTag);
        return this;
    }

    public VarDefBuilder type(String typeName) {
        type = createIdent(typeName);
        return this;
    }

    public VarDefBuilder type(String typeName, String... paramArgTypeNames) {
        type = createParametrizedType(typeName, paramArgTypeNames);
        return this;
    }

    @Override
    public JCTree.JCVariableDecl build() {
        return factory.VarDef(factory.Modifiers(modifiers), createName(name), type, init);
    }
}
