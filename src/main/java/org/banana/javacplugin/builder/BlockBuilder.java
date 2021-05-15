package org.banana.javacplugin.builder;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import lombok.Setter;
import lombok.experimental.Accessors;

import static org.banana.javacplugin.util.TreeMakerUtil.emptyJavacList;


@Setter
@Accessors(chain = true, fluent = true)
public class BlockBuilder extends AbstractTreeBuilder<JCTree.JCBlock> {
    private long flags = 0;
    private List<JCTree.JCStatement> statements = emptyJavacList();

    public BlockBuilder(TreeMaker factory, Names symbolsTable) {
        super(factory, symbolsTable);
    }

    @Override
    public JCTree.JCBlock build() {
        return factory.Block(flags, statements);
    }
}
