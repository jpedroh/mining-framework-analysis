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

import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class generates the code of the GraphElement Factory.
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
 * 
 * @author ist@uni-koblenz.de
 * 
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
 * @author dbildh
 *
=======
 *
 * @author ist@uni-koblenz.de
 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
 */
public class GraphFactoryGenerator extends CodeGenerator {

	private final Schema schema;

	public GraphFactoryGenerator(Schema schema, String schemaPackageName,
			CodeGeneratorConfiguration config) {
		super(schemaPackageName, "", config);
		this.schema = schema;
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
		rootBlock.setVariable("className", schema.getName() + "Factory");
		rootBlock.setVariable("simpleClassName", schema.getName() + "Factory");
		rootBlock.setVariable("isClassOnly", "true");
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
		rootBlock.setVariable("className", schema.getSimpleName() + "Factory");
		rootBlock.setVariable("simpleImplClassName", schema.getSimpleName() + "Factory");
		//rootBlock.setVariable("isClassOnly", "true");
		rootBlock.setVariable("isImplementationClassOnly", "true");
=======
		rootBlock.setVariable("schemaName", schema.getQualifiedName());
		rootBlock.setVariable("simpleClassName", schema.getGraphClass()
				.getSimpleName() + "Factory");
		rootBlock.setVariable("simpleImplClassName", schema.getGraphClass()
				.getSimpleName() + "FactoryImpl");
		rootBlock.setVariable("isClassOnly", "false");
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
	@Override
	protected CodeBlock createHeader() {
		addImports("#jgImplPackage#.GraphFactoryImpl");
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("className", schema.getName() + "Factory");
		code.add("public class #className# extends GraphFactoryImpl {");
		return code;
	}

||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
	protected CodeBlock createHeader(boolean createClass) {
	//	addImports("#schemaPackage#.*");
		addImports("#jgImplPackage#.GraphFactoryImpl");
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("className", schema.getSimpleName() + "Factory");
		code.add("public class #className# extends GraphFactoryImpl {");
		return code;
	}
	
	
	
=======
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
	@Override
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
	protected CodeBlock createBody() {
		CodeList code = new CodeList();
		if (currentCycle.isClassOnly()) {
			code.add(createConstructor());
			code.add(createFillTableMethod());
		}
		return code;
	}

	protected CodeBlock createConstructor() {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
	protected CodeBlock createBody(boolean createClass) {
		CodeList code = new CodeList();
		code.add(createConstructor());
		code.add(createFillTableMethod());
		return code;
	}
	
	protected CodeBlock createConstructor() {
=======
	protected CodeBlock createHeader() {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
		CodeSnippet code = new CodeSnippet(true);
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
		code.setVariable("className", schema.getName() + "Factory");
		code.add("public #className#() {");
		code.add("\tsuper();");
		code.add("\tfillTable();");
		code.add("}");
		return code;
	}

	protected CodeBlock createFillTableMethod() {
		CodeList code = new CodeList();
		CodeSnippet s = new CodeSnippet(true);
		s.add("protected void fillTable() { ");
		code.addNoIndent(s);

		GraphClass graphClass = schema.getGraphClass();
		code.add(createFillTableForGraph(graphClass));
		for (VertexClass vertexClass : graphClass.getVertexClasses()) {
			code.add(createFillTableForVertex(vertexClass));
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
		code.setVariable("className", schema.getSimpleName() + "Factory");
		code.add("public #className#() {");
		code.add("\tsuper();");
		code.add("\tfillTable();");
		code.add("}");
		return code;
	}
	
	protected CodeBlock createFillTableMethod() {
		CodeList code = new CodeList();
		CodeSnippet s = new CodeSnippet(true);
		s.add("protected void fillTable() { ");
		code.addNoIndent(s);
		for (GraphClass graphClass : schema.getGraphClasses().values()) {
			code.add(createFillTableForGraph(graphClass));
			for (VertexClass vertexClass : graphClass.getOwnVertexClasses())
				code.add(createFillTableForVertex(vertexClass));
			for (EdgeClass edgeClass : graphClass.getOwnEdgeClasses())
				code.add(createFillTableForEdge(edgeClass));
			for (EdgeClass edgeClass : graphClass.getOwnAggregationClasses())
				code.add(createFillTableForEdge(edgeClass));
			for (EdgeClass edgeClass : graphClass.getOwnCompositionClasses())
				code.add(createFillTableForEdge(edgeClass));
=======
		if (currentCycle.isAbstract()) {
			addImports("#jgPackage#.GraphFactory");
			code.add("public interface #simpleClassName# extends GraphFactory {");
		} else {
			addImports("#schemaPackage#.#simpleClassName#");
			addImports("#jgImplPackage#.GraphFactoryImpl");
			addImports("#jgPackage#.ImplementationType");
			code.add("public class #simpleImplClassName# extends GraphFactoryImpl implements #simpleClassName# {");
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
		for (EdgeClass edgeClass : graphClass.getEdgeClasses()) {
			code.add(createFillTableForEdge(edgeClass));
		}

		s = new CodeSnippet(true);
		s.add("}");
		code.addNoIndent(s);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
		s = new CodeSnippet(true);
		s.add("}");
		code.addNoIndent(s);
=======
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
		return code;
	}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java

	protected CodeBlock createFillTableForGraph(GraphClass graphClass) {
		if (graphClass.isAbstract()) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
	
	protected CodeBlock createFillTableForGraph(GraphClass graphClass) {
		if (graphClass.isAbstract())
=======

	@Override
	protected CodeBlock createBody() {
		CodeList code = new CodeList();
		if (currentCycle.isStdOrDbImplOrTransImpl()) {
			code.add(createConstructor());
		}
		return code;
	}

	protected CodeBlock createConstructor() {
		CodeList code = new CodeList();
		if (currentCycle.isStdImpl()) {
			code.setVariable("implTypeInfix", "STANDARD");
		}
		if (currentCycle.isTransImpl()) {
			code.setVariable("implTypeInfix", "TRANSACTION");
		}
		if (currentCycle.isDbImpl()) {
			code.setVariable("implTypeInfix", "DATABASE");
		}
		CodeSnippet s = new CodeSnippet(true);
		s.add("public #simpleImplClassName#() {",
				"\tsuper(#schemaName#.instance(), ImplementationType.#implTypeInfix#);",
				"\tcreateMaps();");

		code.addNoIndent(s);
		code.add(createFillTableMethod());
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}

	protected CodeBlock createFillTableMethod() {
		if (currentCycle.isAbstract()) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
			return null;
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("graphName",
				schemaRootPackageName + "." + graphClass.getQualifiedName());
		code.setVariable("graphImplName", schemaRootPackageName + ".impl.std."
				+ graphClass.getQualifiedName());
		code.setVariable("graphTransactionImplName", schemaRootPackageName
				+ ".impl.trans." + graphClass.getQualifiedName());
		code.setVariable("graphDatabaseImplName", schemaRootPackageName
				+ ".impl.db." + graphClass.getQualifiedName());
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("graphName", schemaRootPackageName + "." +graphClass.getQualifiedName());
		code.setVariable("graphImplName", schemaRootPackageName + ".impl." + graphClass.getQualifiedName());
=======
		}
		CodeList code = new CodeList();
		GraphClass graphClass = schema.getGraphClass();
		code.addNoIndent(createFillTableForGraph(graphClass));
		for (VertexClass vertexClass : graphClass.getVertexClasses()) {
			code.addNoIndent(createFillTableForVertex(vertexClass));
		}
		for (EdgeClass edgeClass : graphClass.getEdgeClasses()) {
			code.addNoIndent(createFillTableForEdge(edgeClass));
		}
		return code;
	}

	protected CodeBlock createFillTableForGraph(GraphClass graphClass) {
		if (graphClass.isAbstract()) {
			return null;
		}

		CodeSnippet code = new CodeSnippet(false);
		code.setVariable("graphName", graphClass.getQualifiedName() + ".GC");
		code.setVariable("graphImplName", "#schemaImplStdPackage#."
				+ graphClass.getQualifiedName() + "Impl");
		code.setVariable("graphTransactionImplName",
				"#schemaImplTransPackage#." + graphClass.getQualifiedName()
						+ "Impl");
		code.setVariable("graphDatabaseImplName", "#schemaImplDbPackage#."
				+ graphClass.getQualifiedName() + "Impl");
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java

		if (!graphClass.isAbstract()) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
			code.add("/* code for graph #graphName# */");
			if (config.hasStandardSupport()) {
				code.add("setGraphImplementationClass(#graphName#.class, #graphImplName#Impl.class);");
			}
			if (config.hasTransactionSupport()) {
				code.add("setGraphTransactionImplementationClass(#graphName#.class, #graphTransactionImplName#Impl.class);");
			}
			if (config.hasDatabaseSupport()) {
				code.add("setGraphDatabaseImplementationClass(#graphName#.class, #graphDatabaseImplName#Impl.class);");
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
			code.add("/* code for graph #graphName# */");
			code.add("setGraphImplementationClass(#graphName#.class, #graphImplName#Impl.class);");
		}	
=======
			if (currentCycle.isStdImpl() && config.hasStandardSupport()) {
				code.add("setGraphImplementationClass(#schemaPackage#.#graphName#, #graphImplName#.class);");
			}
			if (currentCycle.isTransImpl() && config.hasTransactionSupport()) {
				code.add("setGraphImplementationClass(#schemaPackage#.#graphName#, #graphTransactionImplName#.class);");
			}
			if (currentCycle.isDbImpl() && config.hasDatabaseSupport()) {
				code.add("setGraphImplementationClass(#schemaPackage#.#graphName#, #graphDatabaseImplName#.class);");
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
		return code;
	}

	protected CodeBlock createFillTableForVertex(VertexClass vertexClass) {
		if (vertexClass.isAbstract()) {
			return null;
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("vertexName", schemaRootPackageName + "."
				+ vertexClass.getQualifiedName());
		code.setVariable("vertexImplName", schemaRootPackageName + ".impl.std."
				+ vertexClass.getQualifiedName());
		code.setVariable("vertexTransactionImplName", schemaRootPackageName
				+ ".impl.trans." + vertexClass.getQualifiedName());
		code.setVariable("vertexDatabaseImplName", schemaRootPackageName
				+ ".impl.db." + vertexClass.getQualifiedName());

		if (!vertexClass.isAbstract()) {
			if (config.hasStandardSupport()) {
				code.add("setVertexImplementationClass(#vertexName#.class, #vertexImplName#Impl.class);");
			}
			if (config.hasTransactionSupport()) {
				code.add("setVertexTransactionImplementationClass(#vertexName#.class, #vertexTransactionImplName#Impl.class);");
			}
			if (config.hasDatabaseSupport()) {
				code.add("setVertexDatabaseImplementationClass(#vertexName#.class, #vertexDatabaseImplName#Impl.class);");
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("vertexName", schemaRootPackageName + "." +vertexClass.getQualifiedName());
		code.setVariable("vertexImplName", schemaRootPackageName + ".impl." + vertexClass.getQualifiedName());
		if (!vertexClass.isAbstract())
		code.add("setVertexImplementationClass(#vertexName#.class, #vertexImplName#Impl.class);");
=======
		}

		CodeSnippet code = new CodeSnippet(false);
		code.setVariable("vertexName", vertexClass.getQualifiedName() + ".VC");

		code.setVariable("vertexImplName", "#schemaImplStdPackage#."
				+ vertexClass.getQualifiedName() + "Impl");
		code.setVariable("vertexTransactionImplName",
				"#schemaImplTransPackage#." + vertexClass.getQualifiedName()
						+ "Impl");
		code.setVariable("vertexDatabaseImplName", "#schemaImplDbPackage#."
				+ vertexClass.getQualifiedName() + "Impl");

		if (!vertexClass.isAbstract()) {
			if (currentCycle.isStdImpl() && config.hasStandardSupport()) {
				code.add("setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexImplName#.class);");
			}
			if (currentCycle.isTransImpl() && config.hasTransactionSupport()) {
				code.add("setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexTransactionImplName#.class);");
			}
			if (currentCycle.isDbImpl() && config.hasDatabaseSupport()) {
				code.add("setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexDatabaseImplName#.class);");
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
		return code;
	}

	protected CodeBlock createFillTableForEdge(EdgeClass edgeClass) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("edgeName",
				schemaRootPackageName + "." + edgeClass.getQualifiedName());
		code.setVariable("edgeImplName", schemaRootPackageName + ".impl.std."
				+ edgeClass.getQualifiedName());
		code.setVariable("edgeTransactionImplName", schemaRootPackageName
				+ ".impl.trans." + edgeClass.getQualifiedName());
		code.setVariable("edgeDatabaseImplName", schemaRootPackageName
				+ ".impl.db." + edgeClass.getQualifiedName());

		if (!edgeClass.isAbstract()) {
			if (config.hasStandardSupport()) {
				code.add("setEdgeImplementationClass(#edgeName#.class, #edgeImplName#Impl.class);");
			}
			if (config.hasTransactionSupport()) {
				code.add("setEdgeTransactionImplementationClass(#edgeName#.class, #edgeTransactionImplName#Impl.class);");
			}
			if (config.hasDatabaseSupport()) {
				code.add("setEdgeDatabaseImplementationClass(#edgeName#.class, #edgeDatabaseImplName#Impl.class);");
			}
		}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/base.java

			//return null;
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("edgeName", schemaRootPackageName + "." + edgeClass.getQualifiedName());
		code.setVariable("edgeImplName", schemaRootPackageName + ".impl." + edgeClass.getQualifiedName());

		if (!edgeClass.isAbstract())
		code.add("setEdgeImplementationClass(#edgeName#.class, #edgeImplName#Impl.class);");
=======
		CodeSnippet code = new CodeSnippet(false);
		code.setVariable("edgeName", edgeClass.getQualifiedName() + ".EC");
		code.setVariable("edgeImplName",
				"#schemaImplStdPackage#." + edgeClass.getQualifiedName()
						+ "Impl");
		code.setVariable("edgeTransactionImplName", "#schemaImplTransPackage#."
				+ edgeClass.getQualifiedName() + "Impl");
		code.setVariable("edgeDatabaseImplName", "#schemaImplDbPackage#."
				+ edgeClass.getQualifiedName() + "Impl");

		if (!edgeClass.isAbstract()) {
			if (currentCycle.isStdImpl() && config.hasStandardSupport()) {
				code.add("setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeImplName#.class);");
			}
			if (currentCycle.isTransImpl() && config.hasTransactionSupport()) {
				code.add("setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeTransactionImplName#.class);");
			}
			if (currentCycle.isDbImpl() && config.hasDatabaseSupport()) {
				code.add("setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeDatabaseImplName#.class);");
			}
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
		return code;
	}
}
