package org.deepee.javacplugin.builder;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import lombok.Setter;
import lombok.experimental.Accessors;

import static org.deepee.javacplugin.util.ListUtil.javacList;

@Setter
@Accessors(chain = true, fluent = true)
public class IfBuilder extends AbstractTreeBuilder<JCTree.JCIf> {

    private JCTree.JCExpression condition;
    private JCTree.JCBlock ifBlock;
    private JCTree.JCBlock elseBlock;

    public IfBuilder(TreeMaker factory, Names symbolsTable) {
        super(factory, symbolsTable);
        ifBlock = new BlockBuilder(factory, symbolsTable).build();
    }

    public IfBuilder ifBlock(JCTree.JCBlock ifBlock) {
        this.ifBlock = ifBlock;
        return this;
    }

    public IfBuilder ifBlock(JCTree.JCStatement... ifStatement) {
        this.ifBlock = new BlockBuilder(factory, symbolsTable).statements(javacList(ifStatement)).build();
        return this;
    }

    public IfBuilder elseBlock(JCTree.JCBlock elseBlock) {
        this.elseBlock = elseBlock;
        return this;
    }

    public IfBuilder elseBlock(JCTree.JCStatement... elseStatement) {
        this.elseBlock = new BlockBuilder(factory, symbolsTable).statements(javacList(elseStatement)).build();
        return this;
    }

    @Override
    public JCTree.JCIf build() {
        return factory.If(condition, ifBlock, elseBlock);
    }
}
