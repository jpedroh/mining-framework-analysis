/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2010 Institute for Software Technology
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

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.AbstractGraphMarker;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueCollection;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueType;

/**
 * Retrieves the value associated with a given key in the given map.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>OBJECT get(m:MAP, key:OBJECT)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>m</code> - map to be used</dd>
 * <dd><code>key</code> - object serving as key</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>the object which is mapped by <code>obj</code> in <code>m</code>.</dd>
 * <dd><code>Null</code> if one of the given parameters is <code>Null</code> or
 * if <code>obj</code> is no key in <code>m</code></dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class Get extends Greql2Function {
	{
		JValueType[][] x = {
				{ JValueType.MAP, JValueType.OBJECT, JValueType.OBJECT },
				{ JValueType.COLLECTION, JValueType.INT, JValueType.OBJECT } };
		signatures = x;

		description = "Returns the value associated with KEY in MAP, or the element of INDEX in COLLECTION.";

		Category[] c = { Category.COLLECTIONS_AND_MAPS };
		categories = c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#evaluate(de.uni_koblenz
	 * .jgralab.Graph, de.uni_koblenz.jgralab.BooleanGraphMarker,
	 * de.uni_koblenz.jgralab.greql2.jvalue.JValue[])
	 */
	@Override
	public JValue evaluate(Graph graph, AbstractGraphMarker<AttributedElement> subgraph,
			JValue[] arguments) throws EvaluateException {
		switch (checkArguments(arguments)) {
		case 0:
			return arguments[0].toJValueMap().get(arguments[1]);
		case 1:
			int index = arguments[1].toInteger();
			JValueCollection col = arguments[0].toCollection();
			if (index >= col.size()) {
				throw new EvaluateException(
						"The given collection has fewer than " + (index + 1)
								+ " elements.  There are exactly " + col.size()
								+ ".");
			}
			return col.toJValueList().get(index);
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#getEstimatedCardinality
	 * (int)
	 */
	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#getEstimatedCosts
	 * (java.util.ArrayList)
	 */
	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#getSelectivity()
	 */
	@Override
	public double getSelectivity() {
		return 1;
	}

}
