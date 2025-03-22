package de.uni_koblenz.jgralab.impl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class GraphElementImpl implements GraphElementBase {
  protected int id;

  protected GraphElementImpl(Graph graph) {
    assert graph != null;
    this.graph = (GraphBaseImpl) graph;
  }

  protected GraphBaseImpl graph;

  @Override public Graph getGraph() {
    return graph;
  }

  @Override public GraphClass getGraphClass() {
    return (GraphClass) graph.getAttributedElementClass();
  }

  @Override public Schema getSchema() {
    return graph.getSchema();
  }

  /**
	 * Changes the graph version of the graph this element belongs to. Should be
	 * called whenever the graph is changed, all changes like adding, creating
	 * and reordering of edges and vertices or changes of attributes of the
	 * graph, an edge or a vertex are treated as a change.
	 */
  public void graphModified() {
    graph.graphModified();
  }

  /**
	 * Triggers ECA-rules before an Attribute is changed
	 * 
	 * @param name
	 *            of the changing Attribute
	 */
  public void ecaAttributeChanging(String name, Object oldValue, Object newValue) {
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/9e10661f5de9a2260cc2cf184d92598a1285679c/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/left.java
    !this.graph.isLoading() && this.graph.getECARuleManagerIfThere() != null
=======
    !graph.isLoading()
>>>>>>> /usr/src/app/output/jgralab/jgralab/9e10661f5de9a2260cc2cf184d92598a1285679c/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/right.java
    ) {
      graph.getECARuleManager().
<<<<<<< /usr/src/app/output/jgralab/jgralab/9e10661f5de9a2260cc2cf184d92598a1285679c/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/left.java
      getECARuleManagerIfThere().fireBeforeChangeAttributeEvents(this, name, oldValue, newValue)
=======
      fireBeforeChangeAttributeEvents(this, name, oldValue, newValue)
>>>>>>> /usr/src/app/output/jgralab/jgralab/9e10661f5de9a2260cc2cf184d92598a1285679c/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/right.java
      ;
    }
  }

  /**
	 * Triggers ECA-rule after an Attribute is changed
	 * 
	 * @param name
	 *            of the changed Attribute
	 */
  public void ecaAttributeChanged(String name, Object oldValue, Object newValue) {
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/9e10661f5de9a2260cc2cf184d92598a1285679c/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/left.java
    !this.graph.isLoading() && this.graph.getECARuleManagerIfThere() != null
=======
    !graph.isLoading()
>>>>>>> /usr/src/app/output/jgralab/jgralab/9e10661f5de9a2260cc2cf184d92598a1285679c/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/right.java
    ) {
      graph.getECARuleManager().
<<<<<<< /usr/src/app/output/jgralab/jgralab/9e10661f5de9a2260cc2cf184d92598a1285679c/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/left.java
      getECARuleManagerIfThere().fireAfterChangeAttributeEvents(this, name, oldValue, newValue)
=======
      fireAfterChangeAttributeEvents(this, name, oldValue, newValue)
>>>>>>> /usr/src/app/output/jgralab/jgralab/9e10661f5de9a2260cc2cf184d92598a1285679c/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/right.java
      ;
    }
  }

  @Override public int getId() {
    return id;
  }

  @Override public void initializeAttributesWithDefaultValues() {
    for (Attribute attr : getAttributedElementClass().getAttributeList()) {
      if (attr.getDefaultValueAsString() == null) {
        continue;
      }
      try {
        internalSetDefaultValue(attr);
      } catch (GraphIOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
	 * 
	 * @param attr
	 * @throws GraphIOException
	 */
  public void internalSetDefaultValue(Attribute attr) throws GraphIOException {
    attr.setDefaultValue(this);
  }
}