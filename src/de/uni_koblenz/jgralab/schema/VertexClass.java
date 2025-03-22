package de.uni_koblenz.jgralab.schema;
import java.util.Set;
import de.uni_koblenz.jgralab.Vertex;

/**
 * Represents a VertexClass in the Schema.
 *
 * @author ist@uni-koblenz.de
 */
public interface VertexClass extends GraphElementClass<VertexClass, Vertex> {
  public final static String DEFAULTVERTEXCLASS_NAME = "Vertex";

  /**
	 * adds a superclass to the list of superclasses, all attributes get
	 * inherited from those classes
	 *
	 * @param superClass
	 *            the vertex class to be added to the list of superclasses
	 *
	 */
  public void addSuperClass(VertexClass superClass);

  public Set<IncidenceClass> getAllInIncidenceClasses();

  public Set<IncidenceClass> getAllOutIncidenceClasses();

  public Set<IncidenceClass> getValidFromFarIncidenceClasses();

  public Set<IncidenceClass> getValidToFarIncidenceClasses();

  public Set<IncidenceClass> getOwnAndInheritedFarIncidenceClasses();

  public boolean isValidFromFor(EdgeClass ec);

  public boolean isValidToFor(EdgeClass ec);

  public Set<EdgeClass> getValidToEdgeClasses();

  public Set<EdgeClass> getValidFromEdgeClasses();

  public Set<EdgeClass> getConnectedEdgeClasses();

  public Set<EdgeClass> getOwnConnectedEdgeClasses();
}