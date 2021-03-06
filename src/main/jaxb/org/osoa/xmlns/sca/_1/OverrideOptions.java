//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.23 at 05:22:53 PM CET 
//


package org.osoa.xmlns.sca._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OverrideOptions.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OverrideOptions"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="no"/&gt;
 *     &lt;enumeration value="may"/&gt;
 *     &lt;enumeration value="must"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "OverrideOptions")
@XmlEnum
public enum OverrideOptions {

    @XmlEnumValue("no")
    NO("no"),
    @XmlEnumValue("may")
    MAY("may"),
    @XmlEnumValue("must")
    MUST("must");
    private final String value;

    OverrideOptions(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OverrideOptions fromValue(String v) {
        for (OverrideOptions c: OverrideOptions.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
