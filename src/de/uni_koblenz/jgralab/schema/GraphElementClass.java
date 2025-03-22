package de.uni_koblenz.jgralab.schema;
import java.util.Set;
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

  /**
	 * Checks if the current element is a direct or indirect subclass of another
	 * attributed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSubClass = attrElement.isSubClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct or inherited superclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct or
	 * inherited superclass of <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anAttributedElementClass
	 *            the possible superclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct or indirect subclass of this element, otherwise
	 *         <code>false</code>
	 */
  public boolean isSubClassOf(SC anAttributedElementClass);

  /**
	 * Checks if the current element is a direct or inherited superclass of
	 * another attributed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClass = attrElement.isSuperClass(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> attributed element is a
	 * direct or inherited subclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> attributed
	 * element are the same</li>
	 * <li>the <code>other</code> attributed element is not a direct or indirect
	 * subclass of <code>attrElement</code></li>
	 * <li>the <code>other</code> attributed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anAttributedElementClass
	 *            the possible subclass of this attributed element
	 * @return <code>true</code> if <code>anAttributedElementClass</code> is a
	 *         direct or indirect subclass of this element, otherwise
	 *         <code>false</code>
	 */
  public boolean isSuperClassOf(SC anAttributedElementClass);

  /**
	 * Lists all direct subclasses of this element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>subClasses = attrElement.getDirectSubClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct subclasses</li>
	 * <li><code>subClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited subclasses</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct subclasses of this element
	 */
  public Set<SC> getDirectSubClasses();

  /**
	 * Returns all direct superclasses of this element.
	 * 
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>AttributedElementClass</code> has one default direct superclass.
	 * Please consult the specifications of the used subclass for details.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = attrElement.getDirectSuperClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null</code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>attrElement´s</code>
	 * direct superclasses (including the default superclass)</li>
	 * <li><code>superClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited superclasses
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct superclasses of this element
	 */
  public Set<SC> getDirectSuperClasses();

  /**
	 * Returns all direct and indirect subclasses of this element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>subClasses = attrElement.getAllSubClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct and indirect subclasses</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct and indirect subclasses of this element
	 */
  public Set<SC> getAllSubClasses();

  /**
	 * Lists all direct and indirect superclasses of this element.
	 * 
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>AttributedElementClass</code> has a dedicated default superclass at
	 * the top of its inheritance hierarchy. Please consult the specifications
	 * of the used subclass for details.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = attrElement.getAllSuperClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null </code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>attrElement´s</code>
	 * direct and indirect superclasses (including the default superclass)</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct and indirect superclasses of this element
	 */
  public Set<SC> getAllSuperClasses();
}