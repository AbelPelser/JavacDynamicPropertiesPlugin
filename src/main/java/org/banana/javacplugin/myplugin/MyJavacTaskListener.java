package org.banana.javacplugin.myplugin;

import com.rits.cloning.Cloner;
import com.rits.cloning.IDumpCloned;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.*;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.*;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import org.banana.javacplugin.debug.SimplePrintTreeScanner;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.banana.javacplugin.util.ListUtil.javacList;


public class MyJavacTaskListener implements TaskListener {
    private final Context context;
    private final Log log;
    private final Enter enter;
    private final List<JCTree> newMembersToEnter = new ArrayList<>();
    private final Attr attr;
    private final Todo todo;
    private final Check chk;
    private Env<AttrContext> env;
    // By making this a member of this class, the Cloner will find and clone it
    private JCTree.JCCompilationUnit jcCompilationUnit;

    private final ReplaceMemberSelectTreeScanner replaceMemberSelectTreeScanner;
    private final SimplePrintTreeScanner simplePrintTreeScanner;

    public MyJavacTaskListener(Context context) {
        this.context = context;
        log = Log.instance(context);
        enter = Enter.instance(context);
        attr = Attr.instance(context);
        todo = Todo.instance(context);
        chk = Check.instance(context);
        replaceMemberSelectTreeScanner = new ReplaceMemberSelectTreeScanner(context);
        simplePrintTreeScanner = new SimplePrintTreeScanner(context);
    }

    private void runTree(CompilationUnitTree compilationUnit, TreeScanner<?, List<JCTree>> treeScanner) {
//        compilationUnit.accept(treeScanner, newMembers);
        treeScanner.scan(compilationUnit, newMembersToEnter);
    }

    private Env<AttrContext> cloneEnv(Env<AttrContext> env) {
        Cloner cloner = new Cloner();
        cloner.setDumpClonedClasses(true);
        return cloner.deepClone(env);
    }

    private void updateEnvIfPresent() {
        Env<AttrContext> newEnv = Optional.of(todo.iterator())
                .filter(Iterator::hasNext)
                .map(Iterator::next)
                .map(this::cloneEnv)
                .orElse(null);
        if (newEnv != null) {
            if (env != null) {
                log.printRawLines("got another env!");
            }
            env = newEnv;
        }
    }

    private void tmpDoCloneExperiment() {
        Cloner cloner = new Cloner();
        cloner.setDumpClonedClasses(true);
        Context newContext = cloner.deepClone(context);
        context.dump();
        newContext.dump();
    }

    @Override
    public void started(TaskEvent e) {
        log.printRawLines("STARTED " + e.getKind());
        CompilationUnitTree compilationUnit = e.getCompilationUnit();
        JCTree.JCCompilationUnit jcCompilationUnit = (JCTree.JCCompilationUnit) compilationUnit;
        if (e.getKind() == TaskEvent.Kind.ANALYZE) {
//            log.printRawLines("ANALYZE begun, doing stuff!");
//            performAttribute(jcCompilationUnit);
//            attr.attrib(env);
//            runTree(jcCompilationUnit, replaceMemberSelectTreeScanner);
//            chk.compiled.clear();
//            enter.main(javacList(jcCompilationUnit));
//            attr.attrib(env);
//            runTree(jcCompilationUnit, simplePrintTreeScanner);
//            newMembersToEnter.forEach(m -> m.accept(enter);
        }
    }

    private void performAttribute(JCTree.JCCompilationUnit compilationUnit) {
//        Env<AttrContext> tmpEnv = enter.getTopLevelEnv(compilationUnit);
//        attr.attrib(tmpEnv);
//
//        new TreeScanner<Void, Void>() {
//            @Override
//            public Void visitClass(ClassTree classTree, Void unused) {
//                JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classTree;
//                Env<AttrContext> tmpEnv2 = enter.classEnv(classDecl, tmpEnv);
//                attr.attrib(tmpEnv2);
//                return super.visitClass(classTree, unused);
//            }
//        }.scan(compilationUnit, null);
//        log.printRawLines("Found " + log.nerrors + " errors ");
    }

    private void performEnter() {
//        newMembersToEnter.forEach(e -> e.accept(enter));
//        jcCompilationUnit.accept(enter);
    }

    private void addMonkeyPatchCode() {
//        compilationUnit.accept(new AddMonkeyGetTreeScanner(context), newMembersToEnter);
//        compilationUnit.accept(new AddMonkeySetTreeScanner(context), newMembersToEnter);
//        compilationUnit.accept(new AddEnvironmentSetupTreeScanner(context), newMembersToEnter);
//        compilationUnit.accept(new AddImportsTreeScanner(context), newMembersToEnter);
        new AddMonkeyGetTreeScanner(context).scan(jcCompilationUnit, newMembersToEnter);
        new AddMonkeySetTreeScanner(context).scan(jcCompilationUnit, newMembersToEnter);
        new AddEnvironmentSetupTreeScanner(context).scan(jcCompilationUnit, newMembersToEnter);
        new AddImportsTreeScanner(context).scan(jcCompilationUnit, newMembersToEnter);
    }

    private static <T> T getKeyOfType(Map<Object, Object> map, Class<T> clz) {
        return getKeysOfType(map, clz).stream().findFirst().orElseThrow(RuntimeException::new);
    }

    private static <T> List<T> getKeysOfType(Map<Object, Object> map, Class<T> clz) {
        return map.keySet().stream().filter(k -> clz.isAssignableFrom(k.getClass())).map(clz::cast).collect(Collectors.toList());
    }

    private static <T> T getValueOfType(Map<Object, Object> map, Class<T> clz) {
        return getValuesOfType(map, clz).stream().findFirst().orElseThrow(RuntimeException::new);
    }

    private static <T> List<T> getValuesOfType(Map<Object, Object> map, Class<T> clz) {
        return map.values().stream().filter(k -> clz.isAssignableFrom(k.getClass())).map(clz::cast).collect(Collectors.toList());
    }

    @Override
    public void finished(TaskEvent e) {
        jcCompilationUnit = (JCTree.JCCompilationUnit) e.getCompilationUnit();
        log.printRawLines("FINISHED " + e.getKind());
        if (e.getKind() == TaskEvent.Kind.PARSE) {
            log.printRawLines("PARSE finished, adding monkey patch code!");
            addMonkeyPatchCode();
            Cloner cloner = new Cloner();
            //cloner.setDumpClonedClasses(true);
            /*cloner.setDumpCloned(new IDumpCloned() {
                private void p(Object o) {
                    System.err.println(o);
                }

                @Override
                public void startCloning(Class<?> clz) {
                    String cName = clz.getSimpleName();
                    if (cName.contains("JC")) {
                        p("Cloning class " + cName);
                    }
                }

                @Override
                public void cloning(Field field, Class<?> clz) {
                    String cName = clz.getSimpleName();
                    String fName = field.getType().getSimpleName();
                    if (cName.contains("JC") || fName.contains("JC")) {
                        p("Cloning " + cName + ".(" + fName + " " + field.getName() + ")");
                    }
                }
            });*/
            cloner.setDontCloneInstanceOf(File.class, Closeable.class); // Type.class, Symbol.TypeSymbol.class, Name.class, Name.Table.class, Names.class
            Context contextClone = cloner.deepClone(context);
            Map<Object, Object> map = cloner.clonesMap;
            JavaCompiler javaCompiler = getKeyOfType(map, JavaCompiler.class);
            JavaCompiler javaCompilerClone = (JavaCompiler) map.get(javaCompiler);
            JCTree.JCCompilationUnit jcCompilationUnitClone = (JCTree.JCCompilationUnit) map.get(jcCompilationUnit);
            List<JCTree.JCCompilationUnit> result = javaCompiler.enterTrees(javacList(jcCompilationUnit));
            Todo todo = getKeyOfType(map, Todo.class);
            Attr attr = getKeyOfType(map, Attr.class);
            attr.attrib(todo.peek());
            runTree(jcCompilationUnit, replaceMemberSelectTreeScanner);
//            List<JCTree.JCCompilationUnit> resultClone = javaCompilerClone.enterTrees(javacList(jcCompilationUnitClone));

            chk.compiled.clear();
            log.printRawLines(":tnm");
        } else if (e.getKind() == TaskEvent.Kind.ENTER) {
//            updateEnvIfPresent();
        }
    }
}
