//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.26 at 10:38:48 AM CET 
//


package com.tibco.schemas.tra.model.core.packagingmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Property complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Property"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="visibility" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="scalable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="overrideValue" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property", propOrder = {
    "name",
    "type",
    "visibility",
    "scalable",
    "overrideValue"
})
public class Property {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String type;
    @XmlElement(required = true)
    protected String visibility;
    protected boolean scalable;
    protected boolean overrideValue;

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
     * Gets the value of the visibility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisibility(String value) {
        this.visibility = value;
    }

    /**
     * Gets the value of the scalable property.
     * 
     */
    public boolean isScalable() {
        return scalable;
    }

    /**
     * Sets the value of the scalable property.
     * 
     */
    public void setScalable(boolean value) {
        this.scalable = value;
    }

    /**
     * Gets the value of the overrideValue property.
     * 
     */
    public boolean isOverrideValue() {
        return overrideValue;
    }

    /**
     * Sets the value of the overrideValue property.
     * 
     */
    public void setOverrideValue(boolean value) {
        this.overrideValue = value;
    }

}