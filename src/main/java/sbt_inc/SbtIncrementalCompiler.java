package sbt_inc;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.maven.plugin.logging.Log;
import sbt.internal.inc.*;
import sbt.internal.inc.FileAnalysisStore;
import sbt.internal.inc.ScalaInstance;
import sbt.internal.inc.classpath.ClasspathUtilities;
import scala.Option;
import scala.compat.java8.functionConverterImpls.*;
import scala_maven.VersionNumber;
import xsbti.Logger;
import xsbti.T2;
import xsbti.compile.*;
import xsbti.compile.AnalysisStore;
import xsbti.compile.CompilerCache;


public class SbtIncrementalCompiler {
    public static final String SBT_GROUP_ID = "org.scala-sbt";

    public static final String ZINC_ARTIFACT_ID = "zinc";

    public static final String COMPILER_BRIDGE_ARTIFACT_ID = "compiler-bridge";

    private final CompileOrder compileOrder;

    private final Logger logger;

    private final IncrementalCompilerImpl compiler;

    private final Compilers compilers;

    private final Setup setup;

    private final AnalysisStore analysisStore;

    public SbtIncrementalCompiler(File libraryJar, File reflectJar, File compilerJar, VersionNumber scalaVersion, List<File> extraJars, File compilerBridgeJar, Log l, File cacheFile, CompileOrder compileOrder) throws Exception {
        this.compileOrder = compileOrder;
        this.logger = new SbtLogger(l);
        l.info(("Using incremental compilation using " + compileOrder) + " compile order");
        List<File> allJars = new ArrayList<>(extraJars);
        allJars.add(libraryJar);
        allJars.add(reflectJar);
        allJars.add(compilerJar);
        ScalaInstance scalaInstance = // version
        // loader
        // loaderLibraryOnly
        // libraryJar
        // compilerJar
        // allJars
        // explicitActual
        new ScalaInstance(scalaVersion.toString(), new URLClassLoader(new URL[]{ libraryJar.toURI().toURL(), reflectJar.toURI().toURL(), compilerJar.toURI().toURL() }), ClasspathUtilities.rootLoader(), libraryJar, compilerJar, allJars.toArray(new File[]{  }), Option.apply(scalaVersion.toString()));
        compiler = new IncrementalCompilerImpl();
        ScalaCompiler scalaCompiler = // scalaInstance
        // provider
        // classpathOptions
        // FIXME foo -> {}, // onArgsHandler
        // classLoaderCache
        new AnalyzingCompiler(scalaInstance, ZincCompilerUtil.constantBridgeProvider(scalaInstance, compilerBridgeJar), ClasspathOptionsUtil.auto(), new FromJavaConsumer<>(( noop) -> {
        }), Option.apply(null));
        compilers = compiler.compilers(scalaInstance, ClasspathOptionsUtil.boot(), Option.apply(null), scalaCompiler);
        PerClasspathEntryLookup lookup = new PerClasspathEntryLookup() {
            @Override
            public Optional<CompileAnalysis> analysis(File classpathEntry) {
                return Optional.empty();
            }

            @Override
            public DefinesClass definesClass(File classpathEntry) {
                return Locate.definesClass(classpathEntry);
            }
        };
        LoggedReporter reporter = new LoggedReporter(100, logger, ( pos) -> pos);
        analysisStore = AnalysisStore.getCachedStore(FileAnalysisStore.binary(cacheFile));
        setup = // lookup
        // skip
        // cacheFile
        // cache
        // incOptions
        // reporter
        // optionProgress
        compiler.setup(lookup, false, cacheFile, CompilerCache.fresh(), IncOptions.of(), reporter, Option.apply(null), new T2[]{  });
    }

    public void compile(List<String> classpathElements, List<File> sources, File classesDirectory, List<String> scalacOptions, List<String> javacOptions) {
        List<File> fullClasspath = new ArrayList<>();
        fullClasspath.add(classesDirectory);
        for (String classpathElement : classpathElements) {
            fullClasspath.add(new File(classpathElement));
        }
        Inputs inputs = //classpath
            // sources
            // classesDirectory
            // scalacOptions
            // javacOptions
            // maxErrors
            // sourcePositionMappers
            // order
        compiler.inputs(fullClasspath.toArray(new File[]{  }), sources.toArray(new File[]{  }), classesDirectory, scalacOptions.toArray(new String[]{  }), javacOptions.toArray(new String[]{  }), 100, new Function[]{  }, compileOrder, compilers, setup, compiler.emptyPreviousResult());
        Optional<AnalysisContents> analysisContents = analysisStore.get();
        if (analysisContents.isPresent()) {
            AnalysisContents analysisContents0 = analysisContents.get();
            CompileAnalysis previousAnalysis = analysisContents0.getAnalysis();
            MiniSetup previousSetup = analysisContents0.getMiniSetup();
            PreviousResult previousResult = PreviousResult.of(Optional.of(previousAnalysis), Optional.of(previousSetup));
            inputs = inputs.withPreviousResult(previousResult);
        }
        CompileResult newResult = compiler.compile(inputs, logger);
        analysisStore.set(AnalysisContents.create(newResult.analysis(), newResult.setup()));
    }
}