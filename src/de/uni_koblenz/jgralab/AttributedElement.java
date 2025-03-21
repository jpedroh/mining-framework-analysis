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

package de.uni_koblenz.jgralab;

import java.io.IOException;

import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/left.java
 * aggregates graphs, edges and vertices
 * 
 * @author ist@uni-koblenz.de
 * 
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/base.java
 * aggregates graphs, edges and vertices 
 * @author Steffen Kahle
 *
=======
 * superclass of graphs, edges and vertices
 * 
 * @author ist@uni-koblenz.de
 * 
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/right.java
 */
public interface AttributedElement<SC extends AttributedElementClass<SC, IC>, IC extends AttributedElement<SC, IC>>
		extends Comparable<AttributedElement<SC, IC>> {
	/**
	 * @return the {@link AttributedElementClass} of this
	 *         {@link AttributedElement}
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/left.java
	public AttributedElementClass getAttributedElementClass();

||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/base.java
	public AttributedElementClass getAttributedElementClass();
	
=======
	public SC getAttributedElementClass();

>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/right.java
	/**
	 * @return the schema class of this attributedelement
	 */
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/left.java
	public Class<? extends AttributedElement> getSchemaClass();

||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/base.java
	public Class<? extends AttributedElement> getM1Class();
	
=======
	public Class<? extends IC> getSchemaClass();

>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/right.java
	public GraphClass getGraphClass();

	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException;

	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException;

	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException;

	public void readAttributeValues(GraphIO io) throws GraphIOException;

	public <T> T getAttribute(String name) throws NoSuchAttributeException;

	public <T> void setAttribute(String name, T data)
			throws NoSuchAttributeException;

	/**
	 * @return the schema this AttributedElement belongs to
	 */
	public Schema getSchema();
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/left.java

	void initializeAttributesWithDefaultValues();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/base.java
=======

	void initializeAttributesWithDefaultValues();

	/**
	 * @param cls
	 * @return true, iff this attributed element is an instance of cls.
	 */
	public boolean isInstanceOf(SC cls);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/right.java
}
