//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.05.16 at 05:29:02 PM MESZ
//


package org.apache.ode.schemas.dd._2007._03;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for tCleanup complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tCleanup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="category" maxOccurs="unbounded" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="instance"/>
 *               &lt;enumeration value="variables"/>
 *               &lt;enumeration value="messages"/>
 *               &lt;enumeration value="correlations"/>
 *               &lt;enumeration value="events"/>
 *               &lt;enumeration value="all"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="filter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="on">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="success"/>
 *             &lt;enumeration value="failure"/>
 *             &lt;enumeration value="always"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCleanup", propOrder = {"category", "filter"})
public class TCleanup {

    @XmlElement(defaultValue = "all")
    protected List<String> category;
    protected List<String> filter;
    @XmlAttribute
    protected String on;

    /**
     * Gets the value of the category property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why
     * there is not a <CODE>set</CODE> method for the category property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getCategory().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     *
     *
     */
    public List<String> getCategory() {
        if (this.category == null) {
            this.category = new ArrayList<>();
        }
        return this.category;
    }

    /**
     * Gets the value of the filter property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why
     * there is not a <CODE>set</CODE> method for the filter property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getFilter().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     *
     *
     */
    public List<String> getFilter() {
        if (this.filter == null) {
            this.filter = new ArrayList<>();
        }
        return this.filter;
    }

    /**
     * Gets the value of the on property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getOn() {
        return this.on;
    }

    /**
     * Sets the value of the on property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setOn(final String value) {
        this.on = value;
    }

}
