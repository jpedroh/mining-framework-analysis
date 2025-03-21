package com.technophobia.substeps.runner;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.ImplementationCache;
import com.technophobia.substeps.execution.MethodExecutor;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.runner.setupteardown.SetupAndTearDown;
import com.technophobia.substeps.runner.syntax.SyntaxBuilder;

/**
 * Takes a tree of execution nodes and executes them, all variables, args,
 * backgrounds already pre-determined
 * 
 * @author ian
 * 
 */
public class ExecutionNodeRunner implements SubstepsRunner {
  private final Logger log = LoggerFactory.getLogger(ExecutionNodeRunner.class);

  private boolean noTestsRun = true;

  private boolean dryRun;

  private ExecutionNode rootNode;

  private final INotificationDistributor notificationDistributor = new NotificationDistributor();

  private SetupAndTearDown setupAndTearDown;

  private ExecutionConfig config;

  private TagManager nonFatalTagmanager = null;

  private final MethodExecutor methodExecutor = new ImplementationCache();

  public void addNotifier(final INotifier notifier) {
    this.notificationDistributor.addListener(notifier);
  }

  public ExecutionNode prepareExecutionConfig(final ExecutionConfig theConfig) {
    this.config = theConfig;
    this.config.initProperties();
    this.setupAndTearDown = new SetupAndTearDown(this.config.getInitialisationClasses(), this.methodExecutor);
    final String loggingConfigName = this.config.getDescription() != null ? this.config.getDescription() : "SubStepsMojo";
    this.setupAndTearDown.setLoggingConfigName(loggingConfigName);
    final TagManager tagmanager = new TagManager(this.config.getTags());
    if (this.config.getNonFatalTags() != null) {
      this.nonFatalTagmanager = new TagManager(this.config.getNonFatalTags());
    }
    File subStepsFile = null;
    if (this.config.getSubStepsFileName() != null) {
      subStepsFile = new File(this.config.getSubStepsFileName());
    }
    final Syntax syntax = SyntaxBuilder.buildSyntax(this.config.getStepImplementationClasses(), subStepsFile, this.config.isStrict(), this.config.getNonStrictKeywordPrecedence());
    final TestParameters parameters = new TestParameters(tagmanager, syntax, this.config.getFeatureFile());
    parameters.setFailParseErrorsImmediately(this.config.isFastFailParseErrors());
    parameters.init();
    final ExecutionNodeTreeBuilder nodeTreeBuilder = new ExecutionNodeTreeBuilder(parameters);
    this.rootNode = nodeTreeBuilder.buildExecutionNodeTree();
    ExecutionContext.put(Scope.SUITE, INotificationDistributor.NOTIFIER_DISTRIBUTOR_KEY, this.notificationDistributor);
    final String dryRunProperty = System.getProperty("dryRun");
    if (dryRunProperty != null && Boolean.parseBoolean(dryRunProperty)) {
      this.log.info("**** DRY RUN ONLY **");
      this.setupAndTearDown.setDryRun(true);
      setDryRun(true);
    }
    return this.rootNode;
  }

  public List<SubstepExecutionFailure> run() {
    this.log.trace("run root node");
    this.noTestsRun = true;
    ExecutionContext.put(Scope.SUITE, INotificationDistributor.NOTIFIER_DISTRIBUTOR_KEY, this.notificationDistributor);
    final List<SubstepExecutionFailure> failures = runExecutionNodeHierarchy(Scope.SUITE, this.rootNode);
    if (this.noTestsRun) {
      final Throwable t = new IllegalStateException("No tests executed");
      this.rootNode.getResult().setFailed(t);
      this.notificationDistributor.notifyNodeFailed(this.rootNode, t);
      addFailure(failures, new SubstepExecutionFailure(t, this.rootNode));
    }
    return failures;
  }

  private void addFailure(final List<SubstepExecutionFailure> failures, final SubstepExecutionFailure failure) {
    failures.add(failure);
    logFailure(failure);
    if (!failure.isSetupOrTearDown() && this.nonFatalTagmanager != null && this.nonFatalTagmanager.acceptTaggedScenario(failure.getExeccutionNode().getTagsFromHierarchy())) {
      failure.setNonCritical(true);
    }
  }

  private List<SubstepExecutionFailure> runExecutionNodeHierarchy(final Scope scope, final ExecutionNode node) {
    this.log.trace("run Node Hierarchy @ " + scope.name() + ":" + node.getDebugStringForThisNode());
    final List<SubstepExecutionFailure> failures = Lists.newArrayList();
    if (node.hasError()) {
      this.notificationDistributor.notifyNodeFailed(node, node.getResult().getThrown());
      addFailure(failures, new SubstepExecutionFailure(node.getResult().getThrown(), node));
    } else {
      node.getResult().setStarted();

<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
      this.notificationDistributor.notifyNodeStarted(node);
=======
      runSetupIfNecessary(scope, node, failures);
>>>>>>> /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/right.java


<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
      try {
        if (!node.isOutlineScenario()) {
          this.setupAndTearDown.runSetup(scope);
        }
      } catch (final Throwable t) {
        this.log.debug("setup failed", t);
        addFailure(failures, new SubstepExecutionFailure(t, node, true));
      }
=======
      runBackgroundNodes(node, failures);
>>>>>>> /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/right.java


<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
      if (failures.isEmpty() && node.hasBackground() && !node.isOutlineScenario()) {
        for (final ExecutionNode backgroundNode : node.getBackgrounds()) {
          final List<SubstepExecutionFailure> backgroundScenarioFailures = runExecutionNodeHierarchy(Scope.SCENARIO_BACKGROUND, backgroundNode);
          if (!backgroundScenarioFailures.isEmpty()) {
            this.log.debug("running background scenarios failed");
            failures.addAll(backgroundScenarioFailures);
          }
          if (!failures.isEmpty()) {
            break;
          }
        }
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
      if (failures.isEmpty() && node.isExecutable()) {
        final SubstepExecutionFailure methodInvocationFailure = executeNodeMethod(node);
        if (methodInvocationFailure != null) {
          addFailure(failures, methodInvocationFailure);
        }
      } else {
        if (failures.isEmpty() && node.shouldHaveChildren() && !node.hasChildren()) {
          addFailure(failures, new SubstepExecutionFailure(new IllegalStateException("node should have children but doesn\'t"), node));
        } else {
          if (failures.isEmpty() && node.hasChildren()) {
            this.log.trace("node has children");
            for (final ExecutionNode child : node.getChildren()) {
              final Scope childScope = getChildScope(node, scope);
              final List<SubstepExecutionFailure> childFailures = runExecutionNodeHierarchy(childScope, child);
              if (!childFailures.isEmpty()) {
                this.log.debug("running children failed");
                failures.addAll(childFailures);
              }
              if (!failures.isEmpty() && (scope == Scope.STEP || scope == Scope.SCENARIO_OUTLINE_ROW || (scope == Scope.SCENARIO && childScope == Scope.STEP))) {
                this.log.debug("bailing out of execution");
                break;
              }
            }
          }
        }
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
      try {
        if (!node.isOutlineScenario()) {
          this.setupAndTearDown.runTearDown(scope);
          ExecutionContext.clear(scope);
        }
      } catch (final Throwable t) {
        this.log.debug("tear down failed", t);
        failures.add(new SubstepExecutionFailure(t, node, true));
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.

      if (failures.isEmpty()) {

<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
        this.log.trace("node " + node.getId() + " success");
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
        this.notificationDistributor.notifyNodeFinished(node);
=======
        executeNodeOrRunChildren(scope, node, failures);
>>>>>>> /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/right.java
      } else 
<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
      {
        this.log.debug("node failures");
        final Throwable lastException = failures.get(failures.size() - 1).getCause();
        node.getResult().setFailed(lastException);
        this.notificationDistributor.notifyNodeFailed(node, lastException);
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.

      runTearDown(scope, node, failures);
      recordResult(node, failures);
    }
    return failures;
  }

  private void runSetupIfNecessary(final Scope scope, final ExecutionNode node, final List<SubstepExecutionFailure> failures) {
    try {
      if (!node.isOutlineScenario()) {
        setupAndTearDown.runSetup(scope);
      }
    } catch (final Throwable t) {
      log.debug("setup failed", t);
      addFailure(failures, new SubstepExecutionFailure(t, node, true));
    }
  }

  private void runBackgroundNodes(final ExecutionNode node, final List<SubstepExecutionFailure> failures) {
    if (failures.isEmpty() && node.hasBackground() && !node.isOutlineScenario()) {
      Iterator<ExecutionNode> backgroundNodesIt = node.getBackgrounds().iterator();
      while (failures.isEmpty() && backgroundNodesIt.hasNext()) {
        final List<SubstepExecutionFailure> backgroundScenarioFailures = runExecutionNodeHierarchy(Scope.SCENARIO_BACKGROUND, backgroundNodesIt.next());
        if (!backgroundScenarioFailures.isEmpty()) {
          log.debug("running background scenarios failed");
          failures.addAll(backgroundScenarioFailures);
        }
      }
    }
  }

  private void executeNodeOrRunChildren(final Scope scope, final ExecutionNode node, final List<SubstepExecutionFailure> failures) {
    if (node.isExecutable()) {
      final SubstepExecutionFailure methodInvocationFailure = executeNodeMethod(node);
      if (methodInvocationFailure != null) {
        addFailure(failures, methodInvocationFailure);
      }
    } else {
      runChildren(scope, node, failures);
    }
  }

  private void runChildren(final Scope scope, final ExecutionNode node, final List<SubstepExecutionFailure> failures) {
    if (node.shouldHaveChildren() && !node.hasChildren()) {
      addFailure(failures, new SubstepExecutionFailure(new IllegalStateException("node should have children but doesn\'t"), node));
    } else {
      if (node.hasChildren()) {
        log.debug("node has children");
        for (final ExecutionNode child : node.getChildren()) {
          final Scope childScope = getChildScope(node, scope);
          final List<SubstepExecutionFailure> childFailures = runExecutionNodeHierarchy(childScope, child);
          if (!childFailures.isEmpty()) {
            log.debug("running children failed");
            failures.addAll(childFailures);
          }
          if (!failures.isEmpty() && (scope == Scope.STEP || scope == Scope.SCENARIO_OUTLINE_ROW || (scope == Scope.SCENARIO && childScope == Scope.STEP))) {
            log.debug("bailing out of execution");
            break;
          }
        }
      }
    }
  }

  private void runTearDown(final Scope scope, final ExecutionNode node, final List<SubstepExecutionFailure> failures) {
    try {
      if (!node.isOutlineScenario()) {
        setupAndTearDown.runTearDown(scope);
        ExecutionContext.clear(scope);
      }
    } catch (final Throwable t) {
      log.debug("tear down failed", t);
      failures.add(new SubstepExecutionFailure(t, node, true));
    }
  }

  private void recordResult(final ExecutionNode node, final List<SubstepExecutionFailure> failures) {
    if (failures.isEmpty()) {
      log.debug("node success");
      notificationDistributor.notifyNodeFinished(node);
      node.getResult().setFinished();
    } else {
      log.debug("node failures");
      final Throwable lastException = failures.get(failures.size() - 1).getCause();
      notificationDistributor.notifyNodeFailed(node, lastException);
      node.getResult().setFailed(lastException);
    }
  }

  /**
     * @param failure
     */
  private void logFailure(final SubstepExecutionFailure failure) {

<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/left.java
    this.log.info("SubstepExecutionFailure @ " + failure.getExeccutionNode().getDebugStringForThisNode(), failure.getCause());
=======
    final Throwable failureCause = failure.getCause();
>>>>>>> /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/runner/ExecutionNodeRunner.java/right.java

    final Throwable here = new Throwable();
    final StackTraceElement[] failureTrace = failureCause.getStackTrace();
    final StackTraceElement[] hereTrace = here.getStackTrace();
    final int requiredTraceSize = failureTrace.length - hereTrace.length;
    if (requiredTraceSize > 0 && requiredTraceSize < failureTrace.length) {
      final StringBuilder stackTraceBuilder = new StringBuilder();
      stackTraceBuilder.append(failureCause.toString()).append("\n");
      for (int i = 0; i < requiredTraceSize; i++) {
        stackTraceBuilder.append("\tat ").append(failureTrace[i]).append("\n");
      }
      log.info("SubstepExecutionFailure @ " + failure.getExeccutionNode().getDebugStringForThisNode() + "\n" + stackTraceBuilder.toString());
    } else {
      log.info("SubstepExecutionFailure @ " + failure.getExeccutionNode().getDebugStringForThisNode(), failureCause);
    }
  }

  /**
     * @param node
     * @param theException
     * @return
     */
  private SubstepExecutionFailure executeNodeMethod(final ExecutionNode node) {
    SubstepExecutionFailure theFailure = null;
    this.log.trace("executing node method");
    try {
      if (!this.dryRun) {
        this.methodExecutor.executeMethod(node.getTargetClass(), node.getTargetMethod(), node.getMethodArgs());
      }
      this.noTestsRun = false;
    } catch (final InvocationTargetException e) {
      theFailure = new SubstepExecutionFailure(e.getTargetException(), node);
    } catch (final Throwable e) {
      theFailure = new SubstepExecutionFailure(e, node);
    }
    return theFailure;
  }

  private Scope getChildScope(final ExecutionNode node, final Scope currentScope) {
    Scope rtn = null;
    switch (currentScope) {
      case SUITE:
      {
        rtn = Scope.FEATURE;
        break;
      }
      case FEATURE:
      {
        rtn = Scope.SCENARIO;
        break;
      }
      case SCENARIO:
      {
        if (node.isOutlineScenario()) {
          rtn = Scope.SCENARIO_OUTLINE_ROW;
        } else {
          rtn = Scope.STEP;
        }
        break;
      }
      case SCENARIO_BACKGROUND:
      case SCENARIO_OUTLINE_ROW:
      case STEP:
      {
        rtn = Scope.STEP;
        break;
      }
      default:
      {
        throw new IllegalStateException("impossible state");
      }
    }
    this.log.trace("child scope: " + rtn.name() + " for: " + currentScope.name());
    return rtn;
  }

  public void setDryRun(final boolean dryRun) {
    this.dryRun = dryRun;
  }
}