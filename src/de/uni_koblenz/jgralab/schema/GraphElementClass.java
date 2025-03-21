package de.uni_koblenz.jgralab.schema;
import de.uni_koblenz.jgralab.GraphElement;

/**
 * Base class for Vertex/Edge/Aggregation/Composition classes.
 * 
 * @author ist@uni-koblenz.de
 */
public interface GraphElementClass<SC extends GraphElementClass<SC, IC>, IC extends GraphElement<SC, IC>> extends AttributedElementClass<SC, IC> {
  /**
	 * Returns the GraphClass of this AttributedElementClass.
	 * 
	 * @return the GraphClass in which this graph element class resides
	 */
  public GraphClass getGraphClass();
}