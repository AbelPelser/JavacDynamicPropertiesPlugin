package org.deepee.javacplugin.builder;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import lombok.Setter;
import lombok.experimental.Accessors;

import static org.deepee.javacplugin.util.ListUtil.emptyJavacList;

@Setter
@Accessors(chain = true, fluent = true)
public class NewClassBuilder extends AbstractTreeBuilder<JCTree.JCNewClass> {
    private JCTree.JCExpression outer = null;
    private List<JCTree.JCExpression> typeArgs = emptyJavacList();
    private JCTree.JCExpression clazz;
    private List<JCTree.JCExpression> args = emptyJavacList();
    private JCTree.JCClassDecl def = null;

    public NewClassBuilder(TreeMaker factory, Names symbolsTable) {
        super(factory, symbolsTable);
    }

    public NewClassBuilder clazz(JCTree.JCExpression clazz) {
        this.clazz = clazz;
        return this;
    }

    public NewClassBuilder clazz(String className) {
        this.clazz = createIdent(className);
        return this;
    }

    @Override
    public JCTree.JCNewClass build() {
        return factory.NewClass(outer, typeArgs, clazz, args, def);
    }
}
