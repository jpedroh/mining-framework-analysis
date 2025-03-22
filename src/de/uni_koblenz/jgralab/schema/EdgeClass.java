package de.uni_koblenz.jgralab.schema;
import de.uni_koblenz.jgralab.Edge;

/**
 * Interface for edge classes. Instances of this class represent a grUML
 * EdgeClass schema element.
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


<<<<<<< /usr/src/app/output/jgralab/jgralab/1599d6b3faa82c01a998c961c15a55304d634e59/src/de/uni_koblenz/jgralab/schema/EdgeClass.java/left.java
  @Override public Class<? extends Edge> getSchemaClass();
=======
>>>>>>> Unknown file: This is a bug in JDime.
}