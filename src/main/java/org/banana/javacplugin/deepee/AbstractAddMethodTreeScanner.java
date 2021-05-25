package org.banana.javacplugin.deepee;

import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

public abstract class AbstractAddMethodTreeScanner extends AbstractCustomTreeScanner {
    protected static final String CONTAINS_KEY = "containsKey";
    protected static final String OBJECT_ID = "id";
    protected static final String OBJECT_PARAM = "object";
    protected static final String OBJECT_PROPERTY_MAP = "propMap";
    protected static final String PROPERTY_NAME_PARAM = "propName";

    public AbstractAddMethodTreeScanner(Context context) {
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

    // globalPropertyMap.containsKey(id)
    protected JCTree.JCExpression ifMapContainsObjectId() {
        return createMethodInvocation(GLOBAL_PROPERTY_MAP, CONTAINS_KEY, createIdent(OBJECT_ID));
    }

    // globalPropertyMap.get(id)
    protected JCTree.JCMethodInvocation getObjectPropertyMap() {
        return createMapGet(GLOBAL_PROPERTY_MAP, OBJECT_ID);
    }

    // (Map<String, Object>)globalPropertyMap.get(id)
    protected JCTree.JCExpression getAndCastObjectPropertyMap() {
        return factory.TypeCast(
                createParametrizedType(MAP, STRING, OBJECT),
                getObjectPropertyMap()
        );
    }

    // Map<String, Object> propMap = (Map<String, Object>)globalPropertyMap.get(id);
    protected JCTree.JCVariableDecl getAndStoreObjectPropertyMap() {
        return getVarDefBuilder()
                .name(OBJECT_PROPERTY_MAP)
                .type(MAP, STRING, OBJECT)
                .init(getAndCastObjectPropertyMap())
                .build();
    }
}
