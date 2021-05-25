package org.deepee.javacplugin.util;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;

// Custom TreePathScanner that allows changing the current path
public class CustomTreePathScanner<R, P> extends TreeScanner<R, P> {
    private TreePath path;

    public CustomTreePathScanner() {
    }

    public R scan(TreePath var1, P var2) {
        this.path = var1;

        R var3;
        try {
            var3 = var1.getLeaf().accept(this, var2);
        } finally {
            this.path = null;
        }

        return var3;
    }

    public R scan(Tree var1, P var2) {
        if (var1 == null) {
            return null;
        } else {
            TreePath var3 = this.path;
            this.path = new TreePath(this.path, var1);

            R var4;
            try {
                var4 = var1.accept(this, var2);
            } finally {
                this.path = var3;
            }

            return var4;
        }
    }

    public TreePath getCurrentPath() {
        return this.path;
    }

    // Only custom addition:
    protected void setPath(TreePath path) {
        this.path = path;
    }
}
