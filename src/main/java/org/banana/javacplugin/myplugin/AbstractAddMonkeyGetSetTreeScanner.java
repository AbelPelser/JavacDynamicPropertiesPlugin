package org.banana.javacplugin.myplugin;

import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

public abstract class AbstractAddMonkeyGetSetTreeScanner extends AbstractBananaTreeScanner {
    protected static final String CONTAINS_KEY = "containsKey";
    protected static final String OBJECT_ID = "id";
    protected static final String OBJECT_PARAM = "object";
    protected static final String PROP_MAP = "propMap";
    protected static final String PROP_NAME_PARAM = "propName";

    public AbstractAddMonkeyGetSetTreeScanner(Context context) {
        super(context);
    }

    // object.hashCode()
    protected JCTree.JCMethodInvocation getObjectId() {
        return createMethodInvocation(OBJECT_PARAM, "hashCode");
    }

    //int id = object.hashCode();
    protected JCTree.JCVariableDecl getAndStoreObjectId() {
        return getVarDefBuilder()
                .name(OBJECT_ID)
                .type(TypeTag.INT)
                .init(getObjectId())
                .build();
    }

    // monkeyMap.containsKey(id)
    protected JCTree.JCExpression ifMapContainsObjectId() {
        return createMethodInvocation(MONKEY_MAP, CONTAINS_KEY, createIdent(OBJECT_ID));
    }

    // monkeyMap.get(id)
    protected JCTree.JCMethodInvocation getPropMap() {
        return createMapGet(MONKEY_MAP, OBJECT_ID);
    }

    // (Map<String, Object>)monkeyMap.get(id)
    protected JCTree.JCExpression getAndCastPropMap() {
        return factory.TypeCast(
                createParametrizedType(MAP, STRING, OBJECT),
                getPropMap()
        );
    }

    // Map<String, Object> propMap = (Map<String, Object>)monkeyMap.get(id);
    protected JCTree.JCVariableDecl getAndStoreObjectPropMap() {
        return getVarDefBuilder()
                .name(PROP_MAP)
                .type(MAP, STRING, OBJECT)
                .init(getAndCastPropMap())
                .build();
    }
}
