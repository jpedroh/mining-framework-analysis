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

package de.uni_koblenz.jgralab.codegenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
/**
 * TODO add comment
 *
 * @author ist@uni-koblenz.de
 *
 */
public class GraphCodeGenerator extends AttributedElementCodeGenerator {

	public GraphCodeGenerator(GraphClass graphClass, String schemaPackageName,
			String schemaName, CodeGeneratorConfiguration config) {
		super(graphClass, schemaPackageName, config);
		rootBlock.setVariable("graphElementClass", "Graph");
		rootBlock.setVariable("schemaElementClass", "GraphClass");
		rootBlock.setVariable("schemaName", schemaName);
		rootBlock.setVariable("theGraph", "this");
	}

	@Override
	protected CodeBlock createHeader() {
		return super.createHeader();
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
	@Override
	protected CodeBlock createBody() {
		CodeList code = (CodeList) super.createBody();
		if (currentCycle.isStdOrDbImplOrTransImpl()) {
			if (currentCycle.isStdImpl()) {
				addImports("#jgImplStdPackage#.#baseClassName#");
			}
			if (currentCycle.isTransImpl()) {
				addImports("#jgImplTransPackage#.#baseClassName#");
			}
			if (currentCycle.isDbImpl()) {
				addImports("de.uni_koblenz.jgralab.GraphException",
						"#jgImplDbPackage#.#baseClassName#",
						"#jgImplDbPackage#.GraphDatabase",
						"#jgImplDbPackage#.GraphDatabaseException");
			}

			rootBlock.setVariable("baseClassName", "GraphImpl");

			code.add(new CodeSnippet(
					"@Override",
					"public synchronized <T extends de.uni_koblenz.jgralab.Vertex> org.pcollections.POrderedSet<T> reachableVertices(de.uni_koblenz.jgralab.Vertex startVertex, String pathDescription, Class<T> vertexType) {",
					"\tde.uni_koblenz.jgralab.greql2.evaluator.Query q = new de.uni_koblenz.jgralab.greql2.evaluator.Query(\"using v: v \" + pathDescription);",
					"\tjava.util.HashMap<String, Object> variables = new java.util.HashMap<String, Object>();",
					"\tvariables.put(\"v\", startVertex);",
					"\tde.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator eval = new de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator(q, this, variables, null);",
					"\treturn eval.getResultSet();", "}"));
		}
		code.add(createGraphElementClassMethods());
		code.add(createEdgeIteratorMethods());
		code.add(createVertexIteratorMethods());
		return code;
	}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
=======
	@Override
	protected CodeBlock createBody() {
		CodeList code = (CodeList) super.createBody();
		if (currentCycle.isStdOrDbImplOrTransImpl()) {
			if (currentCycle.isStdImpl()) {
				addImports("#jgImplStdPackage#.#baseClassName#");
			}
			if (currentCycle.isTransImpl()) {
				addImports("#jgImplTransPackage#.#baseClassName#");
			}
			if (currentCycle.isDbImpl()) {
				addImports("#jgImplDbPackage#.#baseClassName#",
						"#jgImplDbPackage#.GraphDatabase");
			}

			rootBlock.setVariable("baseClassName", "GraphImpl");

			// for Vertex.reachableVertices()
			addImports("org.pcollections.POrderedSet");
			addImports("#jgPackage#.Vertex");
			addImports("#jgPackage#.greql2.evaluator.GreqlEvaluator");

			code.add(new CodeSnippet(
					"\n\tprotected GreqlEvaluator greqlEvaluator;\n",
					"@Override",
					"public synchronized <T extends Vertex> POrderedSet<T> reachableVertices(Vertex startVertex, String pathDescription, Class<T> vertexType) {",
					"\tif (greqlEvaluator == null) {",
					"\t\tgreqlEvaluator = new GreqlEvaluator((String) null, this, null);",
					"\t}",
					"\tgreqlEvaluator.setVariable(\"v\", startVertex);",
					"\tgreqlEvaluator.setQuery(\"using v: v \" + pathDescription);",
					"\tgreqlEvaluator.startEvaluation();",
					"\treturn greqlEvaluator.getResultSet();", "}"));
		}
		code.add(createGraphElementClassMethods());
		code.add(createEdgeIteratorMethods());
		code.add(createVertexIteratorMethods());
		return code;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
	@Override
	protected CodeBlock createConstructor() {
		addImports("#schemaPackageName#.#schemaName#");
		CodeSnippet code = new CodeSnippet(true);
		if (currentCycle.isTransImpl()) {
			code.setVariable("createSuffix", "WithTransactionSupport");
		}
		if (currentCycle.isStdImpl()) {
			code.setVariable("createSuffix", "");
		}
		if (currentCycle.isDbImpl()) {
			code.setVariable("createSuffix", "WithDatabaseSupport");
		}
		// TODO if(currentCycle.isDbImpl()) only write ctors and create with
		// GraphDatabase as param.
		if (!currentCycle.isDbImpl()) {
			code.add(
					"/* Constructors and create methods with values for initial vertex and edge count */",
					"public #simpleClassName#Impl(int vMax, int eMax) {",
					"\tthis(null, vMax, eMax);",
					"}",
					"",
					"public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax) {",
					"\tsuper(id, #schemaName#.instance().#schemaVariableName#, vMax, eMax);",
					"\tinitializeAttributesWithDefaultValues();",
					"}",
					"",
					"public static #javaClassName# create(int vMax, int eMax) {",
					"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(null, vMax, eMax);",
					"}",
					"",
					"public static #javaClassName# create(String id, int vMax, int eMax) {",
					"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id, vMax, eMax);",
					"}",
					"",
					"/* Constructors and create methods without values for initial vertex and edge count */",
					"public #simpleClassName#Impl() {",
					"\tthis(null);",
					"}",
					"",
					"public #simpleClassName#Impl(java.lang.String id) {",
					"\tsuper(id, #schemaName#.instance().#schemaVariableName#);",
					"\tinitializeAttributesWithDefaultValues();",
					"}",
					"",
					"public static #javaClassName# create() {",
					"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(null);",
					"}",
					"",
					"public static #javaClassName# create(String id) {",
					"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id);",
					"}");
		} else {
			code.add(
					"/* Constructors and create methods for database support */",
					"",
					/*
					 * "public #simpleClassName#Impl(java.lang.String id) {",
					 * "\tsuper(id, #schemaName#.instance().#schemaVariableName#);"
					 * , // TODO Should not be allowed. "}", "",
					 * "public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax) {"
					 * ,
					 * "\tsuper(id, #schemaName#.instance().#schemaVariableName#, vMax, eMax);"
					 * , // TODO Should not be allowed.
					 * "\tinitializeAttributesWithDefaultValues();", "}", "",
					 */
					"public #simpleClassName#Impl(java.lang.String id, GraphDatabase graphDatabase) {",
					"\tsuper(id, #schemaName#.instance().#schemaVariableName#, graphDatabase);",
					"\tinitializeAttributesWithDefaultValues();",
					"}",
					"",
					"public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax, GraphDatabase graphDatabase) {",
					"\tsuper(id, vMax, eMax, #schemaName#.instance().#schemaVariableName#, graphDatabase);",
					"\tinitializeAttributesWithDefaultValues();",
					"}",
					"",

					"public static #javaClassName# create(String id, GraphDatabase graphDatabase) {",
					"\ttry{",
					"\t\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id, graphDatabase);",
					"\t}",
					"\tcatch(GraphDatabaseException exception){",
					"\t\tthrow new GraphException(\"Could not create graph.\", exception);",
					"\t}", "}");
		}
		return code;
	}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
=======
	@Override
	protected CodeBlock createConstructor() {
		CodeSnippet code = new CodeSnippet(true);
		if (currentCycle.isTransImpl()) {
			code.setVariable("createSuffix", "TRANSACTION");
		}
		if (currentCycle.isStdImpl()) {
			code.setVariable("createSuffix", "STANDARD");
		}
		if (currentCycle.isDbImpl()) {
			code.setVariable("createSuffix", "DATABASE");
		}
		// TODO if(currentCycle.isDbImpl()) only write ctors and create with
		// GraphDatabase as param.
		if (!currentCycle.isDbImpl()) {
			code.add(
					"/**",
					" * DON'T USE THE CONSTRUCTOR",
					" * For instantiating a Graph, use the Schema and a GraphFactory",
					"**/",
					"public #simpleImplClassName#() {",
					"\tthis(null);",
					"}",
					"",
					"/**",
					" * DON'T USE THE CONSTRUCTOR",
					" * For instantiating a Graph, use the Schema and a GraphFactory",
					"**/",
					"public #simpleImplClassName#(int vMax, int eMax) {",
					"\tthis(null, vMax, eMax);",
					"}",
					"",
					"/**",
					" * DON'T USE THE CONSTRUCTOR",
					" * For instantiating a Graph, use the Schema and a GraphFactory",
					"**/",
					"public #simpleImplClassName#(java.lang.String id, int vMax, int eMax) {",
					"\tsuper(id, #javaClassName#.GC, vMax, eMax);",
					"\tinitializeAttributesWithDefaultValues();",
					"}",
					"",
					"/**",
					" * DON'T USE THE CONSTRUCTOR",
					" * For instantiating a Graph, use the Schema and a GraphFactory",
					"**/",
					"public #simpleImplClassName#(java.lang.String id) {",
					"\tsuper(id, #javaClassName#.GC);",
					"\tinitializeAttributesWithDefaultValues();", "}");
		} else {
			code.add(
					"/**",
					" * DON'T USE THE CONSTRUCTOR",
					" * For instantiating a Graph, use a GraphFactory",
					"**/",
					"public #simpleImplClassName#(java.lang.String id, GraphDatabase graphDatabase) {",
					"\tsuper(id, #javaClassName#.GC, graphDatabase);",
					"\tinitializeAttributesWithDefaultValues();",
					"}",
					"",
					"/**",
					" * DON'T USE THE CONSTRUCTOR",
					" * For instantiating a Graph, use a GraphFactory",
					"**/",
					"public #simpleImplClassName#(java.lang.String id, int vMax, int eMax, GraphDatabase graphDatabase) {",
					"\tsuper(id, vMax, eMax, #javaClassName#.GC, graphDatabase);",
					"\tinitializeAttributesWithDefaultValues();", "}");
		}
		return code;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java

	private CodeBlock createGraphElementClassMethods() {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
		if (!gec.isInternal()) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
		if (gec.getQualifiedName() != "Vertex" && gec.getQualifiedName() != "Edge"
				&& gec.getQualifiedName() != "Aggregation"
				&& gec.getQualifiedName() != "Composition") {
			// if (createClass) {
			// addImports("#schemaPackage#." + gec.getName());
			// }
=======
		CodeList code = new CodeList();

		GraphClass gc = (GraphClass) aec;
		TreeSet<GraphElementClass<?, ?>> sortedClasses = new TreeSet<GraphElementClass<?, ?>>();
		sortedClasses.addAll(gc.getGraphElementClasses());
		for (GraphElementClass<?, ?> gec : sortedClasses) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
			CodeList gecCode = new CodeList();
			code.addNoIndent(gecCode);

			gecCode.addNoIndent(new CodeSnippet(
					true,
					"// ------------------------ Code for #ecQualifiedName# ------------------------"));

			gecCode.setVariable("ecSimpleName", gec.getSimpleName());
			gecCode.setVariable("ecUniqueName", gec.getUniqueName());
			gecCode.setVariable("ecQualifiedName", gec.getQualifiedName());
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
			gecCode.setVariable("ecSchemaVariableName",
					gec.getVariableName());
			gecCode.setVariable("ecJavaClassName", schemaRootPackageName
					+ "." + gec.getQualifiedName());
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
			gecCode.setVariable("ecSchemaVariableName", gec
					.getVariableName());
			gecCode.setVariable("ecJavaClassName", schemaRootPackageName
					+ "." + gec.getQualifiedName());
=======
			gecCode.setVariable("ecSchemaVariableName", gec.getVariableName());
			gecCode.setVariable("ecJavaClassName", schemaRootPackageName + "."
					+ gec.getQualifiedName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
			gecCode.setVariable("ecType",
					(gec instanceof VertexClass ? "Vertex" : "Edge"));
			gecCode.setVariable("ecTypeInComment",
					(gec instanceof VertexClass ? "vertex" : "edge"));
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
			gecCode.setVariable("ecCamelName",
					camelCase(gec.getUniqueName()));
			gecCode.setVariable(
					"ecImplName",
					(gec.isAbstract() ? "**ERROR**" : camelCase(gec
							.getQualifiedName()) + "Impl"));
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
			gecCode.setVariable("ecCamelName", camelCase(gec
					.getUniqueName()));
			gecCode.setVariable("ecImplName",
					(gec.isAbstract() ? "**ERROR**" : camelCase(gec
							.getQualifiedName())
							+ "Impl"));
=======
			gecCode.setVariable("ecTypeAecConstant",
					(gec instanceof VertexClass ? "VC" : "EC"));
			gecCode.setVariable("ecCamelName", camelCase(gec.getUniqueName()));
			gecCode.setVariable("ecImplName", (gec.isAbstract() ? "**ERROR**"
					: camelCase(gec.getQualifiedName()) + "Impl"));
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java

			gecCode.addNoIndent(createGetFirstMethods(gec));
			gecCode.addNoIndent(createFactoryMethods(gec));
		}

		return code;
	}

	private CodeBlock createGetFirstMethods(GraphElementClass<?, ?> gec) {
		CodeList code = new CodeList();
		if (config.hasTypeSpecificMethodsSupport()) {
			code.addNoIndent(createGetFirstMethod(gec));
		}
		return code;
	}

	private CodeBlock createGetFirstMethod(GraphElementClass<?, ?> gec) {
		CodeSnippet code = new CodeSnippet(true);
		if (currentCycle.isAbstract()) {
			code.add("/**",
					" * @return the first #ecSimpleName# #ecTypeInComment# in this graph");
			code.add(" */",
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
					"public #ecJavaClassName# getFirst#ecCamelName#(#formalParams#);");
		}
		if (currentCycle.isStdOrDbImplOrTransImpl()) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
					"public #ecJavaClassName# getFirst#ecCamelName##inGraph#(#formalParams#);");
		} else {
=======
					"public #ecJavaClassName# getFirst#ecCamelName#();");
		}
		if (currentCycle.isStdOrDbImplOrTransImpl()) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
			code.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
					"public #ecJavaClassName# getFirst#ecCamelName#(#formalParams#) {",
					"\treturn (#ecJavaClassName#)getFirst#ecType#(#schemaName#.instance().#ecSchemaVariableName##actualParams#);",
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
					"public #ecJavaClassName# getFirst#ecCamelName##inGraph#(#formalParams#) {",
					"\treturn (#ecJavaClassName#)getFirst#ecType#OfClass#inGraph#(#schemaName#.instance().#ecSchemaVariableName##actualParams#);",
=======
					"public #ecJavaClassName# getFirst#ecCamelName#() {",
					"\treturn (#ecJavaClassName#)getFirst#ecType#(#ecJavaClassName#.#ecTypeAecConstant#);",
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
					"}");
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
		code.setVariable("formalParams", "");
		code.setVariable("actualParams", "");
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
		code.setVariable("inGraph", (gec instanceof VertexClass ? ""
				: "InGraph"));
		code.setVariable("formalParams", (withTypeFlag ? "boolean noSubClasses"
				: ""));
		code.setVariable("actualParams", (withTypeFlag ? ", noSubClasses"
					: ""));
=======
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java

		return code;
	}

	private CodeBlock createFactoryMethods(GraphElementClass<?, ?> gec) {
		if (gec.isAbstract()) {
			return null;
		}
		CodeList code = new CodeList();
		code.addNoIndent(createFactoryMethod(gec, false));
		if (currentCycle.isStdOrDbImplOrTransImpl()) {
			code.addNoIndent(createFactoryMethod(gec, true));
		}
		return code;
	}

	private CodeBlock createFactoryMethod(GraphElementClass<?, ?> gec,
			boolean withId) {
		CodeSnippet code = new CodeSnippet(true);

		if (currentCycle.isStdImpl()) {
			code.setVariable("cycleSupportSuffix", "");
		} else if (currentCycle.isTransImpl()) {
			code.setVariable("cycleSupportSuffix", "WithTransactionSupport");
		} else if (currentCycle.isDbImpl()) {
			code.setVariable("cycleSupportSuffix", "WithDatabaseSupport");
		}

		if (currentCycle.isAbstract()) {
			code.add(
					"/**",
					" * Creates a new #ecUniqueName# #ecTypeInComment# in this graph.",
					" *");
			if (withId) {
				code.add(" * @param id the <code>id</code> of the #ecTypeInComment#");
			}
			if (gec instanceof EdgeClass) {
				code.add(" * @param alpha the start vertex of the edge",
						" * @param omega the target vertex of the edge");
			}
			code.add("*/",
					"public #ecJavaClassName# create#ecCamelName#(#formalParams#);");
		}
		if (currentCycle.isStdOrDbImplOrTransImpl()) {
			code.add(
					"public #ecJavaClassName# create#ecCamelName#(#formalParams#) {",
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
					"\t#ecJavaClassName# new#ecType# = (#ecJavaClassName#) graphFactory.create#ecType##cycleSupportSuffix#(#ecJavaClassName#.class, #newActualParams#, this#additionalParams#);",
					"\treturn new#ecType#;", "}");
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
					"\t#ecJavaClassName# new#ecType# = (#ecJavaClassName#) graphFactory.create#ecType#(#ecJavaClassName#.class, #newActualParams#, this);",
					"\tadd#ecType#(new#ecType##addActualParams#);",
					"\treturn new#ecType#;", "}");
=======
					"\treturn graphFactory.<#ecJavaClassName#> create#ecType#(#ecJavaClassName#.#ecTypeAecConstant#, #newActualParams#, this#additionalParams#);",
					"}");
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
			code.setVariable("additionalParams", "");
		}

		if (gec instanceof EdgeClass) {
			EdgeClass ec = (EdgeClass) gec;
			String fromClass = ec.getFrom().getVertexClass().getQualifiedName();
			String toClass = ec.getTo().getVertexClass().getQualifiedName();
			if (fromClass.equals("Vertex")) {
				code.setVariable("fromClass", "#jgPackage#.Vertex");
			} else {
				code.setVariable("fromClass", "#schemaPackage#." + fromClass);
			}
			if (toClass.equals("Vertex")) {
				code.setVariable("toClass", "#jgPackage#.Vertex");
			} else {
				code.setVariable("toClass", "#schemaPackage#." + toClass);
			}
			code.setVariable("formalParams", (withId ? "int id, " : "")
					+ "#fromClass# alpha, #toClass# omega");
			code.setVariable("addActualParams", ", alpha, omega");
			code.setVariable("additionalParams", ", alpha, omega");
		} else {
			code.setVariable("formalParams", (withId ? "int id" : ""));
			code.setVariable("addActualParams", "");
		}
		code.setVariable("newActualParams", (withId ? "id" : "0"));
		return code;
		// TODO if isDbImpl() only write two create methods!
	}

	private CodeBlock createEdgeIteratorMethods() {
		GraphClass gc = (GraphClass) aec;

		CodeList code = new CodeList();
		if (!config.hasTypeSpecificMethodsSupport()) {
			return code;
		}

		Set<EdgeClass> edgeClassSet = new HashSet<EdgeClass>();
		edgeClassSet.addAll(gc.getEdgeClasses());

		for (EdgeClass edge : edgeClassSet) {
			if (edge.isInternal()) {
				continue;
			}
			if (currentCycle.isStdOrDbImplOrTransImpl()) {
				addImports("#jgImplPackage#.EdgeIterable");
			}
			CodeSnippet s = new CodeSnippet(true);
			code.addNoIndent(s);

			s.setVariable("edgeUniqueName", camelCase(edge.getUniqueName()));
			s.setVariable("edgeQualifiedName", edge.getQualifiedName());
			s.setVariable("edgeJavaClassName", schemaRootPackageName + "."
					+ edge.getQualifiedName());
			// getFooIncidences()
			if (currentCycle.isAbstract()) {
				s.add("/**");
				s.add(" * @return an Iterable for all edges of this graph that are of type #edgeQualifiedName# or subtypes.");
				s.add(" */");
				s.add("public Iterable<#edgeJavaClassName#> get#edgeUniqueName#Edges();");
			}
			if (currentCycle.isStdOrDbImplOrTransImpl()) {
				s.add("public Iterable<#edgeJavaClassName#> get#edgeUniqueName#Edges() {");
				s.add("\treturn new EdgeIterable<#edgeJavaClassName#>(this, #edgeJavaClassName#.class);");
				s.add("}");
			}
			s.add("");
		}
		return code;
	}

	private CodeBlock createVertexIteratorMethods() {
		GraphClass gc = (GraphClass) aec;

		CodeList code = new CodeList();
		if (!config.hasTypeSpecificMethodsSupport()) {
			return code;
		}

		Set<VertexClass> vertexClassSet = new HashSet<VertexClass>();
		vertexClassSet.addAll(gc.getVertexClasses());

		for (VertexClass vertex : vertexClassSet) {
			if (vertex.isInternal()) {
				continue;
			}
			if (currentCycle.isStdOrDbImplOrTransImpl()) {
				addImports("#jgImplPackage#.VertexIterable");
			}

			CodeSnippet s = new CodeSnippet(true);
			code.addNoIndent(s);
			s.setVariable("vertexQualifiedName", vertex.getQualifiedName());
			s.setVariable("vertexJavaClassName",
					"#schemaPackage#." + vertex.getQualifiedName());
			s.setVariable("vertexCamelName", camelCase(vertex.getUniqueName()));
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
			// getFooIncidences()
			if (currentCycle.isAbstract()) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/base.java
			/* getFooIncidences() */
			if (!createClass) {
=======
			if (currentCycle.isAbstract()) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
				s.add("/**");
				s.add(" * @return an Iterable for all vertices of this graph that are of type #vertexQualifiedName# or subtypes.");
				s.add(" */");
				s.add("public Iterable<#vertexJavaClassName#> get#vertexCamelName#Vertices();");
			}
			if (currentCycle.isStdOrDbImplOrTransImpl()) {
				s.add("public Iterable<#vertexJavaClassName#> get#vertexCamelName#Vertices() {");
				s.add("\treturn new VertexIterable<#vertexJavaClassName#>(this, #vertexJavaClassName#.class);");
				s.add("}");
			}
			s.add("");
		}
		return code;
	}

	@Override
	protected void addCheckValidityCode(CodeSnippet code) {
		// just do nothing here
	}

	@Override
	protected CodeBlock createAttributedElementClassConstant() {
		return new CodeSnippet(
				true,
				"public static final #jgSchemaPackage#.#schemaElementClass# GC"
						+ " = #schemaPackageName#.#schemaName#.instance().#schemaVariableName#;");
	}

	@Override
	protected CodeBlock createGetAttributedElementClassMethod() {
		return new CodeSnippet(
				true,
				"@Override",
				"public final #jgSchemaPackage#.#schemaElementClass# getAttributedElementClass() {",
				"\treturn #javaClassName#.GC;", "}");
	}
}
