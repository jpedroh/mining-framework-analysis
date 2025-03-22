package org.opendope.conditions;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.docx4j.XmlUtils;
import org.docx4j.model.datastorage.DomToXPathMap;
import org.docx4j.model.datastorage.OpenDoPEHandler;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.CustomXmlPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://opendope.org/conditions}xpathref"/>
 *         &lt;element ref="{http://opendope.org/conditions}and"/>
 *         &lt;element ref="{http://opendope.org/conditions}or"/>
 *         &lt;element ref="{http://opendope.org/conditions}not"/>
 *       &lt;/choice>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="comments" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(value = XmlAccessType.FIELD) @XmlType(name = "", propOrder = { "particle" }) @XmlRootElement(name = "condition") public class Condition implements Evaluable {
  private static Logger log = LoggerFactory.getLogger(Condition.class);

  @XmlElements(value = { @XmlElement(name = "xpathref", type = Xpathref.class), @XmlElement(name = "and", type = And.class), @XmlElement(name = "or", type = Or.class), @XmlElement(name = "not", type = Not.class) }) protected Evaluable particle;

  @XmlAttribute(name = "id", required = true) @XmlJavaTypeAdapter(value = CollapsedStringAdapter.class) @XmlID @XmlSchemaType(name = "ID") protected String id;

  @XmlAttribute(name = "name") protected String name;

  @XmlAttribute(name = "description") protected String description;

  @XmlAttribute(name = "comments") protected String comments;

  @XmlAttribute(name = "source") protected String source;

  /**
     * Gets the value of the particle property.
     * 
     * @return
     *     possible object is
     *     {@link Xpathref }
     *     {@link And }
     *     {@link Or }
     *     {@link Not }
     *     
     */
  public Evaluable getParticle() {
    return particle;
  }

  /**
     * Sets the value of the particle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Xpathref }
     *     {@link And }
     *     {@link Or }
     *     {@link Not }
     *     
     */
  public void setParticle(Evaluable value) {
    this.particle = value;
  }

  public boolean evaluate(WordprocessingMLPackage pkg, Map<String, CustomXmlPart> customXmlDataStorageParts, Map<String, Condition> conditionsMap, Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap) {
    if (OpenDoPEHandler.ENABLE_XPATH_CACHE && domToXPathMap != null && particle instanceof Xpathref) {
      Xpathref xpRef = (Xpathref) particle;
      String xpath = xpRef.toString(conditionsMap, xpathsMap);
      if (xpath.startsWith("string")) {
        String tmpPath = xpath.replace("][1]", "]");
        String path = extractPath(tmpPath);
        if (tmpPath.startsWith("string(" + path + ")=\'")) {
          String val = domToXPathMap.getPathMap().get(path);
          boolean result = (tmpPath.equals("string(" + path + ")=\'" + val + "\'"));
          if (
<<<<<<< /usr/src/app/output/plutext/docx4j/aa42a5b3f77c350916c1b790b26cb2633e9aa31c/src/main/java/org/opendope/conditions/Condition.java/left.java
          result == false
=======
          val == null
>>>>>>> /usr/src/app/output/plutext/docx4j/aa42a5b3f77c350916c1b790b26cb2633e9aa31c/src/main/java/org/opendope/conditions/Condition.java/right.java
          ) {

<<<<<<< /usr/src/app/output/plutext/docx4j/aa42a5b3f77c350916c1b790b26cb2633e9aa31c/src/main/java/org/opendope/conditions/Condition.java/left.java
            result = (tmpPath.equals("string(" + path + ")=\'" + val.trim() + "\'"));
=======
            log.info("Couldn\'t find " + val + " in domToXPathMap path map; reverting to default handling");
>>>>>>> /usr/src/app/output/plutext/docx4j/aa42a5b3f77c350916c1b790b26cb2633e9aa31c/src/main/java/org/opendope/conditions/Condition.java/right.java
          } else {
            boolean result = (tmpPath.equals("string(" + path + ")=\'" + val + "\'"));
            if (result == false) {
              result = (tmpPath.equals("string(" + path + ")=\'" + val.trim() + "\'"));
            }
            if (log.isDebugEnabled()) {
              boolean tmpCheck = particle.evaluate(pkg, customXmlDataStorageParts, conditionsMap, xpathsMap);
              if (result == tmpCheck) {
              } else {
                String message = "PANIC! Manual string calc doesn\'t match XPath eval!\n" + xpath + "\nstring(" + path + ")=\'" + val + "\'\n";
                log.error(message);
                throw new RuntimeException(message);
              }
            }
            return result;
          }
          if (this.id.startsWith("tVK") || log.isDebugEnabled()) {
            boolean tmpCheck = particle.evaluate(pkg, customXmlDataStorageParts, conditionsMap, xpathsMap);
            if (result == tmpCheck) {
            } else {
              String message = "PANIC! Manual string calc doesn\'t match XPath eval!\n" + xpath + "\nstring(" + path + ")=\'" + val + "\'\n";
              log.error(message);
              throw new RuntimeException(message);
            }
          }
          return result;
        }
      } else {
        if (xpath.startsWith("count")) {
          String tmpPath = xpath.replace("][1]", "]");
          String path = extractPath(tmpPath);
          if (tmpPath.equals("count(" + path + ")>0")) {
            Integer val = domToXPathMap.getCountMap().get(path);
            if (val == null) {
              val = domToXPathMap.getCountMap().get(DomToXPathMap.PREFIX_ALL_NODES + path);
            }
            if (val == null) {
              if (log.isDebugEnabled()) {
                boolean tmpCheck = particle.evaluate(pkg, customXmlDataStorageParts, conditionsMap, xpathsMap);
                if (tmpCheck) {
                  String message = "FIXME.  Expected map entry facilitating manual eval of  " + path;
                  log.error(message);
                  throw new RuntimeException(message);
                } else {
                  System.out.println("Manual count calc worked for null case");
                }
                return tmpCheck;
              } else {
                return false;
              }
            } else {
              boolean result = (val > 0);
              if (log.isDebugEnabled()) {
                boolean tmpCheck = particle.evaluate(pkg, customXmlDataStorageParts, conditionsMap, xpathsMap);
                if (result == tmpCheck) {
                  System.out.println("Manual count calc worked");
                } else {
                  String message = "PANIC! Manual count calc doesn\'t match XPath eval!\n" + xpath + "\ncount(" + path + ")>0\n" + val;
                  log.error(message);
                  throw new RuntimeException(message);
                }
              }
              return result;
            }
          } else {
            log.debug("No manual count eval coded for: " + tmpPath);
          }
        }
      }
    }
    return particle.evaluate(pkg, customXmlDataStorageParts, conditionsMap, xpathsMap);
  }

  private String extractPath(String xpath) {
    int firstBracket = xpath.indexOf("(");
    int lastBracket = xpath.indexOf(")");
    return xpath.substring(firstBracket + 1, lastBracket);
  }

  @XmlTransient private DomToXPathMap domToXPathMap = null;

  public void setDomToXPathMap(DomToXPathMap domToXPathMap) {
    this.domToXPathMap = domToXPathMap;
  }

  public void listXPaths(List<org.opendope.xpaths.Xpaths.Xpath> theList, Map<String, Condition> conditionsMap, Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap) {
    particle.listXPaths(theList, conditionsMap, xpathsMap);
  }

  /**
	 * Map the IDs used in this condition to new values; useful for merging ConditionParts.
	 * 
	 * @param xpathIdMap
	 * @param conditionIdMap
	 * @since 3.0.0
	 */
  public void mapIds(Map<String, String> xpathIdMap, Map<String, String> conditionIdMap) {
    particle.mapIds(xpathIdMap, conditionIdMap);
  }

  public String toString(Map<String, Condition> conditionsMap, Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap) {
    return particle.toString(conditionsMap, xpathsMap);
  }

  public Condition repeat(String xpathBase, int index, Map<String, Condition> conditionsMap, Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap) {
    Condition newCondition;
    if (this.getParticle() instanceof Xpathref) {
      newCondition = new Condition();
      Xpathref xpathref = new Xpathref();
      xpathref.setId(((Xpathref) this.getParticle()).getId());
      newCondition.setParticle(xpathref);
    } else {
      if ((this.getParticle() instanceof Not) && (((Not) this.getParticle()).getParticle() instanceof Xpathref)) {
        newCondition = new Condition();
        Not notParticle = new Not();
        newCondition.setParticle(notParticle);
        Xpathref xpathref = new Xpathref();
        xpathref.setId(((Xpathref) ((Not) this.getParticle()).getParticle()).getId());
        notParticle.setParticle(xpathref);
      } else {
        newCondition = XmlUtils.deepCopy(this);
      }
    }
    String newConditionId = id + "_" + index;
    newCondition.setId(newConditionId);
    Condition preExistingSanity = conditionsMap.put(newCondition.getId(), newCondition);
    if (preExistingSanity != null) {
      String preExisting = XmlUtils.marshaltoString(preExistingSanity);
      String newC = XmlUtils.marshaltoString(newCondition);
      if (preExistingSanity.equals(newC)) {
        log.debug("Duplicate identical Condition being added: " + newCondition.getId());
      } else {
        log.error("Duplicate Condition " + newCondition.getId() + ": " + "\n" + newC + " overwriting " + "\n" + preExisting);
      }
    }
    newCondition.getParticle().repeat(xpathBase, index, conditionsMap, xpathsMap);
    return newCondition;
  }

  /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
  public String getId() {
    return id;
  }

  /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
  public void setId(String value) {
    this.id = value;
  }

  /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
  public String getName() {
    return name;
  }

  /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
  public void setName(String value) {
    this.name = value;
  }

  /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
  public String getDescription() {
    return description;
  }

  /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
  public void setDescription(String value) {
    this.description = value;
  }

  /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
  public String getComments() {
    return comments;
  }

  /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
  public void setComments(String value) {
    this.comments = value;
  }

  /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
  public String getSource() {
    return source;
  }

  /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
  public void setSource(String value) {
    this.source = value;
  }
}