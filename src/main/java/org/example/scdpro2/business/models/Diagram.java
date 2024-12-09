package org.example.scdpro2.business.models;

import java.io.Serializable;
/**
 * Abstract class representing a diagram within a project.
 * This class serves as the base class for different types of diagrams
 * (such as class diagrams, package diagrams, etc.) in the system.
 * It provides common properties and methods that all diagram types should have,
 * including a title and methods for rendering the diagram and generating code.
 */
public abstract class Diagram implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The title of the diagram.
     * This represents the name or label of the diagram, used for identification and display.
     */
    protected String title;
    /**
     * Constructs a new {@code Diagram} object with the given title.
     *
     * @param title The title of the diagram.
     */
    public Diagram(String title) {
        this.title = title;
    }
    /**
     * Returns the title of the diagram.
     *
     * @return The title of the diagram.
     */
    public String getTitle() {
        return title;
    }
    /**
     * Sets the title of the diagram.
     *
     * @param title The new title for the diagram.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Abstract method for rendering the diagram.
     * This method is intended to be implemented by subclasses to define
     * how the diagram should be visually rendered in the UI.
     */
    public abstract void render(); // For UI purposes
    /**
     * Abstract method for generating the code representation of the diagram.
     * This method is intended to be implemented by subclasses to define
     * how the diagram should be translated into code for code generation purposes.
     *
     * @return A string representing the code generated for the diagram.
     */
    public abstract String toCode(); // For code generation purposes

    /**
     * Abstract method for retrieving the type of the diagram.
     * This method is intended to be implemented by subclasses to specify
     * the type of diagram (e.g., class diagram, package diagram, etc.).
     *
     * @return The type of the diagram as a {@code DiagramType}.
     */
    public abstract DiagramType getType();
}
