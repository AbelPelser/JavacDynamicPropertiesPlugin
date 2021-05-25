package org.banana.javacplugin.deepee;

import com.sun.source.tree.ClassTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import static org.banana.javacplugin.util.ListUtil.javacList;

public class AddGetPropertyMethodTreeScanner extends AbstractAddMethodTreeScanner {
    public AddGetPropertyMethodTreeScanner(Context context) {
        super(context);
    }

    // propMap.containsKey(propName)
    private JCTree.JCExpression ifPropMapContainsPropName() {
        return createMethodInvocation(OBJECT_PROPERTY_MAP, CONTAINS_KEY, createIdent(PROPERTY_NAME_PARAM));
    }

    // return propMap.get(propName);
    private JCTree.JCReturn returnValueFromPropMap() {
        return factory.Return(createMapGet(OBJECT_PROPERTY_MAP, PROPERTY_NAME_PARAM));
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

    /* Map<String, Object> propMap = (Map<String, Object>)globalPropertyMap.get(id);
     * if (propMap.containsKey(propName)) {
     *     return propMap.get(propName);
     * }
     */
    private JCTree.JCBlock getObjectPropertyMapAndReturnValueIfPresent() {
        List<JCTree.JCStatement> statements = javacList(
                getAndStoreObjectPropertyMap(),
                ifPropNameIsKnown()
        );
        return getBlockBuilder()
                .statements(statements)
                .build();
    }

    /* if (globalPropertyMap.containsKey(id)) {
     *     Map<String, Object> propMap = (Map<String, Object>)globalPropertyMap.get(id);
     *     if (propMap.containsKey(propName)) {
     *         return propMap.get(propName);
     *     }
     * }
     */
    private JCTree.JCIf ifObjectInGlobalPropertyMap() {
        return getIfBuilder()
                .condition(ifMapContainsObjectId())
                .ifBlock(getObjectPropertyMapAndReturnValueIfPresent())
                .build();
    }

    // "Attempting to read unknown property " + propName +  "!"
    private JCTree.JCBinary getPropertyUnknownErrorMessage() {
        return factory.Binary(
                JCTree.Tag.PLUS,
                factory.Literal("Attempting to read unknown property "),
                factory.Binary(
                        JCTree.Tag.PLUS,
                        createIdent(PROPERTY_NAME_PARAM),
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
                ifObjectInGlobalPropertyMap(),
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
                createParameter(STRING, PROPERTY_NAME_PARAM)
        );
    }

    /*  private static Object getProperty(Object object, String propName) {
     *      int id = object.hashCode();
     *      if (globalPropertyMap.containsKey(id)) {
     *          Map<String, Object> propMap = (Map<String, Object>)globalPropertyMap.get(id);
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
                .name(GET_PROPERTY_METHOD)
                .returnType(createIdent(OBJECT))
                .params(createGetMethodParams())
                .block(createGetMethodBlock())
                .build();
    }

    @Override
    public Void visitClass(ClassTree classTree, Void unused) {
        if (classTree instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classTree;
            factory.at(classDecl.pos);
            JCTree.JCMethodDecl getMethod = createGetMethod();
            classDecl.defs = classDecl.defs.prependList(javacList(getMethod));
        }
        return super.visitClass(classTree, unused);
    }
}
