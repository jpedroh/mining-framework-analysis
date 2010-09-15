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
package de.uni_koblenz.jgralab.algolib.visitors;

import java.util.ArrayList;
import java.util.List;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;

public class GraphVisitorComposition extends
		VisitorComposition implements GraphVisitor {

	private List<GraphVisitor> visitors;

	@Override
	protected void createVisitorsLazily() {
		super.createVisitorsLazily();
		if (visitors == null) {
			visitors = new ArrayList<GraphVisitor>();
		}
	}

	@Override
	public void addVisitor(Visitor visitor) {
		super.addVisitor(visitor);
		if (visitor instanceof GraphVisitor) {
			if (!visitors.contains(visitor)) {
				visitors.add((GraphVisitor) visitor);
			}
		}
	}

	@Override
	public void removeVisitor(Visitor visitor) {
		super.removeVisitor(visitor);
		if (visitor instanceof GraphVisitor) {
			visitors.remove(visitor);
		}
	}

	@Override
	public void clearVisitors() {
		super.clearVisitors();
		visitors.clear();
	}

	@Override
	public void visitEdge(Edge e) {
		int n = visitors.size();
		for (int i = 0; i < n; i++) {
			visitors.get(i).visitEdge(e);
		}
	}

	@Override
	public void visitVertex(Vertex v) {
		int n = visitors.size();
		for (int i = 0; i < n; i++) {
			visitors.get(i).visitVertex(v);
		}
	}
}
