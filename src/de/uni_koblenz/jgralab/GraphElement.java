package de.uni_koblenz.jgralab;
import de.uni_koblenz.jgralab.schema.GraphElementClass;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/GraphElement.java/left.java
/**
 * aggregates vertices and edges
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface GraphElement extends AttributedElement {
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
=======
>>>>>>> Unknown file: This is a bug in JDime.


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