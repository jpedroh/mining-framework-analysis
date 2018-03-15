//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.06.28 at 10:18:28 AM CEST
//


package org.eclipse.winery.model.selfservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for ApplicationOption complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ApplicationOption">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="iconUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="planServiceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="planInputMessageUrl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationOption", propOrder = {"description", "iconUrl", "planServiceName", "planInputMessageUrl"})
public class ApplicationOption {

    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String iconUrl;
    @XmlElement(required = true)
    protected String planServiceName;
    @XmlElement(required = true)
    protected String planInputMessageUrl;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDescription(final String value) {
        this.description = value;
    }

    /**
     * Gets the value of the iconUrl property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getIconUrl() {
        return this.iconUrl;
    }

    /**
     * Sets the value of the iconUrl property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setIconUrl(final String value) {
        this.iconUrl = value;
    }

    /**
     * Gets the value of the planServiceName property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getPlanServiceName() {
        return this.planServiceName;
    }

    /**
     * Sets the value of the planServiceName property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setPlanServiceName(final String value) {
        this.planServiceName = value;
    }

    /**
     * Gets the value of the planInputMessageUrl property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getPlanInputMessageUrl() {
        return this.planInputMessageUrl;
    }

    /**
     * Sets the value of the planInputMessageUrl property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setPlanInputMessageUrl(final String value) {
        this.planInputMessageUrl = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setId(final String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(final String value) {
        this.name = value;
    }

}
