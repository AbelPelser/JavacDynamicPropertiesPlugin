package org.banana.javacplugin.myplugin;

import com.sun.source.tree.ClassTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import static org.banana.javacplugin.util.TreeMakerUtil.javacList;

public class AddMonkeySetTreeScanner extends AbstractAddMonkeyGetSetTreeScanner {
    private static final String SET_METHOD = "__setMonkeyPatchProperty" + RANDOM_NR;
    private static final String VALUE_PARAM = "value";

    public AddMonkeySetTreeScanner(Context context) {
        super(context);
    }

    // new HashMap<String, Object>()
    private JCTree.JCNewClass createPropMap() {
        return getNewClassBuilder()
                .clazz(createParametrizedType(HASH_MAP, STRING, OBJECT))
                .build();
    }

    // monkeyMap.put(id, new HashMap<String, Object>());
    private JCTree.JCExpressionStatement createAndStorePropMap() {
        return exprToStmt(createMethodInvocation(MONKEY_MAP, PUT, createIdent(OBJECT_ID), createPropMap()));
    }

    /* if (!monkeyMap.containsKey(id)) {
     *     monkeyMap.put(id, new HashMap<String, Object>());
     * }
     */
    private JCTree.JCIf ifObjectNotInMonkeyMap() {
        return getIfBuilder()
                .condition(factory.Unary(JCTree.Tag.NOT, ifMapContainsObjectId()))
                .ifBlock(createAndStorePropMap())
                .build();
    }

    // propMap.put(propName, value);
    private JCTree.JCExpressionStatement storeValueInPropMap() {
        return exprToStmt(createMethodInvocation(PROP_MAP, PUT, createIdent(PROP_NAME_PARAM), createIdent(VALUE_PARAM)));
    }

    /* {
     *     int id = object.hashCode();
     *     if (!monkeyMap.containsKey(id)) {
     *         monkeyMap.put(id, new HashMap<String, Object>());
     *     }
     *     Map<String, Object> propMap = (Map<String, Object>)monkeyMap.get(id);
     *     propMap.put(propName, value);
     * }
     */
    private JCTree.JCBlock createSetMethodBlock() {
        List<JCTree.JCStatement> statements = javacList(
                getAndStoreObjectId(),
                ifObjectNotInMonkeyMap(),
                getAndStoreObjectPropMap(),
                storeValueInPropMap()
        );
        return getBlockBuilder()
                .statements(statements)
                .build();
    }

    // Object object, String propName, Object value
    private List<JCTree.JCVariableDecl> createSetMethodParams() {
        return javacList(
                createParameter(OBJECT, OBJECT_PARAM),
                createParameter(STRING, PROP_NAME_PARAM),
                createParameter(OBJECT, VALUE_PARAM)
        );
    }

    /* private static void setMonkeyPatchProperty(Object object, String propName, Object value) {
     *     int id = object.hashCode();
     *     if (!monkeyMap.containsKey(id)) {
     *         monkeyMap.put(id, new HashMap<String, Object>());
     *     }
     *     Map<String, Object> propMap = (Map<String, Object>)monkeyMap.get(id);
     *     propMap.put(propName, value);
     * }
     */
    private JCTree.JCMethodDecl createSetMethod() {
        return getMethodDefBuilder()
                .modifiers(Flags.PRIVATE | Flags.STATIC)
                .name(SET_METHOD)
                .params(createSetMethodParams())
                .block(createSetMethodBlock())
                .build();
    }

    @Override
    public Void visitClass(ClassTree classTree, Void unused) {
        if (classTree instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classTree;
            factory.at(classDecl.pos);
            classDecl.defs = classDecl.defs.prependList(javacList(createSetMethod()));
        }
        return super.visitClass(classTree, unused);
    }
}