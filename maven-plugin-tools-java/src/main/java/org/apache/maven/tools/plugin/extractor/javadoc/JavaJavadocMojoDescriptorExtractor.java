package org.apache.maven.tools.plugin.extractor.javadoc;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.descriptor.InvalidParameterException;
import org.apache.maven.plugin.descriptor.InvalidPluginDescriptorException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.Requirement;
import org.apache.maven.project.MavenProject;
import org.apache.maven.tools.plugin.ExtendedMojoDescriptor;
import org.apache.maven.tools.plugin.PluginToolsRequest;
import org.apache.maven.tools.plugin.extractor.ExtractionException;
import org.apache.maven.tools.plugin.extractor.GroupKey;
import org.apache.maven.tools.plugin.extractor.MojoDescriptorExtractor;
import org.apache.maven.tools.plugin.util.PluginUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaType;

/**
 * <p>
 * Extracts Mojo descriptors from <a href="http://java.sun.com/">Java</a> sources.
 * </p>
 * For more information about the usage tag, have a look to:
 * <a href="http://maven.apache.org/developers/mojo-api-specification.html">
 * http://maven.apache.org/developers/mojo-api-specification.html</a>
 *
 * @see org.apache.maven.plugin.descriptor.MojoDescriptor
 */
@Named(value = "java-javadoc", 
<<<<<<< /usr/src/app/output/apache/maven-plugin-tools/5fe527d2614d39b64e81881e52ff4fb88a0c6111/maven-plugin-tools-java/src/main/java/org/apache/maven/tools/plugin/extractor/javadoc/JavaJavadocMojoDescriptorExtractor.java/left.java
hint = JavaJavadocMojoDescriptorExtractor.NAME
=======
>>>>>>> Unknown file: This is a bug in JDime.
) @Singleton public class JavaJavadocMojoDescriptorExtractor extends AbstractLogEnabled implements MojoDescriptorExtractor, JavadocMojoAnnotation {
  public static final String NAME = "java-javadoc";

  private static final GroupKey GROUP_KEY = new GroupKey(GroupKey.JAVA_GROUP, 200);

  @Override public String getName() {
    return NAME;
  }

  @Override public boolean isDeprecated() {
    return true;
  }

  @Override public GroupKey getGroupKey() {
    return GROUP_KEY;
  }

  /**
     * @param parameter not null
     * @param i positive number
     * @throws InvalidParameterException if any
     */
  protected void validateParameter(Parameter parameter, int i) throws InvalidParameterException {
    String name = parameter.getName();
    if (name == null) {
      throw new InvalidParameterException("name", i);
    }
    String type = parameter.getType();
    if (type == null) {
      throw new InvalidParameterException("type", i);
    }
    String description = parameter.getDescription();
    if (description == null) {
      throw new InvalidParameterException("description", i);
    }
  }

  /**
     * @param javaClass not null
     * @return a mojo descriptor
     * @throws InvalidPluginDescriptorException if any
     */
  protected MojoDescriptor createMojoDescriptor(JavaClass javaClass) throws InvalidPluginDescriptorException {
    ExtendedMojoDescriptor mojoDescriptor = new ExtendedMojoDescriptor();
    mojoDescriptor.setLanguage("java");
    mojoDescriptor.setImplementation(javaClass.getFullyQualifiedName());
    mojoDescriptor.setDescription(javaClass.getComment());
    DocletTag aggregator = findInClassHierarchy(javaClass, JavadocMojoAnnotation.AGGREGATOR);
    if (aggregator != null) {
      mojoDescriptor.setAggregator(true);
    }
    DocletTag configurator = findInClassHierarchy(javaClass, JavadocMojoAnnotation.CONFIGURATOR);
    if (configurator != null) {
      mojoDescriptor.setComponentConfigurator(configurator.getValue());
    }
    DocletTag execute = findInClassHierarchy(javaClass, JavadocMojoAnnotation.EXECUTE);
    if (execute != null) {
      String executePhase = execute.getNamedParameter(JavadocMojoAnnotation.EXECUTE_PHASE);
      String executeGoal = execute.getNamedParameter(JavadocMojoAnnotation.EXECUTE_GOAL);
      if (executePhase == null && executeGoal == null) {
        throw new InvalidPluginDescriptorException(javaClass.getFullyQualifiedName() + ": @execute tag requires either a \'phase\' or \'goal\' parameter");
      } else {
        if (executePhase != null && executeGoal != null) {
          throw new InvalidPluginDescriptorException(javaClass.getFullyQualifiedName() + ": @execute tag can have only one of a \'phase\' or \'goal\' parameter");
        }
      }
      mojoDescriptor.setExecutePhase(executePhase);
      mojoDescriptor.setExecuteGoal(executeGoal);
      String lifecycle = execute.getNamedParameter(JavadocMojoAnnotation.EXECUTE_LIFECYCLE);
      if (lifecycle != null) {
        mojoDescriptor.setExecuteLifecycle(lifecycle);
        if (mojoDescriptor.getExecuteGoal() != null) {
          throw new InvalidPluginDescriptorException(javaClass.getFullyQualifiedName() + ": @execute lifecycle requires a phase instead of a goal");
        }
      }
    }
    DocletTag goal = findInClassHierarchy(javaClass, JavadocMojoAnnotation.GOAL);
    if (goal != null) {
      mojoDescriptor.setGoal(goal.getValue());
    }
    boolean value = getBooleanTagValue(javaClass, JavadocMojoAnnotation.INHERIT_BY_DEFAULT, mojoDescriptor.isInheritedByDefault());
    mojoDescriptor.setInheritedByDefault(value);
    DocletTag tag = findInClassHierarchy(javaClass, JavadocMojoAnnotation.INSTANTIATION_STRATEGY);
    if (tag != null) {
      mojoDescriptor.setInstantiationStrategy(tag.getValue());
    }
    tag = findInClassHierarchy(javaClass, JavadocMojoAnnotation.MULTI_EXECUTION_STRATEGY);
    if (tag != null) {
      getLogger().warn("@" + JavadocMojoAnnotation.MULTI_EXECUTION_STRATEGY + " in " + javaClass.getFullyQualifiedName() + " is deprecated: please use \'@" + JavadocMojoAnnotation.EXECUTION_STATEGY + " always\' instead.");
      mojoDescriptor.setExecutionStrategy(MojoDescriptor.MULTI_PASS_EXEC_STRATEGY);
    } else {
      mojoDescriptor.setExecutionStrategy(MojoDescriptor.SINGLE_PASS_EXEC_STRATEGY);
    }
    tag = findInClassHierarchy(javaClass, JavadocMojoAnnotation.EXECUTION_STATEGY);
    if (tag != null) {
      mojoDescriptor.setExecutionStrategy(tag.getValue());
    }
    DocletTag phase = findInClassHierarchy(javaClass, JavadocMojoAnnotation.PHASE);
    if (phase != null) {
      mojoDescriptor.setPhase(phase.getValue());
    }
    DocletTag requiresDependencyResolution = findInClassHierarchy(javaClass, JavadocMojoAnnotation.REQUIRES_DEPENDENCY_RESOLUTION);
    if (requiresDependencyResolution != null) {
      String v = requiresDependencyResolution.getValue();
      if (StringUtils.isEmpty(v)) {
        v = "runtime";
      }
      mojoDescriptor.setDependencyResolutionRequired(v);
    }
    DocletTag requiresDependencyCollection = findInClassHierarchy(javaClass, JavadocMojoAnnotation.REQUIRES_DEPENDENCY_COLLECTION);
    if (requiresDependencyCollection != null) {
      String v = requiresDependencyCollection.getValue();
      if (StringUtils.isEmpty(v)) {
        v = "runtime";
      }
      mojoDescriptor.setDependencyCollectionRequired(v);
    }
    value = getBooleanTagValue(javaClass, JavadocMojoAnnotation.REQUIRES_DIRECT_INVOCATION, mojoDescriptor.isDirectInvocationOnly());
    mojoDescriptor.setDirectInvocationOnly(value);
    value = getBooleanTagValue(javaClass, JavadocMojoAnnotation.REQUIRES_ONLINE, mojoDescriptor.isOnlineRequired());
    mojoDescriptor.setOnlineRequired(value);
    value = getBooleanTagValue(javaClass, JavadocMojoAnnotation.REQUIRES_PROJECT, mojoDescriptor.isProjectRequired());
    mojoDescriptor.setProjectRequired(value);
    value = getBooleanTagValue(javaClass, JavadocMojoAnnotation.REQUIRES_REPORTS, mojoDescriptor.isRequiresReports());
    mojoDescriptor.setRequiresReports(value);
    DocletTag deprecated = javaClass.getTagByName(JavadocMojoAnnotation.DEPRECATED);
    if (deprecated != null) {
      mojoDescriptor.setDeprecated(deprecated.getValue());
    }
    DocletTag since = findInClassHierarchy(javaClass, JavadocMojoAnnotation.SINCE);
    if (since != null) {
      mojoDescriptor.setSince(since.getValue());
    }
    value = getBooleanTagValue(javaClass, JavadocMojoAnnotation.THREAD_SAFE, true, mojoDescriptor.isThreadSafe());
    mojoDescriptor.setThreadSafe(value);
    extractParameters(mojoDescriptor, javaClass);
    return mojoDescriptor;
  }

  /**
     * @param javaClass not null
     * @param tagName not null
     * @param defaultValue the wanted default value
     * @return the boolean value of the given tagName
     * @see #findInClassHierarchy(JavaClass, String)
     */
  private static boolean getBooleanTagValue(JavaClass javaClass, String tagName, boolean defaultValue) {
    DocletTag tag = findInClassHierarchy(javaClass, tagName);
    if (tag != null) {
      String value = tag.getValue();
      if (StringUtils.isNotEmpty(value)) {
        defaultValue = Boolean.valueOf(value).booleanValue();
      }
    }
    return defaultValue;
  }

  /**
     * @param javaClass     not null
     * @param tagName       not null
     * @param defaultForTag The wanted default value when only the tagname is present
     * @param defaultValue  the wanted default value when the tag is not specified
     * @return the boolean value of the given tagName
     * @see #findInClassHierarchy(JavaClass, String)
     */
  private static boolean getBooleanTagValue(JavaClass javaClass, String tagName, boolean defaultForTag, boolean defaultValue) {
    DocletTag tag = findInClassHierarchy(javaClass, tagName);
    if (tag != null) {
      String value = tag.getValue();
      if (StringUtils.isNotEmpty(value)) {
        return Boolean.valueOf(value).booleanValue();
      } else {
        return defaultForTag;
      }
    }
    return defaultValue;
  }

  /**
     * @param javaClass not null
     * @param tagName not null
     * @return docletTag instance
     */
  private static DocletTag findInClassHierarchy(JavaClass javaClass, String tagName) {
    DocletTag tag = javaClass.getTagByName(tagName);
    if (tag == null) {
      JavaClass superClass = javaClass.getSuperJavaClass();
      if (superClass != null) {
        tag = findInClassHierarchy(superClass, tagName);
      }
    }
    return tag;
  }

  /**
     * @param mojoDescriptor not null
     * @param javaClass not null
     * @throws InvalidPluginDescriptorException if any
     */
  private void extractParameters(MojoDescriptor mojoDescriptor, JavaClass javaClass) throws InvalidPluginDescriptorException {
    Map<String, JavaField> rawParams = extractFieldParameterTags(javaClass);
    for (Map.Entry<String, JavaField> entry : rawParams.entrySet()) {
      JavaField field = entry.getValue();
      JavaType type = field.getType();
      Parameter pd = new Parameter();
      pd.setName(entry.getKey());
      pd.setType(type.getFullyQualifiedName());
      pd.setDescription(field.getComment());
      DocletTag deprecationTag = field.getTagByName(JavadocMojoAnnotation.DEPRECATED);
      if (deprecationTag != null) {
        pd.setDeprecated(deprecationTag.getValue());
      }
      DocletTag sinceTag = field.getTagByName(JavadocMojoAnnotation.SINCE);
      if (sinceTag != null) {
        pd.setSince(sinceTag.getValue());
      }
      DocletTag componentTag = field.getTagByName(JavadocMojoAnnotation.COMPONENT);
      if (componentTag != null) {
        String role = componentTag.getNamedParameter(JavadocMojoAnnotation.COMPONENT_ROLE);
        if (role == null) {
          role = field.getType().toString();
        }
        String roleHint = componentTag.getNamedParameter(JavadocMojoAnnotation.COMPONENT_ROLEHINT);
        if (roleHint == null) {
          roleHint = componentTag.getNamedParameter("role-hint");
        }
        boolean isDeprecated = PluginUtils.MAVEN_COMPONENTS.containsValue(role);
        if (!isDeprecated) {
          pd.setRequirement(new Requirement(role, roleHint));
        } else {
          getLogger().warn("Deprecated @component Javadoc tag for \'" + pd.getName() + "\' field in " + javaClass.getFullyQualifiedName() + ": replace with @Parameter( defaultValue = \"" + role + "\", readonly = true )");
          pd.setDefaultValue(role);
          pd.setRequired(true);
        }
        pd.setEditable(false);
      } else {
        DocletTag parameter = field.getTagByName(JavadocMojoAnnotation.PARAMETER);
        pd.setRequired(field.getTagByName(JavadocMojoAnnotation.REQUIRED) != null);
        pd.setEditable(field.getTagByName(JavadocMojoAnnotation.READONLY) == null);
        String name = parameter.getNamedParameter(JavadocMojoAnnotation.PARAMETER_NAME);
        if (!StringUtils.isEmpty(name)) {
          pd.setName(name);
        }
        String alias = parameter.getNamedParameter(JavadocMojoAnnotation.PARAMETER_ALIAS);
        if (!StringUtils.isEmpty(alias)) {
          pd.setAlias(alias);
        }
        String expression = parameter.getNamedParameter(JavadocMojoAnnotation.PARAMETER_EXPRESSION);
        String property = parameter.getNamedParameter(JavadocMojoAnnotation.PARAMETER_PROPERTY);
        if (StringUtils.isNotEmpty(expression) && StringUtils.isNotEmpty(property)) {
          getLogger().error(javaClass.getFullyQualifiedName() + "#" + field.getName() + ":");
          getLogger().error("  Cannot use both:");
          getLogger().error("    @parameter expression=\"${property}\"");
          getLogger().error("  and");
          getLogger().error("    @parameter property=\"property\"");
          getLogger().error("  Second syntax is preferred.");
          throw new InvalidParameterException(javaClass.getFullyQualifiedName() + "#" + field.getName() + ": cannot" + " use both @parameter expression and property", null);
        }
        if (StringUtils.isNotEmpty(expression)) {
          getLogger().warn(javaClass.getFullyQualifiedName() + "#" + field.getName() + ":");
          getLogger().warn("  The syntax");
          getLogger().warn("    @parameter expression=\"${property}\"");
          getLogger().warn("  is deprecated, please use");
          getLogger().warn("    @parameter property=\"property\"");
          getLogger().warn("  instead.");
        } else {
          if (StringUtils.isNotEmpty(property)) {
            expression = "${" + property + "}";
          }
        }
        pd.setExpression(expression);
        if (StringUtils.isNotEmpty(expression) && expression.startsWith("${component.")) {
          getLogger().warn(javaClass.getFullyQualifiedName() + "#" + field.getName() + ":");
          getLogger().warn("  The syntax");
          getLogger().warn("    @parameter expression=\"${component.<role>#<roleHint>}\"");
          getLogger().warn("  is deprecated, please use");
          getLogger().warn("    @component role=\"<role>\" roleHint=\"<roleHint>\"");
          getLogger().warn("  instead.");
        }
        if ("${reports}".equals(pd.getExpression())) {
          mojoDescriptor.setRequiresReports(true);
        }
        pd.setDefaultValue(parameter.getNamedParameter(JavadocMojoAnnotation.PARAMETER_DEFAULT_VALUE));
        pd.setImplementation(parameter.getNamedParameter(JavadocMojoAnnotation.PARAMETER_IMPLEMENTATION));
      }
      mojoDescriptor.addParameter(pd);
    }
  }

  /**
     * extract fields that are either parameters or components.
     * 
     * @param javaClass not null
     * @return map with Mojo parameters names as keys
     */
  private Map<String, JavaField> extractFieldParameterTags(JavaClass javaClass) {
    Map<String, JavaField> rawParams;
    JavaClass superClass = javaClass.getSuperJavaClass();
    if (superClass != null) {
      rawParams = extractFieldParameterTags(superClass);
    } else {
      rawParams = new TreeMap<String, JavaField>();
    }
    for (JavaField field : javaClass.getFields()) {
      if (field.getTagByName(JavadocMojoAnnotation.PARAMETER) != null || field.getTagByName(JavadocMojoAnnotation.COMPONENT) != null) {
        rawParams.put(field.getName(), field);
      }
    }
    return rawParams;
  }

  @Override public List<MojoDescriptor> execute(PluginToolsRequest request) throws ExtractionException, InvalidPluginDescriptorException {
    Collection<JavaClass> javaClasses = discoverClasses(request);
    List<MojoDescriptor> descriptors = new ArrayList<>();
    for (JavaClass javaClass : javaClasses) {
      DocletTag tag = javaClass.getTagByName(GOAL);
      if (tag != null) {
        MojoDescriptor mojoDescriptor = createMojoDescriptor(javaClass);
        mojoDescriptor.setPluginDescriptor(request.getPluginDescriptor());
        validate(mojoDescriptor);
        descriptors.add(mojoDescriptor);
      }
    }
    return descriptors;
  }

  /**
     * @param request The plugin request.
     * @return an array of java class
     */
  protected Collection<JavaClass> discoverClasses(final PluginToolsRequest request) {
    JavaProjectBuilder builder = new JavaProjectBuilder(new SortedClassLibraryBuilder());
    builder.setEncoding(request.getEncoding());
    List<URL> urls = new ArrayList<>(request.getDependencies().size());
    for (Artifact artifact : request.getDependencies()) {
      try {
        urls.add(artifact.getFile().toURI().toURL());
      } catch (MalformedURLException e) {
      }
    }
    builder.addClassLoader(new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader()));
    MavenProject project = request.getProject();
    for (String source : project.getCompileSourceRoots()) {
      builder.addSourceTree(new File(source));
    }
    File generatedPlugin = new File(project.getBasedir(), "target/generated-sources/plugin");
    if (!project.getCompileSourceRoots().contains(generatedPlugin.getAbsolutePath())) {
      builder.addSourceTree(generatedPlugin);
    }
    return builder.getClasses();
  }

  /**
     * @param mojoDescriptor not null
     * @throws InvalidParameterException if any
     */
  protected void validate(MojoDescriptor mojoDescriptor) throws InvalidParameterException {
    List<Parameter> parameters = mojoDescriptor.getParameters();
    if (parameters != null) {
      for (int j = 0; j < parameters.size(); j++) {
        validateParameter(parameters.get(j), j);
      }
    }
  }
}