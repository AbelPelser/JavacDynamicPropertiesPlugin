package org.banana.javacplugin.deepee;

import com.sun.source.tree.ClassTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import static org.banana.javacplugin.util.ListUtil.javacList;

public class AddSetPropertyMethodTreeScanner extends AbstractAddMethodTreeScanner {
    private static final String VALUE_PARAM = "value";

    public AddSetPropertyMethodTreeScanner(Context context) {
        super(context);
    }

    // new HashMap<String, Object>()
    private JCTree.JCNewClass createObjectPropertyMap() {
        return getNewClassBuilder()
                .clazz(createParametrizedType(HASH_MAP, STRING, OBJECT))
                .build();
    }

    // globalPropertyMap.put(id, new HashMap<String, Object>());
    private JCTree.JCExpressionStatement createAndStoreObjectPropertyMap() {
        return exprToStmt(createMethodInvocation(GLOBAL_PROPERTY_MAP, PUT, createIdent(OBJECT_ID), createObjectPropertyMap()));
    }

    /* if (!globalPropertyMap.containsKey(id)) {
     *     globalPropertyMap.put(id, new HashMap<String, Object>());
     * }
     */
    private JCTree.JCIf ifObjectNotInGlobalPropertyMap() {
        return getIfBuilder()
                .condition(factory.Unary(JCTree.Tag.NOT, ifMapContainsObjectId()))
                .ifBlock(createAndStoreObjectPropertyMap())
                .build();
    }

    // propMap.put(propName, value);
    private JCTree.JCExpressionStatement storeValueInObjectPropertyMap() {
        return exprToStmt(createMethodInvocation(OBJECT_PROPERTY_MAP, PUT, createIdent(PROPERTY_NAME_PARAM), createIdent(VALUE_PARAM)));
    }

    // return value;
    private JCTree.JCReturn returnValue() {
        return factory.Return(createIdent(VALUE_PARAM));
    }

    /* {
     *     int id = object.hashCode();
     *     if (!globalPropertyMap.containsKey(id)) {
     *         globalPropertyMap.put(id, new HashMap<String, Object>());
     *     }
     *     Map<String, Object> propMap = (Map<String, Object>)globalPropertyMap.get(id);
     *     propMap.put(propName, value);
     *     return value;
     * }
     */
    private JCTree.JCBlock createSetMethodBlock() {
        List<JCTree.JCStatement> statements = javacList(
                getAndStoreObjectId(),
                ifObjectNotInGlobalPropertyMap(),
                getAndStoreObjectPropertyMap(),
                storeValueInObjectPropertyMap(),
                returnValue()
        );
        return getBlockBuilder()
                .statements(statements)
                .build();
    }

    // Object object, String propName, Object value
    private List<JCTree.JCVariableDecl> createSetMethodParams() {
        return javacList(
                createParameter(OBJECT, OBJECT_PARAM),
                createParameter(STRING, PROPERTY_NAME_PARAM),
                createParameter(OBJECT, VALUE_PARAM)
        );
    }

    /* private static Object setProperty(Object object, String propName, Object value) {
     *     int id = object.hashCode();
     *     if (!globalPropertyMap.containsKey(id)) {
     *         globalPropertyMap.put(id, new HashMap<String, Object>());
     *     }
     *     Map<String, Object> propMap = (Map<String, Object>)globalPropertyMap.get(id);
     *     propMap.put(propName, value);
     *     return value;
     * }
     */
    private JCTree.JCMethodDecl createSetMethod() {
        return getMethodDefBuilder()
                .modifiers(Flags.PRIVATE | Flags.STATIC)
                .returnType(createIdent(OBJECT))
                .name(SET_PROPERTY_METHOD)
                .params(createSetMethodParams())
                .block(createSetMethodBlock())
                .build();
    }

    @Override
    public Void visitClass(ClassTree classTree, Void unused) {
        if (classTree instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classTree;
            factory.at(classDecl.pos);
            JCTree.JCMethodDecl setMethod = createSetMethod();
            classDecl.defs = classDecl.defs.prependList(javacList(setMethod));
        }
        return super.visitClass(classTree, unused);
    }
}
