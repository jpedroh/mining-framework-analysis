//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.10.07 at 06:05:13 PM CEST
//


package org.opentosca.bus.management.api.soaphttp.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for invokeOperationSync complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="invokeOperationSync">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CsarID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServiceInstanceID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NodeInstanceID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServiceTemplateIDNamespaceURI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServiceTemplateIDLocalPart" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="NodeTemplateID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="RelationshipTemplateID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;element name="InterfaceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OperationName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="Params" type="{http://siserver.org/schema}ParamsMap" minOccurs="0"/>
 *           &lt;element name="Doc" type="{http://siserver.org/schema}Doc" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invokeOperationSync",
         propOrder = {"csarID", "serviceInstanceID", "nodeInstanceID", "serviceTemplateIDNamespaceURI",
                      "serviceTemplateIDLocalPart", "nodeTemplateID", "relationshipTemplateID", "interfaceName",
                      "operationName", "params", "doc"})
public class InvokeOperationSync {

    @XmlElement(name = "CsarID", required = true)
    protected String csarID;
    @XmlElement(name = "ServiceInstanceID")
    protected String serviceInstanceID;
    @XmlElement(name = "NodeInstanceID")
    protected String nodeInstanceID;
    @XmlElement(name = "ServiceTemplateIDNamespaceURI", required = true)
    protected String serviceTemplateIDNamespaceURI;
    @XmlElement(name = "ServiceTemplateIDLocalPart", required = true)
    protected String serviceTemplateIDLocalPart;
    @XmlElement(name = "NodeTemplateID")
    protected String nodeTemplateID;
    @XmlElement(name = "RelationshipTemplateID")
    protected String relationshipTemplateID;
    @XmlElement(name = "InterfaceName")
    protected String interfaceName;
    @XmlElement(name = "OperationName", required = true)
    protected String operationName;
    @XmlElement(name = "Params")
    protected ParamsMap params;
    @XmlElement(name = "Doc")
    protected Doc doc;

    /**
     * Gets the value of the csarID property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCsarID() {
        return this.csarID;
    }

    /**
     * Sets the value of the csarID property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCsarID(final String value) {
        this.csarID = value;
    }

    /**
     * Gets the value of the serviceInstanceID property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getServiceInstanceID() {
        return this.serviceInstanceID;
    }

    /**
     * Sets the value of the serviceInstanceID property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setServiceInstanceID(final String value) {
        this.serviceInstanceID = value;
    }

    /**
     * Gets the value of the nodeInstanceID property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getNodeInstanceID() {
        return this.nodeInstanceID;
    }

    /**
     * Sets the value of the nodeInstanceID property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setNodeInstanceID(final String value) {
        this.nodeInstanceID = value;
    }

    /**
     * Gets the value of the serviceTemplateIDNamespaceURI property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getServiceTemplateIDNamespaceURI() {
        return this.serviceTemplateIDNamespaceURI;
    }

    /**
     * Sets the value of the serviceTemplateIDNamespaceURI property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setServiceTemplateIDNamespaceURI(final String value) {
        this.serviceTemplateIDNamespaceURI = value;
    }

    /**
     * Gets the value of the serviceTemplateIDLocalPart property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getServiceTemplateIDLocalPart() {
        return this.serviceTemplateIDLocalPart;
    }

    /**
     * Sets the value of the serviceTemplateIDLocalPart property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setServiceTemplateIDLocalPart(final String value) {
        this.serviceTemplateIDLocalPart = value;
    }

    /**
     * Gets the value of the nodeTemplateID property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getNodeTemplateID() {
        return this.nodeTemplateID;
    }

    /**
     * Sets the value of the nodeTemplateID property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setNodeTemplateID(final String value) {
        this.nodeTemplateID = value;
    }

    /**
     * Gets the value of the relationshipTemplateID property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getRelationshipTemplateID() {
        return this.relationshipTemplateID;
    }

    /**
     * Sets the value of the relationshipTemplateID property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setRelationshipTemplateID(final String value) {
        this.relationshipTemplateID = value;
    }

    /**
     * Gets the value of the interfaceName property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getInterfaceName() {
        return this.interfaceName;
    }

    /**
     * Sets the value of the interfaceName property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setInterfaceName(final String value) {
        this.interfaceName = value;
    }

    /**
     * Gets the value of the operationName property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getOperationName() {
        return this.operationName;
    }

    /**
     * Sets the value of the operationName property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setOperationName(final String value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the params property.
     *
     * @return possible object is {@link ParamsMap }
     *
     */
    public ParamsMap getParams() {
        return this.params;
    }

    /**
     * Sets the value of the params property.
     *
     * @param value allowed object is {@link ParamsMap }
     *
     */
    public void setParams(final ParamsMap value) {
        this.params = value;
    }

    /**
     * Gets the value of the doc property.
     *
     * @return possible object is {@link Doc }
     *
     */
    public Doc getDoc() {
        return this.doc;
    }

    /**
     * Sets the value of the doc property.
     *
     * @param value allowed object is {@link Doc }
     *
     */
    public void setDoc(final Doc value) {
        this.doc = value;
    }

}
