//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.16 at 07:21:33 PM CET 
//


package com.tibco.xmlns.repo.types._2002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for globalVariable complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="globalVariable"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="deploymentSettable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="serviceSettable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="isOverride" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="modTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "globalVariable", propOrder = {
    "name",
    "value",
    "deploymentSettable",
    "serviceSettable",
    "type",
    "isOverride",
    "modTime"
})
public class GlobalVariable {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String value;
    protected boolean deploymentSettable;
    protected boolean serviceSettable;
    @XmlElement(required = true)
    protected String type;
    protected Boolean isOverride;
    protected String modTime;

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
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the deploymentSettable property.
     * 
     */
    public boolean isDeploymentSettable() {
        return deploymentSettable;
    }

    /**
     * Sets the value of the deploymentSettable property.
     * 
     */
    public void setDeploymentSettable(boolean value) {
        this.deploymentSettable = value;
    }

    /**
     * Gets the value of the serviceSettable property.
     * 
     */
    public boolean isServiceSettable() {
        return serviceSettable;
    }

    /**
     * Sets the value of the serviceSettable property.
     * 
     */
    public void setServiceSettable(boolean value) {
        this.serviceSettable = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the isOverride property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsOverride() {
        return isOverride;
    }

    /**
     * Sets the value of the isOverride property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsOverride(Boolean value) {
        this.isOverride = value;
    }

    /**
     * Gets the value of the modTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModTime() {
        return modTime;
    }

    /**
     * Sets the value of the modTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModTime(String value) {
        this.modTime = value;
    }

}
