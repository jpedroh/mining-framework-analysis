package com.google.common.css.compiler.passes;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.css.JobDescription;
import com.google.common.css.PrefixingSubstitutionMap;
import com.google.common.css.RecordingSubstitutionMap;
import com.google.common.css.SubstitutionMap;
import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.ErrorManager;
import com.google.common.css.compiler.ast.GssFunction;
import java.util.Map;
import javax.annotation.Nullable;

public class PassRunner {
  private static final ImmutableMap<String, GssFunction> EMPTY_GSS_FUNCTION_MAP = ImmutableMap.of();

  private final JobDescription job;

  private final ErrorManager errorManager;

  private final RecordingSubstitutionMap recordingSubstitutionMap;

  public PassRunner(JobDescription job, ErrorManager errorManager) {
    this(job, errorManager, createSubstitutionMap(job));
  }

  public PassRunner(JobDescription job, ErrorManager errorManager, RecordingSubstitutionMap recordingSubstitutionMap) {
    this.job = job;
    this.errorManager = errorManager;
    this.recordingSubstitutionMap = recordingSubstitutionMap;
  }

  public void runPasses(CssTree cssTree) {
    new CheckDependencyNodes(cssTree.getMutatingVisitController(), errorManager, job.suppressDependencyCheck).runPass();
    new CreateStandardAtRuleNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
    new CreateMixins(cssTree.getMutatingVisitController(), errorManager).runPass();
    new CreateDefinitionNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
    new CreateConstantReferences(cssTree.getMutatingVisitController()).runPass();
    new CreateConditionalNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
    new CreateForLoopNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
    new CreateComponentNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
    new ValidatePropertyValues(cssTree.getVisitController(), errorManager).runPass();
    new WarnOnCustomProperty(cssTree.getVisitController(), errorManager).runPass();
    new HandleUnknownAtRuleNodes(cssTree.getMutatingVisitController(), errorManager, job.allowedAtRules, true, false).runPass();
    new ProcessKeyframes(cssTree.getMutatingVisitController(), errorManager, job.allowKeyframes || job.allowWebkitKeyframes, job.simplifyCss).runPass();
    new CreateVendorPrefixedKeyframes(cssTree.getMutatingVisitController(), errorManager).runPass();
    new EvaluateCompileConstants(cssTree.getMutatingVisitController(), job.compileConstants).runPass();
    new UnrollLoops(cssTree.getMutatingVisitController(), errorManager).runPass();
    new ProcessRefiners(cssTree.getMutatingVisitController(), errorManager, job.simplifyCss).runPass();
    new EliminateConditionalNodes(cssTree.getMutatingVisitController(), ImmutableSet.copyOf(job.trueConditionNames)).runPass();
    CollectMixinDefinitions collectMixinDefinitions = new CollectMixinDefinitions(cssTree.getMutatingVisitController(), errorManager);
    collectMixinDefinitions.runPass();
    new ReplaceMixins(cssTree.getMutatingVisitController(), errorManager, collectMixinDefinitions.getDefinitions()).runPass();
    new ProcessComponents<Object>(cssTree.getMutatingVisitController(), errorManager).runPass();
    CollectConstantDefinitions collectConstantDefinitionsPass = new CollectConstantDefinitions(cssTree);
    collectConstantDefinitionsPass.runPass();
    ReplaceConstantReferences replaceConstantReferences = new ReplaceConstantReferences(cssTree, collectConstantDefinitionsPass.getConstantDefinitions(), true, errorManager, job.allowUndefinedConstants);
    replaceConstantReferences.runPass();
    Map<String, GssFunction> gssFunctionMap = getGssFunctionMap();
    new ResolveCustomFunctionNodes(cssTree.getMutatingVisitController(), errorManager, gssFunctionMap, job.allowUnrecognizedFunctions, job.allowedNonStandardFunctions).runPass();
    if (job.simplifyCss) {
      new EliminateEmptyRulesetNodes(cssTree.getMutatingVisitController()).runPass();
      new EliminateUnitsFromZeroNumericValues(cssTree.getMutatingVisitController()).runPass();
      new ColorValueOptimizer(cssTree.getMutatingVisitController()).runPass();
      new AbbreviatePositionalValues(cssTree.getMutatingVisitController()).runPass();
    }
    if (!job.allowDuplicateDeclarations) {
      new DisallowDuplicateDeclarations(cssTree.getVisitController(), errorManager).runPass();
    }
    if (job.eliminateDeadStyles) {
      new SplitRulesetNodes(cssTree.getMutatingVisitController()).runPass();
      new MarkRemovableRulesetNodes(cssTree).runPass();
      new EliminateUselessRulesetNodes(cssTree).runPass();
      new MergeAdjacentRulesetNodesWithSameSelector(cssTree).runPass();
      new EliminateUselessRulesetNodes(cssTree).runPass();
      new MergeAdjacentRulesetNodesWithSameDeclarations(cssTree).runPass();
      new EliminateUselessRulesetNodes(cssTree).runPass();
    }
    if (job.needsBiDiFlipping()) {
      new MarkNonFlippableNodes(cssTree.getVisitController(), errorManager).runPass();
      new BiDiFlipper(cssTree.getMutatingVisitController(), job.swapLtrRtlInUrl, job.swapLeftRightInUrl).runPass();
    }
    if (job.vendor != null) {
      new RemoveVendorSpecificProperties(job.vendor, cssTree.getMutatingVisitController()).runPass();
    }
    if (!job.allowUnrecognizedProperties) {
      new VerifyRecognizedProperties(job.allowedUnrecognizedProperties, cssTree.getVisitController(), errorManager).runPass();
    }
    if (recordingSubstitutionMap != null) {
      new CssClassRenaming(cssTree.getMutatingVisitController(), recordingSubstitutionMap, null).runPass();
    }
  }

  @Nullable public RecordingSubstitutionMap getRecordingSubstitutionMap() {
    return recordingSubstitutionMap;
  }

  private static RecordingSubstitutionMap createSubstitutionMap(JobDescription job) {
    if (job.cssSubstitutionMapProvider != null) {
      SubstitutionMap baseMap = job.cssSubstitutionMapProvider.get();
      if (baseMap != null) {
        SubstitutionMap map = baseMap;
        if (!job.cssRenamingPrefix.isEmpty()) {
          map = new PrefixingSubstitutionMap(baseMap, job.cssRenamingPrefix);
        }
        RecordingSubstitutionMap recording = new RecordingSubstitutionMap.Builder().withSubstitutionMap(map).shouldRecordMappingForCodeGeneration(Predicates.not(Predicates.in(job.excludedClassesFromRenaming))).build();
        recording.initializeWithMappings(job.inputRenamingMap);
        return recording;
      }
    }
    return null;
  }

  private Map<String, GssFunction> getGssFunctionMap() {
    if (job.gssFunctionMapProvider == null) {
      return EMPTY_GSS_FUNCTION_MAP;
    }
    Map<String, GssFunction> map = job.gssFunctionMapProvider.get(GssFunction.class);
    if (map == null) {
      return EMPTY_GSS_FUNCTION_MAP;
    }
    return map;
  }
}