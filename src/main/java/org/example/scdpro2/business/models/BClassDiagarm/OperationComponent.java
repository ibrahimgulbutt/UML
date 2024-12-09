package org.example.scdpro2.business.models.BClassDiagarm;

import java.io.Serializable;
/**
 * Represents an operation (or method) component in a class diagram.
 * The {@code OperationComponent} class stores details about an operation such as its visibility,
 * name, and return type. It provides methods for validation, code generation, and modification of these details.
 * This class extends the {@code ModelComponent} class.
 */
public class OperationComponent extends ModelComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The visibility of the operation (e.g., public, private, protected).
     * The visibility determines how accessible the operation is from other classes.
     */
    private String visibility;
    /**
     * The name of the operation.
     * This field stores the name of the operation, which can be used in code generation and diagrams.
     */
    private String name;
    /**
     * The return type of the operation (e.g., String, int).
     * This field defines what type of value the operation will return when called.
     */
    private String returntype;
    /**
     * Constructs a new {@code OperationComponent} object with the specified name, visibility, and return type.
     * This constructor initializes the operation with the given details.
     *
     * @param name The name of the operation.
     * @param visibility The visibility of the operation (e.g., "public", "private").
     * @param returntype The return type of the operation (e.g., "String", "int").
     */
    public OperationComponent(String name, String visibility,String returntype) {
        super(name);
        this.name=name;
        this.visibility = visibility;
        this.returntype=returntype;
    }
    /**
     * Returns a string representation of the operation component.
     * The string includes the visibility, name, and return type of the operation.
     *
     * @return A string representation of the operation.
     */
    @Override
    public String toString() {
        return "visibility='" + visibility + '\'' +
                ", name='" + name + '\'' + returntype+
                '}';
    }
    /**
     * Returns the visibility of the operation.
     *
     * @return The visibility of the operation (e.g., "public", "private").
     */
    public String getVisibility() {
        return visibility;
    }
    /**
     * Performs operation-specific validation.
     * This method can be overridden in subclasses to add additional validation logic.
     */
    @Override
    public void validate() {
        // Add operation-specific validation here
    }
    /**
     * Returns the name of the operation.
     *
     * @return The name of the operation.
     */
    public String getName() {
        return name;
    }
    /**
     * Generates code for the operation based on its visibility, return type, and name.
     * The generated code follows the format: "visibility returnType name() {}".
     *
     * @return The generated code for the operation.
     */
    @Override
    public String generateCode() {
        return visibility + " " + returntype + " " + name + "() {}";
    }
    /**
     * Sets the visibility of the operation.
     *
     * @param visibility The visibility to set (e.g., "public", "private").
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    /**
     * Sets the name of the operation.
     *
     * @param newName The new name for the operation.
     */
    public void setName(String newName) {
        this.name = newName; // Update the correct field
    }

    /**
     * Sets the return type of the operation.
     *
     * @param text The new return type for the operation.
     */
    public void setReturnType(String text) {
        this.returntype=text;
    }

    /**
     * Returns the return type of the operation.
     *
     * @return The return type of the operation (e.g., "String", "int").
     */
    public String getReturnType() {
        return returntype;
    }
}
