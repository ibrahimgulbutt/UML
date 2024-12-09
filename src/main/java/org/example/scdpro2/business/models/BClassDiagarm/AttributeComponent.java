package org.example.scdpro2.business.models.BClassDiagarm;

import java.io.Serializable;
/**
 * Represents an attribute component of a class in a class diagram.
 * The {@code AttributeComponent} class models an attribute in a class, which includes
 * visibility, name, and datatype. It extends {@code ModelComponent} and provides methods
 * for setting and retrieving its properties, validating its attributes, and generating
 * code for the attribute representation.
 */
public class AttributeComponent extends ModelComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The visibility of the attribute, typically represented as public, private, or protected.
     */
    private String visibility;
    /**
     * The name of the attribute.
     */
    private String name;
    /**
     * The datatype of the attribute (e.g., int, String).
     */
    private String datatype;
    /**
     * Constructs a new {@code AttributeComponent} with the specified name, visibility, and datatype.
     *
     * @param name The name of the attribute.
     * @param visibility The visibility of the attribute (e.g., "public", "private").
     * @param datatype The datatype of the attribute (e.g., "String", "int").
     */
    public AttributeComponent(String name, String visibility,String datatype) {
        super(name);
        this.name=name;
        this.visibility = visibility;
        this.datatype=datatype;
    }

    /**
     * Returns a string representation of the attribute component.
     * The string includes the visibility, name, and datatype of the attribute.
     *
     * @return A string representation of the attribute in the format:
     *         visibility + " name='" + name + "' : datatype".
     */
    @Override
    public String toString() {
        return visibility + '\'' +
                " name='" + name + '\'' +
                ':';
    }
    /**
     * Returns the visibility of the attribute.
     *
     * @return The visibility of the attribute (e.g., "public", "private").
     */
    public String getVisibility() {
        return visibility;
    }
    /**
     * Validates the attribute. This method can be extended to include attribute-specific validation logic.
     */
    @Override
    public void validate() {
        // Add attribute-specific validation here
    }
    /**
     * Returns the name of the attribute.
     *
     * @return The name of the attribute.
     */
    public String getName() {
        return name;
    }
    /**
     * Generates the code representation of the attribute.
     * The generated code includes visibility, name, and datatype.
     *
     * @return A string representation of the attribute in code format, such as "public name; String".
     */
    @Override
    public String generateCode() {
        return visibility + " " + name + "; "+ datatype;
    }
    /**
     * Sets the visibility of the attribute.
     *
     * @param visibility The visibility to set (e.g., "public", "private").
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    /**
     * Sets the name of the attribute.
     *
     * @param newVal The name to set.
     */
    public void setName(String newVal) {
        this.name=newVal;
    }
    /**
     * Sets the datatype of the attribute.
     *
     * @param newVal The datatype to set (e.g., "String", "int").
     */
    public void setDataType(String newVal) {
        this.datatype=newVal;
    }
    /**
     * Returns the datatype of the attribute.
     *
     * @return The datatype of the attribute (e.g., "String", "int").
     */
    public String getDataType() {
        return this.datatype;
    }
}
