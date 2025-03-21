package de.uni_koblenz.jgralab.schema;
import de.uni_koblenz.jgralab.Edge;

/**
 * Interface for Edge/Aggregation/Composition classes, instances of this class
 * represent an schema element.
 * 
 * @author ist@uni-koblenz.de
 */
public interface EdgeClass extends GraphElementClass<EdgeClass, Edge> {
  public static final String DEFAULTEDGECLASS_NAME = "Edge";

  /**
	 * adds a superclass to the list of superclasses, all attributes get
	 * inherited from those classes
	 * 
	 * @param superClass
	 *            the edge class to be added to the list of superclasses if an
	 *            attribute name exists in superClass and in this class
	 * 
	 */
  public void addSuperClass(EdgeClass superClass);

  public IncidenceClass getFrom();

  public IncidenceClass getTo();

  public Class<? extends Edge> getSchemaClass();
}