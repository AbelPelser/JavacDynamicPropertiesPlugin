package org.banana.javacplugin.builder;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Accessors(chain = true, fluent = true)
public class CatchBuilder extends AbstractTreeBuilder<JCTree.JCCatch> {
    private JCTree.JCBlock catchBlock;
    private Class<? extends Throwable> caughtType;
    private String caughtName = "e";

    public CatchBuilder(TreeMaker factory, Names symbolsTable) {
        super(factory, symbolsTable);
        catchBlock = new BlockBuilder(factory, symbolsTable).build();
    }

    @Override
    public JCTree.JCCatch build() {
        JCTree.JCVariableDecl caughtVarDecl = factory.VarDef(
                factory.Modifiers(Flags.PARAMETER),
                createName(caughtName),
                factory.Ident(createName(caughtType.getSimpleName())),
                null
        );
        return factory.Catch(caughtVarDecl, catchBlock);
    }
}
