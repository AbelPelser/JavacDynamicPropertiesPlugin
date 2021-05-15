package org.banana.javacplugin.myplugin;

import com.sun.source.tree.ClassTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;
import org.banana.javacplugin.builder.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.banana.javacplugin.util.TreeMakerUtil.javacList;

public class AbstractBananaTreeScanner extends TreeScanner<Void, Void> {
    protected static final String JAVA_LANG_REFLECT = "java.lang.reflect";
    protected static final String JAVA_UTIL = "java.util";

    protected static final String CLASS = Class.class.getSimpleName();
    protected static final String FIELD = Field.class.getSimpleName();
    protected static final String HASH_MAP = HashMap.class.getSimpleName();
    protected static final String ILLEGAL_STATE_EXCEPTION = IllegalStateException.class.getSimpleName();
    protected static final String MAP = Map.class.getSimpleName();
    protected static final String MODIFIER = Modifier.class.getSimpleName();
    protected static final String OBJECT = Object.class.getSimpleName();
    protected static final String STRING = String.class.getSimpleName();
    protected static final String SYSTEM = System.class.getSimpleName();

    protected static final String GET = "get";
    protected static final String PUT = "put";
    protected static final String SET = "set";

    protected final TreeMaker factory;
    protected final Names symbolsTable;
    protected final Log log;
    protected static final int RANDOM_NR = Math.abs(new Random().nextInt());
    protected static final String MONKEY_MAP = "__monkeyMap" + RANDOM_NR;

    public AbstractBananaTreeScanner(Context context) {
        factory = TreeMaker.instance(context);
        symbolsTable = Names.instance(context);
        log = Log.instance(context);
    }

    protected BlockBuilder getBlockBuilder() {
        return new BlockBuilder(factory, symbolsTable);
    }

    protected CatchBuilder getCatchBuilder() {
        return new CatchBuilder(factory, symbolsTable);
    }

    protected IfBuilder getIfBuilder() {
        return new IfBuilder(factory, symbolsTable);
    }

    protected ImportBuilder getImportBuilder() {
        return new ImportBuilder(factory, symbolsTable);
    }

    protected MethodDefBuilder getMethodDefBuilder() {
        return new MethodDefBuilder(factory, symbolsTable);
    }

    protected NewClassBuilder getNewClassBuilder() {
        return new NewClassBuilder(factory, symbolsTable);
    }

    protected TryBuilder getTryBuilder() {
        return new TryBuilder(factory, symbolsTable);
    }

    protected VarDefBuilder getVarDefBuilder() {
        return new VarDefBuilder(factory, symbolsTable);
    }

    protected Name createName(String name) {
        return symbolsTable.fromString(name);
    }

    protected JCTree.JCIdent createIdent(String name) {
        return factory.Ident(createName(name));
    }

    protected void p(Object o) {
        log.printRawLines(Log.WriterKind.NOTICE, String.valueOf(o));
    }

    protected JCTree.JCImport createImport(String packageName, String name, boolean staticImport) {
        return getImportBuilder().packageName(packageName).name(name).staticImport(staticImport).build();
    }

    protected JCTree.JCImport createImport(String packageName, String name) {
        return createImport(packageName, name, false);
    }

    protected JCTree.JCMethodInvocation createMapGet(String mapName, String value) {
        return createMethodInvocation(mapName, GET, createIdent(value));
    }

    protected JCTree.JCFieldAccess createMemberSelect(JCTree.JCExpression varName, String memberName) {
        return factory.Select(varName, createName(memberName));
    }

    protected JCTree.JCFieldAccess createMemberSelect(String varName, String memberName) {
        return createMemberSelect(createIdent(varName), memberName);
    }

    protected JCTree.JCMethodInvocation createMethodInvocation(JCTree.JCExpression method, JCTree.JCExpression... args) {
        return factory.Apply(null, method, javacList(args));
    }

    protected JCTree.JCMethodInvocation createMethodInvocation(String objectName, String methodName, JCTree.JCExpression... args) {
        return createMethodInvocation(createMemberSelect(objectName, methodName), args);
    }

    protected JCTree.JCExpressionStatement exprToStmt(JCTree.JCExpression expression) {
        return factory.Exec(expression);
    }

    protected JCTree.JCTypeApply createParametrizedType(String varType, JCTree.JCExpression... paramArgTypes) {
        return factory.TypeApply(
                createIdent(varType),
                javacList(paramArgTypes)
        );
    }

    protected JCTree.JCTypeApply createParametrizedType(String varTypeName, String... paramArgTypeNames) {
        JCTree.JCIdent[] paramArgs = Arrays.stream(paramArgTypeNames)
                .map(this::createIdent)
                .toArray(JCTree.JCIdent[]::new);
        return createParametrizedType(varTypeName, paramArgs);
    }

    protected JCTree.JCVariableDecl createParameter(String type, String name) {
        return getVarDefBuilder()
                .modifiers(Flags.PARAMETER)
                .type(type)
                .name(name)
                .build();
    }
}
