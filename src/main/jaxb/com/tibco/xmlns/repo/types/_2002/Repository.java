//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.16 at 06:31:15 PM CET 
//


package com.tibco.xmlns.repo.types._2002;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="FILE_ALIASES_LIST" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="globalVariables" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="globalVariable" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;element name="deploymentSettable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *                             &lt;element name="serviceSettable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *                             &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;element name="isOverride" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *                             &lt;element name="modTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "globalVariables"
})
@XmlRootElement(name = "repository")
public class Repository {

    protected Repository.Name name;
    protected Repository.GlobalVariables globalVariables;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link Repository.Name }
     *     
     */
    public Repository.Name getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link Repository.Name }
     *     
     */
    public void setName(Repository.Name value) {
        this.name = value;
    }

    /**
     * Gets the value of the globalVariables property.
     * 
     * @return
     *     possible object is
     *     {@link Repository.GlobalVariables }
     *     
     */
    public Repository.GlobalVariables getGlobalVariables() {
        return globalVariables;
    }

    /**
     * Sets the value of the globalVariables property.
     * 
     * @param value
     *     allowed object is
     *     {@link Repository.GlobalVariables }
     *     
     */
    public void setGlobalVariables(Repository.GlobalVariables value) {
        this.globalVariables = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="globalVariable" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;element name="deploymentSettable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
     *                   &lt;element name="serviceSettable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
     *                   &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;element name="isOverride" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
     *                   &lt;element name="modTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "globalVariable"
    })
    public static class GlobalVariables {

        protected List<Repository.GlobalVariables.GlobalVariable> globalVariable;

        /**
         * Gets the value of the globalVariable property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the globalVariable property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGlobalVariable().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Repository.GlobalVariables.GlobalVariable }
         * 
         * 
         */
        public List<Repository.GlobalVariables.GlobalVariable> getGlobalVariable() {
            if (globalVariable == null) {
                globalVariable = new ArrayList<Repository.GlobalVariables.GlobalVariable>();
            }
            return this.globalVariable;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
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
        @XmlType(name = "", propOrder = {
            "name",
            "value",
            "deploymentSettable",
            "serviceSettable",
            "type",
            "isOverride",
            "modTime"
        })
        public static class GlobalVariable {

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

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="FILE_ALIASES_LIST" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "filealiaseslist"
    })
    public static class Name {

        @XmlElement(name = "FILE_ALIASES_LIST", required = true)
        protected String filealiaseslist;
        @XmlAttribute(name = "name")
        protected String name;

        /**
         * Gets the value of the filealiaseslist property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFILEALIASESLIST() {
            return filealiaseslist;
        }

        /**
         * Sets the value of the filealiaseslist property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFILEALIASESLIST(String value) {
            this.filealiaseslist = value;
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

    }

}
