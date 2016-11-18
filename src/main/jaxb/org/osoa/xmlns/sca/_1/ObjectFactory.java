//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.23 at 05:22:53 PM CET 
//


package org.osoa.xmlns.sca._1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import com.tibco.tns.xsd.amf.models.sca.extensions.Implementation;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.osoa.xmlns.sca._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ComponentType_QNAME = new QName("http://www.osoa.org/xmlns/sca/1.0", "componentType");
    private final static QName _Composite_QNAME = new QName("http://www.osoa.org/xmlns/sca/1.0", "composite");
    private final static QName _Interface_QNAME = new QName("http://www.osoa.org/xmlns/sca/1.0", "interface");
    private final static QName _Binding_QNAME = new QName("http://www.osoa.org/xmlns/sca/1.0", "binding");
    private final static QName _Implementation_QNAME = new QName("http://xsd.tns.tibco.com/amf/models/sca/extensions", "implementation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.osoa.xmlns.sca._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ComponentType }
     * 
     */
    public ComponentType createComponentType() {
        return new ComponentType();
    }

    /**
     * Create an instance of {@link Composite }
     * 
     */
    public Composite createComposite() {
        return new Composite();
    }

    /**
     * Create an instance of {@link CompositeServiceType }
     * 
     */
    public CompositeServiceType createCompositeServiceType() {
        return new CompositeServiceType();
    }

    /**
     * Create an instance of {@link CompositeReferenceType }
     * 
     */
    public CompositeReferenceType createCompositeReferenceType() {
        return new CompositeReferenceType();
    }

    /**
     * Create an instance of {@link ServiceType }
     * 
     */
    public ServiceType createServiceType() {
        return new ServiceType();
    }

    /**
     * Create an instance of {@link ReferenceType }
     * 
     */
    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    /**
     * Create an instance of {@link PropertyType }
     * 
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link Component }
     * 
     */
    public Component createComponent() {
        return new Component();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Reference }
     * 
     */
    public Reference createReference() {
        return new Reference();
    }

    /**
     * Create an instance of {@link Wire }
     * 
     */
    public Wire createWire() {
        return new Wire();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.osoa.org/xmlns/sca/1.0", name = "componentType")
    public JAXBElement<ComponentType> createComponentType(ComponentType value) {
        return new JAXBElement<ComponentType>(_ComponentType_QNAME, ComponentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Composite }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.osoa.org/xmlns/sca/1.0", name = "composite")
    public JAXBElement<Composite> createComposite(Composite value) {
        return new JAXBElement<Composite>(_Composite_QNAME, Composite.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Interface }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.osoa.org/xmlns/sca/1.0", name = "interface")
    public JAXBElement<Interface> createInterface(Interface value) {
        return new JAXBElement<Interface>(_Interface_QNAME, Interface.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Binding }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.osoa.org/xmlns/sca/1.0", name = "binding")
    public JAXBElement<Binding> createBinding(Binding value) {
        return new JAXBElement<Binding>(_Binding_QNAME, Binding.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Implementation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.osoa.org/xmlns/sca/1.0", name = "implementation")
    public JAXBElement<Implementation> createImplementation(Implementation value) {
        return new JAXBElement<Implementation>(_Implementation_QNAME, Implementation.class, null, value);
    }

}
