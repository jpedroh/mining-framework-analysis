package com.citytechinc.cq.component.maven;
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
import com.citytechinc.cq.component.dialog.factory.WidgetFactory;
import com.citytechinc.cq.component.dialog.maker.WidgetMaker;
import com.citytechinc.cq.component.dialog.maker.multifield.MultifieldWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.selection.SelectionWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.simple.SimpleWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.smartimage.Html5SmartImageWidgetMaker;

@Mojo(name = "component", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE) public class ComponentMojo extends AbstractMojo {
  @Parameter(defaultValue = "${project}") private MavenProject project;

  @Parameter private String componentPathBase;

  @Parameter(defaultValue = "content") private String componentPathSuffix;

  @Parameter(defaultValue = "Components") private String defaultComponentGroup;

  @Parameter(required = false) private List<Dependency> includeDependencies;

  @Parameter(required = false) private List<XtypeMapping> xtypeMappings;

  @Parameter(required = false) private List<WidgetMakerMapping> widgetMakerMappings;

  @SuppressWarnings(value = { "unchecked" }) public void execute() throws MojoExecutionException, MojoFailureException {
    LogSingleton.getInstance().setLogger(getLog());
    try {
      ClassLoader classLoader = ComponentMojoUtil.getClassLoader(project.getCompileClasspathElements(), this.getClass().getClassLoader());
      ClassPool classPool = ComponentMojoUtil.getClassPool(classLoader);
      List<CtClass> classList = ComponentMojoUtil.getCompiledClasses(classPool, project.getCompileClasspathElements(), includeDependencies, project.getArtifacts());
      Map<Class<?>, String> 
<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
      classToXTypeMap = getXTypeMapForCustomXTypeMapping(classLoader)
=======
      xtypeMap = ComponentMojoUtil.getXTypeMapForCustomXTypeMapping(classLoader, xtypeMappings)
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java
      ;
      Map<String, WidgetMaker> xTypeToWidgetMakerMap = getXTypeToWidgetMakerMap(classLoader);

<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
      buildArchiveFileForProjectAndClassList(compiledClasses, classToXTypeMap, xTypeToWidgetMakerMap, classLoader, classPool)
=======
      ComponentMojoUtil.buildArchiveFileForProjectAndClassList(classList, xtypeMap, classLoader, classPool, new File(project.getBuild().getDirectory()), componentPathBase, componentPathSuffix, defaultComponentGroup, getArchiveFileForProject(), getTempArchiveFileForProject())
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java
      ;
    } catch (InstantiationException e) {
      getLog().error(e);
    } catch (
<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
    IllegalAccessException
=======
    Exception
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/right.java
     e) {
      getLog().error(e.getMessage(), e);
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private Map<String, WidgetMaker> getXTypeToWidgetMakerMap(ClassLoader classLoader) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    Map<String, WidgetMaker> xTypeToWidgetMakerMap = new HashMap<String, WidgetMaker>();
    WidgetMaker defaultWidgetMaker = new SimpleWidgetMaker();
    WidgetMaker html5SmartImageWidgetMaker = new Html5SmartImageWidgetMaker();
    WidgetMaker selectionWidgetMaker = new SelectionWidgetMaker();
    WidgetMaker multifieldWidgetMaker = new MultifieldWidgetMaker();
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


<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
  /**
	 * Add files to the already constructed Archive file by creating a new Archive file, appending the contents
	 * of the existing Archive file to it, and then adding additional entries for the newly constructed artifacts.
	 *
	 * @param classList
	 * @throws OutputFailureException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws InvalidComponentFieldException
	 * @throws InvalidComponentClassException
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
  private void buildArchiveFileForProjectAndClassList(List<CtClass> classList, Map<Class<?>, String> classToXTypeMap, Map<String, WidgetMaker> xTypeToWidgetMakerMap, ClassLoader classLoader, ClassPool classPool) throws OutputFailureException, IOException, InvalidComponentClassException, InvalidComponentFieldException, ParserConfigurationException, TransformerException, ClassNotFoundException, CannotCompileException, NotFoundException, SecurityException, NoSuchFieldException {
    File existingArchiveFile = getArchiveFileForProject();
    if (!existingArchiveFile.exists()) {
      throw new OutputFailureException("Archive file does not exist");
    }
    File tempArchiveFile = getTempArchiveFileForProject();
    if (tempArchiveFile.exists()) {
      throw new OutputFailureException("Temporary file already exists");
    }
    tempArchiveFile.createNewFile();
    ZipArchiveInputStream existingInputStream = new ZipArchiveInputStream(new FileInputStream(existingArchiveFile));
    ZipArchiveOutputStream tempOutputStream = new ZipArchiveOutputStream(tempArchiveFile);
    ZipArchiveEntry curArchiveEntry;
    Set<String> existingArchiveEntryNames = new HashSet<String>();
    while ((curArchiveEntry = existingInputStream.getNextZipEntry()) != null) {
      existingArchiveEntryNames.add(curArchiveEntry.getName().toLowerCase());
      getLog().debug("Current File Name: " + curArchiveEntry.getName());
      tempOutputStream.putArchiveEntry(curArchiveEntry);
      IOUtils.copy(existingInputStream, tempOutputStream);
      tempOutputStream.closeArchiveEntry();
    }
    buildContentFromClassList(classList, tempOutputStream, existingArchiveEntryNames);
    buildDialogsFromClassList(classList, tempOutputStream, existingArchiveEntryNames, classToXTypeMap, xTypeToWidgetMakerMap, classLoader, classPool);
    buildEditConfigFromClassList(classList, tempOutputStream, existingArchiveEntryNames);
    tempOutputStream.finish();
    existingInputStream.close();
    tempOutputStream.close();
    existingArchiveFile.delete();
    tempArchiveFile.renameTo(existingArchiveFile);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.



<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
  private List<Dialog> buildDialogsFromClassList(List<CtClass> classList, ZipArchiveOutputStream zipOutputStream, Set<String> reservedNames, Map<Class<?>, String> classToXTypeMap, Map<String, WidgetMaker> xTypeToWidgetMakerMap, ClassLoader classLoader, ClassPool classPool) throws InvalidComponentClassException, InvalidComponentFieldException, OutputFailureException, IOException, ParserConfigurationException, TransformerException, ClassNotFoundException, CannotCompileException, NotFoundException, SecurityException, NoSuchFieldException {
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
=======
>>>>>>> Unknown file: This is a bug in JDime.



<<<<<<< /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/maven/ComponentMojo.java/left.java
  private Dialog buildDialogFromClass(CtClass curClass, Map<Class<?>, String> classToXTypeMap, Map<String, WidgetMaker> xTypeToWidgetMakerMap, ClassLoader classLoader, ClassPool classPool) throws InvalidComponentClassException, InvalidComponentFieldException, ClassNotFoundException, CannotCompileException, NotFoundException, SecurityException, NoSuchFieldException {
    return DialogFactory.make(curClass, classToXTypeMap, xTypeToWidgetMakerMap, classLoader, classPool);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


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