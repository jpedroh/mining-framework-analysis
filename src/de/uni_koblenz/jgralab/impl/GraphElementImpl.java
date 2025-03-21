package de.uni_koblenz.jgralab.impl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.Schema;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/left.java
/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class GraphElementImpl implements InternalGraphElement {
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
    if (!graph.isLoading() && graph.getECARuleManagerIfThere() != null) {
      graph.getECARuleManager().fireBeforeChangeAttributeEvents(this, name, oldValue, newValue);
    }
  }

  /**
	 * Triggers ECA-rule after an Attribute is changed
	 * 
	 * @param name
	 *            of the changed Attribute
	 */
  public void ecaAttributeChanged(String name, Object oldValue, Object newValue) {
    if (!graph.isLoading() && graph.getECARuleManagerIfThere() != null) {
      graph.getECARuleManager().fireAfterChangeAttributeEvents(this, name, oldValue, newValue);
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

  public void internalSetDefaultValue(Attribute attr) throws GraphIOException {
    attr.setDefaultValue(this);
  }
}
=======
/**
 * TODO add comment
 *
 * @author ist@uni-koblenz.de
 *
 */
public abstract class GraphElementImpl<SC extends GraphElementClass<SC, IC>, IC extends GraphElement<SC, IC>> implements InternalGraphElement<SC, IC> {
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
    return graph.getAttributedElementClass();
  }

  @Override public Schema getSchema() {
    return graph.getSchema();
  }

  @Override public void graphModified() {
    graph.graphModified();
  }

  /**
	 * Triggers ECA-rules before an Attribute is changed
	 *
	 * @param name
	 *            of the changing Attribute
	 */
  public void ecaAttributeChanging(String name, Object oldValue, Object newValue) {
    if (!graph.isLoading() && (graph.hasECARuleManager())) {
      graph.getECARuleManager().fireBeforeChangeAttributeEvents(this, name, oldValue, newValue);
    }
  }

  /**
	 * Triggers ECA-rule after an Attribute is changed
	 *
	 * @param name
	 *            of the changed Attribute
	 */
  public void ecaAttributeChanged(String name, Object oldValue, Object newValue) {
    if (!graph.isLoading() && (graph.hasECARuleManager())) {
      graph.getECARuleManager().fireAfterChangeAttributeEvents(this, name, oldValue, newValue);
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

  @Override public void internalSetDefaultValue(Attribute attr) throws GraphIOException {
    attr.setDefaultValue(this);
  }

  @Override public boolean isInstanceOf(SC cls) {
    return cls.getSchemaClass().isInstance(this);
  }
}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphElementImpl.java/right.java
