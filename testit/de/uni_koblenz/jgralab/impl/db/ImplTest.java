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
package de.uni_koblenz.jgralab.impl.db;

import static junit.framework.Assert.fail;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralabtest.schemas.vertextest.VertexTestGraph;
import de.uni_koblenz.jgralabtest.schemas.vertextest.VertexTestSchema;

public abstract class ImplTest {

	protected String url = "postgresql://localhost:5432/graphdatabase_test/";
	protected String userName = "postgres";
	protected String password = "energizer";

	protected VertexTestGraph vertexTestGraph;

	protected VertexTestGraph createVertexTestGraphWithDatabaseSupport(
			String id, int vMax, int eMax) {
		try {
			GraphDatabase database = GraphDatabase.openGraphDatabase(url,
					userName, password);
			if (!database.contains(VertexTestSchema.instance())) {
				GraphIO.loadSchemaIntoGraphDatabase(
						"testit/testschemas/VertexTestSchema.tg", database);
			}
			return VertexTestSchema.instance()
					.createVertexTestGraphWithDatabaseSupport(id, vMax, eMax,
							database);
		} catch (Exception exception) {
			exception.printStackTrace();
			fail("Could not create test graph in database.");
			return null;
		}
	}

	protected void cleanDatabaseOfTestGraph(Graph graph) {
		try {
			GraphDatabase database = GraphDatabase.openGraphDatabase(url,
					userName, password);
			if (database.containsGraph(graph.getId())) {
				database.delete((DatabasePersistableGraph) graph);
			}
		} catch (GraphDatabaseException exception) {
			exception.printStackTrace();
			fail("Could not delete test graph from database.");
		}
	}
}
