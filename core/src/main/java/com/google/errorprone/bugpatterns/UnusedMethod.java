package com.google.errorprone.bugpatterns;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Multimaps.asMap;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.fixes.SuggestedFix.emptyFix;
import static com.google.errorprone.fixes.SuggestedFixes.replaceIncludingComments;
import static com.google.errorprone.matchers.Matchers.SERIALIZATION_METHODS;
import static com.google.errorprone.suppliers.Suppliers.typeFromString;
import static com.google.errorprone.util.ASTHelpers.canBeRemoved;
import static com.google.errorprone.util.ASTHelpers.getSymbol;
import static com.google.errorprone.util.ASTHelpers.getType;
import static com.google.errorprone.util.ASTHelpers.isGeneratedConstructor;
import static com.google.errorprone.util.ASTHelpers.isSubtype;
import static com.google.errorprone.util.ASTHelpers.scope;
import static com.google.errorprone.util.ASTHelpers.shouldKeep;
import static com.google.errorprone.util.MoreAnnotations.asStrings;
import static com.google.errorprone.util.MoreAnnotations.getAnnotationValue;
import static java.lang.String.format;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.Modifier.FINAL;
import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker.CompilationUnitTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.suppliers.Supplier;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

/** Bugpattern to detect unused declarations. */
@BugPattern(altNames = { "Unused", "unused", "UnusedParameters" }, summary = "Unused.", severity = WARNING, documentSuppression = false) public final class UnusedMethod extends BugChecker implements CompilationUnitTreeMatcher {
  private static final String GWT_JAVASCRIPT_OBJECT = "com.google.gwt.core.client.JavaScriptObject";

  private static final String EXEMPT_PREFIX = "unused";

  private static final String JUNIT_PARAMS_VALUE = "value";

  private static final String JUNIT_PARAMS_ANNOTATION_TYPE = "junitparams.Parameters";

  private static final ImmutableSet<String> EXEMPTING_METHOD_ANNOTATIONS = ImmutableSet.of("com.fasterxml.jackson.annotation.JsonCreator", "com.fasterxml.jackson.annotation.JsonProperty", "com.fasterxml.jackson.annotation.JsonSetter", "com.google.inject.Provides", "com.google.inject.Inject", "com.google.inject.multibindings.ProvidesIntoMap", "com.google.inject.multibindings.ProvidesIntoSet", "javax.annotation.PreDestroy", "javax.annotation.PostConstruct", "javax.inject.Inject", "javax.persistence.PostLoad", 
<<<<<<< /usr/src/app/output/google/error-prone/c77eec536553d66d9500dcad5f962cce744b89d7/core/src/main/java/com/google/errorprone/bugpatterns/UnusedMethod.java/left.java
  "javax.validation.constraints.AssertFalse"
=======
  "org.apache.beam.sdk.transforms.DoFn.ProcessElement"
>>>>>>> /usr/src/app/output/google/error-prone/c77eec536553d66d9500dcad5f962cce744b89d7/core/src/main/java/com/google/errorprone/bugpatterns/UnusedMethod.java/right.java
  , "javax.validation.constraints.AssertTrue", "org.aspectj.lang.annotation.Pointcut", "org.aspectj.lang.annotation.Before", "org.springframework.context.annotation.Bean", "org.testng.annotations.AfterClass", "org.testng.annotations.AfterMethod", "org.testng.annotations.BeforeClass", "org.testng.annotations.BeforeMethod", "org.testng.annotations.DataProvider", "org.junit.AfterClass", "org.junit.BeforeClass", "org.junit.jupiter.api.AfterAll", "org.junit.jupiter.api.AfterEach", "org.junit.jupiter.api.BeforeAll", "org.junit.jupiter.api.BeforeEach");

  /** The set of types exempting a type that is extending or implementing them. */
  private static final ImmutableSet<String> EXEMPTING_SUPER_TYPES = ImmutableSet.of();

  @Override public Description matchCompilationUnit(CompilationUnitTree tree, VisitorState state) {
    Map<Symbol, TreePath> unusedMethods = new HashMap<>();
    if (hasNativeMethods(tree)) {
      return Description.NO_MATCH;
    }
    AtomicBoolean ignoreUnusedMethods = new AtomicBoolean(false);
    class MethodFinder extends SuppressibleTreePathScanner<Void, Void> {
      MethodFinder(VisitorState state) {
        super(state);
      }

      @Override public Void visitClass(ClassTree tree, Void unused) {
        if (exemptedBySuperType(getType(tree), state)) {
          return null;
        }
        return super.visitClass(tree, null);
      }

      private boolean exemptedBySuperType(Type type, VisitorState state) {
        return EXEMPTING_SUPER_TYPES.stream().anyMatch((t) -> isSubtype(type, typeFromString(t).get(state), state));
      }

      @Override public Void visitMethod(MethodTree tree, Void unused) {
        if (hasJUnitParamsParametersForMethodAnnotation(tree.getModifiers().getAnnotations())) {
          ignoreUnusedMethods.set(true);
        }
        if (isMethodSymbolEligibleForChecking(tree)) {
          unusedMethods.put(getSymbol(tree), getCurrentPath());
        }
        return super.visitMethod(tree, unused);
      }

      private boolean hasJUnitParamsParametersForMethodAnnotation(Collection<? extends AnnotationTree> annotations) {
        for (AnnotationTree tree : annotations) {
          JCAnnotation annotation = (JCAnnotation) tree;
          if (annotation.getAnnotationType().type != null && annotation.getAnnotationType().type.toString().equals(JUNIT_PARAMS_ANNOTATION_TYPE)) {
            if (annotation.getArguments().isEmpty()) {
              return true;
            }
            for (JCExpression arg : annotation.getArguments()) {
              if (arg.getKind() != Kind.ASSIGNMENT) {
                return false;
              }
              JCExpression var = ((JCAssign) arg).getVariable();
              if (var.getKind() == Kind.IDENTIFIER) {
                if (!((IdentifierTree) var).getName().contentEquals(JUNIT_PARAMS_VALUE)) {
                  return true;
                }
              }
            }
          }
        }
        return false;
      }

      private boolean isMethodSymbolEligibleForChecking(MethodTree tree) {
        if (exemptedByName(tree.getName())) {
          return false;
        }
        if (exemptedByAnnotation(tree.getModifiers().getAnnotations())) {
          return false;
        }
        if (shouldKeep(tree)) {
          return false;
        }
        MethodSymbol methodSymbol = getSymbol(tree);
        if (isExemptedConstructor(methodSymbol, state) || isGeneratedConstructor(tree) || SERIALIZATION_METHODS.matches(tree, state)) {
          return false;
        }
        if (!tree.getParameters().isEmpty()) {
          Type lastParamType = getType(getLast(tree.getParameters()));
          if (lastParamType != null && lastParamType.toString().equals(GWT_JAVASCRIPT_OBJECT)) {
            return false;
          }
        }
        return canBeRemoved(methodSymbol, state);
      }

      private boolean isExemptedConstructor(MethodSymbol methodSymbol, VisitorState state) {
        if (!methodSymbol.getKind().equals(CONSTRUCTOR)) {
          return false;
        }
        if (methodSymbol.params().isEmpty()) {
          return true;
        }
        return false;
      }
    }
    new MethodFinder(state).scan(state.getPath(), null);
    class FilterUsedMethods extends TreePathScanner<Void, Void> {
      @Override public Void visitMemberSelect(MemberSelectTree memberSelectTree, Void unused) {
        Symbol symbol = getSymbol(memberSelectTree);
        unusedMethods.remove(symbol);
        return super.visitMemberSelect(memberSelectTree, null);
      }

      @Override public Void visitMemberReference(MemberReferenceTree tree, Void unused) {
        super.visitMemberReference(tree, null);
        MethodSymbol symbol = getSymbol(tree);
        unusedMethods.remove(symbol);
        symbol.getParameters().forEach(unusedMethods::remove);
        return null;
      }

      @Override public Void visitMethodInvocation(MethodInvocationTree tree, Void unused) {
        handle(getSymbol(tree));
        return super.visitMethodInvocation(tree, null);
      }

      @Override public Void visitNewClass(NewClassTree tree, Void unused) {
        handle(getSymbol(tree));
        return super.visitNewClass(tree, null);
      }

      @Override public Void visitAssignment(AssignmentTree tree, Void unused) {
        handle(getSymbol(tree.getVariable()));
        return super.visitAssignment(tree, unused);
      }

      private void handle(Symbol symbol) {
        if (symbol instanceof MethodSymbol) {
          unusedMethods.remove(symbol);
        }
      }

      @Override public Void visitMethod(MethodTree tree, Void unused) {
        handleMethodSource(tree);
        return super.visitMethod(tree, null);
      }

      /**
       * If a method is annotated with @MethodSource, the annotation value refers to another method
       * that is used reflectively to supply test parameters, so that method should not be
       * considered unused.
       */
      private void handleMethodSource(MethodTree tree) {
        MethodSymbol sym = getSymbol(tree);
        Name name = ORG_JUNIT_JUPITER_PARAMS_PROVIDER_METHODSOURCE.get(state);
        sym.getRawAttributes().stream().filter((a) -> a.type.tsym.getQualifiedName().equals(name)).findAny().flatMap((a) -> getAnnotationValue(a, "value")).map((y) -> asStrings(y).map(state::getName).map(Name::toString).collect(toImmutableSet())).ifPresent((referencedNames) -> unusedMethods.entrySet().removeIf((e) -> {
          Symbol unusedSym = e.getKey();
          String simpleName = unusedSym.getSimpleName().toString();
          return referencedNames.contains(simpleName) || referencedNames.contains(unusedSym.owner.getQualifiedName() + "#" + simpleName);
        }));
      }
    }
    new FilterUsedMethods().scan(state.getPath(), null);
    if (ignoreUnusedMethods.get()) {
      return Description.NO_MATCH;
    }
    fixNonConstructors(unusedMethods.values().stream().filter((t) -> !getSymbol(t.getLeaf()).isConstructor()).collect(toImmutableList()), state);
    ImmutableListMultimap<Symbol, TreePath> unusedConstructors = unusedMethods.values().stream().filter((t) -> getSymbol(t.getLeaf()).isConstructor()).collect(toImmutableListMultimap((t) -> getSymbol(t.getLeaf()).owner, (t) -> t));
    fixConstructors(unusedConstructors, state);
    return Description.NO_MATCH;
  }

  private void fixNonConstructors(Iterable<TreePath> unusedPaths, VisitorState state) {
    for (TreePath unusedPath : unusedPaths) {
      Tree unusedTree = unusedPath.getLeaf();
      MethodSymbol symbol = getSymbol((MethodTree) unusedTree);
      String message = String.format("Method \'%s\' is never used.", symbol.getSimpleName());
      state.reportMatch(buildDescription(unusedTree).addFix(replaceIncludingComments(unusedPath, "", state)).setMessage(message).build());
    }
  }

  private void fixConstructors(ImmutableListMultimap<Symbol, TreePath> unusedConstructors, VisitorState state) {
    for (Map.Entry<Symbol, List<TreePath>> entry : asMap(unusedConstructors).entrySet()) {
      Symbol symbol = entry.getKey();
      List<TreePath> trees = entry.getValue();
      SuggestedFix.Builder fix = SuggestedFix.builder();
      int constructorCount = size(scope(symbol.members()).getSymbols(Symbol::isConstructor));
      int finalFields = size(scope(symbol.members()).getSymbols((s) -> s.getKind().equals(FIELD) && s.getModifiers().contains(FINAL)));
      boolean fixable;
      if (constructorCount == trees.size()) {
        fix.postfixWith(getLast(trees).getLeaf(), format("private %s() {}", symbol.getSimpleName()));
        fixable = finalFields == 0;
      } else {
        fixable = true;
      }
      String message = String.format("Constructor \'%s\' is never used.", symbol.getSimpleName());
      trees.forEach((t) -> fix.merge(replaceIncludingComments(t, "", state)));
      state.reportMatch(buildDescription(trees.get(0).getLeaf()).addFix(fixable ? fix.build() : emptyFix()).setMessage(message).build());
    }
  }

  private static boolean hasNativeMethods(CompilationUnitTree tree) {
    AtomicBoolean hasAnyNativeMethods = new AtomicBoolean(false);
    new TreeScanner<Void, Void>() {
      @Override public Void visitMethod(MethodTree tree, Void unused) {
        if (tree.getModifiers().getFlags().contains(Modifier.NATIVE)) {
          hasAnyNativeMethods.set(true);
        }
        return null;
      }
    }.scan(tree, null);
    return hasAnyNativeMethods.get();
  }

  /**
   * Looks at the list of {@code annotations} and see if there is any annotation which exists {@code
   * exemptingAnnotations}.
   */
  private static boolean exemptedByAnnotation(List<? extends AnnotationTree> annotations) {
    for (AnnotationTree annotation : annotations) {
      Type annotationType = getType(annotation);
      if (annotationType == null) {
        continue;
      }
      TypeSymbol tsym = annotationType.tsym;
      if (EXEMPTING_METHOD_ANNOTATIONS.contains(tsym.getQualifiedName().toString())) {
        return true;
      }
    }
    return false;
  }

  private static boolean exemptedByName(Name name) {
    return Ascii.toLowerCase(name.toString()).startsWith(EXEMPT_PREFIX);
  }

  private static final Supplier<com.sun.tools.javac.util.Name> ORG_JUNIT_JUPITER_PARAMS_PROVIDER_METHODSOURCE = VisitorState.memoize((state) -> state.getName("org.junit.jupiter.params.provider.MethodSource"));
}