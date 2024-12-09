package org.example.scdpro2.business.models.BClassDiagarm;

import java.io.Serializable;
/**
 * Represents an abstract base class for model components.
 * The {@code ModelComponent} class provides a structure for all components in the diagram,
 * including their name, and requires subclasses to implement validation and code generation methods.
 * This class implements {@code Serializable} for serialization support.
 */
// Abstract base class
public abstract class ModelComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The name of the model component.
     * This field stores the name of the component, which is used in various diagram and code generation processes.
     */
    protected String name;
    /**
     * Constructs a new {@code ModelComponent} with the specified name.
     * This constructor initializes the component with the given name.
     *
     * @param name The name of the model component.
     */
    public ModelComponent(String name) {
        this.name = name;
    }
    /**
     * Returns the name of the model component.
     *
     * @return The name of the model component.
     */
    public String getName() {
        return name;
    }
    /**
     * Validates the model component.
     * This is an abstract method that must be implemented by subclasses to perform specific validation.
     */
    public abstract void validate();
    /**
     * Generates code for the model component.
     * This is an abstract method that must be implemented by subclasses to generate specific code
     * based on the component's properties.
     *
     * @return The generated code for the component.
     */
    public abstract String generateCode();
}
