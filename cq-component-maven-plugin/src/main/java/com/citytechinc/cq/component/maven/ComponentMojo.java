package com.citytechinc.cq.component.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;

import javax.naming.ConfigurationException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;

import com.citytechinc.cq.component.annotations.Component;
import com.citytechinc.cq.component.dialog.ComponentNameTransformer;
import com.citytechinc.cq.component.dialog.widget.WidgetRegistry;
import com.citytechinc.cq.component.dialog.widget.impl.DefaultWidgetRegistry;
import com.citytechinc.cq.component.maven.util.ComponentMojoUtil;
import com.citytechinc.cq.component.maven.util.LogSingleton;

@Mojo(name = "component", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ComponentMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter
	private String componentPathBase;

	@Parameter(defaultValue = "content")
	private String componentPathSuffix;

	@Parameter(defaultValue = "Components")
	private String defaultComponentGroup;

	@Parameter(defaultValue = "camel-case")
	private String transformerName;

	@Parameter(required = false)
	private List<Dependency> excludeDependencies;

	public void execute() throws MojoExecutionException, MojoFailureException {

		LogSingleton.getInstance().setLogger(getLog());

		try {
<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
			List<String> classpathElements = getClasspathElements();
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
		    List<String> classpathElements = getClasspathElements();
=======
		    @SuppressWarnings("unchecked")
            List<String> classpathElements = project.getCompileClasspathElements();
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

			ClassLoader classLoader = ComponentMojoUtil.getClassLoader(classpathElements, this.getClass()
				.getClassLoader());

			ClassPool classPool = ComponentMojoUtil.getClassPool(classLoader);

			Reflections reflections = ComponentMojoUtil.getReflections(classLoader);

			List<CtClass> classList = ComponentMojoUtil.getAllComponentAnnotations(classPool, reflections, getExcludedClasses());

			WidgetRegistry widgetRegistry = new DefaultWidgetRegistry(classPool, classLoader, reflections);

			Map<String, ComponentNameTransformer> transformers = ComponentMojoUtil.getAllTransformers(classPool,
				reflections);

			ComponentNameTransformer transformer = transformers.get(transformerName);

			if (transformer == null) {
				throw new ConfigurationException("The configured transformer wasn't found");
			}

			ComponentMojoUtil.buildArchiveFileForProjectAndClassList(classList, widgetRegistry, classLoader, classPool,
				new File(project.getBuild().getDirectory()), componentPathBase, componentPathSuffix,
				defaultComponentGroup, getArchiveFileForProject(), getTempArchiveFileForProject(), transformer);

		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
	/**
	 * Returns a list of paths to elements of the classpath for the project. If
	 * dependencies are specified for exclusion via the excludeDependencies POM
	 * configuration, the classpath elements related to the excluded
	 * dependencies are not included in the resultant list.
	 * 
	 * @return
	 * @throws DependencyResolutionRequiredException
	 */
	@SuppressWarnings("unchecked")
	private List<String> getClasspathElements() throws DependencyResolutionRequiredException {
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
	/**
	 * Returns a list of paths to elements of the classpath for the project.  If
	 * dependencies are specified for exclusion via the excludeDependencies POM configuration,
	 * the classpath elements related to the excluded dependencies are not included in
	 * the resultant list.
	 *
	 * @return
	 * @throws DependencyResolutionRequiredException
	 */
    @SuppressWarnings("unchecked")
    private List<String> getClasspathElements() throws DependencyResolutionRequiredException {
=======
	private Set<String> getExcludedClasses() throws DependencyResolutionRequiredException, MalformedURLException {
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
		if (excludeDependencies != null && !excludeDependencies.isEmpty()) {
			List<Artifact> compileArtifacts = project.getCompileArtifacts();
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
        if (excludeDependencies != null && !excludeDependencies.isEmpty()) {
            List<Artifact> compileArtifacts = project.getCompileArtifacts();
=======
	    getLog().debug("Constructing set of excluded Class names");
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
			List<String> classpathElements = new ArrayList<String>();
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
            List<String> classpathElements = new ArrayList<String>();
=======
	    List<String> excludedDependencyPaths = getExcludedDependencyPaths();
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
			classpathElements.add(project.getBuild().getOutputDirectory());
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
            classpathElements.add(project.getBuild().getOutputDirectory());
=======
	    if (excludedDependencyPaths != null) {
    	    ClassLoader exclusionClassLoader = ComponentMojoUtil.getClassLoader(excludedDependencyPaths, this
                .getClass().getClassLoader());
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
			/*
			 * Construct a set representation of the dependency exclusions
			 * mapped by group id and artifact id for easy lookup
			 */
			Set<String> excludedArtifactIdentifiers = new HashSet<String>();
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
            /*
             * Construct a set representation of the dependency exclusions mapped by
             * group id and artifact id for easy lookup
             */
            Set<String> excludedArtifactIdentifiers = new HashSet<String>();
=======
    	    Reflections reflections = ComponentMojoUtil.getReflections(exclusionClassLoader);

    	    Set<String> excludedClassNames = reflections.getStore().getTypesAnnotatedWith(Component.class.getName());

    	    return excludedClassNames;
	    }

	    return null;
	}

	@SuppressWarnings("unchecked")
    private List<String> getExcludedDependencyPaths() throws DependencyResolutionRequiredException {
	    if (excludeDependencies != null && !excludeDependencies.isEmpty()) {
	        getLog().debug("Exclusions Found");

	        List<Artifact> compileArtifacts = project.getCompileArtifacts();

	        List<String> excludedClasspathElements = new ArrayList<String>();

	        Set<String> excludedArtifactIdentifiers = new HashSet<String>();
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

			for (Dependency curDependency : excludeDependencies) {
				excludedArtifactIdentifiers.add(curDependency.getGroupId() + ":" + curDependency.getArtifactId());
			}

			for (Artifact curArtifact : compileArtifacts) {
				String referenceIdentifier = curArtifact.getGroupId() + ":" + curArtifact.getArtifactId();

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
				if (!excludedArtifactIdentifiers.contains(referenceIdentifier)) {
					MavenProject identifiedProject = (MavenProject) project.getProjectReferences().get(
						referenceIdentifier);
					if (identifiedProject != null) {
						classpathElements.add(identifiedProject.getBuild().getOutputDirectory());
					} else {
						File file = curArtifact.getFile();
						if (file == null) {
							throw new DependencyResolutionRequiredException(curArtifact);
						}
						classpathElements.add(file.getPath());
					}
				}
			}
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
                if (!excludedArtifactIdentifiers.contains(referenceIdentifier)) {
                    MavenProject identifiedProject = (MavenProject) project.getProjectReferences().get(referenceIdentifier);
                    if (identifiedProject != null)
                    {
                        classpathElements.add(identifiedProject.getBuild().getOutputDirectory());
                    }
                    else
                    {
                        File file = curArtifact.getFile();
                        if (file == null)
                        {
                            throw new DependencyResolutionRequiredException(curArtifact);
                        }
                        classpathElements.add(file.getPath());
                    }
                }
            }
=======
                if (excludedArtifactIdentifiers.contains(referenceIdentifier)) {
                    MavenProject identifiedProject = (MavenProject) project.getProjectReferences().get(referenceIdentifier);
                    if (identifiedProject != null)
                    {
                        excludedClasspathElements.add(identifiedProject.getBuild().getOutputDirectory());
                        getLog().debug("Excluding " + identifiedProject.getBuild().getOutputDirectory());
                    }
                    else
                    {
                        File file = curArtifact.getFile();
                        if (file == null)
                        {
                            throw new DependencyResolutionRequiredException(curArtifact);
                        }
                        excludedClasspathElements.add(file.getPath());
                        getLog().debug("Excluding " + file.getPath());
                    }
                }
            }
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
			return classpathElements;
		}
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
            return classpathElements;
        }
=======
            return excludedClasspathElements;
	    }
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
		return project.getCompileClasspathElements();

	}
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
        return project.getCompileClasspathElements();

    }
=======
	    return null;
	}
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/16db663469b71596e26c87ce12b67c53668ceda9/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

	private File getArchiveFileForProject() {
		File buildDirectory = new File(project.getBuild().getDirectory());

		String zipFileName = project.getArtifactId() + "-" + project.getVersion() + ".zip";

		getLog().debug("Determined ZIP file name to be " + zipFileName);

		return new File(buildDirectory, zipFileName);
	}

	private File getTempArchiveFileForProject() {
		File buildDirectory = new File(project.getBuild().getDirectory());

		String zipFileName = project.getArtifactId() + "-" + project.getVersion() + "-temp.zip";

		getLog().debug("Temp archive file name " + zipFileName);

		return new File(buildDirectory, zipFileName);
	}

}
