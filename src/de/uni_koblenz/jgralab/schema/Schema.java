/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         https://github.com/jgralab/jgralab
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.schema;

import java.io.IOException;

import java.util.Collection;

import de.uni_koblenz.jgralab.Graph;

import de.uni_koblenz.jgralab.ImplementationType;

import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;

import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;

import de.uni_koblenz.jgralab.schema.impl.compilation.InMemoryJavaSourceFile;

/**
 * The class Schema represents a grUML Schema.
 * 
 * @author ist@uni-koblenz.de
 * 
 */

/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         https://github.com/jgralab/jgralab
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

import java.io.DataOutputStream;

import java.lang.reflect.Method;

import java.util.Arrays;

import java.util.List;

import java.util.Map;

import java.util.Set;

import java.util.TreeSet;

import java.util.Vector;

import de.uni_koblenz.jgralab.Edge;

import de.uni_koblenz.jgralab.GraphFactory;

import de.uni_koblenz.jgralab.GraphIOException;

import de.uni_koblenz.jgralab.ProgressFunction;

import de.uni_koblenz.jgralab.Vertex;

/**
 * The class Schema represents a grUML Schema.
 *
 * @author ist@uni-koblenz.de
 *
 */
public interface Schema extends Comparable<Schema> {

	/**
	 * Reserved Java words that are not allowed as a name for any NamedElement
	 * and/or Schema.
	 */
	public static final Set<String> RESERVED_JAVA_WORDS = new TreeSet<String>(
			Arrays.asList(new String[] { "abstract", "continue", "for", "new",
					"switch", "assert", "default", "goto", "package",
					"synchronized", "boolean", "do", "if", "private", "this",
					"break", "double", "implements", "protected", "throw",
					"byte", "else", "import", "public", "throws", "case",
					"enum", "instanceof", "return", "transient", "catch",
					"extends", "int", "short", "try", "char", "final",
					"interface", "static", "void", "class", "finally", "long",
					"strictfp", "volatile", "const", "float", "native",
					"super", "while" }));

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	/**
	 * Checks if this schema supports enumeration constants with lowercase
	 * letters.
	 * 
	 * @return true iff the schema allows lowercase enum constants
	 */
	public boolean allowsLowercaseEnumConstants();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	public static final Set<String> basicDomains = new TreeSet<String>(Arrays
			.asList(new String[] { "Boolean", "Integer", "Long", "String",
					"Double", "Object" }));

	public static final Set<String> reservedTGWords = new TreeSet<String>(Arrays
			.asList(new String[] { "abstract", "aggregate", "AggregationClass",
					"Boolean", "CompositionClass", "Double", "EdgeClass",
					"EnumDomain", "f", "from", "Graph", "GraphClass",
					"Integer", "List", "Long", "Object", "Package",
					"RecordDomain", "redefines", "role", "Schema", "Set",
					"String", "t", "to", "VertexClass", "id", "reversed", "normal" }));
=======
	/**
	 * Checks if this schema supports enumeration constants with lowercase
	 * letters.
	 *
	 * @return true iff the schema allows lowercase enum constants
	 */
	public boolean allowsLowercaseEnumConstants();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * After creating the schema, this command serves to generate code for the
	 * schema classes, contained in {@code JavaSourceFromString} objects.
	 * 
	 * @param config
	 *            a {@link CodeGeneratorConfiguration} specifying the requested
	 *            implementation variant
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	 * sets the factory that is used to create graphs, vertices and edges
=======
	 * After creating the schema, this command serves to generate code for the
	 * schema classes, contained in {@code JavaSourceFromString} objects.
	 *
	 * @param config
	 *            a {@link CodeGeneratorConfiguration} specifying the requested
	 *            implementation variant
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	public Vector<InMemoryJavaSourceFile> commit(CodeGeneratorConfiguration config);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	public void setGraphFactory(GraphFactory factory);
=======
	public Vector<InMemoryJavaSourceFile> commit(
			CodeGeneratorConfiguration config);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * after creating the schema, this command serves to make it permanent,
	 * schema classes are generated to represent the object oriented access
	 * layer
	 * 
	 * @param path
	 *            the path to the schema classes which are to be generated
	 * @param config
	 *            a {@link CodeGeneratorConfiguration} specifying the requested
	 *            implementation variant
	 * 
	 * @throws GraphIOException
	 *             if an error occured during optional compilation
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	 * @return the factory that is used to create graphs, vertices and edges
=======
	 * after creating the schema, this command serves to make it permanent,
	 * schema classes are generated to represent the object oriented access
	 * layer
	 *
	 * @param path
	 *            the path to the schema classes which are to be generated
	 * @param config
	 *            a {@link CodeGeneratorConfiguration} specifying the requested
	 *            implementation variant
	 *
	 * @throws GraphIOException
	 *             if an error occured during optional compilation
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	public void commit(String path, CodeGeneratorConfiguration config)
			throws GraphIOException;

	/**
	 * after creating the schema, this command serves to make it permanent,
	 * schema classes are generated to represent the object oriented access
	 * layer
	 * 
	 * @param path
	 *            the path to the schema classes which are to be generated
	 * @param config
	 *            a {@link CodeGeneratorConfiguration} specifying the requested
	 *            implementation variant
	 * @param progressFunction
	 *            an optional progressfunction
	 * @throws GraphIOException
	 *             if an error occured during optional compilation
	 */
	public void commit(String path, CodeGeneratorConfiguration config,
			ProgressFunction progressFunction) throws GraphIOException;

	/**
	 * After creating the schema, this command serves to generate and compile
	 * code for the schema classes. The class files are not written to disk, but
	 * only held in memory.
	 * 
	 * @param config
	 *            configures the CodeGenerator and which classes and methods to
	 *            be created
	 */
	public void compile(CodeGeneratorConfiguration config);

	/**
	 * Generates Java classes in a temp directory, compiles them, and packs them
	 * in a JAR file.
	 * 
	 * @param config
	 *            the {@link CodeGeneratorConfiguration} to be used for class
	 *            generation
	 * @param jarFileName
	 *            the name of the JAR file to be created
	 * @throws IOException
	 * @throws GraphIOException
	 */
	public void createJAR(CodeGeneratorConfiguration config, String jarFileName)
			throws IOException, GraphIOException;
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	public GraphFactory getGraphFactory();
=======
	public void commit(String path, CodeGeneratorConfiguration config)
			throws GraphIOException;

	/**
	 * after creating the schema, this command serves to make it permanent,
	 * schema classes are generated to represent the object oriented access
	 * layer
	 *
	 * @param path
	 *            the path to the schema classes which are to be generated
	 * @param config
	 *            a {@link CodeGeneratorConfiguration} specifying the requested
	 *            implementation variant
	 * @param progressFunction
	 *            an optional progressfunction
	 * @throws GraphIOException
	 *             if an error occured during optional compilation
	 */
	public void commit(String path, CodeGeneratorConfiguration config,
			ProgressFunction progressFunction) throws GraphIOException;

	/**
	 * After creating the schema, this command serves to generate and compile
	 * code for the schema classes. The class files are not written to disk, but
	 * only held in memory.
	 *
	 * @param config
	 *            configures the CodeGenerator and which classes and methods to
	 *            be created
	 */
	public void compile(CodeGeneratorConfiguration config);

	/**
	 * Generates Java classes in a temp directory, compiles them, and packs them
	 * in a JAR file.
	 *
	 * @param config
	 *            the {@link CodeGeneratorConfiguration} to be used for class
	 *            generation
	 * @param jarFileName
	 *            the name of the JAR file to be created
	 * @throws IOException
	 * @throws GraphIOException
	 */
	public void createJAR(CodeGeneratorConfiguration config, String jarFileName)
			throws IOException, GraphIOException;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java

	/**
	 * Creates a new Attribute <code>name</code> with domain <code>dom</code>.
	 *
	 * @param name
	 *            the attribute name
	 * @param dom
	 *            the domain for the attribute
	 * @param aec
	 *            the {@link AttributedElementClass} owning the
	 *            {@link Attribute}
	 * @param defaultValueAsString
	 *            a String for the default value in TG value syntax, or null if
	 *            no default value is to be set
	 * @return the new Attribute
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	public Attribute createAttribute(String name, Domain dom,
			AttributedElementClass aec, String defaultValueAsString);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	public Attribute createAttribute(String name, Domain dom);
=======
	public Attribute createAttribute(String name, Domain dom,
			AttributedElementClass<?, ?> aec, String defaultValueAsString);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * Builds a new enumeration domain, multiple domains may exist in a schema.
	 * 
	 * @param qualifiedName
	 *            the qualified name of the {@link EnumDomain}
	 * @return a new enumeration domain
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	 * builds a new graphclass and saves it to the schema object
	 * 
	 * @param id
	 *            the unique identifier of the graphclass in the schema
	 * @return the new graphclass
=======
	 * Builds a new enumeration domain, multiple domains may exist in a schema.
	 *
	 * @param qualifiedName
	 *            the qualified name of the {@link EnumDomain}
	 * @return a new enumeration domain
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 */
	public EnumDomain createEnumDomain(String qualifiedName);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * Builds a new enumeration domain, multiple domains may exist in a schema
	 * 
	 * @param qualifiedName
	 *            the qualified name of the {@link EnumDomain}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	 * builds a new enumeration domain, multiple domains may exist in a schema
	 * 
	 * @param qn
	 *            a unique name which identifies the enum in the schema
=======
	 * Builds a new enumeration domain, multiple domains may exist in a schema
	 *
	 * @param qualifiedName
	 *            the qualified name of the {@link EnumDomain}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 * @param enumComponents
	 *            a list of strings which state the constants of the enumeration
	 * @return a new enumeration domain
	 */
	public EnumDomain createEnumDomain(String qualifiedName,
			List<String> enumComponents);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * Creates a new {@link GraphClass} and saves it to the schema object
	 * 
	 * @param simpleName
	 *            the simple name of the graphclass in the schema
	 * @return the new graphclass
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	 * builds a new enumeration domain, multiple domains may exist in a schema
	 * 
	 * @param qn
	 *            a unique name which identifies the enum in the schema
	 * @return a new enumeration domain
=======
	 * Creates a new {@link GraphClass} and saves it to the schema object
	 *
	 * @param simpleName
	 *            the simple name of the graphclass in the schema
	 * @return the new graphclass
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 */
	public GraphClass createGraphClass(String simpleName);

	/**
	 * builds a new list domain, multiple domains may exist in a schema
	 *
	 * @param baseDomain
	 *            the domain of which all elements in the list are built of
	 * @return the new list domain
	 */
	public ListDomain createListDomain(Domain baseDomain);

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * builds a new map domain, multiple domains may exist in a schema
	 * 
	 * @param keyDomain
	 *            the domain of which all keys in the set are built of
	 * @param valueDomain
	 *            the domain of which all values in the set are built of
	 * @return the new map domain
	 */
	public MapDomain createMapDomain(Domain keyDomain, Domain valueDomain);

	/**
	 * builds a new record domain, multiple domains may exist in a schema
	 * 
	 * @param qualifiedName
	 *            the qualified name of this RecordDomain
	 * @return the new record domain
	 */
	public RecordDomain createRecordDomain(String qualifiedName);

	/**
	 * builds a new record domain, multiple domains may exist in a schema
	 * 
	 * @param qualifiedName
	 *            the qualified name of this RecordDomain
	 * @param recordComponents
	 *            a list of record domain components which state the individual
	 *            components of the record, each consisting of a name and a
	 *            domain, and possibly a default value
	 * @return the new record domain
	 */
	public RecordDomain createRecordDomain(String qualifiedName,
			Collection<RecordComponent> recordComponents);

	/**
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
=======
	 * builds a new map domain, multiple domains may exist in a schema
	 *
	 * @param keyDomain
	 *            the domain of which all keys in the set are built of
	 * @param valueDomain
	 *            the domain of which all values in the set are built of
	 * @return the new map domain
	 */
	public MapDomain createMapDomain(Domain keyDomain, Domain valueDomain);

	/**
	 * builds a new record domain, multiple domains may exist in a schema
	 *
	 * @param qualifiedName
	 *            the qualified name of this RecordDomain
	 * @return the new record domain
	 */
	public RecordDomain createRecordDomain(String qualifiedName);

	/**
	 * builds a new record domain, multiple domains may exist in a schema
	 *
	 * @param qualifiedName
	 *            the qualified name of this RecordDomain
	 * @param recordComponents
	 *            a list of record domain components which state the individual
	 *            components of the record, each consisting of a name and a
	 *            domain, and possibly a default value
	 * @return the new record domain
	 */
	public RecordDomain createRecordDomain(String qualifiedName,
			Collection<RecordComponent> recordComponents);

	/**
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 * builds a new set domain, multiple domains may exist in a schema
	 *
	 * @param baseDomain
	 *            the domain of which all elements in the set are built of
	 * @return the new set domain
	 */
	public SetDomain createSetDomain(Domain baseDomain);

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	public boolean equals(Object other);

||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
=======
	@Override
	public boolean equals(Object other);

>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	/**
	 * @param qn
	 * @return the attributed element class with the specified qualified name
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	public AttributedElementClass getAttributedElementClass(String qn);

	public BooleanDomain getBooleanDomain();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	public RecordDomain createRecordDomain(QualifiedName qn,
			Map<String, Domain> recordComponents);
=======
	public <T extends AttributedElementClass<?, ?>> T getAttributedElementClass(
			String qn);

	public BooleanDomain getBooleanDomain();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * Returns an topologically ordered list of all composite domains, i.e. the
	 * domains are ordered according to the hierarchy of their components.
	 * First, the list contains the domains which only contain basic domains.
	 * The next entries in the list represent those domains which exclusively
	 * contain domains with only basic classes as components, etc.
	 * 
	 * @return an topologically ordered list of all composite domains
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	 * builds a new record domain, multiple domains may exist in a schema
	 * 
	 * @param qn
	 *            a unique name which identifies the record in the schema
	 * @return the new record domain
=======
	 * Returns an topologically ordered list of all composite domains, i.e. the
	 * domains are ordered according to the hierarchy of their components.
	 * First, the list contains the domains which only contain basic domains.
	 * The next entries in the list represent those domains which exclusively
	 * contain domains with only basic classes as components, etc.
	 *
	 * @return an topologically ordered list of all composite domains
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	public List<CompositeDomain> getCompositeDomainsInTopologicalOrder();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	public RecordDomain createRecordDomain(QualifiedName qn);

	/**
	 * after creating the schema, this command serves to make it permanent, m2
	 * classes are generated to represent the object oriented access layer
	 * 
	 * @param path
	 *            the path to the m1 classes which are to be generated
	 * 
	 * 
	 * @throws GraphIOException
	 *             if an error occured during optional compilation
	 */
	public void commit(String path) throws GraphIOException;

	/**
	 * after creating the schema, this command serves to make it permanent, m2
	 * classes are generated to represent the object oriented access layer
	 * 
	 * @param path
	 *            the path to the m1 classes which are to be generated
	 * @param progressFunction
	 *            an optional progressfunction
	 * @throws GraphIOException
	 *             if an error occured during optional compilation
	 */
	public void commit(String path, ProgressFunction progressFunction)
			throws GraphIOException;

	/**
	 * After creating the schema, this command serves to generate code for the
	 * m1 classes, contained in {@code JavaSourceFromString} objects.
	 */
	public Vector<JavaSourceFromString> commit();

	/**
	 * After creating the schema, this command serves to generate and compile
	 * code for the m1 classes. The class files are not written to disk, but
	 * only held in memory.
	 */
	public void compile();

	/**
	 * After creating the schema, this command serves to generate and compile code
	 * for the m1 classes. The class files are not written to disk, but only held in
	 * memory.
	 * 
	 * @param jgralabClassPath the classpath to JGraLab
	 */
	public void compile(String jgralabClassPath);

	/**
	 * @param aGraphClass
	 *            the graph class which is being searched for
	 * @return true, if the graph class is part of the schema
	 */
	public boolean containsGraphClass(GraphClass aGraphClass);

	/**
	 * @param id
	 *            the unique identifier of the graph class in the schema
	 * @return the graph class associated with the id
	 */
	public GraphClass getGraphClass(QualifiedName id);

	/**
	 * @return the textual representation of the schema with all graph classes,
	 *         their edge and vertex classes, all attributes and the whole
	 *         hierarchy of those classes
	 */
	public String toString();

	/**
	 * sets the prefix for the generation of the m2 elements, the prefix is
	 * added prior to the generated package, example: prefix.schemaname
	 * 
	 * @param prefix
	 *            the prefix to set
	 */
	// public void setPrefix(String prefix);
	/**
	 * @param anAttributedElement
	 *            the element which class should be returned
	 * @return the m2 element of anAttributedElement (instances of
	 *         AttributedElementClass)
	 */
	public AttributedElementClass getClass(AttributedElement anAttributedElement);

	/**
	 * @return all the graph classes in the schema
	 */
	public Map<QualifiedName, GraphClass> getGraphClasses();

	/**
	 * @return all the domains in the schema
	 */
	public Map<QualifiedName, Domain> getDomains();
	
	/**
	 * @return all packages in the schema
	 */
	public Map<QualifiedName, Package> getPackages();

	/**
	 * @param id
	 * @return the attributed element class with the specified id
	 */
	public AttributedElementClass getAttributedElementClass(QualifiedName id);

	/**
	 * @param domainName
	 *            the unique name of the enum/record domain
	 * @return the enum or record domain with the name domainName
	 */
	public Domain getDomain(QualifiedName domainName);
	
	/**
	 * @param packageName
	 *            the unique name of the package
	 * @return the package name packageName
	 */
	public Package getPackage(String packageName);

	/**
	 * @param domainName
	 *            the unique name of the enum/record domain
	 * @return the enum or record domain with the name domainName
	 */
	public Domain getDomain(String domainName);

	/**
	 * Gets the method to create a new graphwith the given name
	 * 
	 * @param graphClassName
	 *            the name of the graph class
	 * @return the Method-Object that represents the method to create such
	 *         graphs
	 */
	public Method getGraphCreateMethod(QualifiedName graphClassName);

	/**
	 * Gets the method to create a new vertex with the given name
	 * 
	 * @param vertexClassName
	 *            the name of the vertex to create
	 * @param graphClassName
	 *            the name of the graph class the VertexClass belongs to
	 * @return the Method-Object that represents the method to create such
	 *         vertices
	 */
	public Method getVertexCreateMethod(QualifiedName vertexClassName,
			QualifiedName graphClassName);

	/**
	 * Gets the method to create a new edge with the given name
	 * 
	 * @param edgeClassName
	 *            the name of the edge to create
	 * @param graphClassName
	 *            the name of the graph class the EdgeClass belongs to
	 * @return the Method-Object that represents the method to create such edges
	 */
	public Method getEdgeCreateMethod(QualifiedName edgeClassName,
			QualifiedName graphClassName);

	/**
	 * @return the default AggregationClass of the schema, that is the
	 *         AggregationClass with the name "Aggregation"
	 */
	public AggregationClass getDefaultAggregationClass();

	/**
	 * @return the default CompositionClass of the schema, that is the
	 *         CompositionClass with the name "Composition"
	 */
	public CompositionClass getDefaultCompositionClass();
=======
	public List<CompositeDomain> getCompositeDomains();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java

	/**
	 * @return the default EdgeClass of the schema, that is the EdgeClass with
	 *         the name "Edge"
	 */
	public EdgeClass getDefaultEdgeClass();

	/**
	 * @return the default GraphClass of the schema, that is the GraphClass with
	 *         the name "Graph"
	 */
	public GraphClass getDefaultGraphClass();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * Returns the default package of this Schema.
	 * 
	 * @return the default package, guaranteed to be != null
	 */
	public Package getDefaultPackage();

	/**
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
=======
	 * Returns the default package of this Schema.
	 *
	 * @return the default package, guaranteed to be != null
	 */
	public Package getDefaultPackage();

	/**
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 * @return the default VertexClass of the schema, that is the VertexClass
	 *         with the name "Vertex"
	 */
	public VertexClass getDefaultVertexClass();

	/**
	 * @param domainName
	 *            the unique name of the enum/record domain
	 * @return the enum or record domain with the name domainName
	 */
	public Domain getDomain(String domainName);

	/**
	 * @return all the domains in the schema
	 */
	public Map<String, Domain> getDomains();

	public DoubleDomain getDoubleDomain();

	/**
	 * Returns an topologically ordered list of all edge classes in the schema
	 * (including aggregation and composition classes), i.e. the edge classes
	 * are ordered according to their inheritance hierarchy. In contrast to
	 * {@link GraphClass#getEdgeClasses()}, this list also includes the default
	 * {@link EdgeClass} corresponding to {@link Edge} as its first element.
	 *
	 * @return an topologically ordered list of all edge classes including the
	 *         default edge class
	 */
	public List<EdgeClass> getEdgeClasses();

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java
	 * Gets the method to create a new edge with the given name
	 * 
	 * @param edgeClassName
	 *            the name of the edge to create
	 * @return the Method-Object that represents the method to create such edges
	 */
	public Method getEdgeCreateMethod(String edgeClassName,
			ImplementationType implementationType);

	/**
	 * Returns a list of all enum domains
	 * 
	 * @return a list of all enum domains
	 */
	public List<EnumDomain> getEnumDomains();

	public String getFileName();

	/**
	 * @return the {@link GraphClass} defined by this schema
	 */
	public GraphClass getGraphClass();

	/**
	 * Gets the method to create a new graph of this schema
	 * 
	 * @return the Method-Object that represents the method to create graphs of
	 *         this schema
	 */
	public Method getGraphCreateMethod(ImplementationType implementationType);

	/**
	 * @return the factory that is used to create graphs, vertices and edges
	 */
	public GraphFactory getGraphFactory();

	public IntegerDomain getIntegerDomain();

	public String getName();

	public LongDomain getLongDomain();

	/**
	 * @param qn
	 *            the qualified name of the package
	 * @return the package name packageName
	 */
	public Package getPackage(String qn);

	public String getPackagePrefix();

	/**
	 * @return all packages in the schema
	 */
	public Map<String, Package> getPackages();

	public String getPathName();

	public String getQualifiedName();

	/**
	 * Returns a list of all record domains
	 * 
	 * @return a list of all record domains
	 */
	public List<RecordDomain> getRecordDomains();

	public StringDomain getStringDomain();

	/**
	 * Returns an topologically ordered list of all vertex classes in the
	 * schema, i.e. the vertex classes are ordered according to their
	 * inheritance hierarchy. First, the list contains the classes without a
	 * superclass (except the default vertex class). The next entries in the
	 * list represent those vertex classes which only inherit from the classes
	 * without a superclass, etc.
	 * 
	 * @return an topologically ordered list of all vertex classes
	 */
	public List<VertexClass> getVertexClassesInTopologicalOrder();

	/**
	 * Gets the method to create a new vertex with the given name
	 * 
	 * @param vertexClassQName
	 *            the qualified name of the vertex to create
	 * @return the Method-Object that represents the method to create such
	 *         vertices
	 */
	public Method getVertexCreateMethod(String vertexClassQName,
			ImplementationType implementationType);

	/**
	 * Checks if the given name is a valid enumeration constant in this Schema.
	 * 
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
	 * Checks if the given name is already known in thsi schema. If this is the
	 * case, it's not allowed to use it for any other element in this schema.
	 * Even it'S not allowed to use a domain name also as name of a VertexClass.
	 * 
=======
	 * Gets the method to create a new edge with the given name
	 *
	 * @param edgeClassName
	 *            the name of the edge to create
	 * @return the Method-Object that represents the method to create such edges
	 */
	public Method getEdgeCreateMethod(String edgeClassName,
			ImplementationType implementationType);

	/**
	 * Returns a list of all enum domains
	 *
	 * @return a list of all enum domains
	 */
	public List<EnumDomain> getEnumDomains();

	public String getFileName();

	/**
	 * @return the {@link GraphClass} defined by this schema
	 */
	public GraphClass getGraphClass();

	public IntegerDomain getIntegerDomain();

	public String getName();

	public LongDomain getLongDomain();

	/**
	 * @param qn
	 *            the qualified name of the package
	 * @return the package name packageName
	 */
	public Package getPackage(String qn);

	public String getPackagePrefix();

	/**
	 * @return all packages in the schema
	 */
	public Map<String, Package> getPackages();

	public String getPathName();

	public String getQualifiedName();

	/**
	 * Returns a list of all record domains
	 *
	 * @return a list of all record domains
	 */
	public List<RecordDomain> getRecordDomains();

	public StringDomain getStringDomain();

	/**
	 * Returns an topologically ordered list of all vertex classes in the
	 * schema, i.e. the vertex classes are ordered according to their
	 * inheritance hierarchy. In contrast to
	 * {@link GraphClass#getVertexClasses()}, this list also includes the
	 * default {@link VertexClass} corresponding to {@link Vertex} as its first
	 * element.
	 *
	 * @return an topologically ordered list of all vertex classes including the
	 *         default vertex class
	 */
	public List<VertexClass> getVertexClasses();

	/**
	 * Gets the method to create a new vertex with the given name
	 *
	 * @param vertexClassQName
	 *            the qualified name of the vertex to create
	 * @return the Method-Object that represents the method to create such
	 *         vertices
	 */
	public Method getVertexCreateMethod(String vertexClassQName,
			ImplementationType implementationType);

	/**
	 * Checks if the given name is a valid enumeration constant in this Schema.
	 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
	 * @param name
	 *            the constant name to check
	 * @return true if <code>name</code> is a valid enum constant
	 */
	public boolean isValidEnumConstant(String name);
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/left.java

	/**
	 * Checks if the given <code>qualifiedName</code> is already known in this
	 * Schema. If this is the case, it's not allowed to use it for any other
	 * element in this schema. Even it'S not allowed to use a domain name also
	 * as name of a VertexClass.
	 * 
	 * @param qualifiedName
	 *            the qualified name to check for
	 * @return true if the name is already known, false otherwise
	 */
	public boolean knows(String qualifiedName);

	/**
	 * Returns the NamedElement with the given <code>qualifiedName</code>.
	 * 
	 * @param qualifiedName
	 *            the qualified name of the desired element
	 * @return the corresponding NamedElement, or null if no such element exists
	 */
	public NamedElement getNamedElement(String qualifiedName);

	/**
	 * Sets the schema to allow lowercase enum constants
	 * 
	 * @param allowLowercaseEnumConstants
	 *            set to true to make the schema to allow lowercase enum
	 *            constants
	 */
	public void setAllowLowercaseEnumConstants(
			boolean allowLowercaseEnumConstants);

	/**
	 * sets the factory that is used to create graphs, vertices and edges
	 */
	public void setGraphFactory(GraphFactory factory);

	/**
	 * Creates a string representation of this schema in the TG language. Do not
	 * use in GraphIO.
	 * 
	 * @return the TG String of this schema
	 */
	public String toTGString();

	public Graph createGraph(ImplementationType implementationType);

	public Graph createGraph(ImplementationType implementationType, int vCount,
			int eCount);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/base.java
=======

	/**
	 * Checks if the given <code>qualifiedName</code> is already known in this
	 * Schema. If this is the case, it's not allowed to use it for any other
	 * element in this schema. Even it'S not allowed to use a domain name also
	 * as name of a VertexClass.
	 *
	 * @param qualifiedName
	 *            the qualified name to check for
	 * @return true if the name is already known, false otherwise
	 */
	public boolean knows(String qualifiedName);

	/**
	 * Returns the NamedElement with the given <code>qualifiedName</code>.
	 *
	 * @param qualifiedName
	 *            the qualified name of the desired element
	 * @return the corresponding NamedElement, or null if no such element exists
	 */
	public NamedElement getNamedElement(String qualifiedName);

	/**
	 * Sets the schema to allow lowercase enum constants
	 *
	 * @param allowLowercaseEnumConstants
	 *            set to true to make the schema to allow lowercase enum
	 *            constants
	 */
	public void setAllowLowercaseEnumConstants(
			boolean allowLowercaseEnumConstants);

	/**
	 * Creates a string representation of this schema in the TG language. Do not
	 * use in GraphIO.
	 *
	 * @return the TG String of this schema
	 */
	public String toTGString();

	public GraphFactory createDefaultGraphFactory(
			ImplementationType implementationType);

	public Graph createGraph(ImplementationType implementationType);

	public Graph createGraph(ImplementationType implementationType, String id,
			int vMax, int eMax);

	/**
	 * @return whether the schema is finished
	 */
	public boolean isFinished();

	/**
	 * Signals that the schema is finished. No more changes are allowed. To open
	 * the change mode call reopen. The schema has to be finished before commit
	 * or compile.
	 */
	public void finish();

	/**
	 * Reopens the schema to allow changes. To finish the schema again, call
	 * finish
	 */
	public void reopen();

	public void save(String filename) throws GraphIOException;

	public void save(DataOutputStream out) throws GraphIOException;

>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/schema/Schema.java/right.java
}
