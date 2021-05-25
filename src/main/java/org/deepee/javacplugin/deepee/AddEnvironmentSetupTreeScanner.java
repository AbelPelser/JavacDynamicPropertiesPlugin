package org.deepee.javacplugin.deepee;

import com.sun.source.tree.ClassTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import static com.sun.tools.javac.code.BoundKind.UNBOUND;
import static org.deepee.javacplugin.util.ListUtil.javacList;

public class AddEnvironmentSetupTreeScanner extends AbstractCustomTreeScanner {
    private static final String GET_DECLARED_FIELD = "getDeclaredField";
    private static final String MODIFIERS_FIELD = "modifiersField";
    private static final String OLD_ENV_MAP = "oldMap";
    private static final String PROCESS_ENVIRONMENT = "processEnvironment";
    private static final String UNMODIFIABLE_MAP_FIELD = "unmodifiableMapField";
    private static final String SETUP_METHOD = "__setupEnvironment" + RANDOM_NR;

    public AddEnvironmentSetupTreeScanner(Context context) {
        super(context);
    }

    // System.getenv()
    private JCTree.JCMethodInvocation getEnvMap() {
        return createMethodInvocation(SYSTEM, "getenv");
    }

    // Map<String, String> oldMap = System.getEnv();
    private JCTree.JCVariableDecl getAndStoreEnvMap() {
        return getVarDefBuilder()
                .name(OLD_ENV_MAP)
                .type(MAP, STRING, STRING)
                .init(getEnvMap())
                .build();
    }

    // Class<?>
    private JCTree.JCTypeApply unboundedClassType() {
        return createParametrizedType(CLASS, factory.Wildcard(factory.TypeBoundKind(UNBOUND), null));
    }

    // Class.forName("java.lang.ProcessEnvironment")
    private JCTree.JCMethodInvocation getProcessEnvironmentClassName() {
        return createMethodInvocation(CLASS, "forName", factory.Literal("java.lang.ProcessEnvironment"));
    }

    // Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
    private JCTree.JCVariableDecl getAndStoreProcessEnvironmentClassName() {
        return getVarDefBuilder()
                .name(PROCESS_ENVIRONMENT)
                .type(unboundedClassType())
                .init(getProcessEnvironmentClassName())
                .build();
    }

    private JCTree.JCMethodInvocation getDeclaredField(JCTree.JCExpression object, String fieldName) {
        return createMethodInvocation(createMemberSelect(object, GET_DECLARED_FIELD), factory.Literal(fieldName));
    }

    // processEnvironment.getDeclaredField("theUnmodifiableEnvironment")
    private JCTree.JCMethodInvocation getUnmodifiableEnvironmentField() {
        return createMethodInvocation(PROCESS_ENVIRONMENT, GET_DECLARED_FIELD, factory.Literal("theUnmodifiableEnvironment"));
    }

    // Field unmodifiableMapField = processEnvironment.getDeclaredField("theUnmodifiableEnvironment");
    private JCTree.JCVariableDecl getAndStoreUnmodifiableEnvironmentField() {
        return getVarDefBuilder()
                .name(UNMODIFIABLE_MAP_FIELD)
                .type(FIELD)
                .init(getUnmodifiableEnvironmentField())
                .build();
    }

    // varName.setAccessible(true);
    private JCTree.JCExpressionStatement setAccessibleTrue(String varName) {
        return exprToStmt(createMethodInvocation(varName, "setAccessible", factory.Literal(true)));
    }

    // Field.class.getDeclaredField("modifiers");
    private JCTree.JCMethodInvocation getFieldClassModifiers() {
        return getDeclaredField(createMemberSelect(FIELD, "class"), "modifiers");
    }

    // Field modifiersField = Field.class.getDeclaredField("modifiers");
    private JCTree.JCVariableDecl getAndStoreFieldClassModifiers() {
        return getVarDefBuilder()
                .name(MODIFIERS_FIELD)
                .type(FIELD)
                .init(getFieldClassModifiers())
                .build();
    }

    // modifiersField.setInt(unmodifiableMapField, unmodifiableMapField.getModifiers() & ~Modifier.FINAL);
    private JCTree.JCExpressionStatement unsetFinalModifier() {
        JCTree.JCFieldAccess jcFieldAccess = createMemberSelect(MODIFIERS_FIELD, "setInt");

        // unmodifiableMapField.getModifiers()
        JCTree.JCMethodInvocation getModifiersCall = createMethodInvocation(UNMODIFIABLE_MAP_FIELD, "getModifiers");

        // Modifier.FINAL
        JCTree.JCFieldAccess finalModifier = createMemberSelect("Modifier", "FINAL");

        // ~Modifier.FINAL
        JCTree.JCUnary notFinal = factory.Unary(JCTree.Tag.COMPL, finalModifier);

        // unmodifiableMapField.getModifiers() & ~Modifier.FINAL
        JCTree.JCBinary unsetFinalModifier = factory.Binary(JCTree.Tag.BITAND, getModifiersCall, notFinal);

        return exprToStmt(createMethodInvocation(jcFieldAccess, createIdent(UNMODIFIABLE_MAP_FIELD), unsetFinalModifier));
    }

    // new HashMap<Object, Object>(oldMap)
    private JCTree.JCNewClass copyOldEnvMap() {
        return getNewClassBuilder()
                .clazz(createParametrizedType(HASH_MAP, OBJECT, OBJECT))
                .args(javacList(createIdent(OLD_ENV_MAP)))
                .build();
    }

    // Collections.synchronizedMap(new HashMap<Object, Object>(oldMap))
    private JCTree.JCMethodInvocation copyAndSyncOldEnvMap() {
        return createMethodInvocation(COLLECTIONS, SYNC_MAP, copyOldEnvMap());
    }

    // globalPropertyMap = Collections.synchronizedMap(new HashMap<Object, Object>(oldMap));
    private JCTree.JCExpressionStatement cloneAndStoreEnvMap() {
        return exprToStmt(factory.Assign(createIdent(GLOBAL_PROPERTY_MAP), copyAndSyncOldEnvMap()));
    }

    // unmodifiableMapField.set(null, globalPropertyMap);
    private JCTree.JCExpressionStatement cloneAndReplaceEnvMap() {
        return exprToStmt(
                createMethodInvocation(
                        UNMODIFIABLE_MAP_FIELD,
                        SET,
                        factory.Literal(TypeTag.BOT, null),
                        createIdent(GLOBAL_PROPERTY_MAP)
                )
        );
    }

    /*  Map<String, String> oldMap = System.getenv();
     *  Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
     *  Field unmodifiableMapField = processEnvironment.getDeclaredField("theUnmodifiableEnvironment");
     *  unmodifiableMapField.setAccessible(true);
     *  Field modifiersField = Field.class.getDeclaredField("modifiers");
     *  modifiersField.setAccessible(true);
     *  modifiersField.setInt(unmodifiableMapField, unmodifiableMapField.getModifiers() & ~Modifier.FINAL);
     *  globalPropertyMap = Collections.synchronizedMap(new HashMap<Object, Object>(oldMap));
     *  unmodifiableMapField.set(null, globalPropertyMap);
     */
    private List<JCTree.JCStatement> createSetupTryBlockStmts() {
        return javacList(
                getAndStoreEnvMap(),
                getAndStoreProcessEnvironmentClassName(),
                getAndStoreUnmodifiableEnvironmentField(),
                setAccessibleTrue(UNMODIFIABLE_MAP_FIELD),
                getAndStoreFieldClassModifiers(),
                setAccessibleTrue(MODIFIERS_FIELD),
                unsetFinalModifier(),
                cloneAndStoreEnvMap(),
                cloneAndReplaceEnvMap()
        );
    }

    private JCTree.JCBlock createSetupTryBlock() {
        return getBlockBuilder()
                .statements(createSetupTryBlockStmts())
                .build();
    }

    private JCTree.JCBlock createSetupCatchBlock() {
        return getBlockBuilder()
                //.statements(javacList(factory.Return(null)))
                .build();
    }

    /* catch (Exception ignore) {
     * }
     */
    private JCTree.JCCatch createSetupCatch() {
        return getCatchBuilder()
                .catchBlock(createSetupCatchBlock())
                .caughtType(Exception.class)
                .caughtName("ignore")
                .build();
    }

    /* try {
     *      Map<String, String> oldMap = System.getenv();
     *      Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
     *      Field unmodifiableMapField = processEnvironment.getDeclaredField("theUnmodifiableEnvironment");
     *      unmodifiableMapField.setAccessible(true);
     *      Field modifiersField = Field.class.getDeclaredField("modifiers");
     *      modifiersField.setAccessible(true);
     *      modifiersField.setInt(unmodifiableMapField, unmodifiableMapField.getModifiers() & ~Modifier.FINAL);
     *      globalPropertyMap = Collections.synchronizedMap(new HashMap<Object, Object>(oldMap));
     *      unmodifiableMapField.set(null, globalPropertyMap);
     *  } catch (Exception ignore) {
     *  }
     */
    private JCTree.JCTry createSetupTry() {
        return getTryBuilder()
                .tryBlock(createSetupTryBlock())
                .finallyBlock(null)
                .catches(javacList(createSetupCatch()))
                .build();
    }

    private JCTree.JCBlock createSetupMethodBlock() {
        return getBlockBuilder()
                .statements(javacList(createSetupTry()))
                .build();
    }

    /* private static void setupEnvironment() {
     *     try {
     *          Map<String, String> oldMap = System.getenv();
     *          Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
     *          Field unmodifiableMapField = processEnvironment.getDeclaredField("theUnmodifiableEnvironment");
     *          unmodifiableMapField.setAccessible(true);
     *          Field modifiersField = Field.class.getDeclaredField("modifiers");
     *          modifiersField.setAccessible(true);
     *          modifiersField.setInt(unmodifiableMapField, unmodifiableMapField.getModifiers() & ~Modifier.FINAL);
     *          globalPropertyMap = Collections.synchronizedMap(new HashMap<Object, Object>(oldMap));
     *          unmodifiableMapField.set(null, globalPropertyMap);
     *      } catch (Exception ignore) {
     *      }
     * }
     */
    private JCTree.JCMethodDecl createSetupMethod() {
        return getMethodDefBuilder()
                .modifiers(Flags.PRIVATE | Flags.STATIC)
                .block(createSetupMethodBlock())
                .name(SETUP_METHOD)
                .build();
    }

    /* static {
     *     setupEnvironment();
     * }
     */
    private JCTree.JCBlock createSetupMethodCall() {
        JCTree.JCExpressionStatement methodCallStmt = exprToStmt(createMethodInvocation(createIdent(SETUP_METHOD)));
        return getBlockBuilder()
                .statements(javacList(methodCallStmt))
                .flags(Flags.STATIC)
                .build();
    }

    // private static Map<Object, Object> globalPropertyMap;
    private JCTree.JCVariableDecl createEnvMapVar() {
        return getVarDefBuilder()
                .modifiers(Flags.PRIVATE | Flags.STATIC)
                .name(GLOBAL_PROPERTY_MAP)
                .type(MAP, OBJECT, OBJECT)
                .build();
    }

    @Override
    public Void visitClass(ClassTree classTree, Void unused) {
        if (classTree instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classTree;
            factory.at(classDecl.pos);
            List<JCTree> newMembers = javacList(
                    createEnvMapVar(),
                    createSetupMethodCall(),
                    createSetupMethod()
            );
            classDecl.defs = classDecl.defs.prependList(newMembers);
        }
        return super.visitClass(classTree, unused);
    }
}
