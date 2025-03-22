package de.uni_koblenz.jgralab.schema;
import java.util.Set;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.impl.DirectedSchemaEdgeClass;

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

  /**
	 * @return The set of {@link IncidenceClass}es that can be accessed by role
	 *         name from instances of this vertex class
	 */
  public Set<IncidenceClass> getOwnAndInheritedFarIncidenceClasses();

  /**
	 * @param roleName
	 * @return the {@link EdgeClass} corresponding to the far-end
	 *         <code>roleName</code> including its direction from the view of
	 *         this vertex class
	 */
  public DirectedSchemaEdgeClass getDirectedEdgeClassForFarEndRole(String roleName);

  /**
	 * @param ec
	 * @return true, iff edges of class <code>ec</code> may start at vertices of
	 *         this vertex class
	 */
  public boolean isValidFromFor(EdgeClass ec);

  /**
	 * @param ec
	 * @return true, iff edges of class <code>ec</code> may end at vertices of
	 *         this vertex class
	 */
  public boolean isValidToFor(EdgeClass ec);

  public Set<EdgeClass> getValidToEdgeClasses();

  public Set<EdgeClass> getValidFromEdgeClasses();

  public Set<EdgeClass> getConnectedEdgeClasses();

  public Set<EdgeClass> getOwnConnectedEdgeClasses();
}