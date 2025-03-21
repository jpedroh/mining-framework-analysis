package de.uni_koblenz.jgralab;
import java.io.IOException;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/AttributedElement.java/left.java
/**
 * aggregates graphs, edges and vertices
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface AttributedElement extends Comparable<AttributedElement> {
  /**
	 * @return the {@link AttributedElementClass} of this
	 *         {@link AttributedElement}
	 */
  public AttributedElementClass getAttributedElementClass();

  /**
	 * @return the schema class of this attributedelement
	 */
  public Class<? extends AttributedElement> getSchemaClass();

  public GraphClass getGraphClass();

  public void readAttributeValueFromString(String attributeName, String value) throws GraphIOException, NoSuchAttributeException;

  public String writeAttributeValueToString(String attributeName) throws IOException, GraphIOException, NoSuchAttributeException;

  public void writeAttributeValues(GraphIO io) throws IOException, GraphIOException;

  public void readAttributeValues(GraphIO io) throws GraphIOException;

  public <T extends java.lang.Object> T getAttribute(String name) throws NoSuchAttributeException;

  public <T extends java.lang.Object> void setAttribute(String name, T data) throws NoSuchAttributeException;

  /**
	 * @return the schema this AttributedElement belongs to
	 */
  public Schema getSchema();

  void initializeAttributesWithDefaultValues();
}
=======
>>>>>>> Unknown file: This is a bug in JDime.


/**
 * superclass of graphs, edges and vertices
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface AttributedElement<SC extends AttributedElementClass<SC, IC>, IC extends AttributedElement<SC, IC>> extends Comparable<AttributedElement<SC, IC>> {
  /**
	 * @return the {@link AttributedElementClass} of this
	 *         {@link AttributedElement}
	 */
  public SC getAttributedElementClass();

  /**
	 * @return the schema class of this attributedelement
	 */
  public Class<? extends IC> getSchemaClass();

  public GraphClass getGraphClass();

  public void readAttributeValueFromString(String attributeName, String value) throws GraphIOException, NoSuchAttributeException;

  public String writeAttributeValueToString(String attributeName) throws IOException, GraphIOException, NoSuchAttributeException;

  public void writeAttributeValues(GraphIO io) throws IOException, GraphIOException;

  public void readAttributeValues(GraphIO io) throws GraphIOException;

  public <T extends java.lang.Object> T getAttribute(String name) throws NoSuchAttributeException;

  public <T extends java.lang.Object> void setAttribute(String name, T data) throws NoSuchAttributeException;

  /**
	 * @return the schema this AttributedElement belongs to
	 */
  public Schema getSchema();

  void initializeAttributesWithDefaultValues();

  /**
	 * @param cls
	 * @return true, iff this attributed element is an instance of cls.
	 */
  public boolean isInstanceOf(SC cls);
}