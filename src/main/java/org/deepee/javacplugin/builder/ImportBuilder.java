package org.deepee.javacplugin.builder;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true, fluent = true)
public class ImportBuilder extends AbstractTreeBuilder<JCTree.JCImport> {
    private String packageName;
    private String name;
    private boolean staticImport = false;

    public ImportBuilder(TreeMaker factory, Names symbolsTable) {
        super(factory, symbolsTable);
    }

    @Override
    public JCTree.JCImport build() {
        return factory.Import(createMemberSelect(packageName, name), staticImport);
    }
}
