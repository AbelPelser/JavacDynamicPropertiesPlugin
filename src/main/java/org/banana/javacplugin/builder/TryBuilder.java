package org.banana.javacplugin.builder;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import lombok.Setter;
import lombok.experimental.Accessors;

import static org.banana.javacplugin.util.ListUtil.emptyJavacList;


@Setter
@Accessors(chain = true, fluent = true)
public class TryBuilder extends AbstractTreeBuilder<JCTree.JCTry> {
    private JCTree.JCBlock tryBlock;
    private List<JCTree.JCCatch> catches = emptyJavacList();
    private JCTree.JCBlock finallyBlock;

    public TryBuilder(TreeMaker factory, Names symbolsTable) {
        super(factory, symbolsTable);
        tryBlock = new BlockBuilder(factory, symbolsTable).build();
    }

    @Override
    public JCTree.JCTry build() {
        return factory.Try(tryBlock, catches, finallyBlock);
    }
}
