package com.citytechinc.cq.component.maven;
import com.citytechinc.cq.component.dialog.factory.WidgetFactory;
import com.citytechinc.cq.component.dialog.maker.WidgetMaker;
import com.citytechinc.cq.component.dialog.maker.multifield.MultifieldWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.selection.SelectionWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.simple.SimpleWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.smartimage.Html5SmartImageWidgetMaker;
import java.io.File;
import java.util.List;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import com.citytechinc.cq.component.maven.util.ComponentMojoUtil;
import com.citytechinc.cq.component.maven.util.LogSingleton;

@Mojo( name="component", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE )
public class ComponentMojo extends AbstractMojo {

	@Parameter ( defaultValue = "${project}" )
	private MavenProject project;

	@Parameter
	private String componentPathBase;

	@Parameter ( defaultValue = "content" )
	private String componentPathSuffix;

	@Parameter ( defaultValue = "Components" )
	private String defaultComponentGroup;

	@Parameter ( required = false )
	private List<Dependency> includeDependencies;

	@Parameter ( required = false )
	private List<XtypeMapping> xtypeMappings;

	@Parameter ( required = false )
	private List<WidgetMakerMapping> widgetMakerMappings;

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
	@SuppressWarnings({ "unchecked" })
	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			ClassLoader classLoader = getClassLoader(project.getCompileClasspathElements());
			ClassPool classPool = getClassPool(classLoader);
			List<CtClass> compiledClasses = getCompiledClasses(classPool, project.getCompileClasspathElements());
			Map<Class<?>, String> classToXTypeMap = getXTypeMapForCustomXTypeMapping(classLoader);
			Map<String, WidgetMaker> xTypeToWidgetMakerMap = getXTypeToWidgetMakerMap(classLoader);
			buildArchiveFileForProjectAndClassList(compiledClasses, classToXTypeMap, xTypeToWidgetMakerMap, classLoader, classPool);
		} catch (MalformedURLException e) {
			getLog().error(e);
		} catch (DependencyResolutionRequiredException e) {
			getLog().error(e);
		} catch (ClassNotFoundException e) {
			getLog().error(e);
		} catch (InvalidComponentClassException e) {
			getLog().error(e);
		} catch (InvalidComponentFieldException e) {
			getLog().error(e);
		} catch (UnsupportedEncodingException e) {
			getLog().error(e);
		} catch (ParserConfigurationException e) {
			getLog().error(e);
		} catch (TransformerException e) {
			getLog().error(e);
		} catch (OutputFailureException e) {
			getLog().error(e);
		} catch (IOException e) {
			getLog().error(e);
		} catch (NotFoundException e) {
			getLog().error(e);
		} catch (CannotCompileException e) {
			getLog().error(e);
		} catch (SecurityException e) {
			getLog().error(e);
		} catch (NoSuchFieldException e) {
			getLog().error(e);
		} catch (InstantiationException e) {
			getLog().error(e);
		} catch (IllegalAccessException e) {
			getLog().error(e);
		}


	}
||||||| /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/base.java
	@SuppressWarnings({ "unchecked" })
	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			ClassLoader classLoader = getClassLoader(project.getCompileClasspathElements());
			ClassPool classPool = getClassPool(classLoader);
			List<CtClass> compiledClasses = getCompiledClasses(classPool, project.getCompileClasspathElements());
			Map<Class<?>, String> xtypeMap = getXTypeMapForCustomXTypeMapping(classLoader);
			buildArchiveFileForProjectAndClassList(compiledClasses, xtypeMap, classLoader, classPool);
		} catch (MalformedURLException e) {
			getLog().error(e);
		} catch (DependencyResolutionRequiredException e) {
			getLog().error(e);
		} catch (ClassNotFoundException e) {
			getLog().error(e);
		} catch (InvalidComponentClassException e) {
			getLog().error(e);
		} catch (InvalidComponentFieldException e) {
			getLog().error(e);
		} catch (UnsupportedEncodingException e) {
			getLog().error(e);
		} catch (ParserConfigurationException e) {
			getLog().error(e);
		} catch (TransformerException e) {
			getLog().error(e);
		} catch (OutputFailureException e) {
			getLog().error(e);
		} catch (IOException e) {
			getLog().error(e);
		} catch (NotFoundException e) {
			getLog().error(e);
		} catch (CannotCompileException e) {
			getLog().error(e);
		} catch (SecurityException e) {
			getLog().error(e);
		} catch (NoSuchFieldException e) {
			getLog().error(e);
		}


	}
=======
	@SuppressWarnings({ "unchecked" })
	public void execute() throws MojoExecutionException, MojoFailureException {

		LogSingleton.getInstance().setLogger(getLog());

		try {

			ClassLoader classLoader = ComponentMojoUtil.getClassLoader(project.getCompileClasspathElements(), this.getClass().getClassLoader());

			ClassPool classPool = ComponentMojoUtil.getClassPool(classLoader);

			List<CtClass> classList = ComponentMojoUtil.getCompiledClasses(
					classPool,
					project.getCompileClasspathElements(),
					includeDependencies,
					project.getArtifacts());

			Map<Class<?>, String> xtypeMap = ComponentMojoUtil.getXTypeMapForCustomXTypeMapping(classLoader, xtypeMappings);

			ComponentMojoUtil.buildArchiveFileForProjectAndClassList(
					classList,
					xtypeMap,
					classLoader,
					classPool,
					new File(project.getBuild().getDirectory()),
					componentPathBase,
					componentPathSuffix,
					defaultComponentGroup,
					getArchiveFileForProject(),
					getTempArchiveFileForProject());

		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java

	private Map<String, WidgetMaker> getXTypeToWidgetMakerMap(ClassLoader classLoader)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<String, WidgetMaker> xTypeToWidgetMakerMap = new HashMap<String, WidgetMaker>();

		WidgetMaker defaultWidgetMaker = new SimpleWidgetMaker();
		WidgetMaker html5SmartImageWidgetMaker = new Html5SmartImageWidgetMaker();
		WidgetMaker selectionWidgetMaker = new SelectionWidgetMaker();
		WidgetMaker multifieldWidgetMaker = new MultifieldWidgetMaker();

		/*
		 * Set the Default Widget Makers.  This may be overridden by configured makers
		 * if makers are configured for the same xtype
		 */
		xTypeToWidgetMakerMap.put(WidgetFactory.TEXTFIELD_XTYPE, defaultWidgetMaker);
		xTypeToWidgetMakerMap.put(WidgetFactory.NUMBERFIELD_XTYPE, defaultWidgetMaker);
		xTypeToWidgetMakerMap.put(WidgetFactory.PATHFIELD_XTYPE, defaultWidgetMaker);
		xTypeToWidgetMakerMap.put(WidgetFactory.HTML5SMARTIMAGE_XTYPE, html5SmartImageWidgetMaker);
		xTypeToWidgetMakerMap.put(WidgetFactory.SELECTION_XTYPE, selectionWidgetMaker);
		xTypeToWidgetMakerMap.put(WidgetFactory.MULTIFIELD_XTYPE, multifieldWidgetMaker);

		for (WidgetMakerMapping curWidgetMakerMapping : widgetMakerMappings) {
			xTypeToWidgetMakerMap.put(curWidgetMakerMapping.getXtype(), curWidgetMakerMapping.getMaker(classLoader));
		}

		return xTypeToWidgetMakerMap;
	}

	private ClassLoader getClassLoader(List<String> paths) throws MalformedURLException {
		final List<URL> pathURLs = new ArrayList<URL>();

		for (String curPath : paths) {

			URL newClassPathURL = new File(curPath).toURI().toURL();

			getLog().debug("Adding " + newClassPathURL.toString() + " to class loader");

			pathURLs.add(newClassPathURL);

		}

		return new URLClassLoader(pathURLs.toArray(new URL[0]), this.getClass().getClassLoader());
	}

	private List<CtClass> getCompiledClasses(ClassPool classPool, List<String> classPaths)
			throws ClassNotFoundException, IOException, NotFoundException {

		final List<CtClass> classList = new ArrayList<CtClass>();

		String[] extensions = { "class" };

		for (String curClassPath : classPaths) {
			getLog().debug("Current Class Path : " + curClassPath);

			File curClassPathFile = new File(curClassPath);

			/*
			 * Handle loading of those classes compiled as part of this project
			 */
			if (curClassPathFile.isDirectory()) {

				Collection<File> classFiles = FileUtils.listFiles(curClassPathFile, extensions, true );

				for (File curClassFile : classFiles) {
					String curClassString = classNameFromFilePath(curClassFile.getPath(), curClassPath);

					getLog().debug("Loading class : " + curClassString);

					classList.add(classPool.getCtClass(curClassString));
				}

			}

		}

		@SuppressWarnings("unchecked")
		Set<Artifact> artifacts = project.getArtifacts();

		/*
		 * Look through the project artifacts to find any matching the dependency definition
		 * given in the project parameters.  If one does, look through it for classes to add to our
		 * class list.
		 */
		if (this.includeDependencies != null && !this.includeDependencies.isEmpty()) {
			for (Artifact curArtifact : artifacts) {

				if (includeJarClasses(curArtifact)) {
					classList.addAll(getClassListForJarFile(curArtifact.getFile(), classPool));
				}

			}
		}

		return classList;
	}

	private void buildArchiveFileForProjectAndClassList(List<CtClass> classList, Map<Class<?>, String> classToXTypeMap, Map<String, WidgetMaker> xTypeToWidgetMakerMap, ClassLoader classLoader, ClassPool classPool)
			throws OutputFailureException, IOException, InvalidComponentClassException, InvalidComponentFieldException, ParserConfigurationException, TransformerException, ClassNotFoundException, CannotCompileException, NotFoundException, SecurityException, NoSuchFieldException {

		/*
		 * Get existing archive file
		 */
		File existingArchiveFile = getArchiveFileForProject();

		if (!existingArchiveFile.exists()) {
			throw new  OutputFailureException("Archive file does not exist");
		}

		/*
		 * Establish a temporary file where the new archive will be written to
		 */
		File tempArchiveFile = getTempArchiveFileForProject();

		if (tempArchiveFile.exists()) {
			throw new OutputFailureException("Temporary file already exists");
		}

		tempArchiveFile.createNewFile();

		/*
		 * Create archive input stream
		 */
		ZipArchiveInputStream existingInputStream = new ZipArchiveInputStream(new FileInputStream(existingArchiveFile));

		/*
		 * Create a zip archive output stream for the temp file
		 */
		ZipArchiveOutputStream tempOutputStream = new ZipArchiveOutputStream(tempArchiveFile);

		/*
		 * Iterate through all existing entries adding them to the new archive
		 */
		ZipArchiveEntry curArchiveEntry;

		Set<String> existingArchiveEntryNames = new HashSet<String>();

		while ((curArchiveEntry = existingInputStream.getNextZipEntry()) != null) {
			existingArchiveEntryNames.add(curArchiveEntry.getName().toLowerCase());
			getLog().debug("Current File Name: " + curArchiveEntry.getName());
			tempOutputStream.putArchiveEntry(curArchiveEntry);
			IOUtils.copy(existingInputStream, tempOutputStream);
			tempOutputStream.closeArchiveEntry();
		}

		/*
		 * Create content.xml within temp archive
		 */
		buildContentFromClassList(classList, tempOutputStream, existingArchiveEntryNames);

		/*
		 * Create Dialogs within temp archive
		 */
		buildDialogsFromClassList(classList, tempOutputStream, existingArchiveEntryNames, classToXTypeMap, xTypeToWidgetMakerMap, classLoader, classPool);

		/*
		 * Create edit config within temp archive
		 */
		buildEditConfigFromClassList(classList, tempOutputStream, existingArchiveEntryNames);

		/*
		 * Copy temp archive to the original archive position
		 */
		tempOutputStream.finish();
		existingInputStream.close();
		tempOutputStream.close();

		existingArchiveFile.delete();
		tempArchiveFile.renameTo(existingArchiveFile);

	}

	private List<Dialog> buildDialogsFromClassList(List<CtClass> classList, ZipArchiveOutputStream zipOutputStream, Set<String> reservedNames, Map<Class<?>, String> classToXTypeMap, Map<String, WidgetMaker> xTypeToWidgetMakerMap, ClassLoader classLoader, ClassPool classPool)
			throws InvalidComponentClassException, InvalidComponentFieldException, OutputFailureException, IOException, ParserConfigurationException, TransformerException, ClassNotFoundException, CannotCompileException, NotFoundException, SecurityException, NoSuchFieldException {

		final List<Dialog> dialogList = new ArrayList<Dialog>();

		for (CtClass curClass : classList) {
			getLog().debug("Checking class for Component annotation " + curClass.getName());

			Component annotation = (Component) curClass.getAnnotation(Component.class);

			getLog().debug("Annotation : " + annotation);

			if (annotation != null) {
				getLog().debug("Processing Component Class " + curClass);
				Dialog builtDialog = buildDialogFromClass(curClass, classToXTypeMap, xTypeToWidgetMakerMap, classLoader, classPool);
				dialogList.add(builtDialog);
				File dialogFile = writeDialogeToFile(builtDialog, curClass);
				writeDialogToArchiveFile(dialogFile, curClass, zipOutputStream, reservedNames);
			}
		}

		return dialogList;

	}

	private Dialog buildDialogFromClass(
			CtClass curClass,
			Map<Class<?>, String> classToXTypeMap,
			Map<String, WidgetMaker> xTypeToWidgetMakerMap,
			ClassLoader classLoader,
			ClassPool classPool)
			throws InvalidComponentClassException, InvalidComponentFieldException, ClassNotFoundException, CannotCompileException, NotFoundException, SecurityException, NoSuchFieldException {

		return DialogFactory.make(curClass, classToXTypeMap, xTypeToWidgetMakerMap, classLoader, classPool);

	}

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

	private File getOutputDirectoryForComponentClass(CtClass componentClass) throws OutputFailureException, ClassNotFoundException {
		File buildDirectory = new File(project.getBuild().getDirectory());

		String dialogFilePath = OUTPUT_PATH + "/" + componentPathBase + "/" + getComponentPathSuffixForComponentClass(componentClass) + "/" + getComponentNameForComponentClass(componentClass);

		File componentOutputDirectory = new File(buildDirectory, dialogFilePath);

		if (!componentOutputDirectory.exists()) {
			if (!componentOutputDirectory.mkdirs()) {
				throw new OutputFailureException("Failure creating output directory for Component");
			}
		}

		return componentOutputDirectory;
	}

}
