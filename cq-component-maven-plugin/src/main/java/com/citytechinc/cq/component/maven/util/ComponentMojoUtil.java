package com.citytechinc.cq.component.maven.util;

import com.citytechinc.cq.classpool.ClassLoaderClassPool;
import com.citytechinc.cq.component.annotations.Component;
import com.citytechinc.cq.component.annotations.config.Widget;
import com.citytechinc.cq.component.annotations.transformer.Transformer;
import com.citytechinc.cq.component.content.util.ContentUtil;
import com.citytechinc.cq.component.dialog.AbstractWidget;
import com.citytechinc.cq.component.dialog.ComponentNameTransformer;
import com.citytechinc.cq.component.dialog.exception.InvalidComponentClassException;
import com.citytechinc.cq.component.dialog.exception.InvalidComponentFieldException;
import com.citytechinc.cq.component.dialog.exception.OutputFailureException;
import com.citytechinc.cq.component.dialog.maker.WidgetMaker;
import com.citytechinc.cq.component.dialog.util.DialogUtil;
import com.citytechinc.cq.component.dialog.widget.WidgetRegistry;
import com.citytechinc.cq.component.editconfig.util.EditConfigUtil;
import com.citytechinc.cq.component.util.WidgetConfigHolder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;


public class ComponentMojoUtil {
	private static final String OUTPUT_PATH = "tempComponentConfig";

	private ComponentMojoUtil() {
	}

	public static final LogSingleton getLog() {
		return LogSingleton.getInstance();
	}

	/**
	 * Constructs a Class Loader based on a list of paths to classes and a
	 * parent Class Loader
	 * 
	 * @param paths
	 * @param mojoClassLoader
	 * @return The constructed ClassLoader
	 * @throws MalformedURLException
	 */
	public static ClassLoader getClassLoader(List<String> paths, ClassLoader mojoClassLoader)
		throws MalformedURLException {
		final List<URL> pathURLs = new ArrayList<URL>();

		for (String curPath : paths) {

			URL newClassPathURL = new File(curPath).toURI().toURL();

			getLog().debug("Adding " + newClassPathURL.toString() + " to class loader");

			pathURLs.add(newClassPathURL);

		}

		return new URLClassLoader(pathURLs.toArray(new URL[0]), mojoClassLoader);
	}

	/**
	 * Constructs as Javassist ClassPool which pulls resources based on the
	 * paths provided by the passed in ClassLoader
	 * 
	 * @param classLoader
	 * @return The constructed ClassPool
	 * @throws NotFoundException
	 */
	public static ClassPool getClassPool(ClassLoader loader) {
		ClassPool classPool = new ClassLoaderClassPool(loader);
		return classPool;
	}

	/**
	 * Constructs a fully qualified class name based on the path to the class
	 * file
	 * 
	 * @param filePath
	 * @param rootPath
	 * @return The constructed class name
	 */
	protected static String classNameFromFilePath(String filePath, String rootPath) {
		String placeholder = filePath;

		if (StringUtils.isNotEmpty(rootPath) && placeholder.startsWith(rootPath)) {
			placeholder = placeholder.replace(rootPath, "");
		}

		if (placeholder.charAt(0) == '/') {
			placeholder = placeholder.substring(1);
		}

		if (placeholder.endsWith(".class")) {
			placeholder = placeholder.substring(0, placeholder.length() - ".class".length());
		}

		getLog().debug("Class pre replace " + placeholder);

		return placeholder.replace('/', '.');
	}

	/**
	 * Add files to the already constructed Archive file by creating a new
	 * Archive file, appending the contents of the existing Archive file to it,
	 * and then adding additional entries for the newly constructed artifacts.
	 *
	 * @param classList
	 * 		
	 * @param xtypeMap
	 * 		
	 * @param classLoader
	 * 		
	 * @param classPool
	 * 		
	 * @param buildDirectory
	 * 		
	 * @param componentPathBase
	 * 		
	 * @param defaultComponentPathSuffix
	 * 		
	 * @param defaultComponentGroup
	 * 		
	 * @param existingArchiveFile
	 * 		
	 * @param tempArchiveFile
	 * 		
	 * @throws OutputFailureException
	 * 		
	 * @throws IOException
	 * 		
	 * @throws InvalidComponentClassException
	 * 		
	 * @throws InvalidComponentFieldException
	 * 		
	 * @throws ParserConfigurationException
	 * 		
	 * @throws TransformerException
	 * 		
	 * @throws ClassNotFoundException
	 * 		
	 * @throws CannotCompileException
	 * 		
	 * @throws NotFoundException
	 * 		
	 * @throws SecurityException
	 * 		
	 * @throws NoSuchFieldException
	 * 		
	 * @throws IllegalArgumentException
	 * 		
	 * @throws IllegalAccessException
	 * 		
	 * @throws InvocationTargetException
	 * 		
	 * @throws NoSuchMethodException
	 * 		
	 * @throws InstantiationException
	 * 		
	 */
	public static void buildArchiveFileForProjectAndClassList(List<CtClass> classList, WidgetRegistry widgetRegistry, ClassLoader classLoader, ClassPool classPool, File buildDirectory, String componentPathBase, String defaultComponentPathSuffix, String defaultComponentGroup, File existingArchiveFile, File tempArchiveFile, ComponentNameTransformer transformer) throws InstantiationException, OutputFailureException, IOException, InvalidComponentClassException, InvalidComponentFieldException, ParserConfigurationException, TransformerException, ClassNotFoundException, CannotCompileException, NotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (!existingArchiveFile.exists()) {
			throw new OutputFailureException("Archive file does not exist");
		}
		if (tempArchiveFile.exists()) {
			tempArchiveFile.delete();
		}
		tempArchiveFile.createNewFile();
		deleteTemporaryComponentOutputDirectory(buildDirectory);
		/* Create archive input stream */
		ZipArchiveInputStream existingInputStream = new ZipArchiveInputStream(new FileInputStream(existingArchiveFile));
		/* Create a zip archive output stream for the temp file */
		ZipArchiveOutputStream tempOutputStream = new ZipArchiveOutputStream(tempArchiveFile);
		/* Iterate through all existing entries adding them to the new archive */
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
		ContentUtil.buildContentFromClassList(classList, tempOutputStream, existingArchiveEntryNames, buildDirectory, componentPathBase, defaultComponentPathSuffix, defaultComponentGroup, transformer);
		/*
		 * Create Dialogs within temp archive
		 */
		DialogUtil.buildDialogsFromClassList(transformer, classList, tempOutputStream, existingArchiveEntryNames, widgetRegistry, classLoader, classPool, buildDirectory, componentPathBase, defaultComponentPathSuffix);
		/* Create edit config within temp archive */
		EditConfigUtil.buildEditConfigFromClassList(classList, tempOutputStream, existingArchiveEntryNames, buildDirectory, componentPathBase, defaultComponentPathSuffix, transformer);
		/* Copy temp archive to the original archive position */
		tempOutputStream.finish();
		existingInputStream.close();
		tempOutputStream.close();
		existingArchiveFile.delete();
		tempArchiveFile.renameTo(existingArchiveFile);
	}

	/**
	 * Finds and retrieves the constructed CQ Package archive file for the
	 * project
	 * 
	 * @param project
	 * @return The archive file found for the project
	 */
	protected static File getArchiveFileForProject(MavenProject project) {
		File buildDirectory = new File(project.getBuild().getDirectory());

		String zipFileName = project.getArtifactId() + "-" + project.getVersion() + ".zip";

		getLog().debug("Determined ZIP file name to be " + zipFileName);

		return new File(buildDirectory, zipFileName);
	}

	/**
	 * Create a temporary archive file which will live alongside the constructed
	 * project CQ5 Package archive.
	 * 
	 * @param project
	 * @return The temporary archive file
	 */
	protected static File getTempArchiveFileForProject(MavenProject project) {
		File buildDirectory = new File(project.getBuild().getDirectory());

		String zipFileName = project.getArtifactId() + "-" + project.getVersion() + "-temp.zip";

		getLog().debug("Temp archive file name " + zipFileName);

		return new File(buildDirectory, zipFileName);
	}

	/**
	 * Determine the appropriate output directory for a component's artifacts
	 * based on the component class as well as POM configuration.
	 * 
	 * @param componentClass
	 * @param project
	 * @param componentPathBase
	 * @return The determined output directory
	 * @throws OutputFailureException
	 * @throws ClassNotFoundException
	 */
	public static File getOutputDirectoryForComponentClass(ComponentNameTransformer transformer, CtClass componentClass, File buildDirectory, String componentPathBase, String defaultComponentPathSuffix) throws OutputFailureException, ClassNotFoundException {
		// File buildDirectory = new File(project.getBuild().getDirectory());
		String dialogFilePath = (((((OUTPUT_PATH + "/") + getComponentBasePathForComponentClass(componentClass, componentPathBase)) + "/") + getComponentPathSuffixForComponentClass(componentClass, defaultComponentPathSuffix)) + "/") + getComponentNameForComponentClass(transformer, componentClass);
		File componentOutputDirectory = new File(buildDirectory, dialogFilePath);
		if (!componentOutputDirectory.exists()) {
			if (!componentOutputDirectory.mkdirs()) {
				throw new OutputFailureException("Failure creating output directory for Component");
			}
		}
		return componentOutputDirectory;
	}

	public static String getComponentBasePathForComponentClass(CtClass componentClass, String componentPathBase)
		throws ClassNotFoundException {
		Component componentAnnotation = (Component) componentClass.getAnnotation(Component.class);

		if (componentAnnotation != null) {
			String basePath = componentAnnotation.basePath();

			if (StringUtils.isNotEmpty(basePath)) {
				return basePath;
			}
		}

		return componentPathBase;
	}

	/**
	 * Deletes the temporary output directory which is created as part of the
	 * build process to temporarily hold the generated files for components.
	 * 
	 * @param buildDirectory
	 * @throws IOException
	 */
	protected static void deleteTemporaryComponentOutputDirectory(File buildDirectory) throws IOException {
		File componentOutputDirectory = new File(buildDirectory, OUTPUT_PATH);

		if (componentOutputDirectory.exists()) {
			FileUtils.deleteDirectory(componentOutputDirectory);
		}
	}

	/**
	 * Determines the suffix portion of the path leading to the artifacts of a
	 * particular component
	 * 
	 * @param componentClass
	 * @param defaultComponentPathSuffix
	 * @return The determined suffix
	 * @throws ClassNotFoundException
	 */
	public static String getComponentPathSuffixForComponentClass(CtClass componentClass,
		String defaultComponentPathSuffix) throws ClassNotFoundException {
		Component componentAnnotation = (Component) componentClass.getAnnotation(Component.class);

		if (componentAnnotation != null) {
			String path = componentAnnotation.path();

			if (StringUtils.isNotEmpty(path)) {
				return path;
			}
		}

		return defaultComponentPathSuffix;
	}

	/**
	 * Determines the name of the component class for use in constructing file
	 * paths
	 * 
	 * @param componentClass
	 * @return The determined name
	 * @throws ClassNotFoundException
	 */
	public static String getComponentNameForComponentClass(ComponentNameTransformer transformer, CtClass componentClass)
		throws ClassNotFoundException {
		Component componentAnnotation = (Component) componentClass.getAnnotation(Component.class);

		if (componentAnnotation != null) {
			String name = componentAnnotation.name();

			if (StringUtils.isNotEmpty(name)) {
				return name;
			}
		}

		return transformer.transform(componentClass.getSimpleName());
	}

	/**
	 * Constructs a list of widget configurations based on the information
	 * provided by classes annotated as Widgets.
	 * 
	 * @param classPool
	 * @param classLoader
	 * @param reflections
	 * @return The constructed widget configurations
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 * @throws MalformedURLException
	 */
	public static List<WidgetConfigHolder> getAllWidgetAnnotations(ClassPool classPool, ClassLoader classLoader, Reflections reflections) throws ClassNotFoundException, NotFoundException, MalformedURLException {
		List<WidgetConfigHolder> builtInWidgets = new ArrayList<WidgetConfigHolder>();
		for (Class<?> c : reflections.getTypesAnnotatedWith(Widget.class)) {
			CtClass clazz = classPool.getCtClass(c.getName());
			Widget widgetAnnotation = ((Widget) (clazz.getAnnotation(Widget.class)));
			Class<? extends Annotation> annotationClass = widgetAnnotation.annotationClass();
			Class<? extends WidgetMaker> makerClass = widgetAnnotation.makerClass();
			Class<? extends AbstractWidget> widgetClass = classLoader.loadClass(clazz.getName()).asSubclass(AbstractWidget.class);
			WidgetConfigHolder widgetConfig = new WidgetConfigHolder(annotationClass, widgetClass, makerClass, widgetAnnotation.xtype(), widgetAnnotation.ranking());
			builtInWidgets.add(widgetConfig);
		}
		return builtInWidgets;
	}

	/**
	 * Retrieves a List of all classes which are annotated as Components and are
	 * within the scope of the provided Reflections purview.
	 * 
	 * @param classPool
	 * @param reflections
	 * @return A List of classes annotated as Components
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 * @throws MalformedURLException
	 */
	public static List<CtClass> getAllComponentAnnotations(ClassPool classPool, Reflections reflections)
		throws ClassNotFoundException, NotFoundException, MalformedURLException {
		List<CtClass> classes = new ArrayList<CtClass>();

		for (Class<?> c : reflections.getTypesAnnotatedWith(Component.class)) {
			classes.add(classPool.getCtClass(c.getName()));
		}
		return classes;
	}

	/**
	 * Retrieves a List of all classes which are annotated as Transformers and
	 * are within the scope of the provided Reflections purview.
	 * 
	 * @param classPool
	 * @param reflections
	 * @return A Map of transformer names to transformers
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 * @throws MalformedURLException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static Map<String, ComponentNameTransformer> getAllTransformers(ClassPool classPool, Reflections reflections)
		throws ClassNotFoundException, NotFoundException, MalformedURLException, InstantiationException,
		IllegalAccessException {
		Map<String, ComponentNameTransformer> transformers = new HashMap<String, ComponentNameTransformer>();

		for (Class<?> c : reflections.getTypesAnnotatedWith(Transformer.class)) {
			if (Arrays.asList(c.getInterfaces()).contains(ComponentNameTransformer.class)) {
				CtClass ctclass = classPool.getCtClass(c.getName());
				Transformer transformer = (Transformer) ctclass.getAnnotation(Transformer.class);
				transformers.put(transformer.value(), (ComponentNameTransformer) c.newInstance());
			}
		}

		return transformers;
	}

	/**
	 * Constructs a list of all fields contained in the provided CtClass and any
	 * of its parent classes.
	 * 
	 * @param ctClass
	 * @return The constructed list of fields
	 * @throws NotFoundException
	 */
	public static List<CtField> collectFields(CtClass ctClass) throws NotFoundException {
		List<CtField> fields = new ArrayList<CtField>();
		if (ctClass != null) {
			fields.addAll(Arrays.asList(ctClass.getDeclaredFields()));
			fields.addAll(collectFields(ctClass.getSuperclass()));
		}
		return fields;
	}

	public static List<CtMethod> collectMethods(CtClass ctClass) {
		List<CtMethod> methods = new ArrayList<CtMethod>();
		if (ctClass != null) {
			methods.addAll(Arrays.asList(ctClass.getMethods()));
		}
		return methods;
	}

	/**
	 * Constructs a Reflections object suitable for reflecting on classes
	 * accessible via the provided ClassLoader
	 * 
	 * @param classLoader The ClassLoader containing classes to be reflected
	 *            upon
	 * @return The constructed Reflections object
	 */
	public static Reflections getReflections(ClassLoader classLoader) {
		Reflections reflections = new Reflections(new ConfigurationBuilder().addClassLoader(classLoader)
			.setUrls(ClasspathHelper.forClassLoader(new ClassLoader[] { classLoader }))
			.setScanners(new TypeAnnotationsScanner()));
		return reflections;
	}
}