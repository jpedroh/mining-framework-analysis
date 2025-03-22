package de.uni_koblenz.jgralab;
import de.uni_koblenz.jgralab.schema.GraphElementClass;

/**
 * aggregates vertices and edges
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface GraphElement<SC extends GraphElementClass<SC, IC>, IC extends GraphElement<SC, IC>> extends AttributedElement<SC, IC> {
  /**
	 * returns the id of this graph element
	 * 
	 * @return the id of this graph element
	 */
  public int getId();

  /**
	 * returns the graph containing this graph element
	 * 
	 * @return the graph containing this graph element
	 */
  public Graph getGraph();

  /**
	 * returns true if this GraphElement is still present in the Graph (i.e. not
	 * deleted). This check is equivalent to getGraph().containsVertex(this) or
	 * getGraph().containsEdge(this).
	 */
  public boolean isValid();

  /**
	 * removes this graph element
	 */
  public void delete();
}