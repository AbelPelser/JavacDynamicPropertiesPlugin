package org.banana.javacplugin.builder;

import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.sun.tools.javac.code.TypeTag.VOID;
import static org.banana.javacplugin.util.ListUtil.emptyJavacList;

@Setter
@Accessors(chain = true, fluent = true)
public class MethodDefBuilder extends AbstractTreeBuilder<JCTree.JCMethodDecl> {
    private long modifiers = 0;
    private String name;
    private JCTree.JCExpression returnType;
    private List<JCTree.JCTypeParameter> typeParameters = emptyJavacList();
    private List<JCTree.JCVariableDecl> params = emptyJavacList();
    private List<JCTree.JCExpression> thrown = emptyJavacList();
    private JCTree.JCBlock block;
    private JCTree.JCExpression defaultValue = null;

    public MethodDefBuilder(TreeMaker factory, Names symbolsTable) {
        super(factory, symbolsTable);
        block = new BlockBuilder(factory, symbolsTable).build();
        returnType(VOID);
    }

    public MethodDefBuilder returnType(JCTree.JCExpression returnType) {
        this.returnType = returnType;
        return this;
    }

    public MethodDefBuilder returnType(TypeTag typeTag) {
        this.returnType = factory.TypeIdent(typeTag);
        return this;
    }

    @Override
    public JCTree.JCMethodDecl build() {
        return factory.MethodDef(
                factory.Modifiers(modifiers),
                createName(name),
                returnType,
                typeParameters,
                params,
                thrown,
                block,
                defaultValue
        );
    }
}
