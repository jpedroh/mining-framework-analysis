/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2007 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 *
 *               ist@uni-koblenz.de
 *
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.uni_koblenz.jgralab.codegenerator;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.RolenameEntry;
import de.uni_koblenz.jgralab.schema.impl.VertexEdgeEntry;

/**
 * This class is used by the method Schema.commit() to generate the Java-classes
 * that implement the VertexClasses of a graph schema.
 * 
 */
public class VertexCodeGenerator extends AttributedElementCodeGenerator {

	public VertexCodeGenerator(VertexClass vertexClass,
			String schemaPackageName, String implementationName) {
		super(vertexClass, schemaPackageName, implementationName);
		rootBlock.setVariable("graphElementClass", "Vertex");
	}

	/**
	 * creates the header of the classfile, that is the part
	 * <code>public class VertexClassName extends Vertex {</code>
	 */
	protected CodeBlock createHeader(boolean createClass) {
		return super.createHeader(createClass);
	}

	/**
	 * creates the body of the classfile, that are methods and attributes
	 */
	protected CodeBlock createBody(boolean createClass) {
		CodeList code = (CodeList) super.createBody(createClass);
		if (createClass) {
			addImports("#jgImplPackage#.VertexImpl");
			rootBlock.setVariable("baseClassName", "VertexImpl");
			code.add(createValidEdgeSets((VertexClass) aec));
		}
		code.add(createNextVertexMethods(createClass));
		code.add(createFirstEdgeMethods(createClass));
		code.add(createRolenameMethods(createClass));
		code.add(createIncidenceIteratorMethods(createClass));
		return code;
	}

	/**
	 * creates the methods <code>getFirstEdgeName()</code>
	 * 
	 * @param createClass
	 *            iff set to true, the method bodies will also be created
	 * @return the CodeBlock that contains the methods
	 */
	private CodeBlock createFirstEdgeMethods(boolean createClass) {
		CodeList code = new CodeList();
		for (EdgeClass ec : ((VertexClass) aec).getEdgeClasses()) {
			if (ec.isInternal()) {
				continue;
			}
			addImports("#jgPackage#.EdgeDirection");
			code.addNoIndent(createFirstEdgeMethod(ec, false, false,
					createClass));
			code.addNoIndent(createFirstEdgeMethod(ec, true, false,
							createClass));

			if (CodeGenerator.CREATE_METHODS_WITH_TYPEFLAG) {
				if (!ec.isAbstract()) {
					code.addNoIndent(createFirstEdgeMethod(ec, false, true,
							createClass));
					code.addNoIndent(createFirstEdgeMethod(ec, true, true,
							createClass));
				}
			}
		}
		return code;
	}

	/**
	 * creates the method <code>getFirstEdgeName()</code> for the given
	 * EdgeClass
	 * 
	 * @param createClass
	 *            iff set to true, the method bodies will also be created
	 * @param withOrientation
	 *            toggles if the EdgeDirection-parameter will be created
	 * @param withTypeFlag
	 *            toggles if the "no subclasses"-parameter will be created
	 * @return the CodeBlock that contains the method
	 */
	private CodeBlock createFirstEdgeMethod(EdgeClass ec,
			boolean withOrientation, boolean withTypeFlag, boolean createClass) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("ecQualifiedName", schemaRootPackageName + "."
				+ ec.getQualifiedName());
		code.setVariable("ecCamelName", camelCase(ec.getUniqueName()));
		code.setVariable("formalParams",
				(withOrientation ? "EdgeDirection orientation" : "")
						+ (withOrientation && withTypeFlag ? ", " : "")
						+ (withTypeFlag ? "boolean noSubClasses" : ""));
		code.setVariable("actualParams",
				(withOrientation || withTypeFlag ? ", " : "")
						+ (withOrientation ? "orientation" : "")
						+ (withOrientation && withTypeFlag ? ", " : "")
						+ (withTypeFlag ? "noSubClasses" : ""));
		if (!createClass) {
			code.add("/**",
					" * @return the first edge of class #ecName# at this vertex");

			if (withOrientation) {
				code.add(" * @param orientation the orientation of the edge");
			}
			if (withTypeFlag) {
				code.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #ecName# are accepted");
			}
			code.add(" */",
					"public #ecQualifiedName# getFirst#ecCamelName#(#formalParams#);");
		} else {
			code.add(
					"public #ecQualifiedName# getFirst#ecCamelName#(#formalParams#) {",
					"\treturn (#ecQualifiedName#)getFirstEdgeOfClass(#ecQualifiedName#.class#actualParams#);",
					"}");

		}
		return code;
	}

	/**
	 * creates the <code>getNextVertexClassName()</code> methods
	 * 
	 * @param createClass
	 *            iff set to true, also the method bodies will be created
	 * @return the CodeBlock that contains the methods
	 */
	private CodeBlock createNextVertexMethods(boolean createClass) {
		CodeList code = new CodeList();

		TreeSet<AttributedElementClass> superClasses = new TreeSet<AttributedElementClass>();
		superClasses.addAll(aec.getAllSuperClasses());
		superClasses.add(aec);

		for (AttributedElementClass ec : superClasses) {
			if (ec.isInternal()) {
				continue;
			}
			VertexClass vc = (VertexClass) ec;
			code.addNoIndent(createNextVertexMethod(vc, false, createClass));
			if (CodeGenerator.CREATE_METHODS_WITH_TYPEFLAG) 
				if (!vc.isAbstract()) {
					code.addNoIndent(createNextVertexMethod(vc, true, createClass));
				}
		}
		return code;
	}

	/**
	 * creates the <code>getNextVertexClassName()</code> method for the given
	 * VertexClass
	 * 
	 * @param createClass
	 *            iff set to true, the method bodies will also be created
	 * @param withTypeFlag
	 *            toggles if the "no subclasses"-parameter will be created
	 * @return the CodeBlock that contains the method
	 */
	private CodeBlock createNextVertexMethod(VertexClass vc,
			boolean withTypeFlag, boolean createClass) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("vcQualifiedName", schemaRootPackageName + "."
				+ vc.getQualifiedName());
		code.setVariable("vcCamelName", camelCase(vc.getUniqueName()));
		code.setVariable("formalParams", (withTypeFlag ? "boolean noSubClasses"	: ""));
		code.setVariable("actualParams", (withTypeFlag ? ", noSubClasses" : ""));

		if (!createClass) {
			code.add("/**",
					" * @return the next #vcQualifiedName# vertex in the global vertex sequence");
			if (withTypeFlag) {
				code.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #vcName# are accepted");
			}
			code.add(" */",
					"public #vcQualifiedName# getNext#vcCamelName#(#formalParams#);");
		} else {
			code.add(
					"public #vcQualifiedName# getNext#vcCamelName#(#formalParams#) {",
					"\treturn (#vcQualifiedName#)getNextVertexOfClass(#vcQualifiedName#.class#actualParams#);",
					"}");
		}
		return code;
	}
	
	
	
	private CodeBlock validRolenameSnippet(CodeSnippet s, boolean createClass) {
		if (!createClass) {
			s.add(
				"/**",
				" * @return a List of all #targetSimpleName# vertices related to this by a <code>#roleName#</code> link.",
				" */",
				"public java.util.List<? extends #targetClass#> get#roleCamelName#List();");
		} else {
			s.add(
				"public java.util.List<? extends #targetClass#> get#roleCamelName#List() {",
				"\tjava.util.List<#targetClass#> list = new java.util.ArrayList<#targetClass#>();",
				"\t#ecQualifiedName# edge = getFirst#ecCamelName#(#dir#);",
				"\twhile (edge != null) {",
				"\t\tif (edge.getThatRole().equals(\"#roleName#\")) {",
				"\t\t\tlist.add((#targetClass#)edge.getThat());",
				"\t\t}",
				"\t\tedge = edge.getNext#ecCamelName#(#dir#);",
				"\t}", "\treturn list;", "}");
		}
		return s;
	}
	
	private CodeBlock invalidRolenameSnippet(CodeSnippet s) {
		addImports("#jgPackage#.GraphException");
		s.add("public java.util.List<#targetClass#> get#roleCamelName#List() {");
		s.add("\tthrow new GraphException(\"The rolename #roleName# is redefined for the VertexClass #vertexClassName#\");");
		s.add("}");
		return s;
	}
	
	
	private CodeBlock validAddRolenameSnippet(CodeSnippet s, boolean createClass) {
		if (!createClass) {
			s.add(
				"/**",
				" * adds the given vertex as <code>#roleCamelName#</code> to this vertex, i.e. creates an",
				" * <code>#edgeClassName#</code> edge from this vertex to the given ",
				" * one and returns the created edge.",
				" * @return  a newly created edge of type <code>#edgeClassName#</code>",
				" *          between this vertex and the given one.",
				" */",
				"public #edgeClassName# add#roleCamelName#(#vertexClassName# vertex);");
		} else {
			s.add(
				"public #edgeClassName# add#roleCamelName#(#vertexClassName# vertex) {",
				"\treturn ((#graphClassName#)getGraph()).create#edgeClassUniqueName#(#fromVertex#, #toVertex#);", "}");
		}
		return s;
	}
	
	private CodeBlock invalidAddRolenameSnippet(CodeSnippet s, boolean createClass) {
		s.add(
				"public #edgeClassName# add#roleCamelName#(#vertexClassName# vertex) {",
				"\tthrow new SchemaException(\"No edges of class \" + #edgeClassName# + \"are allowed at this vertex\");", "}");
		return s;
	}

	/**
	 * creates the <code>getRolenameList()</code> methods for the current
	 * vertex.
	 * 
	 * @param createClass
	 *            iff set to true, also the method bodies will be created
	 * @return the CodeBlock that contains the code for the
	 *         getRolenameList-methods
	 */
	private CodeBlock createRolenameMethods(boolean createClass) {
		VertexClass vc = (VertexClass) aec;
		Map<String, RolenameEntry> rolesToGenerateGetters = vc.getRolenameMap();
		/*
	     * if the interface should be created, remove all inherited (and unchanged)
	     * and redefined (in the sense of removed, not overwritten)
	     * rolenames from the list of rolenames to generate 
		 */
//		if (!createClass) {
//			Set<String> inheritedAndRedefinedRolenames = new HashSet<String>();
//			for (RolenameEntry entry : rolesToGenerate.values()) {
//				if (entry.isInherited() || entry.isRedefined())
//					inheritedAndRedefinedRolenames.add(entry.getRoleNameAtFarEnd());
//			}	
//			for (String s : inheritedAndRedefinedRolenames)
//				rolesToGenerate.remove(s);
//		}
			
		/* create code snippets for addROLENAME(Vertex vertex) methods */ 
		CodeList code = new CodeList();
		for (RolenameEntry entry : rolesToGenerateGetters.values()) {
			CodeSnippet s = configureRolenameCodesnippet(entry, createClass);
			if (entry.isRedefined())
				code.addNoIndent(invalidRolenameSnippet(s));
			else		
				code.addNoIndent(validRolenameSnippet(s, createClass));
			for (VertexEdgeEntry edgeEntry : entry.getVertexEdgeEntryList()) {
				if (edgeEntry.getEdge().isAbstract())
					continue;
				CodeSnippet addSnippet = new CodeSnippet(true);
				addSnippet.setVariable("roleCamelName", camelCase(entry.getRoleNameAtFarEnd()));
				addSnippet.setVariable("edgeClassName", schemaRootPackageName + "." + edgeEntry.getEdge().getQualifiedName());
				addSnippet.setVariable("edgeClassUniqueName", camelCase(edgeEntry.getEdge().getUniqueName()));
				addSnippet.setVariable("graphClassName", schemaRootPackageName + "." + edgeEntry.getEdge().getGraphClass().getQualifiedName());
				addSnippet.setVariable("vertexClassName", schemaRootPackageName + "." + edgeEntry.getVertex().getQualifiedName());
				
				if (edgeEntry.getDirection() == EdgeDirection.IN) {
					addSnippet.setVariable("fromVertex", "vertex");
					addSnippet.setVariable("toVertex", "this");
				} else {
					addSnippet.setVariable("toVertex", "vertex");
					addSnippet.setVariable("fromVertex", "this");
				}
				if (!edgeEntry.isRedefined()) 
					code.addNoIndent(validAddRolenameSnippet(addSnippet, createClass));
				else 
					code.addNoIndent(invalidAddRolenameSnippet(addSnippet, createClass));
			}
		}
		return code;
	}

	/**
	 * Creates a codeSnippet
	 * 
	 * @param codeList
	 *            The CodeList that represents the code for all Rolename Methods
	 * @param entry
	 *            the pair <Rolename, Set<EdgeClassTriple>> to generate code
	 *            for
	 * @param allRoles
	 *            all roles that are related to the VertexClass currently
	 *            creating code for
	 * @param createClass
	 *            toggles if to create code for the class or for the
	 *            interface
	 * @return
	 */
	private CodeSnippet configureRolenameCodesnippet(RolenameEntry entry, boolean createClass) {
		CodeSnippet s = new CodeSnippet(true);
		VertexClass lcvc = entry.getVertexClassAtFarEnd();
		if (lcvc.isInternal()) {
			s.setVariable("targetClass", "#jgPackage#." + lcvc.getQualifiedName());
		} else {
			s.setVariable("targetClass", schemaRootPackageName + "."
					+ lcvc.getQualifiedName());
		}
		s.setVariable("targetSimpleName", lcvc.getSimpleName());
		s.setVariable("roleName", entry.getRoleNameAtFarEnd());
		s.setVariable("roleCamelName", camelCase(entry.getRoleNameAtFarEnd()));
		s.setVariable("dir", "EdgeDirection." + entry.getEdgeClassToTraverse().getDirection().toString());
		EdgeClass lcec = entry.getEdgeClassToTraverse().getEdgeClass();
		if (lcec.isInternal()) {
			s.setVariable("ecQualifiedName", "#jgPackage#."
					+ lcec.getSimpleName());
		} else {
			s.setVariable("ecQualifiedName", schemaRootPackageName + "."
					+ lcec.getQualifiedName());
		}
		s.setVariable("ecCamelName", camelCase(lcec.getUniqueName()));
		return s;
	}

	/**
	 * creates the <code>getEdgeNameIncidences</code> methods
	 * 
	 * @param createClass
	 *            if set to true, also the method bodies will be created
	 * @return the CodeBlock that contains the code for the
	 *         getEdgeNameIncidences-methods
	 */
	private CodeBlock createIncidenceIteratorMethods(boolean createClass) {
		VertexClass vc = (VertexClass) aec;

		CodeList code = new CodeList();

		Set<EdgeClass> edgeClassSet = null;
		if (createClass) {
			edgeClassSet = vc.getEdgeClasses();
		} else {
			edgeClassSet = vc.getOwnEdgeClasses();
		}

		for (EdgeClass ec : edgeClassSet) {
			if (ec.isInternal())
				continue;

			if (createClass)
				addImports("#jgImplPackage#.IncidenceIterable");

			CodeSnippet s = new CodeSnippet(true);
			code.addNoIndent(s);

			String targetClassName = schemaRootPackageName + "."
					+ ec.getQualifiedName();
			s.setVariable("edgeClassSimpleName", ec.getSimpleName());
			s.setVariable("edgeClassQualifiedName", targetClassName);
			s.setVariable("edgeClassUniqueName", ec.getUniqueName());

			// getFooIncidences()
			if (!createClass) {
				s.add("/**");
				s.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName# or subtypes.");
				s.add(" */");
				s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences();");
			} else {
				s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences() {");
				s.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class);");
				s.add("}");
			}
			s.add("");
			// getFooIncidences(boolean nosubclasses)
			if (CodeGenerator.CREATE_METHODS_WITH_TYPEFLAG) {
				if (!createClass) {
					s.add("/**");
					s.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName#.");
					s.add(" * @param noSubClasses toggles wether subclasses of #edgeClassName# should be excluded");
					s.add(" */");
					s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(boolean noSubClasses);");
				} else {
					s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(boolean noSubClasses) {");
					s.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class, noSubClasses);");
					s.add("}\n");
				}
			}
			// getFooIncidences(EdgeDirection direction, boolean nosubclasses)
			if (CodeGenerator.CREATE_METHODS_WITH_TYPEFLAG) {
				if (!createClass) {
					s.add("/**");
					s.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName#.");
					s.add(" * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the Iterable");
					s.add(" * @param noSubClasses toggles wether subclasses of #edgeClassName# should be excluded");
					s.add(" */");
					s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction, boolean noSubClasses);");
				} else {
					s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction, boolean noSubClasses) {");
					s.add("\treturn  new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class, direction, noSubClasses);");
					s.add("}");
				}
			}
			s.add("");
			// getFooIncidences(EdgeDirection direction)
			if (!createClass) {
				s.add("/**");
				s.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName#.");
				s.add(" * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the Iterable");
				s.add(" */");
				s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction);");
			} else {
				s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction) {");
				s.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class, direction);");
				s.add("}");
			}
		}
		return code;
	}

	/**
	 * creates the sets of valid in and valid out edges
	 */
	private CodeBlock createValidEdgeSets(VertexClass vc) {
		addImports("java.util.Set");
		addImports("java.util.HashSet");
		addImports("#jgPackage#.Edge");
		CodeList code = new CodeList();
		code.setVariable("vcQualifiedName", schemaRootPackageName + ".impl."
				+ vc.getQualifiedName());
		code.setVariable("vcCamelName", camelCase(vc.getUniqueName()));
		CodeSnippet s = new CodeSnippet(true);
		s.add("/* add all valid from edges */");
		s
				.add("private static Set<java.lang.Class<? extends Edge>> validFromEdges = new HashSet<java.lang.Class<? extends Edge>>();");
		s.add("");
		s.add("/* (non-Javadoc)");
		s.add(" * @see jgralab.Vertex:isValidAlpha()");
		s.add(" */");
		s.add("public boolean isValidAlpha(Edge edge) {");
		s.add("\treturn validFromEdges.contains(edge.getClass());");
		s.add("}");
		s.add("");
		s.add("{");
		code.addNoIndent(s);
		for (EdgeClass ec : vc.getValidFromEdgeClasses()) {
			CodeSnippet line = new CodeSnippet(true);
			line.setVariable("edgeClassQualifiedName", schemaRootPackageName
					+ ".impl." + ec.getQualifiedName());
			line
					.add("\tvalidFromEdges.add(#edgeClassQualifiedName#Impl.class);");
			code.addNoIndent(line);
		}
		s = new CodeSnippet(true);
		s.add("}");
		s.add("");
		s.add("/* add all valid to edges */");
		s
				.add("private static Set<java.lang.Class<? extends Edge>> validToEdges = new HashSet<java.lang.Class<? extends Edge>>();");
		s.add("");
		s.add("/* (non-Javadoc)");
		s.add(" * @see jgralab.Vertex:isValidOemga()");
		s.add(" */");
		s.add("public boolean isValidOmega(Edge edge) {");
		s.add("\treturn validToEdges.contains(edge.getClass());");
		s.add("}");
		s.add("");
		s.add("{");
		code.addNoIndent(s);
		for (EdgeClass ec : vc.getValidToEdgeClasses()) {
			CodeSnippet line = new CodeSnippet(true);
			line.setVariable("edgeClassQualifiedName", schemaRootPackageName
					+ ".impl." + ec.getQualifiedName());
			line.add("\tvalidToEdges.add(#edgeClassQualifiedName#Impl.class);");
			code.addNoIndent(line);
		}
		s = new CodeSnippet(true);
		s.add("}");
		code.addNoIndent(s);
		return code;
	}

}
