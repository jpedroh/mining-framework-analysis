/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2008 Institute for Software Technology
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

package de.uni_koblenz.jgralab.greql2.funlib;

import java.util.ArrayList;

import de.uni_koblenz.jgralab.BooleanGraphMarker;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.JValueInvalidTypeException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;

/**
 * Checks if the given edge is a loop. That means, its start- and end-vertex is the same.
 *
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>BOOLEAN isLoop(e:EDGE)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl><dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>e</code> - edge to check</dd>
 * <dt><b>Returns:</b></dt>
 * <dd><code>true</code> if the start-vertex and the end-vertex is the same</dd>
 * <dd><code>Null</code> if one of the given parameters is <code>Null</code></dd>
 * <dd><code>false</code> otherwise</dd>
 * </dl>
 * </dd>
 * </dl>
 * @author ist@uni-koblenz.de
 *
 */

/*
 * Gets a edge as parameter and returns true, if this edge is a loop, that
 * means, if alpha nd omega vertex are identical
 *
 * @param edge
 *            the edge to check
 * @return true if the given edge is a loop
 * @author ist@uni-koblenz.de
 *
 */
public class IsLoop implements Greql2Function {

	public JValue evaluate(Graph graph, BooleanGraphMarker subgraph,
			JValue[] arguments) throws EvaluateException {
		try {
			if (arguments.length < 1) {
				throw new WrongFunctionParameterException(this, null, arguments);
			}
			Edge edge = arguments[0].toEdge();
			return new JValue(edge.getAlpha() == edge.getOmega(), edge);
		} catch (JValueInvalidTypeException ex) {
			throw new WrongFunctionParameterException(this, null, arguments);
		}
	}

	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 2;
	}

	public double getSelectivity() {
		return 0.01;
	}

	public long getEstimatedCardinality(int inElements) {
		return 1;
	}

	public String getExpectedParameters() {
		return "(Edge)";
	}

}
