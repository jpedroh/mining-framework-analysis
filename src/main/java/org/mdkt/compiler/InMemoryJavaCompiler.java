package org.mdkt.compiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.tools.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
<<<<<<< /usr/src/app/output/trung/inmemoryjavacompiler/6460eed46a2343e156dcb3a6f13b286a75f81667/src/main/java/org/mdkt/compiler/InMemoryJavaCompiler.java/left.java
 * Complile Java sources in-memory
||||||| /usr/src/app/output/trung/inmemoryjavacompiler/6460eed46a2343e156dcb3a6f13b286a75f81667/src/main/java/org/mdkt/compiler/InMemoryJavaCompiler.java/base.java
 * Created by trung on 5/3/15.
=======
 * Created by trung on 5/3/15.
 * Changed by PKeidel on 13.10.15.
>>>>>>> /usr/src/app/output/trung/inmemoryjavacompiler/6460eed46a2343e156dcb3a6f13b286a75f81667/src/main/java/org/mdkt/compiler/InMemoryJavaCompiler.java/right.java
 */
public class InMemoryJavaCompiler {
	private JavaCompiler javac;
	private DynamicClassLoader classLoader;

<<<<<<< /usr/src/app/output/trung/inmemoryjavacompiler/6460eed46a2343e156dcb3a6f13b286a75f81667/src/main/java/org/mdkt/compiler/InMemoryJavaCompiler.java/left.java
	private Map<String, SourceCode> sourceCodes = new HashMap<String, SourceCode>();

	public static InMemoryJavaCompiler newInstance() {
		return new InMemoryJavaCompiler();
	}

	private InMemoryJavaCompiler() {
		this.javac = ToolProvider.getSystemJavaCompiler();
		this.classLoader = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
	}

	public InMemoryJavaCompiler useParentClassLoader(ClassLoader parent) {
		this.classLoader = new DynamicClassLoader(parent);
		return this;
	}

	/**
	 * Compile all sources
	 *
	 * @return
	 * @throws Exception
	 */
	public Map<String, Class<?>> compileAll() throws Exception {
		if (sourceCodes.size() == 0) {
			throw new Exception("No source code to compile");
		}
		Collection<SourceCode> compilationUnits = sourceCodes.values();
		CompiledCode[] code;

		code = new CompiledCode[compilationUnits.size()];
		Iterator<SourceCode> iter = compilationUnits.iterator();
		for (int i = 0; i < code.length; i++) {
			code[i] = new CompiledCode(iter.next().getClassName());
		}

		ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), classLoader);
		JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, null, null, compilationUnits);
		boolean result = task.call();
		if (!result) {
			throw new RuntimeException("Unknown error during compilation.");
		}

		Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		for (String className : sourceCodes.keySet()) {
			classes.put(className, classLoader.loadClass(className));
		}
		return classes;
	}

	/**
	 * Compile single source
	 *
	 * @param className
	 * @param sourceCode
	 * @return
	 * @throws Exception
	 */
	public Class<?> compile(String className, String sourceCode) throws Exception {
		return addSource(className, sourceCode).compileAll().get(className);
	}

	/**
	 * Add source code to the compiler
	 *
	 * @param className
	 * @param sourceCode
	 * @return
	 * @throws Exception
	 * @see {@link #compileAll()}
	 */
	public InMemoryJavaCompiler addSource(String className, String sourceCode) throws Exception {
		sourceCodes.put(className, new SourceCode(className, sourceCode));
		return this;
	}
||||||| /usr/src/app/output/trung/inmemoryjavacompiler/6460eed46a2343e156dcb3a6f13b286a75f81667/src/main/java/org/mdkt/compiler/InMemoryJavaCompiler.java/base.java
    public static Class<?> compile(String className, String sourceCodeInText) throws Exception {
        SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
        CompiledCode compiledCode = new CompiledCode(className);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceCode);
        DynamicClassLoader cl = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
        ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), compiledCode, cl);
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, null, null, compilationUnits);
        boolean result = task.call();
        return cl.loadClass(className);
    }
=======
    public static Class<?> compile(String className, String sourceCodeInText) throws Exception {
        final DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();
        SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
        CompiledCode compiledCode = new CompiledCode(className);
        Iterable<? extends JavaFileObject> compilationUnits = Collections.singletonList(sourceCode);
        DynamicClassLoader cl = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
        ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(diagnosticsCollector, null, null), compiledCode, cl);
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, diagnosticsCollector, null, null, compilationUnits);
        boolean result = task.call();
        fileManager.close();

        if (!result) {
            StringBuilder sb = new StringBuilder();
            List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
                // read error dertails from the diagnostic object
                sb.append("=> ").append(diagnostic.getMessage(null)).append("\n");
            }
            throw new CompileException(sb.toString());
        }

        return cl.loadClass(className);
    }
>>>>>>> /usr/src/app/output/trung/inmemoryjavacompiler/6460eed46a2343e156dcb3a6f13b286a75f81667/src/main/java/org/mdkt/compiler/InMemoryJavaCompiler.java/right.java
}
