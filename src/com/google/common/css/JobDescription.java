package com.google.common.css;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobDescription {
  public final List<SourceCode> inputs;

  public final String copyrightNotice;

  public final OutputFormat outputFormat;

  public final InputOrientation inputOrientation;

  public final OutputOrientation outputOrientation;

  public final OptimizeStrategy optimize;

  public final List<String> trueConditionNames;

  public final boolean useInternalBidiFlipper;

  public final boolean swapLtrRtlInUrl;

  public final boolean swapLeftRightInUrl;

  public final boolean simplifyCss;

  public final boolean eliminateDeadStyles;

  public final boolean allowDefPropagation;

  public final boolean allowUnrecognizedFunctions;

  public final boolean allowDuplicateDeclarations;

  public final Set<String> allowedNonStandardFunctions;

  public final boolean allowUnrecognizedProperties;

  public final Set<String> allowedUnrecognizedProperties;

  public final boolean allowUndefinedConstants;

  public final boolean allowMozDocument;

  public final Vendor vendor;

  public final boolean allowKeyframes;

  public final boolean allowWebkitKeyframes;

  public final boolean processDependencies;

  public final ImmutableSet<String> allowedAtRules;

  public final String cssRenamingPrefix;

  public final List<String> excludedClassesFromRenaming;

  public final GssFunctionMapProvider gssFunctionMapProvider;

  public final SubstitutionMapProvider cssSubstitutionMapProvider;

  public final OutputRenamingMapFormat outputRenamingMapFormat;

  public final boolean preserveComments;

  public final boolean suppressDependencyCheck;

  public final Map<String, Integer> compileConstants;

  public final boolean createSourceMap;

  public final SourceMapDetailLevel sourceMapLevel;

  public final boolean preserveImportantComments;

  static final String CONDITION_FOR_LTR = "GSS_LTR";

  static final String CONDITION_FOR_RTL = "GSS_RTL";

  public enum OutputFormat {
    DEBUG,
    COMPRESSED,
    PRETTY_PRINTED
  }

  public enum InputOrientation {
    LTR,
    RTL
  }

  public enum OutputOrientation {
    LTR,
    RTL,
    NOCHANGE
  }

  public enum OptimizeStrategy {
    NONE,
    SAFE,
    MAXIMUM
  }

  public enum SourceMapDetailLevel {
    ALL,
    DEFAULT
  }

  JobDescription(List<SourceCode> inputs, String copyrightNotice, OutputFormat outputFormat, InputOrientation inputOrientation, OutputOrientation outputOrientation, OptimizeStrategy optimize, List<String> trueConditionNames, boolean useInternalBidiFlipper, boolean swapLtrRtlInUrl, boolean swapLeftRightInUrl, boolean simplifyCss, boolean eliminateDeadStyles, boolean allowDefPropagation, boolean allowUnrecognizedFunctions, boolean allowDuplicateDeclarations, Set<String> allowedNonStandardFunctions, boolean allowUnrecognizedProperties, Set<String> allowedUnrecognizedProperties, boolean allowUndefinedConstants, boolean allowMozDocument, Vendor vendor, boolean allowKeyframes, boolean allowWebkitKeyframes, boolean processDependencies, Set<String> allowedAtRules, String cssRenamingPrefix, List<String> excludedClassesFromRenaming, GssFunctionMapProvider gssFunctionMapProvider, SubstitutionMapProvider cssSubstitutionMapProvider, OutputRenamingMapFormat outputRenamingMapFormat, boolean preserveComments, boolean suppressDependencyCheck, Map<String, Integer> compileConstants, boolean createSourceMap, SourceMapDetailLevel sourceMapLevel, boolean preserveImportantComments) {
    this.allowUndefinedConstants = allowUndefinedConstants;
    Preconditions.checkArgument(!inputs.contains(null));
    Preconditions.checkNotNull(outputFormat);
    Preconditions.checkNotNull(inputOrientation);
    Preconditions.checkNotNull(outputOrientation);
    Preconditions.checkNotNull(optimize);
    Preconditions.checkNotNull(trueConditionNames);
    Preconditions.checkNotNull(allowedAtRules);
    Preconditions.checkNotNull(excludedClassesFromRenaming);
    Preconditions.checkNotNull(compileConstants);
    this.inputs = ImmutableList.copyOf(inputs);
    this.copyrightNotice = copyrightNotice;
    this.outputFormat = outputFormat;
    this.inputOrientation = inputOrientation;
    this.outputOrientation = outputOrientation;
    this.optimize = optimize;
    this.trueConditionNames = ImmutableList.copyOf(trueConditionNames);
    this.useInternalBidiFlipper = useInternalBidiFlipper;
    this.swapLtrRtlInUrl = swapLtrRtlInUrl;
    this.swapLeftRightInUrl = swapLeftRightInUrl;
    this.simplifyCss = simplifyCss;
    this.eliminateDeadStyles = eliminateDeadStyles;
    this.allowDefPropagation = allowDefPropagation;
    this.allowUnrecognizedFunctions = allowUnrecognizedFunctions;
    this.allowDuplicateDeclarations = allowDuplicateDeclarations;
    this.allowedNonStandardFunctions = ImmutableSet.copyOf(allowedNonStandardFunctions);
    this.allowUnrecognizedProperties = allowUnrecognizedProperties;
    this.allowedUnrecognizedProperties = ImmutableSet.copyOf(allowedUnrecognizedProperties);
    this.allowMozDocument = allowMozDocument;
    this.vendor = vendor;
    this.allowKeyframes = allowKeyframes;
    this.allowWebkitKeyframes = allowWebkitKeyframes;
    this.processDependencies = processDependencies;
    this.allowedAtRules = ImmutableSet.copyOf(allowedAtRules);
    this.cssRenamingPrefix = cssRenamingPrefix;
    this.excludedClassesFromRenaming = ImmutableList.copyOf(excludedClassesFromRenaming);
    this.gssFunctionMapProvider = gssFunctionMapProvider;
    this.cssSubstitutionMapProvider = cssSubstitutionMapProvider;
    this.outputRenamingMapFormat = outputRenamingMapFormat;
    this.preserveComments = preserveComments;
    this.suppressDependencyCheck = suppressDependencyCheck;
    this.compileConstants = ImmutableMap.copyOf(compileConstants);
    this.createSourceMap = createSourceMap;
    this.sourceMapLevel = sourceMapLevel;
    this.preserveImportantComments = preserveImportantComments;
  }

  public int getAllInputsLength() {
    int totalLength = 0;
    for (SourceCode input : inputs) {
      totalLength += input.getFileContentsLength();
    }
    return totalLength;
  }

  static boolean orientationsAreTheSame(InputOrientation inputOrientation, OutputOrientation outputOrientation) {
    return inputOrientation.toString().equals(outputOrientation.toString());
  }

  public boolean needsBiDiFlipping() {
    return !(outputOrientation == OutputOrientation.NOCHANGE || JobDescription.orientationsAreTheSame(inputOrientation, outputOrientation));
  }

  public JobDescriptionBuilder toBuilder() {
    return new JobDescriptionBuilder().copyFrom(this);
  }

  public final Map<String, String> inputRenamingMap;

  JobDescription(List<SourceCode> inputs, String copyrightNotice, OutputFormat outputFormat, InputOrientation inputOrientation, OutputOrientation outputOrientation, OptimizeStrategy optimize, List<String> trueConditionNames, boolean useInternalBidiFlipper, boolean swapLtrRtlInUrl, boolean swapLeftRightInUrl, boolean simplifyCss, boolean eliminateDeadStyles, boolean allowDefPropagation, boolean allowUnrecognizedFunctions, Set<String> allowedNonStandardFunctions, boolean allowUnrecognizedProperties, Set<String> allowedUnrecognizedProperties, boolean allowUndefinedConstants, boolean allowMozDocument, Vendor vendor, boolean allowKeyframes, boolean allowWebkitKeyframes, boolean processDependencies, Set<String> allowedAtRules, String cssRenamingPrefix, List<String> excludedClassesFromRenaming, GssFunctionMapProvider gssFunctionMapProvider, SubstitutionMapProvider cssSubstitutionMapProvider, OutputRenamingMapFormat outputRenamingMapFormat, Map<String, String> inputRenamingMap, boolean preserveComments, boolean suppressDependencyCheck, Map<String, Integer> compileConstants, boolean createSourceMap, SourceMapDetailLevel sourceMapLevel, boolean preserveImportantComments) {
    this.allowUndefinedConstants = allowUndefinedConstants;
    Preconditions.checkArgument(!inputs.contains(null));
    Preconditions.checkNotNull(outputFormat);
    Preconditions.checkNotNull(inputOrientation);
    Preconditions.checkNotNull(outputOrientation);
    Preconditions.checkNotNull(optimize);
    Preconditions.checkNotNull(trueConditionNames);
    Preconditions.checkNotNull(allowedAtRules);
    Preconditions.checkNotNull(excludedClassesFromRenaming);
    Preconditions.checkNotNull(compileConstants);
    this.inputs = ImmutableList.copyOf(inputs);
    this.copyrightNotice = copyrightNotice;
    this.outputFormat = outputFormat;
    this.inputOrientation = inputOrientation;
    this.outputOrientation = outputOrientation;
    this.optimize = optimize;
    this.trueConditionNames = ImmutableList.copyOf(trueConditionNames);
    this.useInternalBidiFlipper = useInternalBidiFlipper;
    this.swapLtrRtlInUrl = swapLtrRtlInUrl;
    this.swapLeftRightInUrl = swapLeftRightInUrl;
    this.simplifyCss = simplifyCss;
    this.eliminateDeadStyles = eliminateDeadStyles;
    this.allowDefPropagation = allowDefPropagation;
    this.allowUnrecognizedFunctions = allowUnrecognizedFunctions;
    this.allowedNonStandardFunctions = ImmutableSet.copyOf(allowedNonStandardFunctions);
    this.allowUnrecognizedProperties = allowUnrecognizedProperties;
    this.allowedUnrecognizedProperties = ImmutableSet.copyOf(allowedUnrecognizedProperties);
    this.allowMozDocument = allowMozDocument;
    this.vendor = vendor;
    this.allowKeyframes = allowKeyframes;
    this.allowWebkitKeyframes = allowWebkitKeyframes;
    this.processDependencies = processDependencies;
    this.allowedAtRules = ImmutableSet.copyOf(allowedAtRules);
    this.cssRenamingPrefix = cssRenamingPrefix;
    this.excludedClassesFromRenaming = ImmutableList.copyOf(excludedClassesFromRenaming);
    this.gssFunctionMapProvider = gssFunctionMapProvider;
    this.cssSubstitutionMapProvider = cssSubstitutionMapProvider;
    this.outputRenamingMapFormat = outputRenamingMapFormat;
    this.inputRenamingMap = inputRenamingMap;
    this.preserveComments = preserveComments;
    this.suppressDependencyCheck = suppressDependencyCheck;
    this.compileConstants = ImmutableMap.copyOf(compileConstants);
    this.createSourceMap = createSourceMap;
    this.sourceMapLevel = sourceMapLevel;
    this.preserveImportantComments = preserveImportantComments;
  }
}