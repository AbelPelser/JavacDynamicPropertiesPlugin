package org.banana.javacplugin.myplugin;

import com.sun.source.tree.ClassTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import static org.banana.javacplugin.util.ListUtil.javacList;

public class AddMonkeyGetTreeScanner extends AbstractAddMonkeyGetSetTreeScanner {
    public AddMonkeyGetTreeScanner(Context context) {
        super(context);
    }

    // propMap.containsKey(propName)
    private JCTree.JCExpression ifPropMapContainsPropName() {
        return createMethodInvocation(PROP_MAP, CONTAINS_KEY, createIdent(PROP_NAME_PARAM));
    }

    // return propMap.get(propName);
    private JCTree.JCReturn returnValueFromPropMap() {
        return factory.Return(createMapGet(PROP_MAP, PROP_NAME_PARAM));
    }

    /* if (propMap.containsKey(propName)) {
     *     return propMap.get(propName);
     * }
     */
    private JCTree.JCIf ifPropNameIsKnown() {
        return getIfBuilder()
                .condition(ifPropMapContainsPropName())
                .ifBlock(returnValueFromPropMap())
                .build();
    }

    /* Map<String, Object> propMap = (Map<String, Object>)monkeyMap.get(id);
     * if (propMap.containsKey(propName)) {
     *     return propMap.get(propName);
     * }
     */
    private JCTree.JCBlock getPropMapAndReturnValueIfPresent() {
        List<JCTree.JCStatement> statements = javacList(
                getAndStoreObjectPropMap(),
                ifPropNameIsKnown()
        );
        return getBlockBuilder()
                .statements(statements)
                .build();
    }

    /* if (monkeyMap.containsKey(id)) {
     *     Map<String, Object> propMap = (Map<String, Object>)monkeyMap.get(id);
     *     if (propMap.containsKey(propName)) {
     *         return propMap.get(propName);
     *     }
     * }
     */
    private JCTree.JCIf ifObjectInMonkeyMap() {
        return getIfBuilder()
                .condition(ifMapContainsObjectId())
                .ifBlock(getPropMapAndReturnValueIfPresent())
                .build();
    }

    // "Attempting to read unknown property " + propName +  "!"
    private JCTree.JCBinary getPropertyUnknownErrorMessage() {
        return factory.Binary(
                JCTree.Tag.PLUS,
                factory.Literal("Attempting to read unknown property "),
                factory.Binary(
                        JCTree.Tag.PLUS,
                        createIdent(PROP_NAME_PARAM),
                        factory.Literal("!")
                )
        );
    }

    // throw new IllegalStateException("Attempting to read unknown property " + propName +  "!");
    private JCTree.JCThrow throwPropertyUnknownException() {
        return factory.Throw(
                getNewClassBuilder()
                        .clazz(ILLEGAL_STATE_EXCEPTION)
                        .args(javacList(getPropertyUnknownErrorMessage()))
                        .build()
        );
    }

    private JCTree.JCBlock createGetMethodBlock() {
        List<JCTree.JCStatement> statements = javacList(
                getAndStoreObjectId(),
                ifObjectInMonkeyMap(),
                throwPropertyUnknownException()
        );
        return getBlockBuilder()
                .statements(statements)
                .build();
    }

    // Object object, String propName
    private List<JCTree.JCVariableDecl> createGetMethodParams() {
        return javacList(
                createParameter(OBJECT, OBJECT_PARAM),
                createParameter(STRING, PROP_NAME_PARAM)
        );
    }

    /*  private static Object getMonkeyPatchProperty(Object object, String propName) {
     *      int id = object.hashCode();
     *      if (monkeyMap.containsKey(id)) {
     *          Map<String, Object> propMap = (Map<String, Object>)monkeyMap.get(id);
     *          if (propMap.containsKey(propName)) {
     *              return propMap.get(propName);
     *          }
     *      }
     *      throw new IllegalStateException("Attempting to read unknown property " + propName + "!");
     *  }
     */
    private JCTree.JCMethodDecl createGetMethod() {
        return getMethodDefBuilder()
                .modifiers(Flags.PRIVATE | Flags.STATIC)
                .name(GET_METHOD)
                .returnType(createIdent(OBJECT))
                .params(createGetMethodParams())
                .block(createGetMethodBlock())
                .build();
    }

    @Override
    public Void visitClass(ClassTree classTree, java.util.List<JCTree> unused) {
        if (classTree instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classTree;
            factory.at(classDecl.pos);
            JCTree.JCMethodDecl getMethod = createGetMethod();
            unused.add(getMethod);
            classDecl.defs = classDecl.defs.prependList(javacList(getMethod));
        }
        return super.visitClass(classTree, unused);
    }
}
